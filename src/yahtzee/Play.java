package yahtzee;

import game.Controller;

import java.util.ArrayList;

import player.Player;

public class Play {

	public static void main(String[] args) {
		ArrayList<Player> players = new ArrayList<Player>();
		int i = 0;
		for (String arg : args)
		{
			System.out.println("Loading player " + arg + " ... ");
			Player p = loadPlayer(arg);
			System.out.println("Loaded player: " + p.getName());
			players.add(p);
			p.setID(i);
			i++;
		}
		Controller c = new Controller(players.toArray(new Player[players.size()]));
		c.startGame();
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
