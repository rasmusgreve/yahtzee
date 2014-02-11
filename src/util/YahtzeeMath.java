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
	public static double prob5(int[] r)
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
	public static double prob4(int[] r)
	{
		//count occurrences
		int[] c = new int[6];
		for (int i = 0; i < 4; i++)
			c[r[i]-1]++;
		//sort them and find prob
		Arrays.sort(c);
		if (c[5] == 4){ 				return  6 / 1296.;} /*AAAA*/
		else if (c[5] == 3) { 			return 120 / 1296.;} /*AAAB*/
		else if (c[5] == 2){
			if (c[4] == 2) { 			return 90 / 1296.;} /*AABB*/
			else {						return 720 / 1296.;} /*AABC*/
		}
		else {							return  360 / 1296.;} /*ABCD*/
	}

	public static double prob3(int[] r)
	{
		//count occurrences
		int[] c = new int[6];
		for (int i = 0; i < 3; i++)
			c[r[i]-1]++;
		//sort them and find prob
		Arrays.sort(c);
		if (c[5] == 3){ 				return  6 / 216.;} /*AAA*/
		else if (c[5] == 2) { 			return 90 / 216.;} /*AAB*/
		else {							return  120 / 216.;} /*ABC*/
	}

	public static double prob2(int[] r)
	{
		//count occurrences
		int[] c = new int[6];
		for (int i = 0; i < 2; i++)
			c[r[i]-1]++;
		//sort them and find prob
		Arrays.sort(c);
		if (c[5] == 2){ 				return  6 / 36.;} /*AA*/
		else {							return  30 / 36.;} /*AB*/
	}
	public static double prob1(int[] r)
	{
		return 1;
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
		int[] copy = Arrays.copyOf(c, c.length);
		Arrays.sort(copy);
		int index = 0; 
		for (int i = 0 ; i < copy.length ; i++) {
			index += ch[copy[i]+i-1][i + 1];
		}
		return index;
	}
	
	
	
}