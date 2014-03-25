package tests;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

import game.Controller;
import game.Scoreboard;
import player.MultiPlayerAI;
import player.Player;
import player.SinglePlayerAI;
import util.Persistence;

public class MultiPlayeAIReducedSBTests {

	
	public static int agg;
	private static int reducedBoardInt;
	
	private static SinglePlayerAI spai;
	private static MultiPlayerAI mpai;
	
	private static Scoreboard sb;
	
	@BeforeClass
	public static void testScoreboardUpperZero() {		

		sb = new Scoreboard(6, 7);
		
		MultiPlayerAI.filename = "multiPlayerReducedCache";
		
		reducedBoardInt = sb.ConvertMapToInt();
				
		for (int i = 0; i < MultiPlayerAI.aggresivityLevels; i++)
		{
			agg = i;			
			MultiPlayerAI ai = new MultiPlayerAI(true, agg);
			System.out.println("Calculating boardValues for aggresivitylevel: " + agg);
			ai.getBoardValue(reducedBoardInt);
			
			//System.out.println("agg: " + i + ", " + ai.boardValues[i][reducedBoardInt * 2 + 0]);
			
			Persistence.storeArray(ai.boardValues[agg], MultiPlayerAI.filename + agg + MultiPlayerAI.fileext);
		}
	}
	
	
//	@Test
//	public void testExpectedScores(){
//		MultiPlayerAI ai = new MultiPlayerAI();
//		for (int i = 0; i < MultiPlayerAI.aggresivityLevels; i++) {		
//			assertEquals("testExpectedScores failed ", expectedScores[i], ai.boardValues[i][reducedBoardInt * 2 + 0], 0);
//		}
//		
//	}
	
	
	@Test
	public void testZeroSeedGame(){
		int seed = 8;
		
		mpai = new MultiPlayerAI();
		mpai.OUTPUT = false;
		spai = new SinglePlayerAI();
		spai.OUTPUT = false;
		
		

		Controller c = new Controller(new Player[]{mpai, spai}, seed);
		c.logic.setScoreboardTo(0, sb.clone());
		c.logic.setScoreboardTo(1, sb.clone());
		c.startGame();
		int result = c.getResults()[0].totalInclBonus();
		
		assertEquals("testZeroSeedGame failed ", 172, result);
	}
	
	
	double[] expectedScores = new double[]{74.94642228857835, 82.8198650268849, 87.90034791567008, 91.57840173619886,
								92.20750820999203, 92.2775626858593, 92.24873062156098, 92.19205019248062,
								91.77816024021683, 91.25074044976981, 90.02458582898012};

	
}
