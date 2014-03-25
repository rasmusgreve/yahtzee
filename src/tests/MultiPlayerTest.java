package tests;

import static org.junit.Assert.*;

import game.Controller;

import org.junit.*;

import player.MultiPlayerAI;
import player.Player;
import player.SinglePlayerAI;

public class MultiPlayerTest {

	SinglePlayerAI spai;
	MultiPlayerAI mpai;
	@Before
	public void setup()
	{
		mpai = new MultiPlayerAI();
		mpai.OUTPUT = false;
		spai = new SinglePlayerAI();
		spai.OUTPUT = false;
	}
	
	private int[] testScore(int seed)
	{
		mpai.reset(0);
		spai.reset(1);
		Controller c = new Controller(new Player[]{mpai, spai}, seed);
		c.OUTPUT = false;
		c.startGame();
		int[] result = new int[2];
		for (int i = 0; i < 2; i++)
			result[i] = c.getResults()[i].totalInclBonus();
		return result;
	}
	
	
	private int[][] scores = new int[][]{{278,261},
			{240,226},
			{232,199},
			{293,253},
			{256,253},
			{212,270},
			{152,221},
			{287,269},
			{276,264},
			{290,268}};
	@Test
	public void testScores()
	{
		
		for (int i = 0; i < 10; i++)
		{
			int[] d = testScore(i);
			assertEquals("MultiPlayerAI with should score "+scores[i][0]+" on seed "+i, 
					scores[i][0], 
					d[0]);
			assertEquals("SinglePlayerAI with should score "+scores[i][1]+" on seed "+i, 
					scores[i][1], 
					d[1]);
		}
		mpai.cleanUp();
	}
	
}
