package yahtzee;

import game.Scoreboard;
import game.State;
import player.OptimalMultiPlayerAI;

public class OptimalBinBuilder {

	public static void main(String[] args) {
		OptimalMultiPlayerAI ai = new OptimalMultiPlayerAI();
		
		Scoreboard mysb = new Scoreboard(7, 6);
		Scoreboard opponentsb = new Scoreboard(7, 6);
		
		int state = State.convertScoreboardsToState(mysb, opponentsb, true);
		System.out.println("ai.getStateValue(state, true): " + ai.getStateValue(state));
		
		state = State.setTurn(state, false);
		
		System.out.println("ai.getStateValue(state, false): " + ai.getStateValue(state));
		ai.cleanUp();
	}
}
