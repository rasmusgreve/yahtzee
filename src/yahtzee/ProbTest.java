package yahtzee;

public class ProbTest {

	static double[] p = new double[]{0.5,0.5};
	static double[] m = new double[]{19.5, 99.5};
	static double[] s = new double[]{ 0.5 ,  .5};
	
	public static void main(String[] args) {
		double r = 0;
		int k = 2;
		for (int i = 0 ; i < k ; i++) {
		    r += p[i] * (s[i] * s[i] + m[i] * m[i] * (1 - p[i]));
		    for (int j = i + 1 ; j < k ; j++) {
		        r -= 2 * p[i] * p[j] * m[i] * m[j];
		    }
		}
		r = Math.sqrt(r);
		System.out.println("r: " + r);
	}
}
