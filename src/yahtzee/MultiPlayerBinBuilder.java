package yahtzee;

import java.util.Arrays;

import game.Scoreboard;
import player.MultiPlayerAI;
import util.Persistence;
import util.YahtzeeMath;

public class MultiPlayerBinBuilder implements Runnable {

	public static void main(String[] args) throws InterruptedException {
		MultiPlayerAI ai = new MultiPlayerAI();
		ai.aggresivityLevel = ai.aggresivityLevels/2;
		
		Scoreboard b = new Scoreboard(10,3);
		int[] roll = new int[] {1,1,1,2,3};
		
		ai.valueOfRoll(YahtzeeMath.colexInit(roll), 0, b.ConvertMapToInt(), new double[0][0]);
		
		
		/*filledSpaces = 9; 	//0 is a standard/empty score board
		
		Thread[] ts = new Thread[MultiPlayerAI.aggresivityLevels];		
		for (int i = 0; i < MultiPlayerAI.aggresivityLevels; i++)
		{
			MultiPlayerBinBuilder r = new MultiPlayerBinBuilder();
			r.agg = i;
			ts[i] = new Thread(r);
			ts[i].setDaemon(false);
			ts[i].start();
		}*/
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
