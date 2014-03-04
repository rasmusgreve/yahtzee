package yahtzee;

import game.Controller;

import java.util.ArrayList;

import player.Player;
import tests.MonteCarloTest;

public class Play {

	/**
	 * Args:
	 * 	[0]   int seed
	 *  [1-?] class name for players to load
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<Player> players = new ArrayList<Player>();
		
		int seed = (int)(System.currentTimeMillis() % Integer.MAX_VALUE);
		try
		{
			seed = Integer.parseInt(args[0]);
		}
		catch (Exception e) {}
		
		System.out.println("Starting game with seed " + seed);
		System.out.println("Loading players...");
		boolean monteCarlo = false;
		for (int i = 1; i < args.length; i++)
		{
			String arg = args[i];
			if (arg.equals("MonteCarlo")) {
				monteCarlo = true;
				System.out.println("Doing MonteCarlo Test");
				continue;
			}
			Player p = loadPlayer(arg);
			System.out.println("\t" + args[i] + " -> ["+(i-1)+"] = " + p.getName());
			players.add(p);
			p.reset(i - 1);
		}
		if (!monteCarlo) {
			Controller c = new Controller(players.toArray(new Player[players.size()]), seed);
			c.startGame();
		}
		else {
			MonteCarloTest run = new MonteCarloTest(100, players);
		}
		
		for (Player p : players)
		{
			p.cleanUp();
		}
	}
	
	/**
	 * Load a Yahtzee Player agent from string.
	 * Exits the program on failure (!)
	 * Example: loadPlayer("player.HumanPlayer");
	 * @param name The name (including package) of the player to load (exluding extension)
	 * @return A player object. Exits the program on failure.
	 */
	private static Player loadPlayer(String name)
	{
		Player player;
        try {
            player = (Player) Class.forName (name).newInstance ();
        }
        catch (Exception e) {
            e.printStackTrace ();
            player = null;
            System.exit (1);
        }
        return player;
	}

}
