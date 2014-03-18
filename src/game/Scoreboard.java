package game;

import java.util.ArrayList;
import java.util.Arrays;


public class Scoreboard implements Cloneable {

	public int[] scoreArray;
	
	//Map layout
	//      13       6
	//xxxxxxxxxxxxxvvvvvv
	//0011110000000111111
	//x: scoretypes
	//v: upper value
	final static int upperMask = (1 << 6) - 1;
	final static int typesMask = ((1 << 13) - 1) << 6;
	public static boolean isFilled(int scoreboard, int scoretype)
	{
		return (scoreboard & 1 << (6+scoretype)) != 0;
	}
	public static int fill(int scoreboard, int scoretype, int value)
	{
		if (scoretype < 6)
		{
			int upper = (scoreboard & upperMask) + value; //calculate new upper value
			upper = (upper > 63) ? 63 : upper;
			scoreboard = (scoreboard & typesMask) + upper; //reuse only types and add upper
		}
		return scoreboard | 1 << (6+scoretype);
	}
	public static int upperValue(int scoreboard)
	{
		return scoreboard & upperMask;
	}
	public static boolean isFull(int scoreboard)
	{
		return (scoreboard & typesMask) == typesMask;
	}
	public static int bonus(int scoreboard)
	{
		return (scoreboard & upperMask) >= 63 ? 35 : 0;
	}
	
	public static Scoreboard getOptimalPlayerScoreboard()
	{
		Scoreboard board = new Scoreboard();
		board.insert(ScoreType.THREES, 9);
		board.insert(ScoreType.FOURS, 12);
		board.insert(ScoreType.FIVES, 15);
		board.insert(ScoreType.SIXES, 30);
		return board;
	}
	
	public Scoreboard() {
		scoreArray = new int[ScoreType.values().length];
		for (ScoreType typ : ScoreType.values())
			scoreArray[typ.ordinal()] = -1;
	}
	
	@Override
	public Scoreboard clone(){
		Scoreboard n = new Scoreboard();
		n.scoreArray = Arrays.copyOf(scoreArray, scoreArray.length);
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
		insert(type.ordinal(), value);
	}
	
	public void insert(int type, int value)
	{
		scoreArray[type] = value;
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
			if (scoreArray[i] > -1)
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
	    public static int count = 13;
	}
	
	public int emptySpaces()
	{
		int e = 0;
		for (ScoreType type : ScoreType.values())
			if (scoreArray[type.ordinal()] == -1)
				e++;
		return e;
	}
	
	
	//Map layout
	//      13       6
	//xxxxxxxxxxxxxvvvvvv
	//x: scoretypes
	//v: upper value
	public int ConvertMapToInt(){				
		int upperCounter = 0;
		
		boolean[] scores = new boolean[13];
		
		int i = 0;
		for (int type = 0; type < ScoreType.count; type++) {
			int scoreAmount = scoreArray[type];
			if (scoreAmount > -1){
				scores[i] = true;
				if (type < 6) upperCounter += scoreAmount;
			}
			i++;
		}
		int result = Math.min(upperCounter, 63);
		for (int j = 0; j < scores.length; j++) {
			result |= (scores[j] ? 1 : 0) << (6+j);
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
