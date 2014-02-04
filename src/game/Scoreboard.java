package game;

import java.util.HashMap;
import java.util.Map;

public class Scoreboard {

	HashMap<ScoreType, Integer> scoreMap = new HashMap<ScoreType, Integer>();
	
	public Scoreboard(){				
		for (ScoreType scoreType : ScoreType.values()) {
			scoreMap.put(scoreType, -1);
		}
	}
	
	
	public enum ScoreType{
	    ONES, 
	    TWOS,
	    THREES,
	    FOURS,
	    FIVES,
	    SIXES,
	    BONUS,
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
//	    BONUS(6),
//	    THREE_OF_A_KIND(7),
//	    FOUR_OF_A_KIND(8),
//	    FULL_HOUSE(9),
//	    SMALL_STRAIGHT(10),
//	    BIG_STRAIGHT(11),
//	    YAHTZEE(12),
//	    CHANCE(13);
//	    
//
//	    private final int id;
//	    ScoreType(int id) { this.id = id; }
//	    public int getValue() { return id; }
//	}

	
	
	public void PrintScoreBoard(){
		System.out.println("Scoreboard for player: " + "PLAYERNAMEHERE");
		for (ScoreType scoreType : ScoreType.values()) {
			System.out.println("" + scoreType + ": " + scoreMap.get(scoreType));
		}
		System.out.println("--------------");
		
	}
	
	
	
}
