package yahtzee;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import game.Controller;
import game.Scoreboard;
import game.State;
import player.ErrorCheckPlayer;
import player.Player;
import player.SinglePlayerAI;

public class ErrorCheckStarter {
	
	/**
	 * args[0] = rounds
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		
		PrintStream stats_file = new PrintStream(new FileOutputStream("stats"+new SimpleDateFormat("dd_MM_YYYY_HH_mm_ss").format(Calendar.getInstance().getTime())+".txt", true));
		stats_file.println("------------------------------------------------------------------------------------------");
		stats_file.println(new SimpleDateFormat("dd/MM/YYYY  - HH:mm:ss").format(Calendar.getInstance().getTime()));
		
		ErrorCheckPlayer errorCheckPlayer = new ErrorCheckPlayer(State.NUM_EMPTY);
		SinglePlayerAI singlePlayerAI = new SinglePlayerAI();
		Player[] players = new Player[]{singlePlayerAI, errorCheckPlayer};
		Scoreboard[] boards;
		int rounds = Integer.parseInt(args[0]);
		int startingPlayer = 0;
		for (int i = 0; i < rounds; i++)
		{
			
			for (int j = 0; j < players.length;j++)
				players[j].reset(j);
			System.out.println("Players reset for game " + i + " of " + rounds);
			stats_file.println("Players reset for game " + i + " of " + rounds);
			boards = new Scoreboard[]{ new Scoreboard(State.NUM_FILLED, State.NUM_EMPTY), new Scoreboard(State.NUM_FILLED, State.NUM_EMPTY)};
			
			Controller c = new Controller(players, i * 13 + (int)(System.nanoTime() % Integer.MAX_VALUE), boards , startingPlayer);
			c.OUTPUT = false;
			c.startGame();
			startingPlayer = (startingPlayer == 0) ? 1 : 0;
		} 
		
		errorCheckPlayer.printResults(System.out);
		errorCheckPlayer.printResults(stats_file);
		
		System.out.println("Aggresivity usage:");
		stats_file.println("Aggresivity usage:");
		System.out.println(Arrays.toString(errorCheckPlayer.multiPlayerAI.aggresivityLevelUsage));
		stats_file.println(Arrays.toString(errorCheckPlayer.multiPlayerAI.aggresivityLevelUsage));
	}
}


