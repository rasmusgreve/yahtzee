package player;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;

import game.Answer;
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
	
	private boolean[] searchHold(int[] roll, int rollsLeft)
	{
		return null;
	}
	
	private ScoreType searchScoreType(int[] roll, Scoreboard scoreboard)
	{
		HashMap<ScoreType, Integer> values = new HashMap<ScoreType, Integer>();
		
		for (ScoreType type : scoreboard.possibleScoreTypes())
		{
			
			
			values.put(type, 0);
		}
		
		
		
		
		//Find and return maximum value!
		int max = Integer.MIN_VALUE;
		ScoreType best = null;
		for (ScoreType type : values.keySet())
		{
			if (values.get(type) > max)
			{
				max = values.get(type);
				best = type;
			}
		}
		
		return best;
	}
	
	
	private int valueOfScoreBoard(Scoreboard board){
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
