package game;

public class Question {
	
	public final int playerId, rollsLeft;
	public final int[] roll;
	public final Scoreboard[] scoreboards;
	
	public Question(int playerId, int[] roll, int rollsLeft, Scoreboard[] scoreboards) {
		super();
		this.playerId = playerId;
		this.roll = roll;
		this.rollsLeft = rollsLeft;
		this.scoreboards = scoreboards;
	}
	
}
