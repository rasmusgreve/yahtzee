package game;

import player.Player;

public class Controller {
	public boolean OUTPUT = true;
	private final Player[] players;
	private boolean gameRunning;
	public GameLogic logic;
	
	public Controller(Player[] players, int seed){
		this.players = players;
		logic = new GameLogic(players.length, seed);
	}
	
	public void startGame()
	{
		gameRunning = true;
		while (gameRunning){
			takeTurn();
		}
		Scoreboard[] result = logic.getResult();
		if (OUTPUT)
			System.out.println("Game over. Results:");
		for (int i = 0; i < players.length; i++)
		{
			if (OUTPUT)
			{
				System.out.print("\tPlayer [" + i + "] " + players[i].getName() + ": ");
				System.out.println(result[i].totalInclBonus());
				result[i].PrintScoreBoard();
			}
		}
	}
	
	public Scoreboard[] getResults()
	{
		return logic.getResult();
	}
	
	private void takeTurn()
	{
		Question question = logic.getQuestion();
		if (question == null) {
			gameRunning = false;
			return;
		}
		Answer answer = players[question.playerId].PerformTurn(question);
		logic.applyAnswer(answer);
	}
}
