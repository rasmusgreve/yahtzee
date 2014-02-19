package player;

import java.util.List;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import game.Answer;
import game.GameLogic;
import game.Question;
import game.Scoreboard;
import game.Scoreboard.ScoreType;
import util.YahtzeeMath;

public class SinglePlayerAI implements Player {

	public double[] boardValues;
	public static final String filename = "singlePlayerCache.bin";
	
	public SinglePlayerAI() {
		loadArray();
	}
	
	private static double[] newRollValuesCache()
	{

		double[] rollValues = new double[1025];
		for (int i = 0; i < 1025; i++){rollValues[i] = -1;}
		return rollValues;
	}
	
	@Override
	public Answer PerformTurn(Question question) {		
		System.out.println("q: " + Arrays.toString(question.roll) + ", " + question.rollsLeft);

		Answer ans = new Answer();
		
		if (question.rollsLeft == 0)
			ans.selectedScoreEntry = getBestScoreEntry(question.roll, question.scoreboards[question.playerId]);
		else
			ans.diceToHold = getBestHold(question.roll, question.rollsLeft, question.scoreboards[question.playerId]);

		System.out.println("a: " + Arrays.toString(ans.diceToHold) + ", " + ans.selectedScoreEntry);

		return ans;
	}
	
	private ScoreType getBestScoreEntry(int[] roll, Scoreboard board)
	{
		ScoreType best = null;
		double max = Double.NEGATIVE_INFINITY;
		System.out.println("possible choices:");
		for (ScoreType type : board.possibleScoreTypes()) {
			Scoreboard cloneBoard = board.clone();
			cloneBoard.insert(type, GameLogic.valueOfRoll(type, roll));
			double newVal = bigDynamicProgramming(cloneBoard) + GameLogic.valueOfRoll(type, roll);
			
			System.out.println("type: " + type + ", value: " + newVal);
			
			
			if (newVal > max){
				max = newVal;
				best = type;
			}
		}
		return best;
	}
	
	private boolean[] getBestHold(int[] roll, int rollsLeft, Scoreboard board) //Kickoff
	{
		double max = Double.NEGATIVE_INFINITY;
		boolean[] best = null;
		for (boolean[] hold : getInterestingHolds(roll))
		{
			double sum = 0;
			for (int[] new_roll : getPossibleRolls(roll, hold))
			{
				sum += getProb(hold, new_roll) * valueOfRoll(new_roll, rollsLeft-1, board, newRollValuesCache());
			}
			if (sum > max)
			{
				max = sum;
				best = hold;
			}
		}
		return best;
	}
	private double rollFromScoreboard(Scoreboard board) {

		double s = 0;
		double[] cache = newRollValuesCache();
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++) {
			double v = valueOfRoll(YahtzeeMath.allRolls[i], 2, board, cache);
			s += v * YahtzeeMath.prob(5,YahtzeeMath.allRolls[i]);
		}
		return s;
	}
	
	
	int counter = 0;
	public double bigDynamicProgramming(Scoreboard board) {
		int idx = board.ConvertMapToInt();
		if (boardValues[idx] == -1) {
			if (board.isFull())
			{
				System.out.println("Board is full");
				boardValues[idx] = board.bonus();
			} 
			else
			{
				/*
				System.out.println("Calculating value from board " + board.ConvertMapToInt() + " (" + board.emptySpaces() + " empty)" + " - count: " + counter );
				*/
				System.out.println("Calculating value from board. Count: " + counter + ". " + (counter/4000.) + "%");
				counter++;
				boardValues[idx] = rollFromScoreboard(board);
			}
		}
					
		return boardValues[idx];
	}
	
	private double valueOfRoll(int[] roll, int rollsLeft, Scoreboard board, double[] rollValues)
	{
		if (rollsLeft == 0)
		{		
			double max = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < ScoreType.count; i++) {
				if (board.scoreArray[i] != -1) continue; //Skip filled entries
				Scoreboard cloneBoard = board.clone();
				int rollVal = GameLogic.valueOfRoll(i, roll);
				cloneBoard.insert(i, rollVal);
				max = Math.max(max, bigDynamicProgramming(cloneBoard) + rollVal);
			}
			return max;
		}
		
		int idx = rollIdx(roll, rollsLeft);
		if (rollValues[idx] == -1)
		{
			rollValues[idx] = Integer.MIN_VALUE;
			for (boolean[] hold : getInterestingHolds(roll))
			{
				double sum = 0;
				for (int[] new_roll : getPossibleRolls(roll, hold))
				{
					sum += getProb(hold, new_roll) * valueOfRoll(new_roll, rollsLeft-1, board, rollValues);
				}
				rollValues[idx] = Math.max(rollValues[idx], sum);
			}
		}
		return rollValues[idx];
	}
	
	public static void main(String[] args) {
//		Scoreboard board = new Scoreboard();
//		//board.insert(ScoreType.ONES, 2);
//		//board.insert(ScoreType.TWOS, 6);
//		board.insert(ScoreType.THREES, 9);
//		board.insert(ScoreType.FOURS, 12);
//		board.insert(ScoreType.FIVES, 15);
//		board.insert(ScoreType.SIXES, 18);
//		board.insert(ScoreType.THREE_OF_A_KIND, 25);
//		board.insert(ScoreType.FOUR_OF_A_KIND, 26);
//		board.insert(ScoreType.SMALL_STRAIGHT, 25);
//		board.insert(ScoreType.BIG_STRAIGHT, 30);
//		board.insert(ScoreType.YAHTZEE, 50);
//		board.insert(ScoreType.CHANCE, 20);
//		board.insert(ScoreType.FULL_HOUSE, 30);
//
//		SinglePlayerAI ai = new SinglePlayerAI();
//		System.out.println("Value: " + ai.bigDynamicProgramming(board));
		
		int t1 = YahtzeeMath.colex(new int[]{1,5});
		System.out.println("t1: " + t1);
		int t2 = YahtzeeMath.colex(new int[]{5,1});
		System.out.println("t2: " + t2);
		int t3 = YahtzeeMath.colex(new int[]{3,2});
		System.out.println("t3: " + t3);
		int t4 = YahtzeeMath.colex(new int[]{2,3});
		System.out.println("t4: " + t4);
		int t5 = YahtzeeMath.colex(new int[]{5,6});
		System.out.println("t5: " + t5);
		int t6 = YahtzeeMath.colex(new int[]{6,5});
		System.out.println("t6: " + t6);
		
	}
	
	private static double getProb(boolean[] hold, int[] roll)
	{
		int c = 0;
		for (boolean b : hold)
			if (!b)
				c++;
		
		int[] reducedRoll = new int[c];
		c = 0;
		for (int i=0; i<5; i++){
			if (!hold[i]){
				reducedRoll[c] = roll[i];
				c++;
			}
			
		}
				
		
		return (double)YahtzeeMath.prob(c, reducedRoll);
	}

	
	private static ArrayList<int[]> getPossibleRolls(int[] roll, boolean[] hold)
	{
        int newRollsNeeded = 5;
		for (int i = 0; i < hold.length; i++) if (hold[i]) newRollsNeeded--;
		ArrayList<int[]> rolls;
		if (newRollsNeeded == 0){
			 rolls = new ArrayList<int[]>(YahtzeeMath.rollNumber(1));
			 rolls.add(roll);
			 return rolls;
		}else{
			 rolls = new ArrayList<int[]>(YahtzeeMath.rollNumber(newRollsNeeded));
		}
		
		for (int j = 0; j < YahtzeeMath.rollNumber(newRollsNeeded); j++)
		{
			int[] r_p = YahtzeeMath.allRolls(newRollsNeeded)[j];
			int[] r = new int[5];

			int c = 0;
			for (int i = 0; i < 5; i++) {
				if (hold[i]){
					r[i] = roll[i];
					
				}else{
					r[i] = r_p[c];
					c++;
				}
			}
			rolls.add(r);
		}
		
		
		return rolls;
	}
	
	private static boolean[] holdFromInt(int v)
	{
		boolean[] out = new boolean[5];
		for (int i = 0; i < 5;i++)
		{
			out[i] = (v & (1 << i)) > 0;
		}
		return out;
	}
	
	//TODO: Remove symmetries
	private ArrayList<boolean[]> getInterestingHolds(int[] roll)
	{
		ArrayList<boolean[]> holds = new ArrayList<boolean[]>(32);
		for (int i = 0; i < (1 << 5); i++){
			boolean[] hold = holdFromInt(i);
			
			//Filter out uninteresting holds
			boolean add = true;
			for (int j = 1; j < hold.length; j++) {
				if (hold[j] && !hold[j-1] && roll[j-1] == roll[j]) add = false;
			}
			
			if (add){
				holds.add(hold);
			}
		}
		return holds;
	}
	
	
	private int rollIdx(int[] roll, int rollsLeft)
	{
		int v = YahtzeeMath.colex(roll);
		v |= rollsLeft << 8;
		return v;
	}

	
	
	
	
	
	@Override
	public void reset(int id){
		
	}
	
	
	public void loadArray()
	{
		try {
			FileInputStream fis = new FileInputStream(filename);
			ObjectInputStream ois = new ObjectInputStream(fis);
			boardValues = (double[]) ois.readObject();
			ois.close();
			fis.close();
		} catch (Exception e) {
			System.out.println("WARNING! cache not loaded");
			boardValues = new double[1000000];
			for (int i = 0; i < 1000000; i++){boardValues[i] = -1;}
		}
	}
	
	@Override
	public void cleanUp(){
		//Save lookup table to persistent medium
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(boardValues);
			oos.close();
			fos.close();
			System.out.println("Cache stored to \"" + filename + "\"");
		} catch (IOException e) {
			System.out.println("WARNING! cache not stored");
		}
	}
	
	@Override
	public String getName()
	{
		return "Single player AI";
	}


}


