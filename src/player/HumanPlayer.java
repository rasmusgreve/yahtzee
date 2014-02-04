package player;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import game.Answer;
import game.GameState;

public class HumanPlayer implements Player {

	private BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
	
	@Override
	public Answer PerformTurn(GameState state) {
		System.out.println("Your turn");
		System.out.println("Type something:");
		try {
			String s = input.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void setID(int id){
		
	}
	
	public String getName()
	{
		return "Human player";
	}
}
