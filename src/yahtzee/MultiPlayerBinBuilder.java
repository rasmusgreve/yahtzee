package yahtzee;

import player.MultiPlayerAI;
import util.Persistence;

public class MultiPlayerBinBuilder implements Runnable {

	public static void main(String[] args) throws InterruptedException {
		MultiPlayerAI ai = new MultiPlayerAI();
		Thread[] ts = new Thread[ai.aggresivityLevels];
		for (int i = 0; i < ai.aggresivityLevels; i++)
		{
			MultiPlayerBinBuilder r = new MultiPlayerBinBuilder();
			r.agg = i;
			ts[i] = new Thread(r);
			ts[i].setDaemon(false);
			ts[i].start();
		}
	}
	
	public int agg;

	@Override
	public void run() {
		MultiPlayerAI ai = new MultiPlayerAI(true, agg);
		System.out.println("Calculating boardValues for aggresivitylevel: " + agg);
		ai.getBoardValue(0);
		Persistence.storeDoubleArray(ai.boardValues[agg], MultiPlayerAI.filename + agg + MultiPlayerAI.fileext);
	}

}
