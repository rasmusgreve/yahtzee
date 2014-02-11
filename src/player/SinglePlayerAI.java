package player;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import game.Answer;
import game.GameLogic;
import game.Question;
import game.Scoreboard;
import game.Scoreboard.ScoreType;
import util.Math;

public class SinglePlayerAI implements Player {

	public int[] testCache = new int[100];
	private static final String filename = "testCache.bin";
	
	@Override
	public Answer PerformTurn(Question question) {
		Answer ans = new Answer();
		if (question.rollsLeft == 0)
			ans.selectedScoreEntry = searchScoreType(question.roll, question.scoreboards[question.playerId]);
		else
			ans.diceToHold = searchHold(question.roll, question.rollsLeft);
		
		return ans;
	}
	
	
	private int searchRoll(int[] roll, boolean[] hold, int rollsLeft)
	{
		int max = Integer.MIN_VALUE;
		boolean[] best = null;
		for (int[] nroll : possibleRolls(roll, hold))
		{
			boolean[] choice = searchHold(roll, rollsLeft);
			if (value > max)
			{
				max = value;
				best = hold;
			}
		}
		
		return best;
	}
	
	private boolean[] searchHold(int[] roll, int rollsLeft)
	{
		int max = Integer.MIN_VALUE;
		boolean[] best = null;
		for (boolean[] hold : possibleHolds(roll))
		{
			int value = searchRoll(roll, hold, rollsLeft);
			if (value > max)
			{
				max = value;
				best = hold;
			}
		}
		
		return best;
	}
	
	private ArrayList<boolean[]> possibleHolds(int[] roll)
	{
		ArrayList<boolean[]> holds = new ArrayList<boolean[]>();
		
		//TODO: Magic
		
		return holds;
	}
	
	private ArrayList<int[]> possibleRolls(int[] roll, boolean[] hold)
	{
		ArrayList<int[]> rolls = new ArrayList<int[]>();
		
		//TODO: Magic
		
		return rolls;
	}
	
	private ScoreType searchScoreType(int[] roll, Scoreboard scoreboard)
	{
		int max = Integer.MIN_VALUE;
		ScoreType best = null;
		//Calculate values of all possible scoretypes (recursively!)
		for (ScoreType type : scoreboard.possibleScoreTypes())
		{
			Scoreboard copy = scoreboard.clone();
			copy.put(type, GameLogic.valueOfRoll(type, roll));
			int value = valueOfScoreBoard(copy);
			if (value > max)
			{
				max = value;
				best = type;
			}
		}
		
		return best;
	}
	
	
	private int[] scoreBoardValueCache = new int[1000000];
	
	private int valueOfScoreBoard(Scoreboard board){
		int idx = board.ConvertMapToInt();
		if (scoreBoardValueCache[idx] == -1)
		{
			scoreBoardValueCache[idx] = bigDynamicProgram(board);
		}
		
		return scoreBoardValueCache[idx];
	}
	
	private int bigDynamicProgram(Scoreboard b)
	{
		return -1;
	}
	
	
	
	
	
	
	
	
	
	
	@Override
	public void reset(int id){
		
	}
	
	public static void main(String[] args) {
		java.util.Random rand = new java.util.Random();
		SinglePlayerAI ai = new SinglePlayerAI();
		ai.loadArray();
		ai.testCache[rand.nextInt(ai.testCache.length)] = rand.nextInt();
		
		System.out.println(Arrays.toString(ai.testCache));
		
		ai.finalize();
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
