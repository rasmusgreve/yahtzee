package tests;

import static org.junit.Assert.*;
import java.util.Arrays;
import org.junit.Test;
import util.YahtzeeMath;

public class YahtzeeMathTest {

	@Test
	public void testProbs(){
		int[] roll1 = new int[1];
		int[] roll2 = new int[2];
		int[] roll3 = new int[3];
		int[] roll4 = new int[4];
		int[] roll5 = new int[5];
		
		
		int[] counts1 = new int[6];
		int[] counts2 = new int[21];
		int[] counts3 = new int[56];
		int[] counts4 = new int[126];
		int[] counts5 = new int[252];
		
		double totalProb = 0;
		for (int a = 1; a <= 6; a++){
		 for (int b = 1; b <= 6; b++){
		  for (int c = 1; c <= 6; c++){
		   for (int d = 1; d <= 6; d++){
		    for (int e = 1; e <= 6; e++){
		    	roll5 = new int[]{a,b,c,d,e};
		    	int colex = YahtzeeMath.colex(roll5);
		    	counts5[colex]++;
		    }
	    	roll4 = new int[]{a,b,c,d};
	    	int colex = YahtzeeMath.colex(roll4);
	    	counts4[colex]++;
		   }
		    roll3 = new int[]{a,b,c};
	    	int colex = YahtzeeMath.colex(roll3);
	    	counts3[colex]++;
		  }
		    roll2 = new int[]{a,b};
	    	int colex = YahtzeeMath.colex(roll2);
	    	counts2[colex]++;
		 }
	    	roll1 = new int[]{a};
	    	int colex = YahtzeeMath.colex(roll1);
	    	counts1[colex]++;
		}
		    
		
		for (int i = 0; i < counts5.length; i++) {
			double prob = counts5[i]/7776d;
			totalProb += prob;
			assertEquals("The consequences will never be the same!", prob, YahtzeeMath.prob(5, i), 0);
		}
		System.out.println("total prob 5: " + totalProb);
		totalProb = 0;
		
		for (int i = 0; i < counts4.length; i++) {
			double prob = counts4[i]/1296d;
			totalProb += prob;
			assertEquals("The consequences will never be the same!", prob, YahtzeeMath.prob(4, i), 0);
		}
		System.out.println("total prob 4: " + totalProb);
		totalProb = 0;
		
		for (int i = 0; i < counts3.length; i++) {
			double prob = counts3[i]/216d;
			totalProb += prob;
			assertEquals("The consequences will never be the same!", prob, YahtzeeMath.prob(3, i), 0);
		}
		System.out.println("total prob 3: " + totalProb);
		totalProb = 0;
		
		for (int i = 0; i < counts2.length; i++) {
			double prob = counts2[i]/36d;
			totalProb += prob;
			assertEquals("The consequences will never be the same!", prob, YahtzeeMath.prob(2, i), 0);
		}
		System.out.println("total prob 2: " + totalProb);
		totalProb = 0;
		
		for (int i = 0; i < counts1.length; i++) {
			double prob = counts1[i]/6d;
			totalProb += prob;
			assertEquals("The consequences will never be the same!", prob, YahtzeeMath.prob(1, i), 0);
		}
		System.out.println("total prob 1: " + totalProb);
	}
	
	
	
	
	@Test
	public void testColex(){
		int[] roll1 = new int[1];
		int[] roll2 = new int[2];
		int[] roll3 = new int[3];
		int[] roll4 = new int[4];
		int[] roll5 = new int[5];
		
		for (int a = 1; a <= 6; a++){
			 for (int b = 1; b <= 6; b++){
			  for (int c = 1; c <= 6; c++){
			   for (int d = 1; d <= 6; d++){
			    for (int e = 1; e <= 6; e++){
			    	roll5 = new int[]{a,b,c,d,e};

			    	int oColex = YahtzeeMath.colex(roll5);
			    	int colex = YahtzeeMath.colexInit(roll5);
			    	assertEquals(oColex, colex);			    	
			    }
		    	roll4 = new int[]{a,b,c,d};
		    	int oColex = YahtzeeMath.colex(roll4);
		    	int colex = YahtzeeMath.colexInit(roll4);
		    	assertEquals(oColex, colex);	
			   }
			    roll3 = new int[]{a,b,c};
		    	int oColex = YahtzeeMath.colex(roll3);
		    	int colex = YahtzeeMath.colexInit(roll3);
		    	assertEquals(oColex, colex);	
			  }
			    roll2 = new int[]{a,b};
		    	int oColex = YahtzeeMath.colex(roll2);
		    	int colex = YahtzeeMath.colexInit(roll2);
		    	assertEquals(oColex, colex);	
			 }
		    	roll1 = new int[]{a};
		    	int oColex = YahtzeeMath.colex(roll1);
		    	int colex = YahtzeeMath.colexInit(roll1);
		    	assertEquals(oColex, colex);	
			}
	
	}
}
