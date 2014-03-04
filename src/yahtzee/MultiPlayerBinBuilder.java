package yahtzee;

import player.MultiPlayerAI;
import util.Persistence;

public class MultiPlayerBinBuilder {

	public static void main(String[] args) {
		MultiPlayerAI ai = new MultiPlayerAI();
		ai.staticAggresivity = true;
		for (int i = 0; i < ai.aggresivityLevels; i++)
		{
			System.out.println("Calculating boardValues for aggresivitylevel: " + i);
			ai.aggresivityLevel = i;
			ai.getBoardValue(0);
			Persistence.storeDoubleArray(ai.boardValues[i], MultiPlayerAI.filename + i + MultiPlayerAI.fileext);
		}
		
	}

}
