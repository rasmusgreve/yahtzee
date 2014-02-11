package player;

import java.util.ArrayList;
import java.util.Random;

import game.Answer;
import game.Question;
import game.Scoreboard;
import game.Scoreboard.ScoreType;

public class RandomPlayer implements Player {

	Random random = new Random();
	int id;
	
	public RandomPlayer()
	{
		
	}
	public RandomPlayer(int seed)
	{
		random = new Random(seed);
	}
	
	@Override
	public Answer PerformTurn(Question question) {
		Answer a = new Answer();
		a.diceToHold = randomHold();
		a.selectedScoreEntry = randomValidScoretype(question.scoreboards[id]);
		return a;
	}
	
	private boolean[] randomHold()
	{
		return new boolean[] {random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean(), random.nextBoolean()};
	}

	private ScoreType randomValidScoretype(Scoreboard scoreboard)
	{
		ArrayList<ScoreType> p = scoreboard.possibleScoreTypes();
		return p.get(random.nextInt(p.size()));
	}
	
	@Override
	public String getName() {
		return "Random player";
	}

	@Override
	public void finalize(){
		
	}
	
	@Override
	public void reset(int id) {
		this.id = id;
	}

}
