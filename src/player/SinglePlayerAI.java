package player;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import game.Answer;
import game.Question;
import game.Scoreboard;
import game.Scoreboard.ScoreType;
import util.YahtzeeMath;

public class SinglePlayerAI implements Player {

	public int[] testCache = new int[100];
	private static final String filename = "testCache.bin";
	
	@Override
	public Answer PerformTurn(Question question) {
		Answer ans = new Answer();
		values = new float[1025];
		for (int i = 0; i < 1025; i++){values[i] = -1;}
		
		if (question.rollsLeft == 0)
			ans.selectedScoreEntry = getBestScoreEntry(question.roll, question.scoreboards[question.playerId]);
		else
			ans.diceToHold = getBestHold(question.roll, question.rollsLeft);
		
		return ans;
	}
	
	private ScoreType getBestScoreEntry(int[] roll, Scoreboard board)
	{
		//TODO: Big dynamic program
		return ScoreType.BIG_STRAIGHT;
	}
	
	private boolean[] getBestHold(int[] roll, int rollsLeft) //Kickoff
	{
		float max = Float.MIN_VALUE;
		boolean[] best = null;
		for (boolean[] hold : getInterestingHolds(roll))
		{
			float sum = 0;
			for (int[] new_roll : getPossibleRolls(roll, hold))
			{
				sum += getProb(hold, new_roll) * valueOfRoll(new_roll, rollsLeft-1);
			}
			if (sum > max)
			{
				max = sum;
				best = hold;
			}
		}
		return best;
	}
	
	
	float[] values = new float[1025];
	private float valueOfRoll(int[] roll, int rollsLeft)
	{
		if (rollsLeft == 0)
		{
			return 1;
			//iterate valid ScoreTypes in scoreboard
			//return big dynamic program ( Scoreboard.Apply(roll) );
		}
		
		int idx = rollIdx(roll, rollsLeft);
		if (values[idx] == -1)
		{
			values[idx] = Integer.MIN_VALUE;
			for (boolean[] hold : getInterestingHolds(roll))
			{
				float sum = 0;
				for (int[] new_roll : getPossibleRolls(roll, hold))
				{
					sum += getProb(hold, new_roll) * valueOfRoll(new_roll, rollsLeft-1);
				}
				values[idx] = Math.max(values[idx], sum);
			}
		}
		return values[idx];
	}
	
	
	private static float getProb(boolean[] hold, int[] roll)
	{
		int c = 0;
		for (boolean b : hold)
			if (!b)
				c++;
		
		return (float)YahtzeeMath.prob(c, roll);
	}
	
	public static void main(String[] args) {
		/*ArrayList<int[]> r = getPossibleRolls(new int[] {6,6,6,6,6},new boolean[]{false,false,false,false,false});
		for(int[] v : r)
		{
			System.out.println(Arrays.toString(v));
		}*/
		SinglePlayerAI ai = new SinglePlayerAI();
		ai.doIt();
	}
	
	public void doIt()
	{
		float sum = 0;
		boolean[] hold = new boolean[]{false,false,true,false,false};
		int[] roll = new int[] {1,1,2,2,2};
		for (int[] new_roll : getPossibleRolls(roll, hold))
		{
			sum += getProb(hold, new_roll);
		}
		System.out.println(sum);
		
	}
	
	private static ArrayList<int[]> getPossibleRolls(int[] roll, boolean[] hold)
	{
		ArrayList<int[]> rolls = new ArrayList<int[]>();
		for (int a = 1; a <= 6; a++){
			for (int b = a; b <= 6; b++){
				for (int c = b; c <= 6; c++){
					for (int d = c; d <= 6; d++){
						for (int e = d; e <= 6; e++)
						{
							int[] r = new int[]{a,b,c,d,e};
							//Apply hold
							for (int j = 0; j < 5; j++)
								if (hold[j])
									r[j] = roll[j];
							//Sort and filter unique
							Arrays.sort(r);
							boolean has = false;
							for (int[] ex : rolls)
							{
								if (Arrays.equals(ex, r))
								{
									has = true;
									break;
								}
							}
							if (!has)
								rolls.add(r);
						}
					}
				}
			}
		}
		//TODO: Use reverse coolex instead
		return rolls;
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
	
	private ArrayList<boolean[]> getInterestingHolds(int[] roll)
	{
		ArrayList<boolean[]> holds = new ArrayList<boolean[]>();
		
		//TODO: Filter out uninteresting holds maybe?
		for (int i = 0; i < (1 << 5); i++)
			holds.add(holdFromInt(i));
		
		return holds;
	}
	
	
	private int rollIdx(int[] roll, int rollsLeft)
	{
		int v = YahtzeeMath.colex(roll);
		v |= rollsLeft << 8;
		return v;
	}

	
	
	
	
	
	@Override
	public void reset(int id){
		
	}
	
	
	public void loadArray()
	{
		try {
			FileInputStream fis = new FileInputStream(filename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			testCache = (int[]) ois.readObject();
			ois.close();
			fis.close();
		} catch (Exception e) {
			System.out.println("WARNING! cache not loaded");
			testCache = new int[100];
		}
	}
	
	@Override
	public void finalize(){
		//Save lookup table to persistent medium
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(testCache);
			oos.close();
			fos.close();
		} catch (IOException e) {
			System.out.println("WARNING! cache not stored");
		}
	}
	
	@Override
	public String getName()
	{
		return "Single player AI";
	}

}
