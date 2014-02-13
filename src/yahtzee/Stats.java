package yahtzee;

import game.Controller;

import java.util.ArrayList;
import java.util.Arrays;

import player.Player;

public class Stats {

	private static final int ROUNDS = 100000;
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
		
		System.out.println("Starting stats with " + String.format("%,d",ROUNDS) + " rounds from seed " + seed);
		System.out.println("Loading players...");
		for (int i = 1; i < args.length; i++)
		{
			Player p = loadPlayer(args[i]);
			System.out.println("\t" + args[i] + " -> ["+(i-1)+"] = " + p.getName());
			players.add(p);
			p.reset(i - 1);
		}
		
		int[] results = new int[ROUNDS];
		
		long start = System.currentTimeMillis();
		for (int i = 0; i < ROUNDS; i++)
		{
			Controller c = new Controller(players.toArray(new Player[players.size()]), seed++);
			c.OUTPUT = false;
			c.startGame();
			//TODO: Reset players
			results[i] = c.getResults()[0].totalInclBonus();
		}
		long duration = System.currentTimeMillis() - start;
		
		for (Player p : players)
		{
			p.cleanUp();
		}
		
		System.out.println(String.format("%,d",duration) + " ms, " + stats(results));
		
		//System.out.println(Arrays.toString(results));
	}
	
	private static String stats(int[] results)
	{
		int min = Integer.MAX_VALUE;
		int max = Integer.MIN_VALUE;
		double mean = 0;
		int median;
		
		//median
		Arrays.sort(results);
		median = results[results.length/2];
		
		//min, max, median
		for (int i : results)
		{
			min = Math.min(min, i);
			max = Math.max(max, i);
			mean += (i*1f)/results.length;
		}
		
		//std dev
		double tmp = 0;
		for (int i : results)
		{
			tmp += (Math.pow(i-mean,2))/results.length;
		}
		double std_dev = Math.sqrt(tmp);
		
		return String.format("max: %d, min: %d, avg: %.2f, median: %d, std.dev.: %.3f", max, min, mean, median, std_dev);
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
