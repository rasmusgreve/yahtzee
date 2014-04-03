package game;

import game.Scoreboard.ScoreType;

public class State {

	//convertScoreboardsToInt bits:
	//X: diff between players = 0 - 470
	//A: my current board
	//B: opponent current board
	//TBBBBBBAAAAAAXXXXXXXXX
	//1|  6 ||  6 ||   9   |
	
	final static int myBoardMask = ((1 << 6) - 1) << 9;
	final static int opponentBoardMask = ((1 << 6) - 1) << (9+6);
	final static int diffMask = (1 << 9) - 1;
	final static int turnMask = 1 << (6+6+9);
	
	
	final static int stateDiffZeroValue = 235;
	
	public static int setTurn(int state, boolean myTurn)
	{
		if (myTurn)
		{
			return state | turnMask;
		}
		else
		{
			return state & (turnMask-1);
		}
	}
	
	public static boolean getTurn(int state)
	{
		return (state & (1 << (9+6+6))) != 0;
	}
	
	public static boolean isGameOver(int state){
		return myBoardFull(state) && opponentBoardFull(state);
	}
	
	public static double getWinner(int state){
		int scoreDiff = getScoreDiff(state); // 0 -- 470
		
		if (scoreDiff < stateDiffZeroValue){
			return -1;
		}else if (scoreDiff == stateDiffZeroValue){
			return 0;
		}else{
			return 1;
		}
	}
	
	public static boolean myBoardFull(int state){
		return (state & myBoardMask) == myBoardMask;	
	}
	public static boolean opponentBoardFull(int state){
		return (state & opponentBoardMask) == opponentBoardMask;	
	}
	
	public static int getScoreDiff(int state){
		return (state & diffMask);
	}
	
	public static boolean isFilled(int state, int scoretype, boolean myTurn){
		if (scoretype < 7) return true;
		scoretype = 1 << ((scoretype - 7) + (myTurn ? 9 : (9+6)));
		return (state & scoretype) != 0;
	}
	
	
	public static int fill(int state, int scoretype, int rollVal, boolean myTurn) {
		if (scoretype < 7) throw new RuntimeException();
		
		//fill on off position
		scoretype = 1 << ((scoretype - 7) + (myTurn ? 9 : (9+6)));

		//change available scores
		state |= scoretype;

		//change score diff for state
		state += myTurn ? rollVal : -rollVal;
		
		return state;
	}
	
	
	public static int convertScoreboardsToState(Scoreboard aiBoard, Scoreboard opponentBoard, boolean myTurn){
		int myScore = aiBoard.totalInclBonus();
		int opponentScore = opponentBoard.totalInclBonus();
		int diff = myScore - opponentScore;
		
		boolean[] aiScores = new boolean[7];
		boolean[] opponentScores = new boolean[7];
		
		int i = 0;
		for (int type = 7; type < ScoreType.count; type++) {
			if (aiBoard.scoreArray[type] > -1){
				aiScores[i] = true;
			}
			if (opponentBoard.scoreArray[type] > -1){
				opponentScores[i] = true;
			}
			i++;
		}
		
		
		//diff : -235 -- +235
		int uDiff = diff + stateDiffZeroValue;	//:0 - 470
		int result = uDiff; 
		for (int j = 0; j < aiScores.length; j++) {
			result |= (aiScores[j] ? 1 : 0) << (9+j);
		}		
		for (int j = 0; j < opponentScores.length; j++) {
			result |= (opponentScores[j] ? 1 : 0) << ((9+6)+j);
		}		
		
		return State.setTurn(result, myTurn);
	}


	
}


