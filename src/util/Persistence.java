package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

public class Persistence {

	public static double[][] loadDoubleArray(String filename, int defaultHeight, int defaultWidth)
	{
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(filename);
			ois = new ObjectInputStream(fis);
			return (double[][]) ois.readObject();
			
		} catch (Exception e) {
			double[][] boardValues = new double[defaultHeight][];
			for (int i = 0; i < defaultHeight; i++)
			{
				boardValues[i] = new double[]{-1,-1};
			}
			return boardValues;
		}
		finally{
			try {
				ois.close();
				fis.close();
			} catch (Exception e) {}
		}
	}
	
	public static void storeDoubleArray(double[][] data, String filename)
	{
		store(data,filename);
	}
	
	public static double[] loadArray(String filename, int defaultSize)
	{
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(filename);
			ois = new ObjectInputStream(fis);
			return (double[]) ois.readObject();
			
		} catch (Exception e) {
			double[] boardValues = new double[defaultSize];
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
		store(data,filename);
	}
	
	private static void store(Object data, String filename){
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
