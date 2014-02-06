package game;

public class Question {
	public static enum QuestionType{
		HOLD, CATEGORY;
	}
	
	public final int playerId, rollsLeft;
	public final int[] roll;
	public final QuestionType type;
	public final Scoreboard[] scoreboards;
	
	public Question(int playerId, int[] roll, int rollsLeft, QuestionType type,
			Scoreboard[] scoreboards) {
		super();
		this.playerId = playerId;
		this.roll = roll;
		this.rollsLeft = rollsLeft;
		this.type = type;
		this.scoreboards = scoreboards;
	}
	
}
