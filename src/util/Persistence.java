package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class Persistence {

	public static double[] loadArray(String filename)
	{
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(filename);
			ois = new ObjectInputStream(fis);
			return (double[]) ois.readObject();
			
		} catch (Exception e) {
			double[] boardValues = new double[1000000];
			Arrays.fill(boardValues, -1);
			return boardValues;
		}
		finally{
			try {
				ois.close();
				fis.close();
			} catch (Exception e) {}
		}
	}
	
	//Save lookup table to persistent medium
	public static void storeArray(double[] data, String filename)
	{
			try {
				FileOutputStream fos = new FileOutputStream(filename);
				ObjectOutputStream oos = new ObjectOutputStream(fos);
				oos.writeObject(data);
				oos.close();
				fos.close();
			} catch (IOException e) {
				System.out.println("WARNING! cache not stored");
			}
	}
}
