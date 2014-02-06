package game;

import game.Scoreboard.ScoreType;

import java.util.Arrays;
import java.util.Random;

public class GameLogic {
	
	private static final int
		FULL_HOUSE_SCORE = 25,
		SMALL_STRAIGHT_SCORE = 30,
		BIG_STRAIGHT_SCORE = 40,
		YAHTZEE_SCORE = 50;
	
	int currentPlayer = 0;
	int rollsLeft;
	Random random;
	Scoreboard[] scoreboards;
	int[] roll;
	final int numPlayers;
	
	public GameLogic(int numPlayers, int randomSeed){
		this.numPlayers = numPlayers;
		random = new Random(randomSeed);
		rollsLeft = 2;
		roll = roll();
		scoreboards = new Scoreboard[numPlayers];
		for (int i = 0; i < numPlayers; i++) {
			scoreboards[i] = new Scoreboard();
		}
	}
	
	public Scoreboard[] getResult()
	{
		return scoreboards;
	}
	
	public Question getQuestion(){
		if (scoreboards[currentPlayer].isFull()) return null;
		return new Question(currentPlayer, roll, rollsLeft, scoreboards);
	}
	
	private int[] roll()
	{
		int[] newRoll = new int[5];
		for (int i = 0; i < 5; i++)
			newRoll[i] = random.nextInt(6) + 1;
		return newRoll;
	}
	
	private int[] roll(int[] oldRoll, boolean[] hold)
	{
		int[] newRoll = roll();
		for (int i = 0; i < 5; i++)
		{
			if (hold[i])
				newRoll[i] = oldRoll[i];
		}
		return newRoll;
	}
	
	
	public void applyAnswer(Answer answer){
		if (rollsLeft > 0)
		{
			roll = roll(roll, answer.diceToHold);
			rollsLeft--;
		}
		else //turn over
		{
			int score = valueOfRoll(answer.selectedScoreEntry, roll);
			scoreboards[currentPlayer].put(answer.selectedScoreEntry, score);
			
			//Next player
			currentPlayer = (currentPlayer + 1) % numPlayers;
			rollsLeft = 2;
			roll = roll();
		}
	}
	
	
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
						return FULL_HOUSE_SCORE;
					}
				}
				return 0;
			case SMALL_STRAIGHT:
				if (countConsecutive(roll) >= 4)
					return SMALL_STRAIGHT_SCORE;
				return 0;
			case BIG_STRAIGHT:
				if (countConsecutive(roll) == 5)
					return BIG_STRAIGHT_SCORE;	
				return 0;
			case YAHTZEE:
				for (int v = 1; v <= 6; v++)
					if (count(roll,v) == 5)
						return YAHTZEE_SCORE;
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
