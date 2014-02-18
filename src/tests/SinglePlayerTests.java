package tests;

import static org.junit.Assert.*;

import game.Scoreboard;
import game.Scoreboard.ScoreType;

import org.junit.Before;
import org.junit.Test;

import player.SinglePlayerAI;

public class SinglePlayerTests {

	SinglePlayerAI ai;
	Scoreboard boardAlmostFull;
	
	@Before
	public void setup()
	{
		ai = new SinglePlayerAI();
		boardAlmostFull = new Scoreboard();
		
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
		boardAlmostFull.insert(ScoreType.THREES, 9);
		boardAlmostFull.insert(ScoreType.FOURS, 12);
		
		assertEquals("Full board w/o bonus gave a value > 0", 0, ai.bigDynamicProgramming(boardAlmostFull), 1E-6);		
	}
	
	@Test
	public void testFullBoardBonus() {
		boardAlmostFull.insert(ScoreType.ONES, 3);
		boardAlmostFull.insert(ScoreType.TWOS, 6);
		boardAlmostFull.insert(ScoreType.THREES, 9);
		boardAlmostFull.insert(ScoreType.FOURS, 12);
		
		assertEquals("Full board w/ bonus gave a value != 35", 35, ai.bigDynamicProgramming(boardAlmostFull), 1E-6);		
	}
	
	@Test
	public void testMissingOnes()
	{
		boardAlmostFull.insert(ScoreType.TWOS, 6);
		boardAlmostFull.insert(ScoreType.THREES, 9);
		boardAlmostFull.insert(ScoreType.FOURS, 12);

		assertEquals("Full board w/ bonus gave a wrong value", 14.526231410705554, ai.bigDynamicProgramming(boardAlmostFull), 1E-6);
	}

	@Test
	public void testMissingTwos()
	{
		boardAlmostFull.insert(ScoreType.ONES, 2);
		boardAlmostFull.insert(ScoreType.THREES, 9);
		boardAlmostFull.insert(ScoreType.FOURS, 12);
		assertEquals("Full board w/ bonus gave a wrong value", 7.867883003018436, ai.bigDynamicProgramming(boardAlmostFull), 1E-6);
	}
	
	@Test
	public void testMissingThrees()
	{
		boardAlmostFull.insert(ScoreType.ONES, 2);
		boardAlmostFull.insert(ScoreType.TWOS, 6);
		boardAlmostFull.insert(ScoreType.FOURS, 12);
		assertEquals("Full board w/ bonus gave a wrong value", 7.867883003018436, ai.bigDynamicProgramming(boardAlmostFull), 1E-6);
	}
	
	@Test
	public void testMissingFours()
	{
		boardAlmostFull.insert(ScoreType.ONES, 2);
		boardAlmostFull.insert(ScoreType.TWOS, 6);
		boardAlmostFull.insert(ScoreType.THREES, 9);
		assertEquals("Full board w/ bonus gave a wrong value", 7.867883003018436, ai.bigDynamicProgramming(boardAlmostFull), 1E-6);
	}
	
//	@Test
//	public void testMissingOnesAndTwos()
//	{
//		
//		assertEquals("Full board w/ bonus gave a wrong value", 16.729612099229513, ai.bigDynamicProgramming(boardAlmostFull), 1E-6);
//	}

	
}
