package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class YahtzeeMath {

	static long[][] ch;
	
	//Probability caches
//	static double[] prob5 = new double[252];
//	static double[] prob4 = new double[126];
//	static double[] prob3 = new double[56];
//	static double[] prob2 = new double[21];
//	static double[] prob1 = new double[6];
	static double[] prob5 = new double[252];
	static double[] prob4 = new double[252];
	static double[] prob3 = new double[252];
	static double[] prob2 = new double[252];
	static double[] prob1 = new double[252];

	
	public static int[][] allRolls = new int[252][];
	
	public static int[][] allRolls5 = new int[252][];
	public static int[][] allRolls4 = new int[126][];
	public static int[][] allRolls3 = new int[56][];
	public static int[][] allRolls2 = new int[21][];
	public static int[][] allRolls1 = new int[6][];
	
	static int[] specialToColexLookup = new int[6*6*6*6*6*6];
	final static int[] multTable = new int[] {
		0, // 0 ignore
		0, // 1 dont count
		1, // 2's
		6, // 3's etc..
		6*6,
		6*6*6,
		6*6*6*6,
		6*6*6*6*6 // dice count
	};
	static {

		
		
		int i = 0;
		for (int a = 1; a <= 6; a++)
		 for (int b = a; b <= 6; b++)
		  for (int c = b; c <= 6; c++)
		   for (int d = c; d <= 6; d++)
		    for (int e = d; e <= 6; e++)
		    {
		    	allRolls[i++] = new int[]{a,b,c,d,e};
		    }

		int j = 0, k=0,l=0,m=0,n=0;
		for (int a = 1; a <= 6; a++){
		 for (int b = a; b <= 6; b++){
		  for (int c = b; c <= 6; c++){
		   for (int d = c; d <= 6; d++){
		    for (int e = d; e <= 6; e++)
		    {
		    	allRolls5[j++] = new int[]{a,b,c,d,e};
		    }
		    allRolls4[k++] = new int[]{a,b,c,d};
		   }
		   allRolls3[l++] = new int[]{a,b,c};
		  }
		  allRolls2[m++] = new int[]{a,b};
		 }
		 allRolls1[n++] = new int[]{a};
		}
		

		ch = new long[1][0];
		choose(10,5);
		
		for (int a = 1; a <= 6; a++){
			for (int b = a; b <= 6; b++){
				for (int c = b; c <= 6; c++){
					for (int d = c; d <= 6; d++){
						for (int e = d; e <= 6; e++){
							prob5[colexInit(new int[]{a,b,c,d,e})] = prob5(new int[]{a,b,c,d,e}); 
						}
						prob4[colexInit(new int[]{a,b,c,d})] = prob4(new int[]{a,b,c,d});
					}
					prob3[colexInit(new int[]{a,b,c})] = prob3(new int[]{a,b,c});
				}
				prob2[colexInit(new int[]{a,b})] = prob2(new int[]{a,b});
			}
			prob1[colexInit(new int[]{a})] = prob1(new int[]{a});
		}
		
		
		
		for (int a = 1; a <= 6; a++){
			for (int b = a; b <= 6; b++){
				for (int c = b; c <= 6; c++){
					for (int d = c; d <= 6; d++){
						for (int e = d; e <= 6; e++){
							int colex = colexInit(new int[]{a,b,c,d,e});
							int special = convertToSpecial(new int[]{a,b,c,d,e});
							specialToColexLookup[special] = colex;
						}
						int colex = colexInit(new int[]{a,b,c,d});
						int special = convertToSpecial(new int[]{a,b,c,d});
						specialToColexLookup[special] = colex;
					}
					int colex = colexInit(new int[]{a,b,c});
					int special = convertToSpecial(new int[]{a,b,c});
					specialToColexLookup[special] = colex;
				}
				int colex = colexInit(new int[]{a,b});
				int special = convertToSpecial(new int[]{a,b});
				specialToColexLookup[special] = colex;
			}
			int colex = colexInit(new int[]{a});
			int special = convertToSpecial(new int[]{a});
			specialToColexLookup[special] = colex;
		}
	}
	
	public static int rollNumber(int n){
		switch (n) {
		case 5:
			return 252;
		case 4:
			return 126;
		case 3:
			return 56;
		case 2:
			return 21;
		case 1:
			return 6;
		}
		return 0;
		
	}

	public static int[][] allRolls(int n){
		switch (n) {
		case 5:
			return allRolls5;
		case 4:
			return allRolls4;
		case 3:
			return allRolls3;
		case 2:
			return allRolls2;
		case 1:
			return allRolls1;
		}
		return null;
		
	}
	
	private static int convertToSpecial(int[] r){
		int result = 0;
		
		
		for(int i = 0; i < r.length; i++) {
			result += multTable[r[i]];
		}
		result += (r.length-1) * multTable[multTable.length-1];
		
		return result;
//		
//		int[] rollC = new int[5];
//		
//		for (int i = 0; i < r.length; i++) {
//			if (r[i] == 1) continue;
//			rollC[r[i]-2]++;
//		}
//		
//		int idx = rollC[0] | rollC[1]<<3 | rollC[2]<<6 | rollC[3]<<9 | rollC[4]<<12 | r.length<<15;
//		return idx;
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
		
	
	static long timer = 0;

	
	public static int colexInit(int[] c){
		int[] copy = Arrays.copyOf(c, c.length);
		Arrays.sort(copy);

		int index = 0; 
		for (int i = 0 ; i < copy.length ; i++) {
			index += ch[copy[i]+i-1][i + 1];
		}	
		return index;
	}
	
	public static int colex(int[] c) {	
		
//		long t = System.nanoTime();
		
		int special = convertToSpecial(c);
		int index = specialToColexLookup[special];
		
		
		
//		int[] copy = Arrays.copyOf(c, c.length);
//		Arrays.sort(copy);
//
//		int index = 0; 
//		for (int i = 0 ; i < copy.length ; i++) {
//			index += ch[copy[i]+i-1][i + 1];
//		}
//		timer += System.nanoTime() - t; 
		
				
		return index;
	}
	
	public static void printout(){
		System.out.println("t colex: " + timer) ;
	}
	
	
	static int[] colexLookup;
	

	
	
		
	
}