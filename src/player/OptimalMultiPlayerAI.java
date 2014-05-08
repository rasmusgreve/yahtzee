package player;

import java.util.Arrays;

import util.Persistence;
import util.YahtzeeMath;
import game.Answer;
import game.GameLogic;
import game.Question;
import game.Scoreboard;
import game.State;
import game.Scoreboard.ScoreType;

public class OptimalMultiPlayerAI extends BaseAI {
	
	public static final int CACHE_SIZE = (int)Math.pow(2,24);
	protected int id;
		
	public double[] stateValues;
	public static final String filename = "optimalPlayerCache.bin";
	
	public double winningProbAfterTurn = -1;
	
	public OptimalMultiPlayerAI(){
		stateValues = Persistence.loadArray(filename,CACHE_SIZE, Double.NaN);
		
		int c = 0;
		for (int i = 0; i < stateValues.length; i++)
		{
			if (!Double.isNaN(stateValues[i])) c++;
		}
		//System.out.println("Count: " + c);
		//System.out.println("Value: " + stateValues[(State.convertScoreboardsToState(new Scoreboard(State.NUM_FILLED, State.NUM_EMPTY), new Scoreboard(State.NUM_FILLED, State.NUM_EMPTY), false))]);
	}

	
	public double getStateValue(int state){
		
		if (Double.isNaN(stateValues[state])){
			if (State.isGameOver(state)){
				stateValues[state] = State.getWinner(state);
			}else{
				stateValues[state] = winProbFromState(state);
			}
		}	
		return stateValues[state];
	}
	
	
	public double winProbFromState(int state){
		optimalCacheBuildingPrint(state);
		
		double expected = 0;
		double[] cache = newRollValuesCache();
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++) {
			double v = winProbFromRoll(YahtzeeMath.colex(YahtzeeMath.allRolls[i]), 2, state, cache);
			expected += v * YahtzeeMath.prob(5, YahtzeeMath.allRolls[i]);
		}
		return expected;
	}
	
	public double winProbFromRoll(int rollC, int rollsLeft, int state, double[] rollValues){
		boolean myTurn = State.getTurn(state);
		if (rollsLeft == 0){
			
			double ex = myTurn ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
			for (int i = 0; i < ScoreType.count; i++) {
				if (State.isFilled(state, i, myTurn)) continue;
				int rollVal = GameLogic.valueOfRoll(i, rollC);
				int temp_state = State.fill(state, i, rollVal, myTurn);
				temp_state = State.setTurn(temp_state, !myTurn);
				double stateVal = getStateValue(temp_state);
				ex = myTurn ? Math.max(ex, stateVal) : Math.min(ex, stateVal);
			}
			return ex;
		}
		
		
		int idx = rollIdx(rollC, rollsLeft);
		
		if (Double.isNaN(rollValues[idx])){
			double ex = myTurn ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
			
			for (boolean[] hold : getInterestingHolds(rollC)){
				if (hold == null) continue;
				
				int[] holdDice = getHoldDice(rollC, hold);	
				
				double sum = 0;
				
				for (int new_rollC : getPossibleRolls(rollC, hold))
				{
					sum += getProbSmart(holdDice, new_rollC) * winProbFromRoll(new_rollC, rollsLeft-1, state, rollValues);
				}
				
				ex = myTurn ? Math.max(ex, sum) : Math.min(ex, sum);
			}
			rollValues[idx] = ex;
		}
		return rollValues[idx];
	}
	
	
	
	@Override
	public Answer PerformTurn(Question question) {
		Answer ans = new Answer();
		
		int stateInt = State.convertScoreboardsToState(question.scoreboards[question.playerId], question.scoreboards[question.playerId == 0 ? 1 : 0],true);
		
		if (question.rollsLeft == 0)
			ans.selectedScoreEntry = getBestScoreEntry(question.roll, stateInt);
		else
			ans.diceToHold = getBestHold(question.roll, question.rollsLeft, stateInt);
		
		return ans;
	}
	
	
	private ScoreType getBestScoreEntry(int[] roll, int state){
		int rollC = YahtzeeMath.colex(roll);
		
		int best = -1;
		double max = Double.NEGATIVE_INFINITY;
		
		for (int type = 0; type < ScoreType.count; type++) {
			int value_of_roll = 0;
			double newVal = 0;
			int new_state = 0;
			
			if (State.isFilled(state, type, true)) continue;  //Skip filled entries
			value_of_roll = GameLogic.valueOfRoll(type, rollC);
			new_state = State.fill(state, type, value_of_roll, true);
			new_state = State.setTurn(new_state, false);
			newVal = getStateValue(new_state);
				
			if (newVal > max){
				max = newVal;
				best = type;
			}
		}
		winningProbAfterTurn = max;
		return ScoreType.values()[best];
	}
	
	//HOLD MUST BE SORTED
	public double winningProbFromHold(int[] roll, boolean[] hold, int rollsLeft, int state)
	{
		hold = sortHold(hold, roll);
		int rollC = YahtzeeMath.colex(roll);
		int[] holdDice = getHoldDice(rollC, hold);	
		
		double sum = 0;
		
		for (int new_rollC : getPossibleRolls(rollC, hold))
		{
			sum += getProbSmart(holdDice, new_rollC) * winProbFromRoll(new_rollC, rollsLeft-1, state, newRollValuesCache());
		}
		
		return sum;
	}
	
	private boolean[] sortHold(boolean[] hold, int[] roll)
	{
		//roll: 1,1,4,2,2
		//hold: f,f,f,t,t
		
		int[] sortedRoll = Arrays.copyOf(roll, roll.length);
		Arrays.sort(sortedRoll);
		int[] bestHoldDice = getHoldDiceInit(roll, hold); 
		boolean[] resortedBestHold = new boolean[5];
		for (int i = 0; i < 5; i++) {
			if (bestHoldDice[sortedRoll[i]-1] > 0){
				resortedBestHold[i] = true;
				bestHoldDice[sortedRoll[i]-1]--;
			}
		}
		return resortedBestHold;
		
		//out : f,f,t,t,f
	}
	
	private boolean[] getBestHold(int[] roll, int rollsLeft, int state){
		int rollC = YahtzeeMath.colex(roll);
		
		int[] rollSorted = roll.clone();
		Arrays.sort(rollSorted);
		
		double max = Double.NEGATIVE_INFINITY;
		int[] bestHoldDice = new int[6];
		boolean[][] holds = null;
		holds = getInterestingHolds(rollC);
		double temp = Double.NaN;
		for (boolean[] holdSorted : holds)
		{
			if (holdSorted == null) continue;
			int[] holdDice = getHoldDice(rollC, holdSorted);			
			
			double sum = 0;
			for (int new_rollC : getPossibleRolls(rollC, holdSorted))
			{
				sum += getProbSmart(holdDice, new_rollC) * winProbFromRoll(new_rollC, rollsLeft-1, state, newRollValuesCache());
			}
			if (sum > max)
			{
				max = sum;
				bestHoldDice = getHoldDiceInit(rollSorted, holdSorted);
				temp = winningProbFromHold(rollSorted, holdSorted, rollsLeft, state);
			}
		}
		winningProbAfterTurn = max;
		boolean[] resortedBestHold = new boolean[5];
		for (int i = 0; i < 5; i++) {
			if (bestHoldDice[roll[i]-1] > 0){
				resortedBestHold[i] = true;
				bestHoldDice[roll[i]-1]--;
			}
		}
		/*double p = winningProbFromHold(roll, resortedBestHold, rollsLeft, state);
		if (temp != p)
		{
			System.out.println("Something wrong!");
		}*/
		return resortedBestHold;
	}
	
	
	
	public static double[] newRollValuesCache()
	{
		double[] rollValues = new double[1020];
		Arrays.fill(rollValues, Double.NaN);
		return rollValues;
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
		Persistence.storeArray(stateValues, filename);
	}
	
	int c = 0;
	long t = System.currentTimeMillis();
	private void optimalCacheBuildingPrint(int state){
		if (c%100 == 0){
			float runTime = (System.currentTimeMillis() - t)/1000f/60f;
			float averageSpeed = c / runTime;
			float expectedTimeLeft = (6000000 / averageSpeed) - runTime;
			System.out.println("winProbFromState called, state: " + state + ", count: " + c + ", runTime: " + runTime + " min");
			System.out.println("average speed: "+ averageSpeed + " board/min");
			System.out.println("expected time left: "+ expectedTimeLeft + " min");
			
			cleanUp();
		}
		c++;
	}
}
