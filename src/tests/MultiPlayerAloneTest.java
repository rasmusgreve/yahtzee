package tests;

import static org.junit.Assert.*;
import game.Controller;

import org.junit.*;

import player.MultiPlayerAI;
import player.Player;

public class MultiPlayerAloneTest {

	
	MultiPlayerAI mpai;
	@Before
	public void setup()
	{
		mpai = new MultiPlayerAI();
	}
	
	private int testScore(int seed)
	{
		Controller c = new Controller(new Player[]{mpai}, seed);
		c.startGame();
		return c.getResults()[0].totalInclBonus();
	}
	
	
	private int[] scores = new int[]{300,268,237,240,205,252,220,329,230,157};
	@Test
	public void testScores()
	{
		for (int i = 0; i < scores.length; i++)
		{
			assertEquals("MultiPlayerAI with aggresivity 0 should score "+scores[i]+" on seed "+i, 
					scores[i], 
					testScore(i));
		}
	}
	
}
