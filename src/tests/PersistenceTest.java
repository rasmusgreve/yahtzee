package tests;

import static org.junit.Assert.*;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import player.SinglePlayerAI;
import game.Scoreboard;
import game.Scoreboard.ScoreType;

import java.io.*;


public class PersistenceTest {

	Scoreboard fullBoard;
	
	static final String secureFilename = "cacheBackup.bin";
	
	@BeforeClass
	public static void secureRealCache() throws IOException
	{
		moveFile(SinglePlayerAI.filename, secureFilename);
	}
	
	@AfterClass
	public static void restoreRealCache() throws IOException
	{
		moveFile(secureFilename, SinglePlayerAI.filename);
	}
	
	private static void moveFile(String from, String to) throws IOException
	{
		File fromFile = new File(from);
		File toFile = new File(to);
		if (fromFile.exists())
		{
			FileOutputStream fos = new FileOutputStream(toFile);
			FileInputStream fis = new FileInputStream(fromFile);
			
			byte[] buffer = new byte[1024];
			int len = fis.read(buffer);
			while (len != -1) {
			    fos.write(buffer, 0, len);
			    len = fis.read(buffer);
			}
			
			fos.close();
			fis.close();
			fromFile.delete();
		}
	}
	
	@Before
	public void setUp()
	{
		fullBoard = new Scoreboard();
		
		fullBoard.insert(ScoreType.ONES, 2);
		fullBoard.insert(ScoreType.TWOS, 6);
		fullBoard.insert(ScoreType.THREES, 9);
		fullBoard.insert(ScoreType.FOURS, 12);
		fullBoard.insert(ScoreType.FIVES, 15);
		fullBoard.insert(ScoreType.SIXES, 18);
		fullBoard.insert(ScoreType.THREE_OF_A_KIND, 25);
		fullBoard.insert(ScoreType.FOUR_OF_A_KIND, 26);
		fullBoard.insert(ScoreType.SMALL_STRAIGHT, 25);
		fullBoard.insert(ScoreType.BIG_STRAIGHT, 30);
		fullBoard.insert(ScoreType.YAHTZEE, 50);
		fullBoard.insert(ScoreType.CHANCE, 20);
		fullBoard.insert(ScoreType.FULL_HOUSE, 30);
	}
	
	@Test
	public void testPersistence() throws InterruptedException {
		SinglePlayerAI ai = new SinglePlayerAI(); //Tries to load file (fails)
		
		for (double v : ai.boardValues)
			assertEquals("Board values should be initialized to -1", v, -1, 1E-6);
		
		ai.getBoardValue(fullBoard.ConvertMapToInt());
		assertEquals("The found value should be 0", 0, ai.boardValues[524286], 1E-6);
		ai.cleanUp(); //Stores file
		
		SinglePlayerAI ai2 = new SinglePlayerAI(); //Loads file
		for (int i = 0; i < ai2.boardValues.length; i++)
		{
			if (i == 524286)
				assertEquals("The found value should be 0", 0, ai2.boardValues[i], 1E-6);
			else
				assertEquals("The unfound values should be -1", -1, ai2.boardValues[i], 1E-6);
		}
		
	}

}
