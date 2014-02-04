package game;

import java.util.Random;

public class GameState {
	public int[] roll;
	public int currentPLayerTurn;
	public int currentRollNumber;
	public Scoreboard[] boards;
	public GameLogic logic = new GameLogic();
	public GameState applyAnswer(Answer answer) {
		GameState localGameState = this;
		localGameState = logic.performAction(localGameState, answer);
		return localGameState;
	}
}
