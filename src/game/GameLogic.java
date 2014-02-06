package game;

import game.Scoreboard.ScoreType;

import java.util.Arrays;
import java.util.Random;

public class GameLogic {
	
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
		//TODO: validate player?
		//TODO: Answer not null
		if (rollsLeft > 0)
		{
			//TODO: validate diceToHold is set and long enough
			roll = roll(roll, answer.diceToHold);
			rollsLeft--;
		}
		else //turn over
		{
			//TODO: validate selectedScoreEntry is set and empty in score board
			scoreboards[currentPlayer].put(answer.selectedScoreEntry, 
					valueOfRoll(answer.selectedScoreEntry, roll));
			
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
				boolean f3 = false;
				for (int v = 1; v <= 6; v++)
				{
					if (count(roll,v) >= 3)
					{
						v3 = v;
						f3 = true;
						break;
					}
				}
				for (int v = 1; v <= 6; v++)
				{
					if (v == v3) continue;
					if (count(roll,v) >= 2)
					{
						return 25; //TODO: Move me
					}
				}
				return 0;
			case SMALL_STRAIGHT:
				if (countConsecutive(roll) >= 4)
					return 30; //TODO: Move me
				return 0;
			case BIG_STRAIGHT:
				if (countConsecutive(roll) == 5)
					return 40; //TODO: Move me	
				return 0;
			case YAHTZEE:
				for (int v = 1; v <= 6; v++)
					if (count(roll,v) == 5)
						return 50; //TODO: Move me
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
	
	public GameState doRoll(GameState state, Answer answer) {
		for (int i = 0; i < answer.diceToHold.length; i++) {
			if (answer.diceToHold[i] != true) {
				Random generator = new Random();
				state.roll[i] = generator.nextInt(6) + 1;
			}
		}
		return state;
	}
	public GameState performAction(GameState state, Answer answer) {
		state = this.doRoll(state, answer);
		/*if (answer.selectedScoreEntry != -1) {
			state = this.selectOption(state, answer);
		}*/
		return state;
	}
	public GameState selectOption(GameState state, Answer answer) {
		//TODO
		return state;
	}
}
