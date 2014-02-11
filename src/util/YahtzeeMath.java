package util;

import java.util.Arrays;

public class YahtzeeMath {

	static{
		choose(10,5);
	}
	
	/**
	 * Naïve implementation of combination
	 * C(n,k) n choose k
	 * @param n Total elements
	 * @param k Elements to choose
	 * @return n choose k
	 */
	public static long combination(long n, long k)
	{
		return factorial(n) / (factorial(k) * factorial(n-k));
	}
	
	
	/**
	 * Naïve implementation of factorial
	 * @param n
	 * @return
	 */
	public static long factorial(long n)
	{
		if (n <= 1L) return 1;
		return n * factorial(n-1);
	}
	
	//assumes 5d6
	public static double prob(int[] r)
	{
		//count occurrences
		int[] c = new int[6];
		for (int i = 0; i < 5; i++)
			c[r[i]-1]++;
		//sort them and find prob
		Arrays.sort(c);
		if 		(c[5] == 5){			return    6 / 7776.;} /*AAAAA*/
		else if (c[5] == 4){			return  150 / 7776.;} /*AAAAB*/
		else if (c[5] == 3){
				if (c[4] == 2) {		return  300 / 7776.;} /*AAABB*/
				else 		   {		return 1200 / 7776.;} /*AAABC*/
		}
		else if (c[5] == 2){
				if (c[4] == 2) {		return 1800 / 7776.;} /*AABBC*/
				else 		   {		return 3600 / 7776.;} /*AABCD*/
		}
		else 				{			return  720 / 7776.;} /*ABCDE*/
	}
	
	
	static long[][] ch = new long[1][0];
	public static long choose(int ii, int jj) { // Access method with init check
		if (ii >= ch.length || jj >= ch[0].length) {
			ch = new long[Math.max(ch.length, ii + 1)][Math.max(ch[0].length, jj + 1)];
			for (int i = 0 ; i < ch.length ; i++) ch[i][0] = 1;
			for (int j = Math.min(ch.length, ch[0].length) - 1 ; j > 0 ; j--) ch[j][j] = 1;
			for (int i = 2 ; i < ch.length ; i++) 
				for (int j = Math.min(i, ch[0].length - 1) ; j > 0 ; j--)
					ch[i][j] = ch[i - 1][j] + ch[i - 1][j - 1];
		}
		return ch[ii][jj];
	}
	
	public static int colex(int[] c) {
		Arrays.sort(c);
		int index = 0; 
		for (int i = 0 ; i < c.length ; i++) {
			index += ch[c[i]+i-1][i + 1];
		}
		return index;
	}
	
	
	
}