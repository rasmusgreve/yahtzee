package tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

import game.GameLogic;
import game.Scoreboard.ScoreType;

import org.junit.Test;

import util.YahtzeeMath;

public class GameLogicTest {
	
	@Test
	public void testValues() {
		for (ScoreType typ : ScoreType.values())
		{
			for (int[] roll : YahtzeeMath.allRolls)
			{
				assertEquals("Unexpected value", valueOfRoll(typ, roll), GameLogic.valueOfRoll(typ, roll));
			}
		}
	}
	
	
	/*
	 * OLD CODE FOR VERIFICATION PURPOSES
	 */
	
	public static int valueOfRoll(ScoreType type, int[] roll){
		
		switch (type)
		{
			case ONES:
				return count(roll,1) * 1;
			case TWOS:
				return count(roll,2) * 2;
			case THREES:
				return count(roll,3) * 3;
			case FOURS:
				return count(roll,4) * 4;
			case FIVES:
				return count(roll,5) * 5;
			case SIXES:
				return count(roll,6) * 6;
			case THREE_OF_A_KIND:
				for (int v = 1; v <= 6; v++)
					if (count(roll,v) >= 3)
						return sum(roll);
				return 0;
			case FOUR_OF_A_KIND:
				for (int v = 1; v <= 6; v++)
					if (count(roll,v) >= 4)
						return sum(roll);
				return 0;
			case FULL_HOUSE:
				int v3 = -1;
				for (int v = 1; v <= 6; v++)
				{
					if (count(roll,v) >= 3)
					{
						v3 = v;
						break;
					}
				}
				if (v3 == -1) return 0;
				for (int v = 1; v <= 6; v++)
				{
					if (v == v3) continue;
					if (count(roll,v) >= 2)
					{
						return GameLogic.FULL_HOUSE_SCORE;
					}
				}
				return 0;
			case SMALL_STRAIGHT:
				if (countConsecutive(roll) >= 4)
					return GameLogic.SMALL_STRAIGHT_SCORE;
				return 0;
			case BIG_STRAIGHT:
				if (countConsecutive(roll) == 5)
					return GameLogic.BIG_STRAIGHT_SCORE;	
				return 0;
			case YAHTZEE:
				for (int v = 1; v <= 6; v++)
					if (count(roll,v) == 5)
						return GameLogic.YAHTZEE_SCORE;
				return 0;
			case CHANCE:
				return sum(roll);
				
			default:
				return 0;
		}
	}

	private static int countConsecutive(int[] roll)
	{
		Arrays.sort(roll);
		int cons = 1;
		for (int i = 1; i < roll.length; i++)
		{
			if (roll[i] == roll[i-1] + 1)
				cons++;
			else if (roll[i] != roll[i-1])
				cons = 1;
		}
		return cons;
	}
	
	private static int count(int[] roll, int val)
	{
		int c = 0;
		for (int i = 0; i < roll.length; i++)
			if (roll[i] == val)
				c++;
		return c;
	}
	
	private static int sum(int[] roll)
	{
		int c = 0;
		for (int i = 0; i < roll.length; i++)
			c+= roll[i];
		return c;
	}	

}
