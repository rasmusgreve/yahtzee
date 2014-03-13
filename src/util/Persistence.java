package util;

import java.io.*;
import java.util.Arrays;

public class Persistence {
	
	/**
	 * Load a double array from a file on disc
	 * If the file cannot be found a default array filled with -1's will be returned
	 * @param filename The file to load
	 * @param size The size of the array to load
	 * @return The double array from the specified file or an array of -1's with length size if file not found/invalid 
	 */
	public static double[] loadArray(String filename, int size)
	{
		double[] data = new double[size];
		ObjectInputStream inputStream = null;
		try {
			inputStream = new ObjectInputStream(new FileInputStream(filename));	
			System.arraycopy(inputStream.readObject(), 0, data, 0, size);
			inputStream.close();
		} catch (Exception e) {
			Arrays.fill(data, -1);
		}
		return data;
	}
	
	/**
	 * Store a double array to file on disc
	 * @param data The data to write to disc
	 * @param filename The name of the file to write
	 */
	public static void storeArray(double[] data, String filename)
	{
		try {
			ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(filename));
			outputStream.writeObject(data);
			outputStream.close();
		} catch (IOException e) {
			System.out.println("WARNING! Cache might not be stored!");
		}
	}
	
}
