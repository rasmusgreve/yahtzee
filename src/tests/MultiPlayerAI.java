package tests;

import game.Answer;
import game.Question;
import player.Player;

public class MultiPlayerAI implements Player {

	private int id;
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
	public void reset(int id) {
		this.id = id;
	}

	@Override
	public void cleanUp() {
		System.out.println("Multiplayer AI cleanup. Nothing to do?");
	}

}
