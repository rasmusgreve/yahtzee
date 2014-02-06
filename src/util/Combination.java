package util;

public class Combination {

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
	
}
