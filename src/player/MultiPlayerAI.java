package player;

import game.Answer;
import game.Question;

public class MultiPlayerAI extends BaseAI {

	private int targetScore = 200;
	
	@Override
	public Answer PerformTurn(Question question) {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public String getName() {
		return "Multi player AI";
	}

	@Override
	public void cleanUp() {
		System.out.println("Multiplayer AI cleanup. Nothing to do?");
	}


}
