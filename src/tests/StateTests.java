package tests;

import game.Scoreboard;
import game.State;

import static org.junit.Assert.*;
import org.junit.*;

public class StateTests {

	Scoreboard sb1, sb2;
	int state;
	
	@Before
	public void setup()
	{
		sb1 = new Scoreboard(6,7);
		sb2 = new Scoreboard(6,7);
		state = State.convertScoreboardsToState(sb1, sb2);
	}
	
	@Test
	public void testEmptyState()
	{
		assertEquals(235, state);
	}
	
	@Test 
	public void testStateFill()
	{
		state = State.fill(state, 6, 25, true);
		assertEquals(235+25, State.getScoreDiff(state));
		assertTrue(State.isFilled(state, 6, true));
		assertFalse(State.isFilled(state, 6, false));
	}
	
	@Test 
	public void testStateFill2()
	{
		state = State.fill(state, 6, 25, false);
		assertEquals(235-25, State.getScoreDiff(state));
		assertTrue(State.isFilled(state, 6, false));
		assertFalse(State.isFilled(state, 6, true));
	}
}
