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

	public static final int CACHE_SIZE = (int)Math.pow(2,19);
	public double[] boardValues;
	public static final String filename = "singlePlayerCache.bin";
	public boolean OUTPUT = false;
	public boolean optimize = true;
	
	public SinglePlayerAI() {
		boardValues = Persistence.loadArray(filename,CACHE_SIZE);
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

		/*if (question.scoreboards[question.playerId].emptySpaces() == 4 && question.rollsLeft == 2)
		{
			Scoreboard b = question.scoreboards[question.playerId];
			System.out.println("Scoreboard: " + b.ConvertMapToInt() + ". score: " + b.totalInclBonus() + " exp: " + boardValues[b.ConvertMapToInt()] + " total: " +  (b.totalInclBonus() + boardValues[b.ConvertMapToInt()]));
			b.PrintScoreBoard();
			System.out.println("------------------------------------------------------------------------------------");
		}*/
		
		Answer ans = new Answer();
		
		if (question.rollsLeft == 0)
			ans.selectedScoreEntry = getBestScoreEntry(question.roll, question.scoreboards[question.playerId]);
		else
			ans.diceToHold = getBestHold(question.roll, question.rollsLeft, question.scoreboards[question.playerId]);
		if (OUTPUT)
			System.out.println("a: " + Arrays.toString(ans.diceToHold) + ", " + ans.selectedScoreEntry);

		return ans;
	}
	
	public Scoreboard scoreboardAfterTurn;
	
	private ScoreType getBestScoreEntry(int[] roll, Scoreboard scoreboard)
	{
		int board = scoreboard.ConvertMapToInt();
		int rollC = YahtzeeMath.colex(roll);
		
		int best = -1;
		double max = Double.NEGATIVE_INFINITY;
		if (OUTPUT)
			System.out.println("Choosing scoreboard slot - possible choices:");
		for (int type = 0; type < ScoreType.count; type++) {
			int value_of_roll = 0;
			double newVal = 0;
			if (optimize){
				int new_board = 0;
				if (Scoreboard.isFilled(board, type)) continue; //Skip filled entries
				value_of_roll = GameLogic.valueOfRoll(type, rollC);
				new_board = Scoreboard.fill(board, type, value_of_roll);
				newVal = getBoardValue(new_board) + value_of_roll;
			}else{
				if (scoreboard.get(ScoreType.values()[type]) >= 0) continue; //Skip filled entries
				value_of_roll = GameLogic.valueOfRoll(type, roll);
				Scoreboard newBoard = scoreboard.clone();
				newBoard.insert(type, value_of_roll);
				newVal = getBoardValue(newBoard) + value_of_roll;
			}
			
			if (OUTPUT)
				System.out.println("type: " + type + ", value: " + newVal);
			
			
			if (newVal > max){
				max = newVal;
				best = type;
			}
		}
		Scoreboard clone = scoreboard.clone();
		clone.insert(best, GameLogic.valueOfRoll(best, rollC));
		scoreboardAfterTurn = clone;
		
		return ScoreType.values()[best];
	}
	
	private boolean[] getBestHold(int[] roll, int rollsLeft, Scoreboard scoreboard) //Kickoff
	{
		int board = scoreboard.ConvertMapToInt();
		int rollC = YahtzeeMath.colex(roll);
		
		int[] rollSorted = roll.clone();
		Arrays.sort(rollSorted);
		
		double max = Double.NEGATIVE_INFINITY;
		int[] bestHoldDice = new int[6];
		boolean[] bestHold = new boolean[5];
		boolean[][] holds = null;
		if (optimize) holds = getInterestingHolds(rollC);
		else holds = getInterestingHoldsInit(roll);
		
		double[] rollValues = newRollValuesCache();
		
		for (boolean[] hold : holds)
		{
			if (hold == null) continue;
			int[] holdDice = getHoldDice(rollC, hold);			
			
			double sum = 0;
			if (optimize){
				for (int new_rollC : getPossibleRolls(rollC, hold))
				{
					sum += getProbSmart(holdDice, new_rollC) * valueOfRoll(new_rollC, rollsLeft-1, board, rollValues);
				}
			}else{
				for (int[] new_roll : getPossibleRolls(roll, hold))
				{
					sum += getProb(hold, new_roll) * valueOfRoll(new_roll, rollsLeft-1, scoreboard, rollValues);
				}
			}
			
			if (sum > max)
			{
				max = sum;
				if (optimize) bestHoldDice = getHoldDiceInit(rollSorted, hold);
				else bestHold = hold;
			}
		}
		
		
		
		
		if (optimize){
			boolean[] resortedBestHold = new boolean[5];
			for (int i = 0; i < 5; i++) {
				if (bestHoldDice[roll[i]-1] > 0){
					resortedBestHold[i] = true;
					bestHoldDice[roll[i]-1]--;
				}
			}
			
			return resortedBestHold;
		}
		else return bestHold;
	}

	

	

	//OPTIMIZED VERSIONs
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
			double max = Double.NEGATIVE_INFINITY;
			for (boolean[] hold : getInterestingHolds(rollC))
			{
				if (hold == null) continue;
				int[] holdDice = getHoldDice(rollC, hold);		
				double sum = 0;
				for (int new_rollC : getPossibleRolls(rollC, hold)){
					sum += getProbSmart(holdDice, new_rollC) * valueOfRoll(new_rollC, rollsLeft-1, board, rollValues);
				}
				max = Math.max(max, sum);
			}
			rollValues[idx] = max;
		}
		
		return rollValues[idx];
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
	private double rollFromScoreboard(int board) {
		cacheBuildingPrint(board);
		
		double s = 0;
		double[] cache = newRollValuesCache();
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++) {
			double v = valueOfRoll(YahtzeeMath.colex(YahtzeeMath.allRolls[i]), 2, board, cache);
			s += v * YahtzeeMath.prob(5,YahtzeeMath.allRolls[i]);
		}
		return s;
	}
	
	//NON-OPTIMIZED VERSION
	private double valueOfRoll(int[] roll, int rollsLeft, Scoreboard scoreboard, double[] rollValues)
	{
		if (rollsLeft == 0)
		{		
			double max = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < ScoreType.count; i++) {
				if (scoreboard.get(ScoreType.values()[i]) >= 0) continue; //Skip filled entries
				int rollVal = GameLogic.valueOfRoll(i, roll);
				Scoreboard newBoard = scoreboard.clone();
				newBoard.insert(i, rollVal);
				double boardVal = getBoardValue(newBoard);
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
				if (hold == null) continue;
								
				double sum = 0;
								
				for (int[] new_roll : getPossibleRolls(roll, hold))
				{
					sum += getProb(hold, new_roll) * valueOfRoll(new_roll, rollsLeft-1, scoreboard, rollValues);
				}
				rollValues[idx] = Math.max(rollValues[idx], sum);
			}
		}
		return rollValues[idx];
	}

	
	public double getBoardValue(Scoreboard scoreboard) {
		int board = scoreboard.ConvertMapToInt();
		
		if (boardValues[board] == -1) {
			if (scoreboard.isFull())
			{
				boardValues[board] = scoreboard.bonus();
			} 
			else
			{
				boardValues[board] = rollFromScoreboard(scoreboard);
			}
		}
					
		return boardValues[board];
	}
	
	private double rollFromScoreboard(Scoreboard scoreboard) {
		cacheBuildingPrint(scoreboard.ConvertMapToInt());

		double s = 0;
		double[] cache = newRollValuesCache();
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++) {
			double v = valueOfRoll(YahtzeeMath.allRolls[i], 2, scoreboard, cache);
			s += v * YahtzeeMath.prob(5,YahtzeeMath.allRolls[i]);
		}
		return s;
	}
	
	
	

	//Utility
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


