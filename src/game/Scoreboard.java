package game;

import java.util.ArrayList;
import java.util.HashMap;


public class Scoreboard implements Cloneable {

	int[] scoreArray;
	//TODO: Change to array. Use ScoreType.ordinal() as lookup thingie
	
	public Scoreboard() {
		scoreArray = new int[ScoreType.values().length];
		for (ScoreType typ : ScoreType.values())
			scoreArray[typ.ordinal()] = -1;
	}
	
	@Override
	public Scoreboard clone(){
		Scoreboard n = new Scoreboard();
		n.scoreArray = (int[]) scoreArray.clone();
		return n;
	}
	
	
	public boolean isFull()
	{
		for (int i = 0; i < scoreArray.length; i++)
			if (scoreArray[i] == -1)
				return false;
		return true;
	}
	
	public void insert(ScoreType type, int value)
	{
		if (scoreArray[type.ordinal()] != -1)
			throw new IllegalArgumentException("Category already used!");
		scoreArray[type.ordinal()] = value;
	}
	
	public int get(ScoreType type)
	{
		return scoreArray[type.ordinal()];
	}
	
	public int sum()
	{
		int sum = 0;
		for (int i = 0; i < scoreArray.length; i++)
		{
			sum += scoreArray[i];
		}
		return sum;
	}
	
	public int bonus()
	{
		int first6 = 0;
		for (ScoreType scoreType : ScoreType.values())
		{
			if (scoreType.ordinal() < 6)
				first6 += scoreArray[scoreType.ordinal()];
		}
		return (first6 >= 63) ? 35 : 0;
	}
	
	public ArrayList<ScoreType> possibleScoreTypes()
	{
		ArrayList<ScoreType> types = new ArrayList<ScoreType>();
		for (ScoreType st : ScoreType.values())
			if (scoreArray[st.ordinal()] == -1)
				types.add(st);
		return types;
	}
	
	public int totalInclBonus()
	{
		return bonus() + sum();
	}

	public enum ScoreType{
	    ONES, 
	    TWOS,
	    THREES,
	    FOURS,
	    FIVES,
	    SIXES,
	    THREE_OF_A_KIND,
	    FOUR_OF_A_KIND,
	    FULL_HOUSE,
	    SMALL_STRAIGHT,
	    BIG_STRAIGHT,
	    YAHTZEE,
	    CHANCE;
	}
	
	
	public int ConvertMapToInt(){
		int upperCounter = 0;
		
		boolean[] scores = new boolean[13];
		
		int i = 0;
		for (ScoreType scoreType : ScoreType.values()) {
			int scoreAmount = scoreArray[scoreType.ordinal()];
			if (scoreAmount > -1){
				scores[i] = true;
				if (scoreType.ordinal() < 6) upperCounter += scoreAmount;
			}
			
			i++;
		}
		
		
		
		int result = upperCounter;
		
		for (int j = 0; j < scores.length; j++) {
			int trueOrFalse = scores[j] ? 1 : 0;
			result |= (trueOrFalse) << (6+j);
		}
		return result;
	}
	/*
	
	void ConvertIntToMap(int aInt){
		System.out.println("--------------");
		System.out.println("CONVERTING TO MAP");
		System.out.println("--------------");
		int maskUpperCounter = (1<<6)-1;
		int maskTotalCounter = ((1<<9)-1)<<6;
		
		int upperCounter = aInt & maskUpperCounter;
		int totalCounter = (aInt & maskTotalCounter) >> 6;
		boolean[] scores = new boolean[13];
		
		for (int i = 0; i < scores.length; i++) {
			int maskScores = ((1<<(15+i)));
			scores[i] = (aInt & maskScores) >> (15+i) == 1;
			
			System.out.println("ScoreType: " + ScoreType.values()[i] + ", scores[i]: " + scores[i]);
		}
		
	
		System.out.println("upperCounter: " + upperCounter + ", totalCounter: " + totalCounter);

	}
	*/
	
	public void PrintScoreBoard(){
		for (ScoreType scoreType : ScoreType.values()) {
			//System.out.println("" + scoreType + ":" + scoreMap.get(scoreType));
			System.out.println(String.format("%15s: %d", scoreType, scoreArray[scoreType.ordinal()]));
		}
		
		System.out.println("_____________________________");
		System.out.println(String.format("%15s: %d", "Sum", sum()));
		System.out.println(String.format("%15s: %d", "Bonus", bonus()));
		System.out.println(String.format("%15s: %d", "Total", totalInclBonus()));
		
	}
	
	
	
}
