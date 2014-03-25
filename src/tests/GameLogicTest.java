package tests;

import static org.junit.Assert.*;
import game.GameLogic;
import game.Scoreboard.ScoreType;
import org.junit.Test;

public class GameLogicTest {
	
	@Test
	public void testOnes() {
		assertEquals(5, GameLogic.valueOfRoll(ScoreType.ONES, new int[]{1,1,1,1,1}));
		assertEquals(4, GameLogic.valueOfRoll(ScoreType.ONES, new int[]{1,1,1,1,2}));
		assertEquals(4, GameLogic.valueOfRoll(ScoreType.ONES, new int[]{2,1,1,1,1}));
		assertEquals(0, GameLogic.valueOfRoll(ScoreType.ONES, new int[]{2,6,4,3,2}));
	}
	@Test
	public void testFull() {
		assertEquals(25, GameLogic.valueOfRoll(ScoreType.FULL_HOUSE, new int[]{2,2,3,3,3}));
		assertEquals(25, GameLogic.valueOfRoll(ScoreType.FULL_HOUSE, new int[]{2,2,2,3,3}));
		assertEquals(25, GameLogic.valueOfRoll(ScoreType.FULL_HOUSE, new int[]{3,3,3,2,2}));
		assertEquals(25, GameLogic.valueOfRoll(ScoreType.FULL_HOUSE, new int[]{3,3,2,2,2}));
		assertEquals(25, GameLogic.valueOfRoll(ScoreType.FULL_HOUSE, new int[]{3,2,3,2,3}));
		assertEquals(0, GameLogic.valueOfRoll(ScoreType.FULL_HOUSE, new int[]{3,3,1,2,2}));
		assertEquals(0, GameLogic.valueOfRoll(ScoreType.FULL_HOUSE, new int[]{1,3,1,2,2}));
		assertEquals(0, GameLogic.valueOfRoll(ScoreType.FULL_HOUSE, new int[]{1,1,1,1,1}));
		assertEquals(0, GameLogic.valueOfRoll(ScoreType.FULL_HOUSE, new int[]{1,1,1,1,2}));
		assertEquals(25, GameLogic.valueOfRoll(ScoreType.FULL_HOUSE, new int[]{2,2,3,3,2,3}));
	}
	@Test
	public void testThreeOfAKind() {
		assertEquals(8, GameLogic.valueOfRoll(ScoreType.THREE_OF_A_KIND, new int[]{1,1,2,2,2}));
		assertEquals(19, GameLogic.valueOfRoll(ScoreType.THREE_OF_A_KIND, new int[]{5,5,2,5,2}));
		assertEquals(25, GameLogic.valueOfRoll(ScoreType.THREE_OF_A_KIND, new int[]{5,5,5,5,5}));
		assertEquals(0, GameLogic.valueOfRoll(ScoreType.THREE_OF_A_KIND, new int[]{2,5,6,5,2}));
	}
	@Test
	public void testFourOfAKind() {
		assertEquals(9, GameLogic.valueOfRoll(ScoreType.FOUR_OF_A_KIND, new int[]{1,2,2,2,2}));
		assertEquals(13, GameLogic.valueOfRoll(ScoreType.FOUR_OF_A_KIND, new int[]{2,5,2,2,2}));
		assertEquals(25, GameLogic.valueOfRoll(ScoreType.FOUR_OF_A_KIND, new int[]{5,5,5,5,5}));
		assertEquals(0, GameLogic.valueOfRoll(ScoreType.FOUR_OF_A_KIND, new int[]{2,5,5,5,2}));
	}
	@Test
	public void testSmallStraight()	{
		assertEquals(30, GameLogic.valueOfRoll(ScoreType.SMALL_STRAIGHT, new int[]{1,2,3,4,6}));
		assertEquals(30, GameLogic.valueOfRoll(ScoreType.SMALL_STRAIGHT, new int[]{4,2,3,2,5}));
		assertEquals(30, GameLogic.valueOfRoll(ScoreType.SMALL_STRAIGHT, new int[]{3,3,4,5,6}));
		assertEquals(30, GameLogic.valueOfRoll(ScoreType.SMALL_STRAIGHT, new int[]{6,6,3,4,5}));
	}
	@Test
	public void testBigStraight()	{
		assertEquals(0, GameLogic.valueOfRoll(ScoreType.BIG_STRAIGHT, new int[]{1,2,3,4,6}));
		assertEquals(40, GameLogic.valueOfRoll(ScoreType.BIG_STRAIGHT, new int[]{1,2,3,4,5}));
		assertEquals(40, GameLogic.valueOfRoll(ScoreType.BIG_STRAIGHT, new int[]{5,4,3,2,1}));
		assertEquals(40, GameLogic.valueOfRoll(ScoreType.BIG_STRAIGHT, new int[]{6,2,4,3,5}));
		assertEquals(40, GameLogic.valueOfRoll(ScoreType.BIG_STRAIGHT, new int[]{5,2,4,3,1}));
		assertEquals(0, GameLogic.valueOfRoll(ScoreType.BIG_STRAIGHT, new int[]{4,2,3,2,5}));
		assertEquals(0, GameLogic.valueOfRoll(ScoreType.BIG_STRAIGHT, new int[]{3,3,4,5,6}));
		assertEquals(0, GameLogic.valueOfRoll(ScoreType.BIG_STRAIGHT, new int[]{6,6,3,4,5}));
	}
	@Test
	public void testYahtzee()	{
		assertEquals(50, GameLogic.valueOfRoll(ScoreType.YAHTZEE, new int[]{1,1,1,1,1}));
		assertEquals(0, GameLogic.valueOfRoll(ScoreType.YAHTZEE, new int[]{1,2,2,2,2}));
		assertEquals(50, GameLogic.valueOfRoll(ScoreType.YAHTZEE, new int[]{2,2,2,2,2}));
		assertEquals(0, GameLogic.valueOfRoll(ScoreType.YAHTZEE, new int[]{5,6,6,6,6}));
		assertEquals(50, GameLogic.valueOfRoll(ScoreType.YAHTZEE, new int[]{6,6,6,6,6}));
		assertEquals(0, GameLogic.valueOfRoll(ScoreType.YAHTZEE, new int[]{1,2,3,4,5}));
	}
	
	@Test
	public void testConsecutive(){
		assertEquals(1, GameLogic.countConsecutive(new int[]{1,1,1,1,1}));
		assertEquals(2, GameLogic.countConsecutive(new int[]{1,2,1,1,1}));
		assertEquals(2, GameLogic.countConsecutive(new int[]{1,2,5,5,5}));
		assertEquals(3, GameLogic.countConsecutive(new int[]{1,2,4,5,6}));
		assertEquals(3, GameLogic.countConsecutive(new int[]{1,1,2,1,3}));
		assertEquals(5, GameLogic.countConsecutive(new int[]{6,5,4,3,2}));
		assertEquals(5, GameLogic.countConsecutive(new int[]{1,5,2,4,3}));
		assertEquals(3, GameLogic.countConsecutive(new int[]{1,1,2,2,3}));
		assertEquals(5, GameLogic.countConsecutive(new int[]{2,3,4,5,6}));
		assertEquals(5, GameLogic.countConsecutive(new int[]{1,2,3,4,5,7,8,10,11,12}));
		assertEquals(1, GameLogic.countConsecutive(new int[]{1}));
		assertEquals(2, GameLogic.countConsecutive(new int[]{1,2}));
		assertEquals(2, GameLogic.countConsecutive(new int[]{2,1}));
		assertEquals(1, GameLogic.countConsecutive(new int[]{3,3,3,3,3}));
		assertEquals(2, GameLogic.countConsecutive(new int[]{1,3,4,6,6}));
		assertEquals(3, GameLogic.countConsecutive(new int[]{1,2,3,5,6}));
	}

}
