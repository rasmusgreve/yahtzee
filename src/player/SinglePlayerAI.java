package player;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

import game.Answer;
import game.GameLogic;
import game.Question;
import game.Scoreboard;
import game.Scoreboard.ScoreType;
import util.YahtzeeMath;

public class SinglePlayerAI implements Player {

	public double[] boardValues;
	//Values is the dynamic program cache for the inner search
	double[] rollValues = new double[1025];
	private static final String filename = "testCache.bin";
	//A list of all 252 possible different rolls
	private static ArrayList<int[]> allRolls = new ArrayList<int[]>();
	static {
		for (int a = 1; a <= 6; a++)
		 for (int b = a; b <= 6; b++)
		  for (int c = b; c <= 6; c++)
		   for (int d = c; d <= 6; d++)
		    for (int e = d; e <= 6; e++)
		     allRolls.add(new int[]{a,b,c,d,e});
	}
	
	public SinglePlayerAI() {
		loadArray();
	}
	@Override
	public Answer PerformTurn(Question question) {		
		System.out.println("q: " + Arrays.toString(question.roll) + ", " + question.rollsLeft);
		
		Answer ans = new Answer();
		rollValues = new double[1025];
		for (int i = 0; i < 1025; i++){rollValues[i] = -1;}
		
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
		for (ScoreType type : board.possibleScoreTypes()) {
			Scoreboard cloneBoard = board.clone();
			cloneBoard.insert(type, GameLogic.valueOfRoll(type, roll));
			double newVal = bigDynamicProgramming(cloneBoard);
			
			if (newVal > max){
				max = newVal;
				best = type;
			
			}
		}
		return best;
		
		//TODO: Big dynamic program
		//return ScoreType.BIG_STRAIGHT;
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
				sum += getProb(hold, new_roll) * valueOfRoll(new_roll, rollsLeft-1, board);
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
		for (int[] roll: allRolls) {
			double v = valueOfRoll(roll, 2, board);
			s += v * YahtzeeMath.prob5(roll);
		}
		return s;
	}
	private double bigDynamicProgramming(Scoreboard board) {
		int idx = board.ConvertMapToInt();
		if (boardValues[idx] == -1) {
			boardValues[idx] = rollFromScoreboard(board);
		}
		return boardValues[idx];
	}
	
	private double valueOfRoll(int[] roll, int rollsLeft, Scoreboard board)
	{
		if (rollsLeft == 0)
		{		
			double max = Double.NEGATIVE_INFINITY;
			for (ScoreType type : board.possibleScoreTypes()) {
				Scoreboard cloneBoard = board.clone();
				cloneBoard.insert(type, GameLogic.valueOfRoll(type, roll));
				max = Math.max(max, bigDynamicProgramming(cloneBoard));
			}
			return max;
			//iterate valid ScoreTypes in scoreboard
			//return big dynamic program ( Scoreboard.Apply(roll) );
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
					sum += getProb(hold, new_roll) * valueOfRoll(new_roll, rollsLeft-1, board);
				}
				rollValues[idx] = Math.max(rollValues[idx], sum);
			}
		}
		return rollValues[idx];
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
	/*
	public static void main(String[] args) {
		SinglePlayerAI ai = new SinglePlayerAI();
		ai.doIt();
	}
	
	public void doIt()
	{
		double sum = 0;
		boolean[] hold = new boolean[]{true,false,false,false,true};
		int[] roll = new int[] {1,5,1,3,1};
		for (int[] new_roll : getPossibleRolls(roll, hold))
		{
			System.out.println("new_roll: " + Arrays.toString(new_roll));
			sum += getProb(hold, new_roll);
			System.out.println("getProb(hold, new_roll): " + getProb(hold, new_roll));
		}
		System.out.println(sum);
		
	}
	*/
	private static ArrayList<int[]> getPossibleRolls(int[] roll, boolean[] hold)
	{
		//TODO: calculate rolls once. Apply roll,hold dyn
		ArrayList<int[]> rolls = new ArrayList<int[]>();
		for (int[] r_p : allRolls)
		{
			int[] r = Arrays.copyOf(r_p, r_p.length);
			//Apply hold
			for (int j = 0; j < 5; j++)
				if (hold[j])
					r[j] = roll[j];
			//Sort and filter unique
			boolean has = false;
			for (int[] ex : rolls)
			{
				if (Arrays.equals(ex, r))
				{
					has = true;
					break;
				}
			}
			if (!has)
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
	
	private ArrayList<boolean[]> getInterestingHolds(int[] roll)
	{
		ArrayList<boolean[]> holds = new ArrayList<boolean[]>();
		
		//TODO: Filter out uninteresting holds maybe?
		for (int i = 0; i < (1 << 5); i++)
			holds.add(holdFromInt(i));
		
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
	public void finalize(){
		//Save lookup table to persistent medium
		try {
			FileOutputStream fos = new FileOutputStream(filename);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(boardValues);
			oos.close();
			fos.close();
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
