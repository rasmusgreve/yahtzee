package util;

import java.util.Arrays;

public class YahtzeeMath {

	static long[][] ch;
	
	//Probability caches
	static double[] prob5 = new double[252];
	static double[] prob4 = new double[126];
	static double[] prob3 = new double[56];
	static double[] prob2 = new double[21];
	static double[] prob1 = new double[6];
	
	static{
		ch = new long[1][0];
		choose(10,5);
		
		for (int a = 1; a <= 6; a++){
			for (int b = a; b <= 6; b++){
				for (int c = b; c <= 6; c++){
					for (int d = c; d <= 6; d++){
						for (int e = d; e <= 6; e++){
							prob5[colex(new int[]{a,b,c,d,e})] = prob5(new int[]{a,b,c,d,e}); 
						}
						prob4[colex(new int[]{a,b,c,d})] = prob4(new int[]{a,b,c,d});
					}
					prob3[colex(new int[]{a,b,c})] = prob3(new int[]{a,b,c});
				}
				prob2[colex(new int[]{a,b})] = prob2(new int[]{a,b});
			}
			prob1[colex(new int[]{a})] = prob1(new int[]{a});
		}
	}
	
	//assumes 5d6
	private static double prob5(int[] r)
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
	private static double prob4(int[] r)
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

	private static double prob3(int[] r)
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

	private static double prob2(int[] r)
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
	private static double prob1(int[] r)         
	{                                            
		return 1 / 6.;                           
	}                                            
	public static double prob(int n, int[] roll) 
	{
		switch(n){
			case 0: return 1;
			case 1: return prob1[colex(roll)];
			case 2: return prob2[colex(roll)];
			case 3: return prob3[colex(roll)];
			case 4: return prob4[colex(roll)];
			case 5: return prob5[colex(roll)];
			default:
				throw new IllegalArgumentException("Max n is 5. you gave " + n);
		}
	}
	
	
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