package player;

import java.util.Arrays;

import util.Persistence;
import util.YahtzeeMath;
import game.Answer;
import game.GameLogic;
import game.Question;
import game.Scoreboard;
import game.Scoreboard.ScoreType;

public class OptimalMultiPlayerAI implements Player {
	
	protected int id;
	
	public double[] boardValues;
	public static final String filename = "optimalPlayerCache.bin";
	
	public OptimalMultiPlayerAI(){
		
	
	}

	@Override
	public Answer PerformTurn(Question question) {
		Answer ans = new Answer();
		
		if (question.rollsLeft == 0)
			ans.selectedScoreEntry = getBestScoreEntry(question.roll, question.scoreboards[question.playerId].ConvertMapToInt());
		else
			ans.diceToHold = getBestHold(question.roll, question.rollsLeft, question.scoreboards[question.playerId].ConvertMapToInt());
		
		return ans;
	}
	
	
	private ScoreType getBestScoreEntry(int[] roll, int board){
		int rollC = YahtzeeMath.colex(roll);
		
		int best = -1;
		double max = Double.NEGATIVE_INFINITY;

		for (int type = 0; type < ScoreType.count; type++) {
			if (Scoreboard.isFilled(board, type)) continue; //Skip filled entries
			int value_of_roll = GameLogic.valueOfRoll(type, rollC);
			int new_board = Scoreboard.fill(board, type, value_of_roll);
			double newVal = getBoardValue(new_board) + value_of_roll;
			
			if (newVal > max){
				max = newVal;
				best = type;
			}
		}
		return ScoreType.values()[best];
	}
	
	public double getBoardValue(int board) {
		if (boardValues[board] == -1) {
			if (Scoreboard.isFull(board))
			{
				boardValues[board] = Scoreboard.bonus(board);
			} 
			else
			{
				boardValues[board] = rollFromScoreboard(board);
			}
		}
					
		return boardValues[board];
	}
	
	
	private boolean[] getBestHold(int[] roll, int rollsLeft, int board){
		
		return null;
	}
	
	
	private double rollFromScoreboard(int board) {
//		double s = 0;
//		double[] cache = newRollValuesCache();
//		for (int i = 0; i < YahtzeeMath.allRolls.length; i++) {
//			double v = valueOfRoll(YahtzeeMath.colex(YahtzeeMath.allRolls[i]), 2, board, cache);
//			s += v * YahtzeeMath.prob(5,YahtzeeMath.allRolls[i]);
//		}
//		return s;
		return 0;
	}

	
	
	
	
	
	

	@Override
	public String getName() {
		return "Optimal multi player AI";
	}

	@Override
	public void reset(int id) {
		this.id = id;
	}

	@Override
	public void cleanUp() {
		Persistence.storeArray(boardValues, filename);
	}
	
	
	
	
	
}
