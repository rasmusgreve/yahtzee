package player;

import java.util.Arrays;

import util.Persistence;
import util.YahtzeeMath;
import game.Answer;
import game.GameLogic;
import game.Question;
import game.Scoreboard;
import game.Scoreboard.ScoreType;

public class OptimalMultiPlayerAI implements Player {
	
	protected int id;
		
	public double[] boardValues;
	public static final String filename = "optimalPlayerCache.bin";
	
	public OptimalMultiPlayerAI(){
		
	
	}

	@Override
	public Answer PerformTurn(Question question) {
		Answer ans = new Answer();
		
		int stateInt = convertScoreboardsToInt(question.scoreboards[question.playerId], question.scoreboards[question.playerId == 0 ? 1 : 0]);
		
		if (question.rollsLeft == 0)
			ans.selectedScoreEntry = getBestScoreEntry(question.roll, stateInt);
		else
			ans.diceToHold = getBestHold(question.roll, question.rollsLeft, stateInt);
		
		return ans;
	}
	
	
	
	private static double[] newRollValuesCache()
	{
		double[] rollValues = new double[1020];
		Arrays.fill(rollValues, -1);
		return rollValues;
	}
	
	
	private ScoreType getBestScoreEntry(int[] roll, int board){
		int rollC = YahtzeeMath.colex(roll);
		
		int best = -1;
		double max = Double.NEGATIVE_INFINITY;

		for (int type = 0; type < ScoreType.count; type++) {
			if (Scoreboard.isFilled(board, type)) continue; //Skip filled entries
			int value_of_roll = GameLogic.valueOfRoll(type, rollC);
			int new_board = Scoreboard.fill(board, type, value_of_roll);
			double newVal = getBoardValue(new_board) + value_of_roll;
			
			if (newVal > max){
				max = newVal;
				best = type;
			}
		}
		return ScoreType.values()[best];
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
	
	
	private boolean[] getBestHold(int[] roll, int rollsLeft, int state){
		
		
		int rollC = YahtzeeMath.colex(roll);
		
		int[] rollSorted = roll.clone();
		Arrays.sort(rollSorted);
		
		double max = Double.NEGATIVE_INFINITY;
		int[] bestHoldDice = new int[6];
		boolean[] bestHold = new boolean[5];
		boolean[][] holds = null;
		holds = getInterestingHolds(roll);
		
//		for (boolean[] hold : holds)
//		{
//			if (hold == null) continue;
//			int[] holdDice = getHoldDice(rollC, hold);			
//			
//			double sum = 0;
//			if (optimize){
//				for (int new_rollC : getPossibleRolls(rollC, hold))
//				{
//					sum += getProbSmart(holdDice, new_rollC) * valueOfRoll(new_rollC, rollsLeft-1, board, newRollValuesCache());
//				}
//			}else{
//				for (int[] new_roll : getPossibleRolls(roll, hold))
//				{
//					sum += getProb(hold, new_roll) * valueOfRoll(new_roll, rollsLeft-1, scoreboard, newRollValuesCache());
//				}
//			}
//			
//			if (sum > max)
//			{
//				max = sum;
//				if (optimize) bestHoldDice = getHoldDiceInit(rollSorted, hold);
//				else bestHold = hold;
//			}
//		}
//		
		
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
			double v = valueOfRoll(YahtzeeMath.allRolls[i], 2, board, cache);
			s += v * YahtzeeMath.prob(5,YahtzeeMath.allRolls[i]);
		}
		return s;
	}

	private double valueOfRoll(int[] roll, int rollsLeft, int board, double[] rollValues)
	{
		
		if (rollsLeft == 0)
		{		
			double max = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < ScoreType.count; i++) {
				if (Scoreboard.isFilled(board, i)) continue; //Skip filled entries
				int rollVal = GameLogic.valueOfRoll(i, YahtzeeMath.colex(roll));
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
				if (hold == null) continue;
				
				//int[] holdDice = getHoldDice(roll, hold);		
				
				double sum = 0;
								
				for (int[] new_roll : getPossibleRolls(roll, hold))
				{
					sum += getProb(hold, new_roll) * valueOfRoll(new_roll, rollsLeft-1, board, rollValues);
					//sum += getProbSmart(holdDice, new_rollC) * valueOfRoll(new_rollC, rollsLeft-1, board, rollValues);
				}
				rollValues[idx] = Math.max(rollValues[idx], sum);
			}
		}
		return rollValues[idx];
	}
	
	
	private static int[][] getPossibleRolls(int[] roll, boolean[] hold)
	{
        int newRollsNeeded = 5;
		for (int i = 0; i < hold.length; i++) if (hold[i]) newRollsNeeded--;
		int[][] rolls;
		if (newRollsNeeded == 0){
			 rolls = new int[1][];
			 rolls[0] = roll;
			 return rolls;
		}else{
			 rolls = new int[YahtzeeMath.rollNumber(newRollsNeeded)][];
		}
		
		for (int j = 0; j < YahtzeeMath.rollNumber(newRollsNeeded); j++)
		{
			int[] r_p = YahtzeeMath.allRolls(newRollsNeeded)[j];
			int[] r = new int[5];

			int c = 0;
			for (int i = 0; i < 5; i++) {
				if (hold[i]){
					r[i] = roll[i];
					
				}else{
					r[i] = r_p[c];
					c++;
				}
			}
			
			rolls[j] = r;
		}
		
		
		return rolls;
	}
	
	
	
	private static boolean[][] getInterestingHolds(int[] roll)
	{	
		
		boolean[][] holds = new boolean[32][];
		for (int i = 0; i < (1 << 5); i++){
			boolean[] hold = holdFromInt(i);
			
			//Filter out uninteresting holds
			boolean add = true;
			for (int j = 1; j < hold.length; j++) {
				if (hold[j] && !hold[j-1] && roll[j-1] == roll[j]) add = false;
			}
			
			if (add){
				holds[i] = hold;
			}
		}
		return holds;
	}
	
	
	protected static double getProb(boolean[] hold, int[] roll)
	{
		int c = 0;
		for (boolean b : hold)
			if (!b)
				c++;
		
		int[] reducedRoll = new int[c];
		c = 0;
		for (int i=0; i<5; i++){
			if (!hold[i]){
				reducedRoll[c] = roll[i];
				c++;
			}
			
		}
				
		return (double)YahtzeeMath.prob(c, reducedRoll);
	}
	
	
	
	private static boolean[] holdFromInt(int v)
	{
		boolean[] out = new boolean[5];
		for (int i = 0; i < 5;i++)
		{
			out[i] = (v & (1 << i)) > 0;
		}
		return out;
	}
	
	
	protected int rollIdx(int[] roll, int rollsLeft)
	{
		int v = YahtzeeMath.colex(roll);
		v |= rollsLeft << 8;
		return v;
	}
	
	protected int rollIdx(int rollC, int rollsLeft)
	{
		int v = rollC;
		v |= rollsLeft << 8;
		return v;
	}

	@Override
	public String getName() {
		return "Optimal multi player AI";
	}

	@Override
	public void reset(int id) {
		this.id = id;
	}

	@Override
	public void cleanUp() {
		Persistence.storeArray(boardValues, filename);
	}

	
	
	
	private static int convertScoreboardsToInt(Scoreboard aiBoard, Scoreboard opponentBoard){
		int aiScore = aiBoard.totalInclBonus();
		int opponentScore = opponentBoard.totalInclBonus();
		int diff = aiScore - opponentScore;
		
		boolean[] aiScores = new boolean[7];
		boolean[] opponentScores = new boolean[7];
		
		int i = 0;
		for (int type = 6; type < ScoreType.count; type++) {
			if (aiBoard.scoreArray[type] > -1){
				aiScores[i] = true;
			}
			if (opponentBoard.scoreArray[type] > -1){
				opponentScores[i] = true;
			}
			i++;
		}
		
		
		//diff : -235 -- +235
		int uDiff = diff + 235;	//:0 - 470
		int result = uDiff; 
		for (int j = 0; j < aiScores.length; j++) {
			result |= (aiScores[j] ? 1 : 0) << (9+j);
		}		
		for (int j = 0; j < opponentScores.length; j++) {
			result |= (opponentScores[j] ? 1 : 0) << ((9+7)+j);
		}		
		
		return result;
		
	}
	//convertScoreboardsToInt bits:
	//X: diff between players = 0 - 470
	//A: ai current score
	//B: opponent current score
	//BBBBBBBAAAAAAAXXXXXXXXX
	//|	 7  ||  7  ||   9   |
}
