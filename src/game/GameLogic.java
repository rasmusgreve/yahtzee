package game;

import game.Scoreboard.ScoreType;

import java.util.Random;

public class GameLogic {
	
	int currentPlayer = 0;
	
	
	public GameLogic(int numPlayers){
		
	}
	
	public Question getQuestion(){
		
		
		return new Question();
	}
	
	
	public void applyAnswer(Answer answer){
		
	}
	
	
	public int valueOfRoll(ScoreType type, int[] roll){
		
		return 0;
	}
	
	public GameState doRoll(GameState state, Answer answer) {
		for (int i = 0; i < answer.diceToHold.length; i++) {
			if (answer.diceToHold[i] != true) {
				Random generator = new Random();
				state.roll[i] = generator.nextInt(6) + 1;
			}
		}
		return state;
	}
	public GameState performAction(GameState state, Answer answer) {
		state = this.doRoll(state, answer);
		if (answer.selectedScoreEntry != -1) {
			state = this.selectOption(state, answer);
		}
		return state;
	}
	public GameState selectOption(GameState state, Answer answer) {
		//TODO
		return state;
	}
}
