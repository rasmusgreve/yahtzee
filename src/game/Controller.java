package game;

import player.Player;

public class Controller {
	private final Player[] players;
	private GameState state;
	private boolean gameRunning;
	private GameLogic logic;
	
	public Controller(Player[] players){
		this.players = players;
		state = new GameState();
		logic = new GameLogic(players.length, 0); //TODO: todo
	}
	
	public void startGame()
	{
		gameRunning = true;
		while (gameRunning){
			takeTurn();
		}
		//TODO: Declare a winner
		System.out.println("Game is over");
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
