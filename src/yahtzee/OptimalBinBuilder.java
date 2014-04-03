package yahtzee;

import game.Scoreboard;
import game.State;
import player.OptimalMultiPlayerAI;

public class OptimalBinBuilder {

	public static void main(String[] args) {
		OptimalMultiPlayerAI ai = new OptimalMultiPlayerAI();
		
		Scoreboard mysb = new Scoreboard(11, 2);
		Scoreboard opponentsb = new Scoreboard(11, 2);
		
		int state = State.convertScoreboardsToState(mysb, opponentsb);
		
		mysb.PrintScoreBoard();
		
		System.out.println("ai.getStateValue(state, true): " + ai.getStateValue(state, true));
		ai.cleanUp();
	}
}
