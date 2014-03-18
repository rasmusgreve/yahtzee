package player;

import java.util.Arrays;

import game.Answer;
import game.GameLogic;
import game.Question;
import game.Scoreboard;
import game.Scoreboard.ScoreType;
import util.Persistence;
import util.YahtzeeMath;

public class SinglePlayerAIFloat extends BaseAIFloat {

	public float[] boardValues;
	public static final String filename = "singlePlayerCacheFloat.bin";
	public boolean OUTPUT = false;
	
	public SinglePlayerAIFloat() {
		boardValues = Persistence.loadFloatArray(filename,1000000);
		getBoardValue(0);
		System.out.println(boardValues[0]);
	}
	
	private static float[] newRollValuesCache()
	{
		float[] rollValues = new float[1020];
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
		int rollC = YahtzeeMath.colex(roll);
		
		int best = -1;
		float max = Float.NEGATIVE_INFINITY;
		if (OUTPUT)
			System.out.println("possible choices:");
		for (int type = 0; type < ScoreType.count; type++) {
			if (Scoreboard.isFilled(board, type)) continue; //Skip filled entries
			int value_of_roll = GameLogic.valueOfRoll(type, rollC);
			int new_board = Scoreboard.fill(board, type, value_of_roll);
			float newVal = getBoardValue(new_board) + value_of_roll;
			
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
		
		float max = Float.NEGATIVE_INFINITY;
		int[] bestHoldDice = new int[6];
		for (boolean[] hold : getInterestingHolds(rollC))
		{
			if (hold == null) continue;
			int[] holdDice = getHoldDice(rollC, hold);			
			
			float sum = 0;
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
	private float rollFromScoreboard(int board) {
		float s = 0;
		float[] cache = newRollValuesCache();
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++) {
			float v = valueOfRoll(YahtzeeMath.colex(YahtzeeMath.allRolls[i]), 2, board, cache);
			s += v * YahtzeeMath.prob(5,YahtzeeMath.allRolls[i]);
		}
		return s;
	}
	
	public float getBoardValue(int board) {
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
	
	private float valueOfRoll(int rollC, int rollsLeft, int board, float[] rollValues)
	{
		
		if (rollsLeft == 0)
		{		
			float max = Float.NEGATIVE_INFINITY;
			for (int i = 0; i < ScoreType.count; i++) {
				if (Scoreboard.isFilled(board, i)) continue; //Skip filled entries
				int rollVal = GameLogic.valueOfRoll(i, rollC);
				float boardVal = getBoardValue(Scoreboard.fill(board, i, rollVal));
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
				
				float sum = 0;
								
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


