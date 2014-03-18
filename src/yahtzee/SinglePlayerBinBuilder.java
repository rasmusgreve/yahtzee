package yahtzee;

import player.SinglePlayerAI;
import player.SinglePlayerAIFloat;

public class SinglePlayerBinBuilder {

	
	public static void main(String[] args) {
		System.out.println("Starting singleplayer ai bin build");
		SinglePlayerAI spai1 = new SinglePlayerAI();
		spai1.getBoardValue(0);
		spai1.cleanUp();
		
		System.out.println("Starting singleplayer ai FLOAT bin build");
		SinglePlayerAIFloat spai2 = new SinglePlayerAIFloat();
		spai2.getBoardValue(0);
		spai2.cleanUp();
		System.out.println("All done");
	}
	
}
