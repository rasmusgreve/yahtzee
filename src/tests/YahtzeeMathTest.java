package tests;

import static org.junit.Assert.*;
import java.util.Arrays;
import org.junit.Test;
import util.YahtzeeMath;

public class YahtzeeMathTest {

	@Test
	public void testProb5() {
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++)
			assertEquals("The probabilities are not the same", prob(YahtzeeMath.allRolls[i].length,YahtzeeMath.allRolls[i]), YahtzeeMath.prob(YahtzeeMath.allRolls[i].length, YahtzeeMath.allRolls[i]), 1E-6);
	}
	
	@Test
	public void testProb4() {
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++)
		{
			int[] roll = Arrays.copyOf(YahtzeeMath.allRolls[i], 4);
			assertEquals("The probabilities are not the same", prob(roll.length,roll), YahtzeeMath.prob(roll.length, roll), 1E-6);
		}
	}
	
	@Test
	public void testProb3() {
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++)
		{
			int[] roll = Arrays.copyOf(YahtzeeMath.allRolls[i], 3);
			assertEquals("The probabilities are not the same", prob(roll.length,roll), YahtzeeMath.prob(roll.length, roll), 1E-6);
		}
	}
	
	@Test
	public void testProb2() {
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++)
		{
			int[] roll = Arrays.copyOf(YahtzeeMath.allRolls[i], 2);
			assertEquals("The probabilities are not the same", prob(roll.length,roll), YahtzeeMath.prob(roll.length, roll), 1E-6);
		}
	}
	
	@Test
	public void testProb1() {
		for (int i = 0; i < YahtzeeMath.allRolls.length; i++)
		{
			int[] roll = Arrays.copyOf(YahtzeeMath.allRolls[i], 1);
			assertEquals("The probabilities are not the same", prob(roll.length,roll), YahtzeeMath.prob(roll.length, roll), 1E-6);
		}
	}

	/*
	 * OLD CODE FOR VERIFICATION PURPOSES
	 */
	
	public static double prob5(int[] r)
	{
		//count occurrences
		int[] c = new int[6];
		for (int i = 0; i < 5; i++)
			c[r[i]-1]++;
		//sort them and find prob
		Arrays.sort(c);
		//(A/B) / C
		//Probability = (A/C)
		//Occurences = B
		//Could be rewritten to: (A/C)/B (makes more sense?)
		if 		(c[5] == 5){			return     (6/6.) / 7776.;} /*AAAAA*/
		else if (c[5] == 4){			return  (150/30.) / 7776.;} /*AAAAB*/
		else if (c[5] == 3){
				if (c[4] == 2) {		return  (300/30.) / 7776.;} /*AAABB*/
				else 		   {		return (1200/60.) / 7776.;} /*AAABC*/
		}
		else if (c[5] == 2){
				if (c[4] == 2) {		return (1800/60.) / 7776.;} /*AABBC*/
				else 		   {		return (3600/60.) / 7776.;} /*AABCD*/
		}
		else 				{			return   (720/6.) / 7776.;} /*ABCDE*/
	}
	public static double prob4(int[] r)
	{
		//count occurrences
		int[] c = new int[6];
		for (int i = 0; i < 4; i++)
			c[r[i]-1]++;
		//sort them and find prob
		Arrays.sort(c);
		if (c[5] == 4){ 				return  (6/6.) / 1296.;} /*AAAA*/
		else if (c[5] == 3) { 			return (120/30.) / 1296.;} /*AAAB*/
		else if (c[5] == 2){
			if (c[4] == 2) { 			return (90/15.) / 1296.;} /*AABB*/
			else {						return (720/60.) / 1296.;} /*AABC*/
		}
		else {							return  (360/15.) / 1296.;} /*ABCD*/
	}

	public static double prob3(int[] r)
	{
		//count occurrences
		int[] c = new int[6];
		for (int i = 0; i < 3; i++)
			c[r[i]-1]++;
		//sort them and find prob
		Arrays.sort(c);
		if (c[5] == 3){ 				return  (6/6.) / 216.;} /*AAA*/
		else if (c[5] == 2) { 			return (90/30.) / 216.;} /*AAB*/
		else {							return  (120/20.) / 216.;} /*ABC*/
	}

	public static double prob2(int[] r)
	{
		//count occurrences
		int[] c = new int[6];
		for (int i = 0; i < 2; i++)
			c[r[i]-1]++;
		//sort them and find prob
		Arrays.sort(c);
		if (c[5] == 2){ 				return  (6/6.) / 36.;} /*AA*/
		else {							return  (30/15.) / 36.;} /*AB*/
	}
	public static double prob1(int[] r)
	{
		return 1 / 6.;
	}
	
	public static double prob(int n, int[] roll)
	{
		switch(n){
			case 0: return 1;
			case 1: return prob1(roll);
			case 2: return prob2(roll);
			case 3: return prob3(roll);
			case 4: return prob4(roll);
			case 5: return prob5(roll);
			default:
				throw new IllegalArgumentException("Max n is 5. you gave " + n);
		}
	}
}
