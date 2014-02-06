package player;

import game.Answer;
import game.Question;

public interface Player {
	public Answer PerformTurn(Question question);
	public String getName();
	public void setID(int id);
}
