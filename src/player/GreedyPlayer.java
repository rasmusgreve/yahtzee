package player;

import game.Answer;
import game.GameLogic;
import game.Question;
import game.Scoreboard;
import game.Scoreboard.ScoreType;

public class GreedyPlayer implements Player {

	int id;
	@Override
	public Answer PerformTurn(Question question) {
		Answer ans = new Answer();
		ans.diceToHold = holdHigh(question.roll);
		ans.selectedScoreEntry = bestValidScoretype(question.scoreboards[id], question.roll);
		return ans;
	}
	
	private ScoreType bestValidScoretype(Scoreboard scoreboard, int[] roll)
	{
		int max = -1;
		ScoreType type = null;
		for (ScoreType st : ScoreType.values())
		{
			if (scoreboard.get(st) != -1) continue;
			int v = GameLogic.valueOfRoll(st, roll); 
			if (v > max)
			{
				max = v;
				type = st;
			}
		}
		return type;
	}
	
	private boolean[] holdHigh(int[] roll)
	{
		boolean[] hold = new boolean[roll.length];
		for (int i = 0; i < roll.length; i++)
			hold[i] = (roll[i] >= 4);
		return hold;
	}

	
	@Override
	public String getName() {
		return "Greedy player";
	}
	
	@Override
	public void finalize(){
		
	}

	@Override
	public void reset(int id) {
		this.id = id;
	}

}
