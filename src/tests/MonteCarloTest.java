package tests;

import java.util.ArrayList;

import game.Controller;
import player.Player;

public class MonteCarloTest {
	public MonteCarloTest(int tests, ArrayList<Player> players) {
		int[] wins = new int[players.size()];
		int gameNr = 100;
		for (int i = 0; i < gameNr; i++) {
			int score = 0;
			boolean draw = false;
			int bestScore = -1;
			int bestPlayer = -1;
			Controller c = new Controller(players.toArray(new Player[players.size()]), (int)(System.currentTimeMillis() % Integer.MAX_VALUE));
			c.startGame();
			for (int j = 0; j < players.size(); j++) {
				score = c.getResults()[j].totalInclBonus();
				if (score > bestScore) {
					draw = false;
					bestScore = score;
					bestPlayer = j;
				}
				else if (score == bestScore) {
					draw = true;
				}
			}
			if (!draw) {
				wins[bestPlayer]++;
			}
			for (int j = 0; j < players.size(); j++) {
				int pLoss = 0;
				for (int k = 0; k < players.size(); k++) {
					if (j == k) {
						continue;
					}
					else {
						pLoss+= wins[k];
					}
				}
				System.out.println("Player" + j + " has won " + (((double) wins[j]) / (double)(pLoss + wins[j])) * 100 + " % of the " + (pLoss + wins[j]) + "-" + gameNr + " games.");
			}
		}
	}
}
