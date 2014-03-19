package tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import game.Scoreboard;
import player.MultiPlayerAI;
import util.Persistence;
import yahtzee.MultiPlayerBinBuilder;

public class MultiPlayeAIReducedSBTests {

	
	public static int agg;
	static int reducedBoardInt;
	
	@BeforeClass
	public static void testScoreboardUpperZero() {		
		
		Scoreboard sb = new Scoreboard(6, 5);
		
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
	
	
	@Test
	public void testExpectedScores(){
		MultiPlayerAI ai = new MultiPlayerAI();
		for (int i = 0; i < MultiPlayerAI.aggresivityLevels; i++) {
			System.out.println("aggro " + i + ", expected val: " + ai.boardValues[i][reducedBoardInt * 2 + 0]);
		
			assertEquals("dis don work", expectedScores[i], ai.boardValues[i][reducedBoardInt * 2 + 0], 0);
		}
		
	}
	
	
	double[] expectedScores = new double[]{74.94642228857835, 82.8198650268849, 87.90034791567008, 91.57840173619886,
								92.20750820999203, 92.2775626858593, 92.24873062156098, 92.19205019248062,
								91.77816024021683, 91.25074044976981, 90.02458582898012};

	
}
