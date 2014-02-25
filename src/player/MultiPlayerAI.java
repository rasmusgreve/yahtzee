package player;

import java.util.ArrayList;
import java.util.Arrays;

import util.Persistence;
import util.YahtzeeMath;
import game.Answer;
import game.GameLogic;
import game.Question;
import game.Scoreboard;
import game.Scoreboard.ScoreType;

public class MultiPlayerAI extends BaseAI {

	private double aggresivity = 0;
	public double[][] boardValues; //boardValues[boardhash][0=mean, 1=std.dev.]
	public static final String filename = "multiPlayerCache.bin";
	public boolean OUTPUT = false;
	
	private double getMean(double[][] values, int boardhash)
	{
		return values[boardhash][0];
	}
	private double getStdDev(double[][] values, int boardhash)
	{
		return values[boardhash][1];
	}
	private void setMeanStdDev(double[][] values, int boardhash, double mean, double stddev)
	{
		values[boardhash][0] = mean;
		values[boardhash][1] = stddev;
	}
	
	private static double[][] newRollValuesCache()
	{
		double[][] rollValues = new double[1020][];
		for (int i = 0; i < rollValues.length; i++)
		{
			rollValues[i] = new double[]{-1,-1};
		}
		return rollValues;
	}
	
	
	public MultiPlayerAI()
	{
		boardValues = Persistence.loadDoubleArray(filename, 1000000, 2);
	}
	
	@Override
	public Answer PerformTurn(Question question) {
		if (OUTPUT)
			System.out.println("q: " + Arrays.toString(question.roll) + ", " + question.rollsLeft);

		Answer ans = new Answer();
		
		if (question.rollsLeft == 0)
			ans.selectedScoreEntry = getBestScoreEntry(question.roll, question.scoreboards[question.playerId].ConvertMapToInt());
		else
			ans.diceToHold = getBestHold(question.roll, question.rollsLeft, question.scoreboards[question.playerId].ConvertMapToInt());
		if (OUTPUT)
			System.out.println("a: " + Arrays.toString(ans.diceToHold) + ", " + ans.selectedScoreEntry);

		return ans;
	}
	
	private ScoreType getBestScoreEntry(int[] roll, int board)
	{
		int best = -1;
		double max = Double.NEGATIVE_INFINITY;
		if (OUTPUT)
			System.out.println("possible choices:");
		for (int type = 0; type < ScoreType.count; type++) {
			if (Scoreboard.isFilled(board, type)) continue; //Skip filled entries
			int value_of_roll = GameLogic.valueOfRoll(type, roll);
			int new_board = Scoreboard.fill(board, type, value_of_roll);
			
			
			double[] boardValue = getBoardValue(new_board);
			
			double newVal = getAdjustedMean(boardValue);
			newVal += value_of_roll;
						
			if (OUTPUT)
				System.out.println("type: " + type + ", value: " + newVal);
			
			
			if (newVal > max){
				max = newVal;
				best = type;
			}
		}
		return ScoreType.values()[best];
	}
	
	
	private double getAdjustedMean(double mean, double stddev){
		double val = mean;
		
		
		return val;
	}
	
	private double getAdjustedMean(double[] data){
		
		return getAdjustedMean(data[0], data[1]);
	}
	
	
	private boolean[] getBestHold(int[] roll, int rollsLeft, int board) //Kickoff
	{
		double max = Double.NEGATIVE_INFINITY;
		boolean[] best = null;
		for (boolean[] hold : getInterestingHolds(roll))
		{
			double newMean = 0;
			double newStddev = 0;
					
			ArrayList<int[]> possibleRolls = getPossibleRolls(roll, hold);
			double[][] valueOfRollCache = new double[possibleRolls.size()][];
			double[] probCache = new double[possibleRolls.size()];
			
			
			for (int i = 0; i < possibleRolls.size(); i++) {
				int[] new_roll = possibleRolls.get(i);
							
				double[] rollVal = valueOfRoll(new_roll, rollsLeft-1, board, newRollValuesCache());
				valueOfRollCache[i] = rollVal;
				
				probCache[i] = getProb(hold, new_roll);
				
				newMean += probCache[i] * getAdjustedMean(valueOfRollCache[i]);
			}
			
			for (int i = 0; i < possibleRolls.size(); i++) {
				//TRO(ELS+R)
				newStddev += probCache[i] * (valueOfRollCache[i][1] + Math.abs(valueOfRollCache[i][0] - newMean)); 
				
			}
//				sum += getProb(hold, new_roll) * valueOfRoll(new_roll, rollsLeft-1, board, newRollValuesCache());
			
			if (newMean > max)
			{
				max = newMean;
				best = hold;
			}
		}
		return best;
	}
	private double[] rollFromScoreboard(int board) {

		double[] result = new double[2];
		double[][] cache = newRollValuesCache();
		
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++) {
			double[] value = valueOfRoll(YahtzeeMath.allRolls[i], 2, board, cache);
			
			
		}
		
		
		
		
		
		double s = 0;
		double[][] cache = newRollValuesCache();
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++) {
			double v = valueOfRoll(YahtzeeMath.allRolls[i], 2, board, cache);
			s += v * YahtzeeMath.prob(5,YahtzeeMath.allRolls[i]);
		}
		return s;
	}
	
	public double[] getBoardValue(int board) {
		if (getMean(boardValues,board) == -1) {
			if (Scoreboard.isFull(board))
			{
				setMeanStdDev(boardValues, board, Scoreboard.bonus(board), 0);
			} 
			else
			{
				boardValues[board] = rollFromScoreboard(board);
			}
		}
					
		return boardValues[board];
	}
	
	private double[] valueOfRoll(int[] roll, int rollsLeft, int board, double[][] rollValues)
	{
		if (rollsLeft == 0)
		{		
			double max = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < ScoreType.count; i++) {
				if (Scoreboard.isFilled(board, i)) continue; //Skip filled entries
				int rollVal = GameLogic.valueOfRoll(i, roll);
				double[] boardVal = getBoardValue(Scoreboard.fill(board, i, rollVal));
				max = Math.max(max, boardVal + rollVal);
			}
			return max;
		}
		
		int idx = rollIdx(roll, rollsLeft);
		if (getMean(boardValues, idx) == -1)
		{
			rollValues[idx] = Integer.MIN_VALUE;
			for (boolean[] hold : getInterestingHolds(roll))
			{
				double sum = 0;
				for (int[] new_roll : getPossibleRolls(roll, hold))
				{
					sum += getProb(hold, new_roll) * valueOfRoll(new_roll, rollsLeft-1, board, rollValues);
				}
				rollValues[idx] = Math.max(rollValues[idx], sum);
			}
		}
		return rollValues[idx];
	}
	
	
	
	@Override
	public String getName() {
		return "Multi player AI";
	}

	@Override
	public void cleanUp() {
		Persistence.storeDoubleArray(boardValues, filename);
	}


}
