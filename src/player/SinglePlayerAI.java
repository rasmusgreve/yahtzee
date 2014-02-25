package player;

import java.util.Arrays;
import game.Answer;
import game.GameLogic;
import game.Question;
import game.Scoreboard;
import game.Scoreboard.ScoreType;
import util.Persistence;
import util.YahtzeeMath;

public class SinglePlayerAI extends BaseAI {

	public double[] boardValues;
	public static final String filename = "singlePlayerCache.bin";
	public boolean OUTPUT = false;
	
	public SinglePlayerAI() {
		boardValues = Persistence.loadArray(filename,1000000);
	}
	
	private static double[] newRollValuesCache()
	{
		double[] rollValues = new double[1020];
		Arrays.fill(rollValues, -1);
		return rollValues;
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
			double newVal = getBoardValue(new_board) + value_of_roll;
			
			if (OUTPUT)
				System.out.println("type: " + type + ", value: " + newVal);
			
			
			if (newVal > max){
				max = newVal;
				best = type;
			}
		}
		return ScoreType.values()[best];
	}
	
	private boolean[] getBestHold(int[] roll, int rollsLeft, int board) //Kickoff
	{
		double max = Double.NEGATIVE_INFINITY;
		boolean[] best = null;
		for (boolean[] hold : getInterestingHolds(roll))
		{
			double sum = 0;
			for (int[] new_roll : getPossibleRolls(roll, hold))
			{
				sum += getProb(hold, new_roll) * valueOfRoll(new_roll, rollsLeft-1, board, newRollValuesCache());
			}
			
			if (sum > max)
			{
				max = sum;
				best = hold;
			}
		}
		return best;
	}
	private double rollFromScoreboard(int board) {

		double s = 0;
		double[] cache = newRollValuesCache();
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++) {
			double v = valueOfRoll(YahtzeeMath.allRolls[i], 2, board, cache);
			s += v * YahtzeeMath.prob(5,YahtzeeMath.allRolls[i]);
		}
		return s;
	}
	
	public double getBoardValue(int board) {
		if (boardValues[board] == -1) {
			if (Scoreboard.isFull(board))
			{
				boardValues[board] = Scoreboard.bonus(board);
			} 
			else
			{
				boardValues[board] = rollFromScoreboard(board);
			}
		}
					
		return boardValues[board];
	}
	
	private double valueOfRoll(int[] roll, int rollsLeft, int board, double[] rollValues)
	{
		if (rollsLeft == 0)
		{		
			double max = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < ScoreType.count; i++) {
				if (Scoreboard.isFilled(board, i)) continue; //Skip filled entries
				int rollVal = GameLogic.valueOfRoll(i, roll);
				double boardVal = getBoardValue(Scoreboard.fill(board, i, rollVal));
				max = Math.max(max, boardVal + rollVal);
			}
			return max;
		}
		
		int idx = rollIdx(roll, rollsLeft);
		if (rollValues[idx] == -1)
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
	public void cleanUp(){
		Persistence.storeArray(boardValues, filename);
	}
	
	@Override
	public String getName()
	{
		return "Single player AI";
	}


}


