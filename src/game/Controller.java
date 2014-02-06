package game;

import player.Player;

public class Controller {
	private final Player[] players;
	private boolean gameRunning;
	private GameLogic logic;
	
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
		System.out.println("Game is over\nresults:");
		for (int i = 0; i < players.length; i++)
		{
			System.out.println("\tPlayer " + players[i].getName());
			result[i].PrintScoreBoard();
			System.out.println("-----------------------------");
		}
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
