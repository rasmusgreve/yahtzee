package util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;

import player.MultiPlayerAI;

public class Persistence {

	public static double[][] loadDoubleArray(String filename, int defaultHeight, int defaultWidth)
	{
		
		timer = System.currentTimeMillis();
		
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
			System.out.println("Load array time: " + (System.currentTimeMillis() - timer));

			
			try {
				ois.close();
				fis.close();
			} catch (Exception e) {}
		}
	}
	
	
	public static double[] cacheFixFunction(String filename)
	{
		
		timer = System.currentTimeMillis();
		
		double[][] readArray = null;
		
		
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(filename);
			ois = new ObjectInputStream(fis);
			readArray = (double[][]) ois.readObject();
			
			double[] fixedArray = new double[2000000];
			
			for (int i = 0; i < readArray.length; i++) {
				fixedArray[i*2] = readArray[i][0];
				fixedArray[i*2 + 1] = readArray[i][1];
			}
			
			return fixedArray;
			
		} catch (Exception e) {
			return null;
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
	
	static long timer = 0;
	
	public static double[] loadArray(String filename, int defaultSize)
	{
		timer = System.currentTimeMillis();
		
		FileInputStream fis = null;
		ObjectInputStream ois = null;
		try {
			fis = new FileInputStream(filename);
			ois = new ObjectInputStream(fis);
			double[] data = new double[defaultSize];
			System.arraycopy(ois.readObject(), 0, data, 0, defaultSize);
			return data;
			
		} catch (Exception e) {
			double[] boardValues = new double[defaultSize];
			Arrays.fill(boardValues, -1);
			return boardValues;
		}
		finally{
			System.out.println("Load array time: " + (System.currentTimeMillis() - timer));
			
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
	
	
	
	public static void main(String[] args) {
		
		for (int i = 0; i < 11; i++) {
			
			double[] d = cacheFixFunction("multiPlayerCache" + i + ".bin");
			
			store(d, "multiPlayerCacheIMPROV" + i + ".bin");
			
			
			System.out.println("done for aggro " + i);
		}
	}
}
