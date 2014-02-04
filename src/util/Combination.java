package util;

public class Combination {

	/**
	 * Naïve implementation of combination
	 * C(n,k) n choose k
	 * @param n Total elements
	 * @param k Elements to choose
	 * @return n choose k
	 */
	public static int combination(int n, int k)
	{
		return factorial(n) / (factorial(k) * factorial(n-k));
	}
	
	
	/**
	 * Naïve implementation of factorial
	 * @param n
	 * @return
	 */
	public static int factorial(int n)
	{
		if (n == 1) return 1;
		return n * factorial(n-1);
	}
}
