package game;

import player.Player;

public class Controller {
	private final Player[] players;
	private GameState state;
	private boolean gameRunning;
	
	public Controller(Player[] players){
		this.players = players;
		state = new GameState();
	}
	
	public void startGame()
	{
		gameRunning = true;
		while (gameRunning){
			takeTurn();
		}
		System.out.println("Game is over");
	}
	
	private void takeTurn()
	{
		System.out.println("Take turn");
		java.util.Random r = new java.util.Random();
		players[0].PerformTurn(state);
		if (r.nextInt(10) == 0)
			gameRunning = false;
		/*
		Player cur = players[state.currentPlayerTurn];
		Answer ans = cur.PerformTurn(state);
		state = state.apply(ans);
		*/
	}
}
