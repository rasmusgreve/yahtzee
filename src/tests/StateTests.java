package tests;

import game.Scoreboard;
import game.State;

import static org.junit.Assert.*;
import org.junit.*;

public class StateTests {

	Scoreboard sb1, sb2;
	
	@Before
	public void setup()
	{
		sb1 = new Scoreboard(6,7);
		sb2 = new Scoreboard(6,7);
	}
	
	@Test
	public void testEmptyScoreboard()
	{
		int state = State.convertScoreboardsToState(sb1, sb2);
		assertEquals(235, state);
	}
	
}
