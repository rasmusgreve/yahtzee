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
			System.out.println("***SinglePlayerAI's turn - q: " + Arrays.toString(question.roll) + ", " + question.rollsLeft);

		if (OUTPUT){
			System.out.print("Current scores. SinglePlayerAI: " + question.scoreboards[question.playerId].sum());
			if (question.scoreboards.length > 1)
				System.out.println(", opponent: " + question.scoreboards[question.playerId == 0 ? 1 : 0].sum());
		}

		
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
		int rollC = YahtzeeMath.colex(roll);
		
		int best = -1;
		double max = Double.NEGATIVE_INFINITY;
		if (OUTPUT)
			System.out.println("Choosing scoreboard slot - possible choices:");
		for (int type = 0; type < ScoreType.count; type++) {
			if (Scoreboard.isFilled(board, type)) continue; //Skip filled entries
			int value_of_roll = GameLogic.valueOfRoll(type, rollC);
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
		int[] rollSorted = roll.clone();
		Arrays.sort(rollSorted);
		
		int rollC = YahtzeeMath.colex(roll);
		
		double max = Double.NEGATIVE_INFINITY;
		int[] bestHoldDice = new int[6];
		for (boolean[] hold : getInterestingHolds(rollC))
		{
			if (hold == null) continue;
			int[] holdDice = getHoldDice(rollC, hold);			
			
			double sum = 0;
			for (int new_rollC : getPossibleRolls(rollC, hold))
			{
				sum += getProbSmart(holdDice, new_rollC) * valueOfRoll(new_rollC, rollsLeft-1, board, newRollValuesCache());
			}
			
			if (sum > max)
			{
				max = sum;
				bestHoldDice = getHoldDiceInit(rollSorted, hold);
			}
		}
		
		boolean[] resortedBestHold = new boolean[5];
		for (int i = 0; i < 5; i++) {
			if (bestHoldDice[roll[i]-1] > 0){
				resortedBestHold[i] = true;
				bestHoldDice[roll[i]-1]--;
			}
		}
		
		
		return resortedBestHold;
	}
	private double rollFromScoreboard(int board) {
		double s = 0;
		double[] cache = newRollValuesCache();
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++) {
			double v = valueOfRoll(YahtzeeMath.colex(YahtzeeMath.allRolls[i]), 2, board, cache);
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
	
	private double valueOfRoll(int rollC, int rollsLeft, int board, double[] rollValues)
	{
		
		if (rollsLeft == 0)
		{		
			double max = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < ScoreType.count; i++) {
				if (Scoreboard.isFilled(board, i)) continue; //Skip filled entries
				int rollVal = GameLogic.valueOfRoll(i, rollC);
				double boardVal = getBoardValue(Scoreboard.fill(board, i, rollVal));
				max = Math.max(max, boardVal + rollVal);
			}
			return max;
		}
		
		
		int idx = rollIdx(rollC, rollsLeft);
		if (rollValues[idx] == -1)
		{
			rollValues[idx] = Integer.MIN_VALUE;
			for (boolean[] hold : getInterestingHolds(rollC))
			{
				if (hold == null) continue;
				
				int[] holdDice = getHoldDice(rollC, hold);		
				
				double sum = 0;
								
				for (int new_rollC : getPossibleRolls(rollC, hold))
				{
					sum += getProbSmart(holdDice, new_rollC) * valueOfRoll(new_rollC, rollsLeft-1, board, rollValues);
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


