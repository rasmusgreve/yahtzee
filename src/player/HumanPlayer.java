package player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import game.Answer;
import game.Question;
import game.Scoreboard.ScoreType;

public class HumanPlayer implements Player {

	private BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	
	@Override
	public Answer PerformTurn(Question question) {
		System.out.print("Your turn. Rolls left: ");
		System.out.println(question.rollsLeft);
		System.out.println("Enter to reroll:");
		try {
			input.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Answer ans = new Answer();
		ans.diceToHold = new boolean[] {false, false, false, false, false};
		ans.selectedScoreEntry = ScoreType.BIG_STRAIGHT; 
		return ans;
	}
	
	public void setID(int id){
		
	}
	
	public String getName()
	{
		return "Human player";
	}
}
