package player;

import java.io.PrintStream;
import java.util.Arrays;

import util.YahtzeeMath;
import game.Answer;
import game.GameLogic;
import game.Question;
import game.Scoreboard;
import game.State;

public class ErrorCheckPlayer implements Player {

	SinglePlayerAI singlePlayerAI;
	public MultiPlayerAI multiPlayerAI;
	OptimalMultiPlayerAI optimalMultiPlayerAI;
	
	double moves[], singlePlayerErrors[], multiPlayerErrors[];
	
	
	
	public ErrorCheckPlayer(int emptyCategoriesPrScoreboard)
	{
		singlePlayerAI = new SinglePlayerAI();
		multiPlayerAI = new MultiPlayerAI();
		optimalMultiPlayerAI = new OptimalMultiPlayerAI();
		
		moves = new double[emptyCategoriesPrScoreboard*2+1];
		singlePlayerErrors = new double[emptyCategoriesPrScoreboard*2+1];
		multiPlayerErrors = new double[emptyCategoriesPrScoreboard*2+1];
	}
	
	@Override
	public Answer PerformTurn(Question question) {
		Answer singlePlayerAnswer, multiPlayerAnswer, optimalMultiPlayerAnswer;
		singlePlayerAnswer = singlePlayerAI.PerformTurn(question);
		multiPlayerAnswer = multiPlayerAI.PerformTurn(question);
		optimalMultiPlayerAnswer = optimalMultiPlayerAI.PerformTurn(question);
		
		int emptySpacesTotal = 0;
		int otherPlayerID = (question.playerId == 0) ? 1 : 0;
		emptySpacesTotal += question.scoreboards[question.playerId].emptySpaces();
		emptySpacesTotal += question.scoreboards[otherPlayerID].emptySpaces();
		
		
		if (question.rollsLeft == 0)
		{
			Scoreboard opponentBoard = question.scoreboards[otherPlayerID];
			double optWinProb = optimalMultiPlayerAI.winningProbAfterTurn;
			
			moves[emptySpacesTotal]++;
			if (optimalMultiPlayerAnswer.selectedScoreEntry != singlePlayerAnswer.selectedScoreEntry)
			{
				Scoreboard spBoard = question.scoreboards[question.playerId].clone();
				spBoard.insert(singlePlayerAnswer.selectedScoreEntry, GameLogic.valueOfRoll(singlePlayerAnswer.selectedScoreEntry, question.roll));
				int singlePlayerState = State.convertScoreboardsToState(spBoard, opponentBoard, false);
				double spWinProb = optimalMultiPlayerAI.winProbFromState(singlePlayerState);
				singlePlayerErrors[emptySpacesTotal] += optWinProb - spWinProb;
				if (optWinProb < spWinProb){throw new RuntimeException("Quit");}
			}
			if (optimalMultiPlayerAnswer.selectedScoreEntry != multiPlayerAnswer.selectedScoreEntry)
			{
				Scoreboard mpBoard = question.scoreboards[question.playerId].clone();
				mpBoard.insert(multiPlayerAnswer.selectedScoreEntry, GameLogic.valueOfRoll(multiPlayerAnswer.selectedScoreEntry, question.roll));
				int multiPlayerState = State.convertScoreboardsToState(mpBoard, opponentBoard, false);
				double mpWinProb = optimalMultiPlayerAI.winProbFromState(multiPlayerState);
				multiPlayerErrors[emptySpacesTotal] += optWinProb - mpWinProb;
				if (optWinProb < mpWinProb){throw new RuntimeException("Quit");}
			}
		}
		else
		{
			int stateInt = State.convertScoreboardsToState(question.scoreboards[question.playerId], question.scoreboards[question.playerId == 0 ? 1 : 0], true);
			double optWinProb = optimalMultiPlayerAI.winningProbFromHold(question.roll, optimalMultiPlayerAnswer.diceToHold, question.rollsLeft, stateInt);
			
			moves[emptySpacesTotal]++;
			if (!Arrays.equals(optimalMultiPlayerAnswer.diceToHold, singlePlayerAnswer.diceToHold))
			{
				double spWinProb = optimalMultiPlayerAI.winningProbFromHold(question.roll, singlePlayerAnswer.diceToHold, question.rollsLeft, stateInt);
				singlePlayerErrors[emptySpacesTotal] += optWinProb - spWinProb;
				if (optWinProb < spWinProb){throw new RuntimeException("Quit");}
			}
			if (!Arrays.equals(optimalMultiPlayerAnswer.diceToHold, multiPlayerAnswer.diceToHold))
			{
				double mpWinProb = optimalMultiPlayerAI.winningProbFromHold(question.roll, multiPlayerAnswer.diceToHold, question.rollsLeft, stateInt);
				multiPlayerErrors[emptySpacesTotal] += optWinProb - mpWinProb;
				if (optWinProb < mpWinProb){throw new RuntimeException("Quit");}
			}
		}
		
		return optimalMultiPlayerAnswer;
	}
	
	public void printResults(PrintStream stream)
	{	
		stream.print(String.format("%20s", ""));
		stream.println("|Absolute errors ||       Relative errors      |");
		stream.print(String.format("%20s", ""));
		stream.println("| Single | Multi ||    Single    |    Multi    |");
		for (int i = 0; i < moves.length; i++)
		{
			stream.print(String.format("Empty categories %2d:|", i));
			
			stream.print(String.format("%6f  |", singlePlayerErrors[i]));
			stream.print(String.format("%5f  ||", multiPlayerErrors[i]));
			
			double sp_error = (moves[i] == 0) ? 0 : singlePlayerErrors[i]/moves[i];
			double mp_error = (moves[i] == 0) ? 0 : multiPlayerErrors[i]/moves[i];
			
			stream.print(String.format(" %15f  |", sp_error));
			stream.print(String.format(" %15f  |", mp_error));
			
			stream.println();
		}
	}
	
	public static void main(String[] args) {
		ErrorCheckPlayer ecp = new ErrorCheckPlayer(6);
		ecp.printResults(System.out);
	}

	@Override
	public String getName() {
		return "Error checker";
	}

	@Override
	public void reset(int id) {
		singlePlayerAI.reset(id);
		multiPlayerAI.reset(id);
		optimalMultiPlayerAI.reset(id);
	}

	@Override
	public void cleanUp() {
		singlePlayerAI.cleanUp();
		multiPlayerAI.cleanUp();
		optimalMultiPlayerAI.cleanUp();
	}

}
