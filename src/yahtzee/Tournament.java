package yahtzee;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import game.Controller;
import game.Scoreboard;
import game.Scoreboard.ScoreType;
import player.MultiPlayerAI;
import player.Player;
import player.SinglePlayerAI;
import tests.SinglePlayerTests;
import yahtzee.Stats.Statsmath;

public class Tournament {

	private static Scoreboard getBoard1(){
		Scoreboard b = new Scoreboard();
		b.insert(ScoreType.ONES, 1);
		b.insert(ScoreType.TWOS, 6);
		b.insert(ScoreType.THREES, 9);
		b.insert(ScoreType.FOURS, 12);
		b.insert(ScoreType.SIXES, 18);
		b.insert(ScoreType.FULL_HOUSE, 25);
		b.insert(ScoreType.SMALL_STRAIGHT, 30);
		b.insert(ScoreType.YAHTZEE, 50);
		b.insert(ScoreType.CHANCE, 21);
		return b;
	}
	
	private static Scoreboard getBoard2(){
		Scoreboard b = new Scoreboard();
		b.insert(ScoreType.ONES, 1);
		b.insert(ScoreType.TWOS, 8);
		b.insert(ScoreType.FOURS, 12);
		b.insert(ScoreType.THREE_OF_A_KIND, 25);
		b.insert(ScoreType.FOUR_OF_A_KIND, 9);
		b.insert(ScoreType.FULL_HOUSE, 25);
		b.insert(ScoreType.SMALL_STRAIGHT, 30);
		b.insert(ScoreType.BIG_STRAIGHT, 40);
		b.insert(ScoreType.CHANCE, 22);
		return b;
	}
	
	public static void main(String[] args) throws FileNotFoundException {
		
		Player[] players = new Player[]{new SinglePlayerAI(), new MultiPlayerAI()};
		
		PrintStream stats_file = new PrintStream(new FileOutputStream("stats"+new SimpleDateFormat("dd_MM_YYYY_HH_mm_ss").format(Calendar.getInstance().getTime())+".txt", true));
		
		stats_file.println("------------------------------------------------------------------------------------------");
		stats_file.println(new SimpleDateFormat("dd/MM/YYYY  - HH:mm:ss").format(Calendar.getInstance().getTime()));
		
		int rounds = 15;
		int seed = new java.util.Random().nextInt();
		
		System.out.println("Starting stats with " + String.format("%,d",rounds) + " rounds from seed " + seed);
		stats_file.println("Starting stats with " + String.format("%,d",rounds) + " rounds from seed " + seed);
		for (int i = 0; i < players.length; i++)
		{
			System.out.println("\t -> ["+(i)+"] = " + players[i].getName());
			stats_file.println("\t -> ["+(i)+"] = " + players[i].getName());
			players[i].reset(i);
		}
		
		int[][] results = new int[players.length][rounds];
		int[] wins = new int[players.length];
		
		System.out.println("Beginning tournament with board1="+players[0].getName() + " and board2=" + players[1].getName() + " board 1 starts");
		stats_file.println("Beginning tournament with board1="+players[0].getName() + " and board2=" + players[1].getName() + " board 1 starts");
		
		System.out.print("             Player | ");
		stats_file.print("             Player | ");
		for (int p = 0; p < players.length; p++)
		{
			System.out.print("  ["+p+"]   | ");
			stats_file.print("  ["+p+"]   | ");
		}
		System.out.println();
		stats_file.println();
		
		//Play games
		int startingId = 0;
		for (int i = 0; i < rounds; i++)
		{	
			//Build boards
			Scoreboard[] boards = new Scoreboard[]{getBoard1(), getBoard2()};
			
			
			Controller c = new Controller(players, seed, boards, startingId);
			c.OUTPUT = false;
			
			c.startGame();
			System.out.print(String.format("Seed %11d -> | ",seed));
			stats_file.print(String.format("Seed %11d -> | ",seed));
			double winning_score = -1;
			boolean no_winner = false;
			//Store scores
			for (int p = 0; p < players.length; p++)
			{
				results[p][i] = c.getResults()[p].totalInclBonus();
				if (results[p][i] == winning_score) no_winner = true;
				winning_score = Math.max(winning_score, results[p][i]);
				System.out.print(String.format("%3d     | ",results[p][i]));
				stats_file.print(String.format("%3d     | ",results[p][i]));
				players[p].reset(p);
			}
			//Store wins
			if (!no_winner)
			{
				for (int p = 0; p < players.length; p++)
				{
					if (winning_score == results[p][i])
						wins[p]++;
				}
			}
			
			System.out.println(String.format("%4d / %d  - %s started", i+1, rounds, players[startingId].getName()));
			stats_file.println(String.format("%4d / %d  - %s started", i+1, rounds, players[startingId].getName()));
			stats_file.flush();
			seed++;
			startingId = (startingId == 0) ? 1 : 0;
		}
		
		//Perform calculations
		Stats.Statsmath[] maths = new Statsmath[players.length];
		for (int p = 0; p < players.length; p++)
		{
			maths[p] = new Statsmath(results[p]);
		}
		PrintStream defaultOut = System.out;
		for (int k = 0; k < 2; k++)
		{
			if (k == 1)
			{
				System.setOut(stats_file);
			}
			System.out.println("Stats with " + String.format("%,d",rounds) + " rounds from seed " + (seed-rounds));
			//Print the stats
			System.out.println();
			for (int p = 0; p < players.length; p++)
			{
				System.out.println("["+p+"] : " + players[p].getName());
			}
			
			System.out.println();
			System.out.print(String.format("%22s","Player | "));
			for (int p = 0; p < players.length; p++)
				System.out.print("  ["+p+"]   | ");
			System.out.println();
			System.out.print(String.format("%22s","Mean: | "));
			for (int p = 0; p < players.length; p++)
				System.out.print(String.format("%7.3f | ", maths[p].mean));
			System.out.println();
			System.out.print(String.format("%22s","Median: | "));
			for (int p = 0; p < players.length; p++)
				System.out.print(String.format("%3d     | ", maths[p].median));
			System.out.println();
			System.out.print(String.format("%22s","Min: | "));
			for (int p = 0; p < players.length; p++)
				System.out.print(String.format("%3d     | ", maths[p].min));
			System.out.println();
			System.out.print(String.format("%22s","Max: | "));
			for (int p = 0; p < players.length; p++)
				System.out.print(String.format("%3d     | ", maths[p].max));
			System.out.println();
			System.out.print(String.format("%22s","Std. dev.: | "));
			for (int p = 0; p < players.length; p++)
				System.out.print(String.format("%7.3f | ", maths[p].std_dev));
			System.out.println();
			System.out.print(String.format("%22s","Wins: | "));
			for (int p = 0; p < players.length; p++)
				System.out.print(String.format("%7d | ", wins[p]));
			System.out.println();
			System.out.println();
		
		}
		System.setOut(defaultOut);
		//Swapping time
		players = new Player[]{players[1], players[0]};
		
		results = new int[players.length][rounds];
		wins = new int[players.length];
		

		System.out.println("-----------------------------------------------------------------------------------");
		stats_file.println("-----------------------------------------------------------------------------------");
		System.out.println("SWAPPING TIME");
		stats_file.println("SWAPPING TIME");
		System.out.println("-----------------------------------------------------------------------------------");
		stats_file.println("-----------------------------------------------------------------------------------");
		
		
		System.out.println("Beginning tournament with board1="+players[0].getName() + " and board2=" + players[1].getName() + " board 1 starts");
		stats_file.println("Beginning tournament with board1="+players[0].getName() + " and board2=" + players[1].getName() + " board 1 starts");
		startingId = 0;
		for (int i = 0; i < rounds; i++)
		{	
			//Build boards
			Scoreboard[] boards = new Scoreboard[]{getBoard1(), getBoard2()};
			
			
			Controller c = new Controller(players, seed, boards, startingId);
			c.OUTPUT = false;
			
			c.startGame();
			System.out.print(String.format("Seed %11d -> | ",seed));
			stats_file.print(String.format("Seed %11d -> | ",seed));
			double winning_score = -1;
			boolean no_winner = false;
			//Store scores
			for (int p = 0; p < players.length; p++)
			{
				results[p][i] = c.getResults()[p].totalInclBonus();
				if (results[p][i] == winning_score) no_winner = true;
				winning_score = Math.max(winning_score, results[p][i]);
				System.out.print(String.format("%3d     | ",results[p][i]));
				stats_file.print(String.format("%3d     | ",results[p][i]));
				players[p].reset(p);
			}
			//Store wins
			if (!no_winner)
			{
				for (int p = 0; p < players.length; p++)
				{
					if (winning_score == results[p][i])
						wins[p]++;
				}
			}
			
			System.out.println(String.format("%4d / %d  - %s started", i+1, rounds, players[startingId].getName()));
			stats_file.println(String.format("%4d / %d  - %s started", i+1, rounds, players[startingId].getName()));
			stats_file.flush();
			seed++;
			startingId = (startingId == 0) ? 1 : 0;
		}
		
		//Perform calculations
		maths = new Statsmath[players.length];
		for (int p = 0; p < players.length; p++)
		{
			maths[p] = new Statsmath(results[p]);
		}
		
		for (int k = 0; k < 2; k++)
		{
			if (k == 1)
			{
				System.setOut(stats_file);
			}
			System.out.println("Stats with " + String.format("%,d",rounds) + " rounds from seed " + (seed-rounds));
			//Print the stats
			System.out.println();
			for (int p = 0; p < players.length; p++)
			{
				System.out.println("["+p+"] : " + players[p].getName());
			}
			
			System.out.println();
			System.out.print(String.format("%22s","Player | "));
			for (int p = 0; p < players.length; p++)
				System.out.print("  ["+p+"]   | ");
			System.out.println();
			System.out.print(String.format("%22s","Mean: | "));
			for (int p = 0; p < players.length; p++)
				System.out.print(String.format("%7.3f | ", maths[p].mean));
			System.out.println();
			System.out.print(String.format("%22s","Median: | "));
			for (int p = 0; p < players.length; p++)
				System.out.print(String.format("%3d     | ", maths[p].median));
			System.out.println();
			System.out.print(String.format("%22s","Min: | "));
			for (int p = 0; p < players.length; p++)
				System.out.print(String.format("%3d     | ", maths[p].min));
			System.out.println();
			System.out.print(String.format("%22s","Max: | "));
			for (int p = 0; p < players.length; p++)
				System.out.print(String.format("%3d     | ", maths[p].max));
			System.out.println();
			System.out.print(String.format("%22s","Std. dev.: | "));
			for (int p = 0; p < players.length; p++)
				System.out.print(String.format("%7.3f | ", maths[p].std_dev));
			System.out.println();
			System.out.print(String.format("%22s","Wins: | "));
			for (int p = 0; p < players.length; p++)
				System.out.print(String.format("%7d | ", wins[p]));
			System.out.println();
			System.out.println();
		
		}
		
		
	}
	
	
	
}
