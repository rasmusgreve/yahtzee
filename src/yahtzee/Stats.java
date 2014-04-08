package yahtzee;

import game.Controller;
import game.Scoreboard;
import game.State;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import player.Player;

public class Stats {

	/**
	 * Args:
	 * 	[0]   int seed (x for random)
	 *  [1]	  rounds
	 *  [2] (optional) amount of prefilled scoreentries
	 *  [2/3-?] class name for players to load
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		ArrayList<Player> players = new ArrayList<Player>();
		
		PrintStream stats_file = new PrintStream(new FileOutputStream("stats"+new SimpleDateFormat("dd_MM_YYYY_HH_mm_ss").format(Calendar.getInstance().getTime())+".txt", true));
		
		stats_file.println("------------------------------------------------------------------------------------------");
		stats_file.println(new SimpleDateFormat("dd/MM/YYYY  - HH:mm:ss").format(Calendar.getInstance().getTime()));
		
		int seed = new java.util.Random().nextInt();
		int rounds = 20000;
		int filledSpaces = 0;
		int playerNameStartId = 2;
		try
		{
			seed = Integer.parseInt(args[0]);
		}
		catch (Exception e) {}
		try
		{
			rounds = Integer.parseInt(args[1]);
		}
		catch (Exception e) {}
		try {
			//If the parse fails, id is not increased.
			filledSpaces = Integer.parseInt(args[playerNameStartId]);
			State.NUM_FILLED = filledSpaces;
			State.NUM_EMPTY = 13-filledSpaces;
			playerNameStartId++;
		}
		catch (Exception e) {}
		
		System.out.println("Starting stats with " + String.format("%,d",rounds) + " rounds from seed " + seed);
		System.out.println("Loading players...");
		for (int i = playerNameStartId; i < args.length; i++)
		{
			Player p = loadPlayer(args[i]);
			System.out.println("\t" + args[i] + " -> ["+(i-playerNameStartId)+"] = " + p.getName());
			stats_file.println("\t" + args[i] + " -> ["+(i-playerNameStartId)+"] = " + p.getName());
			players.add(p);
			p.reset(i - playerNameStartId);
		}
		
		int[][] results = new int[players.size()][rounds];
		int[] wins = new int[players.size()];
		
		System.out.println("Results:");
		
		System.out.print("             Player | ");
		stats_file.print("             Player | ");
		for (int p = 0; p < players.size(); p++)
		{
			System.out.print("  ["+p+"]   | ");
			stats_file.print("  ["+p+"]   | ");
		}
		System.out.println();
		stats_file.println();
		long start = System.currentTimeMillis();
		
		//Play games
		for (int i = 0; i < rounds; i++)
		{
			long inner_start = System.currentTimeMillis();
			
			//Build boards
			Scoreboard[] boards = new Scoreboard[players.size()];
			for (int v = 0; v < players.size(); v++)
			{
				boards[v] = new Scoreboard(filledSpaces,13-filledSpaces);
			}
			
			Controller c = new Controller(players.toArray(new Player[players.size()]), seed, boards);
			c.OUTPUT = false;
			
			c.startGame();
			System.out.print(String.format("Seed %11d -> | ",seed));
			stats_file.print(String.format("Seed %11d -> | ",seed));
			double winning_score = -1;
			boolean no_winner = false;
			//Store scores
			for (int p = 0; p < players.size(); p++)
			{
				results[p][i] = c.getResults()[p].totalInclBonus();
				if (results[p][i] == winning_score) no_winner = true;
				winning_score = Math.max(winning_score, results[p][i]);
				System.out.print(String.format("%3d     | ",results[p][i]));
				stats_file.print(String.format("%3d     | ",results[p][i]));
				players.get(p).reset(p);
			}
			//Store wins
			if (!no_winner)
			{
				for (int p = 0; p < players.size(); p++)
				{
					if (winning_score == results[p][i])
						wins[p]++;
				}
			}
			long duration = System.currentTimeMillis() - start;
			long inner_duration = System.currentTimeMillis() - inner_start;
			System.out.println(String.format("%4d / %d - %8s, %8s", i+1, rounds, msToString(inner_duration),  msToString(duration)));
			stats_file.println(String.format("%4d / %d - %8s, %8s", i+1, rounds, msToString(inner_duration),  msToString(duration)));
			stats_file.flush();
			seed++;
		}
		long duration = System.currentTimeMillis() - start;
		
		//Perform calculations
		Statsmath[] maths = new Statsmath[players.size()];
		for (int p = 0; p < players.size(); p++)
		{
			maths[p] = new Statsmath(results[p]);
		}
		
		for (int k = 0; k < 2; k++)
		{
			if (k == 1)
			{
				PrintStream out = new PrintStream(new FileOutputStream("output.txt", true));
				System.setOut(out);
				System.out.println("------------------------------------------------------------------------------------------");
				System.out.println(new SimpleDateFormat("dd/MM/YYYY  - HH:mm:ss").format(Calendar.getInstance().getTime()));
			}
			
			System.out.println("Stats with " + String.format("%,d",rounds) + " rounds from seed " + (seed-rounds));
			//Print the stats
			System.out.println();
			for (int p = 0; p < players.size(); p++)
			{
				System.out.println("["+p+"] : " + players.get(p).getName());
			}
			
			System.out.println();
			System.out.print(String.format("%22s","Player | "));
			for (int p = 0; p < players.size(); p++)
				System.out.print("  ["+p+"]   | ");
			System.out.println();
			System.out.print(String.format("%22s","Mean: | "));
			for (int p = 0; p < players.size(); p++)
				System.out.print(String.format("%7.3f | ", maths[p].mean));
			System.out.println();
			System.out.print(String.format("%22s","Median: | "));
			for (int p = 0; p < players.size(); p++)
				System.out.print(String.format("%3d     | ", maths[p].median));
			System.out.println();
			System.out.print(String.format("%22s","Min: | "));
			for (int p = 0; p < players.size(); p++)
				System.out.print(String.format("%3d     | ", maths[p].min));
			System.out.println();
			System.out.print(String.format("%22s","Max: | "));
			for (int p = 0; p < players.size(); p++)
				System.out.print(String.format("%3d     | ", maths[p].max));
			System.out.println();
			System.out.print(String.format("%22s","Std. dev.: | "));
			for (int p = 0; p < players.size(); p++)
				System.out.print(String.format("%7.3f | ", maths[p].std_dev));
			System.out.println();
			System.out.print(String.format("%22s","Wins: | "));
			for (int p = 0; p < players.size(); p++)
				System.out.print(String.format("%7d | ", wins[p]));
			System.out.println();
			System.out.println();
			System.out.println("Total time: " + msToString(duration));
		
		}
			
		for (Player p : players)
		{
			p.cleanUp();
		}
	}

	
	private static String msToString(long ms)
	{
		if (ms < 10 * 1000)
			return String.format("%d ms.",ms);
		else if (ms < 100 * 1000)
			return String.format("%d sec",ms/1000);
		else if (ms < 100 * 60 * 1000)
			return String.format("%dm %2ds",ms/(1000*60),(ms/1000)%60);
		else
			return String.format("%dh %2dm",ms/(1000*60*60),(ms/(1000*60))%60);
	}
	
	private static class Statsmath
	{
		public int min = Integer.MAX_VALUE;
		public int max = Integer.MIN_VALUE;
		public int median;
		public double mean = 0;
		public double std_dev;
		public Statsmath(int[] results)
		{
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
			std_dev = Math.sqrt(tmp);
		}
		@Override
		public String toString()
		{
			return String.format("max: %d, min: %d, avg: %.2f, median: %d, std.dev.: %.3f", max, min, mean, median, std_dev);
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
