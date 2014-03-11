package yahtzee;

import game.Controller;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import player.Player;

public class Stats {

	private static final int ROUNDS = 40;
	/**
	 * Args:
	 * 	[0]   int seed
	 *  [1-?] class name for players to load
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		ArrayList<Player> players = new ArrayList<Player>();
		
		int seed = new java.util.Random().nextInt();
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
		
		int[][] results = new int[players.size()][ROUNDS];
		int[] wins = new int[players.size()];
		
		System.out.println("Results:");
		System.out.print("             Player | ");
		for (int p = 0; p < players.size(); p++)
			System.out.print("  ["+p+"]   | ");
		System.out.println();
		long start = System.currentTimeMillis();
		
		//Play games
		for (int i = 0; i < ROUNDS; i++)
		{
			long inner_start = System.currentTimeMillis();
			Controller c = new Controller(players.toArray(new Player[players.size()]), seed);
			c.OUTPUT = false;
			c.startGame();
			System.out.print(String.format("Seed %11d -> | ",seed));
			double winning_score = -1;
			boolean no_winner = false;
			//Store scores
			for (int p = 0; p < players.size(); p++)
			{
				results[p][i] = c.getResults()[p].totalInclBonus();
				if (results[p][i] == winning_score) no_winner = true;
				winning_score = Math.max(winning_score, results[p][i]);
				System.out.print(String.format("%3d     | ",results[p][i]));
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
			System.out.println(String.format("%4d / %d - %8s, %8s", i+1, ROUNDS, msToString(inner_duration),  msToString(duration)));

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
			
			System.out.println("Stats with " + String.format("%,d",ROUNDS) + " rounds from seed " + (seed-ROUNDS));
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
