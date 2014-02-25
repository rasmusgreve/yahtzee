package tests;

import java.util.ArrayList;

import game.Controller;
import game.Scoreboard.ScoreType;

import org.junit.Before;
import org.junit.Test;

import player.Player;
import player.SinglePlayerAI;

public class GameTest {
	
	private Controller c;
	private SinglePlayerAI ai;
	
	@Before
	public void setup()
	{
		ArrayList<Player> players = new ArrayList<Player>();
		ai = new SinglePlayerAI();
		players.add(ai);
		
		
		
		c = new Controller(players.toArray(new Player[players.size()]), 142);

		
	}
	
	@Test
	public void startGameMissingOneTwoThree(){
		c.logic.setScoreboardVal(0, ScoreType.FOURS.ordinal(), 0);
		c.logic.setScoreboardVal(0, ScoreType.FIVES.ordinal(), 0);
		c.logic.setScoreboardVal(0, ScoreType.SIXES.ordinal(), 0);
		c.logic.setScoreboardVal(0, ScoreType.THREE_OF_A_KIND.ordinal(), 0);
		c.logic.setScoreboardVal(0, ScoreType.FOUR_OF_A_KIND.ordinal(), 0);
		c.logic.setScoreboardVal(0, ScoreType.FULL_HOUSE.ordinal(), 0);
		c.logic.setScoreboardVal(0, ScoreType.SMALL_STRAIGHT.ordinal(), 0);
		c.logic.setScoreboardVal(0, ScoreType.BIG_STRAIGHT.ordinal(), 0);
		c.logic.setScoreboardVal(0, ScoreType.YAHTZEE.ordinal(), 0);
		c.logic.setScoreboardVal(0, ScoreType.CHANCE.ordinal(), 0);
		
		
		c.startGame();
		
		ai.cleanUp();
		
	}
}
