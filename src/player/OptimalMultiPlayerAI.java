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
	
	public static final int CACHE_SIZE = (int)Math.pow(2,23);
	protected int id;
		
	public double[] stateValues;
	public static final String filename = "optimalPlayerCache.bin";
	
	public OptimalMultiPlayerAI(){
		stateValues = Persistence.loadArray(filename,CACHE_SIZE, Double.NaN);
	}

	
	public double getStateValue(int state, boolean myTurn){
		
		if (Double.isNaN(stateValues[state])){
			if (State.isGameOver(state)){
				stateValues[state] = State.getWinner(state);
			}else{
				stateValues[state] = rollFromState(state, myTurn);
			}
		}	
		return stateValues[state];
	}
	
	
	private double rollFromState(int state, boolean myTurn){
		optimalCacheBuildingPrint(state);
		
		double expected = 0;
		double[] cache = newRollValuesCache();
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++) {
			double v = valueOfRoll(YahtzeeMath.colex(YahtzeeMath.allRolls[i]), 2, state, myTurn, cache);
			expected += v * YahtzeeMath.prob(5, YahtzeeMath.allRolls[i]);
		}
		return expected;
	}
	
	private double valueOfRoll(int rollC, int rollsLeft, int state, boolean myTurn, double[] rollValues){
		if (rollsLeft == 0){
			double ex = myTurn ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
			for (int i = 0; i < ScoreType.count; i++) {
				if (State.isFilled(state, i, myTurn)) continue;
				int rollVal = GameLogic.valueOfRoll(i, rollC);
				double stateVal = getStateValue(State.fill(state, i, rollVal, myTurn), !myTurn);
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
					sum += getProbSmart(holdDice, new_rollC) * valueOfRoll(new_rollC, rollsLeft-1, state, myTurn, rollValues);
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
		
		int stateInt = State.convertScoreboardsToState(question.scoreboards[question.playerId], question.scoreboards[question.playerId == 0 ? 1 : 0]);
		
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
			
			newVal = getStateValue(new_state, false);
				
			if (newVal > max){
				max = newVal;
				best = type;
			}
		}
		return ScoreType.values()[best];
	}
	
	
	private boolean[] getBestHold(int[] roll, int rollsLeft, int state){
		int rollC = YahtzeeMath.colex(roll);
		
		int[] rollSorted = roll.clone();
		Arrays.sort(rollSorted);
		
		double max = Double.NEGATIVE_INFINITY;
		int[] bestHoldDice = new int[6];
		boolean[][] holds = null;
		holds = getInterestingHolds(rollC);
		for (boolean[] hold : holds)
		{
			if (hold == null) continue;
			int[] holdDice = getHoldDice(rollC, hold);			
			
			double sum = 0;
			for (int new_rollC : getPossibleRolls(rollC, hold))
			{
				sum += getProbSmart(holdDice, new_rollC) * valueOfRoll(new_rollC, rollsLeft-1, state, true, newRollValuesCache());
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
	
	
	
	private static double[] newRollValuesCache()
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
		if (c%10000 == 0){
			float runTime = (System.currentTimeMillis() - t)/1000f/60f;
			float averageSpeed = c / runTime;
			float expectedTimeLeft = (6000000 / averageSpeed) - runTime;
			System.out.println("rollFromScoreboard called, state: " + state + ", count: " + c + ", runTime: " + runTime + " min");
			System.out.println("average speed: "+ averageSpeed + " board/min");
			System.out.println("expected time left: "+ expectedTimeLeft + " min");
			
			cleanUp();
		}
		c++;
	}
}
