package player;

import java.util.Arrays;

import util.Persistence;
import util.YahtzeeMath;
import game.Answer;
import game.GameLogic;
import game.Question;
import game.Scoreboard;
import game.Scoreboard.ScoreType;

import org.apache.commons.math3.distribution.*;

public class MultiPlayerAI extends BaseAI {

	public static final int CACHE_SIZE = (int)Math.pow(2,19) * 2;
	public static final int MEAN = 0, VARIANCE = 1;
	public static final int aggresivityLevels = 11;	//These are tied to the .bin cache files
	public int[] aggresivityLevelUsage = new int[aggresivityLevels];
	public int aggresivityLevel = -1;
	public boolean staticAggresivity = false;
	public double[][] boardValues; //boardValues[aggresivity_level][boardhash * 2 + (0=mean, 1=variance)]
	public static String filename = "multiPlayerCache";
	public static final String fileext = ".bin";
	public boolean OUTPUT = false;
	
	
	private static double[][] newRollValuesCache()
	{
		double[][] rollValues = new double[1020][];
		for (int i = 0; i < rollValues.length; i++)
		{
			rollValues[i] = new double[]{-1,-1};
		}
		return rollValues;
	}
	
	public MultiPlayerAI(boolean staticAggro, int aggresivity)
	{
		staticAggresivity = staticAggro;
		aggresivityLevel = aggresivity;
		if (staticAggresivity)
		{
			boardValues = new double[aggresivityLevels][];
			boardValues[aggresivity] = Persistence.loadArray(filename + aggresivity + fileext, CACHE_SIZE);
		}
	}
	
	public MultiPlayerAI()
	{		
		boardValues = new double[aggresivityLevels][];
		for (int i = 0; i < aggresivityLevels; i++)
		{
			boardValues[i] = Persistence.loadArray(filename + i + fileext, CACHE_SIZE);
			System.out.println("Loaded boardValue-cache for aggro: " + i);
		}
	}
	
	@Override
	public Answer PerformTurn(Question question) {
		if (OUTPUT)
			System.out.println("***MultiPlayerAI's turn - q: " + Arrays.toString(question.roll) + ", " + question.rollsLeft);

		if (OUTPUT)
			System.out.println("Current scores. MultiPlayerAI: " + question.scoreboards[question.playerId].sum() + ", opponent: " + question.scoreboards[question.playerId == 0 ? 1 : 0].sum());
		
		
		Answer ans = new Answer();

		updateAggressivity(question.scoreboards[question.playerId], question.scoreboards[question.playerId == 0 ? 1 : 0]);
			
		if (OUTPUT)
			System.out.println("New aggresivity level: " + aggresivityLevel);
		
		if (question.rollsLeft == 0)
			ans.selectedScoreEntry = getBestScoreEntry(question.roll, question.scoreboards[question.playerId].ConvertMapToInt());
		else
			ans.diceToHold = getBestHold(question.roll, question.rollsLeft, question.scoreboards[question.playerId].ConvertMapToInt());
		if (OUTPUT)
			System.out.println("a: " + Arrays.toString(ans.diceToHold) + ", " + ans.selectedScoreEntry);

		
		
		
		return ans;
	}
	
	private void updateAggressivity(Scoreboard mine, Scoreboard other){
		if (staticAggresivity) return; //TODO: Remove this statement when done building caches
		
		//Opponents expected value is estimated remaining scores + the scores in the board
		//TODO: Compare using the variance too
		double otherExpectedMean =  boardValues[aggresivityLevels/2][other.ConvertMapToInt()*2+MEAN] + other.sum();
		double otherExpectedVariance =  boardValues[aggresivityLevels/2][other.ConvertMapToInt()*2+VARIANCE];
		double bestWinningProb = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < aggresivityLevels; i++) {
			//Calculate the expected value of my board
			double myExpectedMean = boardValues[i][mine.ConvertMapToInt() * 2 + MEAN];
			double myExpectedVariance = boardValues[i][mine.ConvertMapToInt() * 2 +VARIANCE];
			//double[] myExpected = boardValues[i][mine.ConvertMapToInt()].clone();
			myExpectedMean += mine.sum();
			
			//Calculate the probability that we win with aggresivity level i
//			NormalDistribution nd = new NormalDistribution(myExpectedMean, Math.sqrt(myExpectedVariance));
//			double winningProb = 1-nd.cumulativeProbability(otherExpectedMean-1);
			//TODO: Sanity check
			NormalDistribution nd = new NormalDistribution(myExpectedMean-otherExpectedMean, Math.sqrt(myExpectedVariance + otherExpectedVariance));
			double winningProb = 1-nd.cumulativeProbability(0);
			
			//-y + x?? eller omvendt. Do it.
			//mean - mean
			//vari + vari
			//cumulativeProb 0
			
			
			//Select the aggresivity level with the highest winning prob
			if (bestWinningProb < winningProb) {
				bestWinningProb = winningProb;
				aggresivityLevel = i;
			}
		}
		aggresivityLevelUsage[aggresivityLevel]++;
	}
	
	private ScoreType getBestScoreEntry(int[] roll, int board)
	{
		int rollC = YahtzeeMath.colex(roll);

		int best = -1;
		double max = Double.NEGATIVE_INFINITY;
		if (OUTPUT)
			System.out.println("Choosing scoreboard slot - possible choices:");
		for (int type = 0; type < ScoreType.count; type++) {
			if (Scoreboard.isFilled(board, type)) continue; //Skip filled entries
			
			int value_of_roll = GameLogic.valueOfRoll(type, rollC);
			double[] boardValue = getBoardValue(Scoreboard.fill(board, type, value_of_roll));
			double newVal = getAdjustedMean(boardValue) + value_of_roll;
						
			if (OUTPUT)
				System.out.println("type: " + type + ", value: " + newVal);
			
			
			if (newVal > max){
				max = newVal;
				best = type;
			}
		}
		return ScoreType.values()[best];
	}
	
	
	private double getAdjustedMean(double mean, double variance){
		
		double stdDev = Math.sqrt(variance);
		double agg = ((aggresivityLevel)/((double)(aggresivityLevels-1))); //0 to 1
		agg = agg * 2;//0 to 2;
		agg = agg - 1;//-1 to 1;
		//agg = agg 												//maskine 1 
		//agg = Math.pow(Math.abs(agg), 1.5f) * Math.signum(agg);	//maskine 2
		//agg = agg * 0.5f;											//maskine 3
		//agg = agg 												//maskine 4 
		//agg = agg * 2f;											//maskine 5
		return mean + stdDev * agg;

	}
	
	private double getAdjustedMean(double[] data){
		
		return getAdjustedMean(data[MEAN], data[VARIANCE]);
	}
	
	private boolean[] getBestHold(int[] roll, int rollsLeft, int board) //Kickoff
	{
		int[] rollSorted = roll.clone();
		Arrays.sort(rollSorted);
		int rollC = YahtzeeMath.colex(roll);

		if (OUTPUT)
			System.out.println("Get best hold");
		double max = Double.NEGATIVE_INFINITY;
		int[] bestHoldDice = new int[6];
		for (boolean[] hold : getInterestingHolds(rollC))
		{
			if (hold == null) continue;
			int[] possibleRolls = getPossibleRolls(rollC, hold);
			int[] holdDice = getHoldDice(rollC, hold);	
			
			double[][] valueOfRollCache = new double[possibleRolls.length][];
			double[] probCache = new double[possibleRolls.length];
			
			for (int i = 0; i < possibleRolls.length; i++) {
				valueOfRollCache[i] = valueOfRoll(possibleRolls[i], rollsLeft-1, board, newRollValuesCache());
				probCache[i] = getProbSmart(holdDice, possibleRolls[i]);
			}
			
			double newMean = getAggregatedMean(probCache, valueOfRollCache);
			double newVariance = getAggregatedVariance(probCache, valueOfRollCache);
			
			newMean = getAdjustedMean(newMean, newVariance);
			
			if (newMean > max)
			{
				max = newMean;
				bestHoldDice = getHoldDiceInit(rollSorted, hold);
			}
		}
		
		boolean[] resortedBestHold = new boolean[5];
		for (int i = 0; i < 5; i++) {
			if (bestHoldDice[roll[i]-1] > 0){
				resortedBestHold[i] = true;
				bestHoldDice[roll[i]-1]--;
			}
		}
		
		return resortedBestHold;
	}
	
	private double[] rollFromScoreboard(int board) {
		if (staticAggresivity) cacheBuildingPrint(board);
		
		if (OUTPUT)
			System.out.println("Roll from scoreboard: " + board);
		
		double[][] cache = newRollValuesCache();
				
		double[] probCache = new double[YahtzeeMath.allRolls.length];
		double[][] valueOfRollCache = new double[YahtzeeMath.allRolls.length][];
		
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++) {
			valueOfRollCache[i] = valueOfRoll(YahtzeeMath.colex(YahtzeeMath.allRolls[i]), 2, board, cache);
			probCache[i] = YahtzeeMath.prob(5,YahtzeeMath.allRolls[i]);
		}
		double newMean = getAggregatedMean(probCache, valueOfRollCache);
		double newVariance = getAggregatedVariance(probCache, valueOfRollCache);
		
		return new double[]{newMean, newVariance};
	}
	
	public double[] getBoardValue(int board) {
		if (boardValues[aggresivityLevel][board*2+MEAN] == -1) {
			if (Scoreboard.isFull(board))
			{
				if (OUTPUT)
					System.out.println("board is full");
				boardValues[aggresivityLevel][board*2+MEAN] = Scoreboard.bonus(board);
				boardValues[aggresivityLevel][board*2+VARIANCE] = 0;
			} 
			else
			{
				if (OUTPUT)
					System.out.println("Calculating board value: " + board);
				double[] result = rollFromScoreboard(board);
				boardValues[aggresivityLevel][board*2+MEAN] = result[MEAN];
				boardValues[aggresivityLevel][board*2+VARIANCE] = result[VARIANCE];
				//boardValues[aggresivityLevel][board] = rollFromScoreboard(board);
				if (OUTPUT)
					System.out.println("Board value was: " + result[MEAN]);
				
			}
		}
		//System.out.println("Returning " + boardValues[board][MEAN]);
		return new double[]{boardValues[aggresivityLevel][board*2+MEAN],boardValues[aggresivityLevel][board*2+VARIANCE]};
		//return boardValues[aggresivityLevel][board].clone();
	}
	
	public double[] valueOfRoll(int rollC, int rollsLeft, int board, double[][] rollValues)
	{
		if (rollsLeft == 0)
		{
			double[] best = {Double.NEGATIVE_INFINITY, 0};
			double bestAdjustedMean = Double.NEGATIVE_INFINITY;
			for (int i = 0; i < ScoreType.count; i++) {
				if (Scoreboard.isFilled(board, i)) continue; //Skip filled entries
				//System.out.println("Scoretype: " + ScoreType.values()[i]);
				
				int rollVal = GameLogic.valueOfRoll(i, rollC);
				double[] boardVal = getBoardValue(Scoreboard.fill(board, i, rollVal));
				
				double adjustedMean = getAdjustedMean(boardVal) + rollVal;
				//System.out.println("Boardval: " + adjustedMean + " sd: " + Math.sqrt(boardVal[1]));
				
				if (bestAdjustedMean < adjustedMean){
					bestAdjustedMean = adjustedMean;
					best = boardVal;
					best[MEAN] += rollVal;
				}
			}
			return best;
		}
		
		int idx = rollIdx(rollC, rollsLeft);
		if (rollValues[idx][MEAN] == -1)
		{
			
			double bestAdjustedMean = Double.NEGATIVE_INFINITY;
			
			rollValues[idx] = new double[] {Double.NEGATIVE_INFINITY, 0};
			for (boolean[] hold : getInterestingHolds(rollC))
			{
				if (hold == null) continue;
				
				int[] holdDice = getHoldDice(rollC, hold);	
				
				int[] possibleRolls = getPossibleRolls(rollC, hold);
				double[] probCache = new double[possibleRolls.length];
				double[][] valueOfRollCache = new double[possibleRolls.length][];
				for (int i = 0; i < possibleRolls.length; i++) { 
					valueOfRollCache[i] = valueOfRoll(possibleRolls[i], rollsLeft-1, board, rollValues);
					probCache[i] = getProbSmart(holdDice, possibleRolls[i]);
				}

				double newMean = getAggregatedMean(probCache, valueOfRollCache);
				double newVariance = getAggregatedVariance(probCache, valueOfRollCache);
				
				double aggressiveMean = getAdjustedMean(newMean, newVariance);
				
				if (bestAdjustedMean < aggressiveMean) {
					bestAdjustedMean = aggressiveMean;
					rollValues[idx] = new double[] {newMean, newVariance};
				}
			}
		}
		return rollValues[idx];
	}
	
	
	
	@Override
	public String getName() {
		return "Multi player AI";
	}

	@Override
	public void cleanUp() {
		for (int i = 0; i < aggresivityLevels; i++){
			Persistence.storeArray(boardValues[i], filename + i + fileext);
		}
		System.out.println("Aggro level usage:");
		System.out.println(Arrays.toString(aggresivityLevelUsage));
	}

}

