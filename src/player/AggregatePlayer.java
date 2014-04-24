package player;

import game.Answer;
import game.Question;

public class AggregatePlayer implements Player {

	SinglePlayerAI single;
	MultiPlayerAI multi;
	OptimalMultiPlayerAI opti;
	
	public AggregatePlayer(){
		single = new SinglePlayerAI();
		multi = new MultiPlayerAI();
		opti = new OptimalMultiPlayerAI();
	}
	
	
	@Override
	public Answer PerformTurn(Question question) {
		int turn = 13-question.scoreboards[question.playerId].emptySpaces(); //0 based
		
		if (turn < 5) //4 = 3 first turns
			return single.PerformTurn(question);
		
		if (turn <= 12)//4 to 12 = 8 turns
			return multi.PerformTurn(question);
		
		//1 turn
		return opti.PerformTurn(question);
	}

	@Override
	public String getName() {
		return "Aggregate player";
	}

	@Override
	public void reset(int id) {
		single.reset(id);
		multi.reset(id);
		opti.reset(id);
	}

	@Override
	public void cleanUp() {
		single.cleanUp();
		multi.cleanUp();
		opti.cleanUp();
	}

}
