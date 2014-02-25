package player;

import java.util.ArrayList;

import util.YahtzeeMath;

public abstract class BaseAI implements Player {
	protected int id;
	
	@Override
	public void reset(int id)
	{
		this.id = id;
	}
	
	/**
	 * Hashing of roll and rollsleft
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
	
	protected boolean[] holdFromInt(int v)
	{
		boolean[] out = new boolean[5];
		for (int i = 0; i < 5;i++)
		{
			out[i] = (v & (1 << i)) > 0;
		}
		return out;
	}
	
	protected ArrayList<int[]> getPossibleRolls(int[] roll, boolean[] hold)
	{
        int newRollsNeeded = 5;
		for (int i = 0; i < hold.length; i++) if (hold[i]) newRollsNeeded--;
		ArrayList<int[]> rolls;
		if (newRollsNeeded == 0){
			 rolls = new ArrayList<int[]>(YahtzeeMath.rollNumber(1));
			 rolls.add(roll);
			 return rolls;
		}else{
			 rolls = new ArrayList<int[]>(YahtzeeMath.rollNumber(newRollsNeeded));
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
			rolls.add(r);
		}
		
		
		return rolls;
	}
	
	
	protected ArrayList<boolean[]> getInterestingHolds(int[] roll)
	{
		ArrayList<boolean[]> holds = new ArrayList<boolean[]>(32);
		for (int i = 0; i < (1 << 5); i++){
			boolean[] hold = holdFromInt(i);
			
			//Filter out uninteresting holds
			boolean add = true;
			for (int j = 1; j < hold.length; j++) {
				if (hold[j] && !hold[j-1] && roll[j-1] == roll[j]) add = false;
			}
			
			if (add){
				holds.add(hold);
			}
		}
		return holds;
	}
	
	
	protected double getProb(boolean[] hold, int[] roll)
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
	
	
}
