package yahtzee;

import game.Scoreboard;
import player.MultiPlayerAI;
import util.Persistence;

public class MultiPlayerBinBuilder implements Runnable {

	public static void main(String[] args) throws InterruptedException {
		filledSpaces = 9; 	//0 is a standard/empty score board
		
		Thread[] ts = new Thread[MultiPlayerAI.aggresivityLevels];		
		for (int i = 0; i < MultiPlayerAI.aggresivityLevels; i++)
		{
			MultiPlayerBinBuilder r = new MultiPlayerBinBuilder();
			r.agg = i;
			ts[i] = new Thread(r);
			ts[i].setDaemon(false);
			ts[i].start();
		}
	}
	
	public int agg;
	public static int filledSpaces;

	@Override
	public void run() {
		MultiPlayerAI ai = new MultiPlayerAI(true, agg);
		System.out.println("Calculating boardValues for aggresivitylevel: " + agg);
		Scoreboard sb = new Scoreboard(filledSpaces,13-filledSpaces);
		ai.getBoardValue(sb.ConvertMapToInt());
		Persistence.storeArray(ai.boardValues[agg], MultiPlayerAI.filename + agg + MultiPlayerAI.fileext);
	}

}
