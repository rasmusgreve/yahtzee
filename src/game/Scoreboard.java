package game;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class Scoreboard {

	HashMap<ScoreType, Integer> scoreMap = new HashMap<ScoreType, Integer>();
	
	
	
	
	public Scoreboard(){				
		for (ScoreType scoreType : ScoreType.values()) {
			scoreMap.put(scoreType, -1);
		}
		
		scoreMap.put(ScoreType.ONES, 4);
		
		scoreMap.put(ScoreType.SMALL_STRAIGHT, 25);
		
		scoreMap.put(ScoreType.YAHTZEE, 50);
		
		ConvertMapToInt();
	}
	
	public boolean isFull()
	{
		for (ScoreType scoreType : ScoreType.values())
			if (scoreMap.get(scoreType) == -1)
				return false;
		return true;
	}
	
	public void put(ScoreType type, int value)
	{
		scoreMap.put(type, value);
	}
	
	public int get(ScoreType type)
	{
		return scoreMap.get(type);
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
	
	
//	public enum ScoreType{
//	    ONES(0), 
//	    TWOS(1),
//	    THREES(2),
//	    FOURS(3),
//	    FIVES(4),
//	    SIXES(5),
//	    THREE_OF_A_KIND(6),
//	    FOUR_OF_A_KIND(7),
//	    FULL_HOUSE(8),
//	    SMALL_STRAIGHT(9),
//	    BIG_STRAIGHT(10),
//	    YAHTZEE(11),
//	    CHANCE(12);
//	    
//
//	    private final int id;
//	    ScoreType(int id) { this.id = id; }
//	    public int getValue() { return id; }
//	}

	
	
	void ConvertMapToInt(){
		System.out.println("--------------");
		System.out.println("CONVERTING TO INT");
		System.out.println("--------------");
		int upperCounter = 0;
		int totalCounter = 0;
		
		
		boolean[] scores = new boolean[13];
		
		
		int i = 0;
		for (ScoreType scoreType : ScoreType.values()) {
			int scoreAmount = scoreMap.get(scoreType);
			if (scoreAmount > -1){
				scores[i] = true;
				if (scoreType.ordinal() < 6) upperCounter += scoreAmount;
				totalCounter += scoreAmount;
			}
			
			i++;
		}
		
		
		
		int result = upperCounter + (totalCounter << 6);
		
		for (int j = 0; j < scores.length; j++) {
			int trueOrFalse = scores[j] ? 1 : 0;
			result |= (trueOrFalse) << (15+j);
		}		
		
		for (int j = 0; j < scores.length; j++) {
			System.out.println("ScoreType: " + ScoreType.values()[j] + ", scores[j]: " + scores[j]);
		}
		System.out.println("upperCounter: " + upperCounter + ", totalCounter: " + totalCounter + " ,result: " + result);

		ConvertIntToMap(result);
	}
	
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
	
	public void PrintScoreBoard(){
		System.out.println("Scoreboard for player: " + "PLAYERNAMEHERE");
		for (ScoreType scoreType : ScoreType.values()) {
			System.out.println("" + scoreType + ": " + scoreMap.get(scoreType));
		}
		System.out.println("--------------");
		
	}
	
	
	
}
