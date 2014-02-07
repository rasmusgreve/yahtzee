package player;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Arrays;

import game.Answer;
import game.GameLogic;
import game.Question;
import game.Scoreboard.ScoreType;

public class HumanPlayer implements Player {

	private BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	
	@Override
	public Answer PerformTurn(Question question) {
		System.out.println("------------------------------");
		System.out.println("Your roll: " + Arrays.toString(question.roll));
		System.out.println("Rolls left: " + question.rollsLeft);
		if (question.rollsLeft == 0)
		{
			System.out.println("Write a # between 0-12");
			for (int i = 0; i < 13; i++)
			{
				ScoreType typ = ScoreType.values()[i];
				if (question.scoreboards[question.playerId].get(typ) != -1) continue;
				System.out.print(i + ") ");
				System.out.print(typ + " ");
				System.out.println("(" + GameLogic.valueOfRoll(typ , question.roll) + ")");
			}
		}
		Answer ans = new Answer();
		try {
			if (question.rollsLeft == 0)
			{
				String in = input.readLine();
				int n = Integer.parseInt(in);
				ans.selectedScoreEntry = ScoreType.values()[n];
			}
			else
			{
				System.out.println("Choose which dice to hold (e.g. (10000) hold the first die only)");
				String in = input.readLine();
				ans.diceToHold = new boolean[] {false, false, false, false, false};
				for (int i = 0; i < 5; i++)
				{
					if (in.charAt(i) == '1') ans.diceToHold[i] = true;
				}
				
			}
		} catch (Exception e) {
			if (question.rollsLeft == 0)
				e.printStackTrace();
		}
		
		 
		return ans;
	}
	
	@Override
	public void reset(int id){
		
	}
	
	@Override
	public void finalize(){
		
	}
	
	@Override
	public String getName()
	{
		return "Human player";
	}
}
