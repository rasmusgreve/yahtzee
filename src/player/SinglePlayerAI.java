package player;

import java.io.*;
import java.util.ArrayList;

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
	
	private float getProb(boolean[] hold, int[] roll)
	{
		//Look at dice roll[i] where hold[i] == false
		//# dice to roll = count(hold[i] == false)
		//TODO: do it
		return 1;
	}
	
	private ArrayList<int[]> getPossibleRolls(int[] roll, boolean[] hold)
	{
		//TODO: This
		return null;
	}
	
	private ArrayList<boolean[]> getInterestingHolds(int[] roll)
	{
		//TODO: This
		return null;
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
