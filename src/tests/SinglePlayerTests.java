package tests;

import static org.junit.Assert.*;

import java.util.Arrays;

import game.Answer;
import game.Controller;
import game.Question;
import game.Scoreboard;
import game.Scoreboard.ScoreType;

import org.junit.Before;
import org.junit.Test;

import player.Player;
import player.SinglePlayerAI;

public class SinglePlayerTests {

	SinglePlayerAI ai;
	Scoreboard boardAlmostFull;
	
	@Before
	public void setup()
	{
		ai = new SinglePlayerAI();
		boardAlmostFull = new Scoreboard();
		
		boardAlmostFull.insert(ScoreType.THREES, 9);
		boardAlmostFull.insert(ScoreType.FOURS, 12);
		boardAlmostFull.insert(ScoreType.FIVES, 15);
		boardAlmostFull.insert(ScoreType.SIXES, 18);
		boardAlmostFull.insert(ScoreType.THREE_OF_A_KIND, 25);
		boardAlmostFull.insert(ScoreType.FOUR_OF_A_KIND, 26);
		boardAlmostFull.insert(ScoreType.SMALL_STRAIGHT, 25);
		boardAlmostFull.insert(ScoreType.BIG_STRAIGHT, 30);
		boardAlmostFull.insert(ScoreType.YAHTZEE, 50);
		boardAlmostFull.insert(ScoreType.CHANCE, 20);
		boardAlmostFull.insert(ScoreType.FULL_HOUSE, 30);
	}
	
	@Test
	public void testFullBoardNoBonus() {
		
		boardAlmostFull.insert(ScoreType.ONES, 2);
		boardAlmostFull.insert(ScoreType.TWOS, 6);
		
		assertEquals("Full board w/o bonus gave a value > 0", 0, ai.getBoardValue(boardAlmostFull.ConvertMapToInt()), 1E-6);		
	}
	
	@Test
	public void testFullBoardBonus() {
		boardAlmostFull.insert(ScoreType.ONES, 3);
		boardAlmostFull.insert(ScoreType.TWOS, 6);
		
		assertEquals("Full board w/ bonus gave a value != 35", 35, ai.getBoardValue(boardAlmostFull.ConvertMapToInt()), 1E-6);		
	}
	
	@Test
	public void testMissingOnes()
	{
		boardAlmostFull.insert(ScoreType.TWOS, 6);

		assertEquals("Full board w/ bonus gave a wrong value", 14.526231410705554, ai.getBoardValue(boardAlmostFull.ConvertMapToInt()), 1E-6);
	}

	@Test
	public void testMissingTwos()
	{
		boardAlmostFull.insert(ScoreType.ONES, 2);
		assertEquals("Full board w/ bonus gave a wrong value", 7.867883003018436, ai.getBoardValue(boardAlmostFull.ConvertMapToInt()), 1E-6);
	}
	
	@Test
	public void testMissingOnesAndTwos()
	{
		double result = ai.getBoardValue(boardAlmostFull.ConvertMapToInt());
		assertEquals("Full board w/ bonus gave a wrong value: " + result, 16.754955416455402, result , 1E-6);
	}

	@Test
	public void testRightChoiceOnes1()
	{
		boolean[] expected = new boolean[]{true, true, false, false, false};
		int[] roll = new int[]{1,1,6,6,6};
		
		Question q = new Question(0, roll, 2, new Scoreboard[]{boardAlmostFull});
		Answer a = ai.PerformTurn(q);
		assertTrue("AI held the wrong dice", Arrays.equals(expected, a.diceToHold));
	}
	
	@Test
	public void testRightChoiceOnes2()
	{
		boolean[] expected = new boolean[]{true, true, true, false, false};
		int[] roll = new int[]{1,1,1,2,6};
		
		Question q = new Question(0, roll, 2, new Scoreboard[]{boardAlmostFull});
		Answer a = ai.PerformTurn(q);
		assertTrue("AI held the wrong dice", Arrays.equals(expected, a.diceToHold));
	}

	@Test
	public void testRightChoiceTwos1()
	{
		boolean[] expected = new boolean[]{false, false, true, true, false};
		int[] roll = new int[]{1,1,2,2,6};
		
		Question q = new Question(0, roll, 2, new Scoreboard[]{boardAlmostFull});
		Answer a = ai.PerformTurn(q);
		assertTrue("AI held the wrong dice", Arrays.equals(expected, a.diceToHold));
	}
	
	@Test
	public void testRightChoiceTwos2()
	{
		boolean[] expected = new boolean[]{false, false, true, true, true};
		int[] roll = new int[]{1,1,2,2,2};
		
		Question q = new Question(0, roll, 2, new Scoreboard[]{boardAlmostFull});
		Answer a = ai.PerformTurn(q);
		assertTrue("AI held the wrong dice", Arrays.equals(expected, a.diceToHold));
	}
	
	private int testScore(int seed)
	{
		Controller c = new Controller(new Player[]{ai}, seed);
		c.startGame();
		return c.getResults()[0].totalInclBonus();
	}
	
	
	private int[] scores = new int[]{300,268,237,240,205,252,213,329,287,157};
	@Test
	public void testScores()
	{
		for (int i = 0; i < scores.length; i++)
		{
			assertEquals("SinglePlayerAI should score "+scores[i]+" on seed "+i, 
					scores[i], 
					testScore(i));
		}
	}
	
}
