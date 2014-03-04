package player;

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
	public double[][] boardValues; //boardValues[boardhash][0=mean, 1=variance]
	public static final String filename = "multiPlayerCache.bin";
	public boolean OUTPUT = false;
	
	
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
		if (OUTPUT)
			System.out.println("Get best score entry");
		int best = -1;
		double max = Double.NEGATIVE_INFINITY;
		if (OUTPUT)
			System.out.println("possible choices:");
		for (int type = 0; type < ScoreType.count; type++) {
			if (Scoreboard.isFilled(board, type)) continue; //Skip filled entries
			
			int value_of_roll = GameLogic.valueOfRoll(type, roll);
			double[] boardValue = getBoardValue(Scoreboard.fill(board, type, value_of_roll));
			double newVal = getAdjustedMean(boardValue) + value_of_roll;
						
			if (OUTPUT)
				System.out.println("type: " + type + ", value: " + newVal);
			
			
			if (newVal > max){
				max = newVal;
				best = type;
			}
		}
		return ScoreType.values()[best];
	}
	
	
	private double getAdjustedMean(double mean, double variance){
		double val = mean;
		
		return val;
	}
	
	private double getAdjustedMean(double[] data){
		
		return getAdjustedMean(data[0], data[1]);
	}
	
	
	private boolean[] getBestHold(int[] roll, int rollsLeft, int board) //Kickoff
	{
		if (OUTPUT)
			System.out.println("Get best hold");
		double max = Double.NEGATIVE_INFINITY;
		boolean[] best = null;
		for (boolean[] hold : getInterestingHoldsInit(roll))
		{
			if (hold == null) continue;
			int[][] possibleRolls = getPossibleRollsInit(roll, hold);
			
			double[][] valueOfRollCache = new double[possibleRolls.length][];
			double[] probCache = new double[possibleRolls.length];
			
			for (int i = 0; i < possibleRolls.length; i++) {
				valueOfRollCache[i] = valueOfRoll(possibleRolls[i], rollsLeft-1, board, newRollValuesCache());
				probCache[i] = getProb(hold, possibleRolls[i]);
			}
			
			double newMean = getAggregatedMean(probCache, valueOfRollCache);
			double newVariance = getAggregatedVariance(probCache, valueOfRollCache);
			
			newMean = getAdjustedMean(newMean, newVariance);
			
			if (newMean > max)
			{
				max = newMean;
				best = hold;
			}
		}
		return best;
	}
	
	private double[] rollFromScoreboard(int board) {
		if (OUTPUT)
			System.out.println("ROLLFROMSCOREBORD");
		
		double[][] cache = newRollValuesCache();
				
		double[] probCache = new double[YahtzeeMath.allRolls.length];
		double[][] valueOfRollCache = new double[YahtzeeMath.allRolls.length][];
		
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++) {
			valueOfRollCache[i] = valueOfRoll(YahtzeeMath.allRolls[i], 2, board, cache);
			probCache[i] = YahtzeeMath.prob(5,YahtzeeMath.allRolls[i]);
		}
		double newMean = getAggregatedMean(probCache, valueOfRollCache);
		double newVariance = getAggregatedVariance(probCache, valueOfRollCache);
		
		return new double[]{newMean, newVariance};
	}
	
	public double[] getBoardValue(int board) {
		if (boardValues[board][0] == -1) {
			if (Scoreboard.isFull(board))
			{
				if (OUTPUT)
					System.out.println("board is full");
				boardValues[board] = new double[]{Scoreboard.bonus(board), 0};
			} 
			else
			{
				if (OUTPUT)
					System.out.println("Calculating board value: " + board);
				boardValues[board] = rollFromScoreboard(board);
				if (OUTPUT)
					System.out.println("Board value was: " + boardValues[board][0]);
			}
		}
		//System.out.println("Returning " + boardValues[board][0]);
		return boardValues[board].clone();
	}
	
	private double[] valueOfRoll(int[] roll, int rollsLeft, int board, double[][] rollValues)
	{
		if (rollsLeft == 0)
		{
			double[] best = {Double.NEGATIVE_INFINITY, 0};
			for (int i = 0; i < ScoreType.count; i++) {
				if (Scoreboard.isFilled(board, i)) continue; //Skip filled entries
				
				int rollVal = GameLogic.valueOfRoll(i, roll);
				double[] boardVal = getBoardValue(Scoreboard.fill(board, i, rollVal));
				//System.out.println("Boardval: " + (Scoreboard.fill(board, i, rollVal)) + " + " + rollVal + " = " + boardVal[0]);
				boardVal[0] = getAdjustedMean(boardVal) + rollVal;
				if (best[0] < boardVal[0]) {
					best = boardVal;
				}
			}
			//System.out.println("Best is: " + best[0]);
			return best;
		}
		
		int idx = rollIdx(roll, rollsLeft);
		if (rollValues[idx][0] == -1)
		{
			rollValues[idx] = new double[] {Double.NEGATIVE_INFINITY, 0};
			for (boolean[] hold : getInterestingHoldsInit(roll))
			{
				if (hold == null) continue;
				
				int[][] possibleRolls = getPossibleRollsInit(roll, hold);
				double[] probCache = new double[possibleRolls.length];
				double[][] valueOfRollCache = new double[possibleRolls.length][];
				for (int i = 0; i < possibleRolls.length; i++) { 
					valueOfRollCache[i] = valueOfRoll(possibleRolls[i], rollsLeft-1, board, rollValues);
					probCache[i] = getProb(hold, possibleRolls[i]);
				}

				double newMean = getAggregatedMean(probCache, valueOfRollCache);
				double newVariance = getAggregatedVariance(probCache, valueOfRollCache);
				
				double aggressiveMean = getAdjustedMean(newMean, newVariance);
				
				if (getAdjustedMean(rollValues[idx]) < aggressiveMean) {
					rollValues[idx] = new double[] {newMean, newVariance};
				}
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
