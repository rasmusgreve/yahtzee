package tests;

import static org.junit.Assert.*;

import java.lang.reflect.Array;
import java.util.Arrays;

import game.Controller;

import org.junit.*;

import player.MultiPlayerAI;
import player.Player;
import player.SinglePlayerAI;

public class MultiPlayerAloneTest {

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
	
	
	//private int[] scores = new int[]{300,268,237,240,205,252,220,329,230,157};
	@Test
	public void testScores()
	{
		
		for (int i = 0; i < 1; i++)
		{
			System.out.println(i + " = " + Arrays.toString(testScore(i)));
			/*assertEquals("MultiPlayerAI with aggresivity 0 should score "+scores[i]+" on seed "+i, 
					scores[i], 
					testScore(i));*/
		}
		mpai.cleanUp();
	}
	
}
