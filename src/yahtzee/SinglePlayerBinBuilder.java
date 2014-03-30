package yahtzee;

import game.Scoreboard;
import player.SinglePlayerAI;
public class SinglePlayerBinBuilder {

	
	public static void main(String[] args) {

		System.out.println("Starting singleplayer ai bin build");
		SinglePlayerAI spai1 = new SinglePlayerAI();
		//spai1.getBoardValue(0);
		spai1.getBoardValue(new Scoreboard());
		spai1.cleanUp();

		System.out.println("All done");
		
		System.out.println("spai1.getBoardValue(0): " + spai1.getBoardValue(0));
		
	}
	
}
