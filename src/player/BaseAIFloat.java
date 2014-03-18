package player;

import java.util.HashMap;

import util.YahtzeeMath;

public abstract class BaseAIFloat implements Player {
	protected int id;
	
	static boolean[][][] interestingHoldsCache = new boolean[252][][];
	static int[][][] possibleRollsCache = new int[252][][];
	static double[][] probCache = new double[252][];
	static HashMap<Integer, Double>[] smartProbCache = new HashMap[252];
	
	static int[][][] holdDiceIntCache = new int[252][][];
	static{
		getInterestingHoldsCacheCalc();
		getPossibleRollsCacheCalc();
		probCacheCalc();
		smartProbCacheCalc();
		holdDiceCacheCalc();
	}
	
	
	@Override
	public void reset(int id)
	{
		this.id = id;
	}
	
	/**
	 * Hashing of roll and rolls left
	 * @param roll The roll to hash
	 * @param rollsLeft The # of rolls left to hash
	 * @return An integer between 0 and 1020
	 */
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
	
	/**
	 * Reverse "hash" from hold (as int) to hold as boolean[]
	 * @param v The integer to convert to boolean[] hold
	 * @return The boolean array matching the given int
	 */
	protected static boolean[] holdFromInt(int v)
	{
		boolean[] out = new boolean[5];
		for (int i = 0; i < 5;i++)
		{
			out[i] = (v & (1 << i)) > 0;
		}
		return out;
	}
	
	/**
	 * "Hash" of hold to integer
	 * @param hold The hold to "hash"
	 * @return The "hash" of the hold
	 */
	protected static int holdToInt(boolean[] hold){
		int result = 0;
		for (int i = 0; i < 5; i++) {
			if (hold[i]) result |= 1 << i;
		}		
		return result;
	}
	
	/**
	 * Get the aggregated standard deviation
	 * @param probabilities The probability that case i will happen
	 * @param means The mean score of case i
	 * @param standard_deviations The standard deviation of case i
	 * @return The aggregated standard deviation
	 */
	protected static double getAggregatedVariance(double[] probabilities, double[][] prob_dist)
	{
		double r = 0;
		int k = probabilities.length;
		for (int i = 0 ; i < k ; i++) {
		    r += probabilities[i] * (prob_dist[i][1] + prob_dist[i][0] * prob_dist[i][0] * (1 - probabilities[i]));
		    for (int j = i + 1 ; j < k ; j++) {
		        r -= 2 * probabilities[i] * probabilities[j] * prob_dist[i][0] * prob_dist[j][0];
		    }
		}
		
		return r;
	}
	
	/**
	 * Get the aggregated mean
	 * @param probabilities The probability that case i will happen
	 * @param means The mean score of case i
	 * @return The aggregated mean
	 */
	protected static double getAggregatedMean(double[] probabilities, double[][] prob_dist)
	{
		double sum = 0;
		double p = 0;
		for (int i = 0; i < probabilities.length;i++)
		{
			p += probabilities[i];
			sum += probabilities[i] * prob_dist[i][0];
		}
		return sum;
	}
	
	
	/**
	 * Get the rolls possible from a given roll and hold
	 * @param roll The roll to base the new rolls on
	 * @param hold The hold to apply to the given hold
	 * @return A list of new possible rolls
	 */
	protected int[] getPossibleRolls(int rollC, boolean[] hold)
	{
		return possibleRollsCache[rollC][holdToInt(hold)];
	}
	
	protected static int[][] getPossibleRollsInit(int[] roll, boolean[] hold)
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
	
	
	
	protected static void getPossibleRollsCacheCalc(){
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++) {
			int[] roll = YahtzeeMath.allRolls[i];
			int rollC = YahtzeeMath.colex(roll);
			
			possibleRollsCache[rollC] = new int[32][];
			
			for (int j = 0; j < (1 << 5); j++){
				boolean[] hold = holdFromInt(j);
				
				int[][] possibleRolls = getPossibleRollsInit(roll, hold);
				
				possibleRollsCache[rollC][j] = new int [possibleRolls.length];
				
				for (int k = 0; k < possibleRolls.length; k++) {
					possibleRollsCache[rollC][j][k] = YahtzeeMath.colex(possibleRolls[k]);
				}
				
				
				
				//possibleRollsCache[rollC][j] = getPossibleRollsInit(roll, hold);
			}
		}		
		
	}

	
	/**
	 * Get a list of interesting holds from a roll
	 * This method removes symmetries etc.
	 * @param roll The roll to generated holds from
	 * @return The list of interesting holds
	 */
	protected boolean[][] getInterestingHolds(int rollC)
	{	
		return interestingHoldsCache[rollC];
	}
	
	
	protected static boolean[][] getInterestingHoldsInit(int[] roll)
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
	
	
	protected static void getInterestingHoldsCacheCalc()
	{
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++) {
			int[] roll = YahtzeeMath.allRolls[i];
			int rollC = YahtzeeMath.colex(roll);
			interestingHoldsCache[rollC] = getInterestingHoldsInit(roll);
		}
	}
	
	
	
	
	
	
	
	protected float getProbSmart(int[] holdD, int rollC){
		return (float)smartProbCache[rollC].get(holdDiceToInt(holdD)).doubleValue();
	}
	
	protected static double getProbSmartInit(int[] holdD, int[] roll){
		
		int[] holdDice = holdD.clone();
		
		int c = 5;
		for (int d : holdDice) c -= d;
		
		int[] reducedRoll = new int[c];
		c = 0;
		for (int i = 0; i < 5; i++) {
			if (holdDice[roll[i]-1] > 0){
				holdDice[roll[i]-1]--;
			}else{
				reducedRoll[c] = roll[i];
				c++;
			}
		}
		
		return (double)YahtzeeMath.prob(c, reducedRoll);
	}
	
	protected static void smartProbCacheCalc(){
		for (int i = 0; i < YahtzeeMath.allRolls .length; i++) {
			int[] roll = YahtzeeMath.allRolls[i];
			int rollC = YahtzeeMath.colex(roll);
			
			smartProbCache[rollC] = new HashMap<Integer,Double>();
			
			for (int j = 0; j < (1 << 5); j++){
				boolean[] hold = holdFromInt(j);
				int[] holdDice = getHoldDiceInit(roll, hold);
				int holdDiceInt = holdDiceToInt(holdDice);
				
				smartProbCache[rollC].put(holdDiceInt, getProbSmartInit(holdDice, roll));
			}
		}

	}
	
	
	
	
	/**
	 * Get the probability of rolling a given roll from a hold
	 * @param hold The hold that was used
	 * @param roll The roll that was rolled
	 * @return The probability (0-1) for rolling that roll 
	 */
	protected double getProb(boolean[] hold, int[] roll)
	{

//		int[] holdDice = getHoldDice(roll, hold);		
//		return getProbSmart(holdDice, roll);
		
		return getProbInit(hold, roll);
		
		//return probCache[rollC][holdToInt(hold)];
	}
	
	
	protected static double getProbInit(boolean[] hold, int[] roll)
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
	
	protected static void probCacheCalc(){
		for (int i = 0; i < YahtzeeMath.allRolls .length; i++) {
			int[] roll = YahtzeeMath.allRolls[i];
			int rollC = YahtzeeMath.colex(roll);
			
			probCache[rollC] = new double[32];
			
			for (int j = 0; j < (1 << 5); j++){
				boolean[] hold = holdFromInt(j);
				
				probCache[rollC][j] = getProbInit(hold, roll);
			}
		}

	}
	
	protected static int[] getHoldDice(int rollC, boolean[] hold){
		return holdDiceIntCache[rollC][holdToInt(hold)];
	}
	
	protected static int[] getHoldDiceInit(int[] roll, boolean[] hold){
		int[] holdDices = new int[6];
		for (int i = 0; i < hold.length; i++) {
			if (hold[i]){
				holdDices[roll[i]-1]++;
			}
		}
		
		return holdDices;
	}
	
	protected static void holdDiceCacheCalc(){
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++) {
			int[] roll = YahtzeeMath.allRolls[i];
			int rollC = YahtzeeMath.colex(roll);
		
			holdDiceIntCache[rollC] = new int[32][];
			for (int j = 0; j < (1 << 5); j++){
				boolean[] hold = holdFromInt(j);
				holdDiceIntCache[rollC][j] = getHoldDiceInit(roll, hold);
			}
		}
	}
	
	protected static int holdDiceToInt(int[] holdDice){
		int result = 0;
		for (int i = 0; i < 6; i++) {
			result |= holdDice[i] << (i*3);
		}		
		return result;
	
	}
	
	
}
