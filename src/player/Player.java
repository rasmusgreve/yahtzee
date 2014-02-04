package player;

import game.Answer;
import game.GameState;

public interface Player {
	public Answer PerformTurn(GameState state);
	public String getName();
	public void setID(int id);
}
