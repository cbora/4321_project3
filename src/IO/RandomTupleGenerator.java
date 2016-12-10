package IO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import Project.Tuple;
import Project.TupleComparator;

/**
 * Generates a file of random tuples
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */

public class RandomTupleGenerator {
	
	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */
	
	/**
	 * generates file of tuples
	 * @param output - name of output file
	 * @param numTuples - number tuples to generate
	 * @param numCols - number of columns to give each tuple
	 */
	public static void genTuples(String output, int numTuples, int[] cols) {
		BinaryTupleWriter writer = new BinaryTupleWriter(output);
		Random rand = new Random();
		ArrayList<Tuple> holder = new ArrayList<Tuple>();
		
		for (int i = 0; i < numTuples; i++) {
			Tuple t = new Tuple(cols.length + 1);
			t.add(i, 0);
			for (int j = 0; j < cols.length; j++) {
				t.add(rand.nextInt(cols[j]), j + 1);
			}
			holder.add(t);
		}
		
		Collections.shuffle(holder);
		
		for (int i = 0; i < holder.size(); i++)
			writer.write(holder.get(i));
		writer.finalize();
		writer.close();
	}
	
	/**
	 * generates file of tuples
	 * @param output - name of output file
	 * @param numTuples - number tuples to generate
	 * @param numCols - number of columns to give each tuple
	 */
	public static void genTuples(String output, int numTuples, int numCols) {
		BinaryTupleWriter writer = new BinaryTupleWriter(output);
		Random rand = new Random();
		ArrayList<Tuple> holder = new ArrayList<Tuple>();
		
		for (int i = 0; i < numTuples; i++) {
			Tuple t = new Tuple(numCols);
			t.add(i, 0);
			for (int j = 1; j < numCols; j++) {
				t.add(rand.nextInt(1000), j);
			}
			holder.add(t);
		}
		
		Collections.shuffle(holder);
		
		for (int i = 0; i < holder.size(); i++)
			writer.write(holder.get(i));
		writer.finalize();
		writer.close();
	}
	
	/**
	 * generates sorted file of tuples with leftmost columns given priority of rightmost columns
	 * @param output - name of output file
	 * @param numTuples - number tuples to generate
	 * @param numCols - number of columns to give each tuple
	 */
	public static void genSortedTuples(String output, int numTuples, int numCols, int[] order) {
		ArrayList<Tuple> tuples = new ArrayList<Tuple>();
		Random rand = new Random();
		
		for (int i = 0; i < numTuples; i++) {
			Tuple t = new Tuple(numCols);
			t.add(i, 0);
			for (int j = 1; j < numCols; j++) {
				t.add(rand.nextInt(1000), j);
			}
			tuples.add(t);
		}
		
		Collections.sort(tuples, new TupleComparator(order)); 
		
		BinaryTupleWriter writer = new BinaryTupleWriter(output);
		for (int i = 0; i < tuples.size(); i++) {
			writer.write(tuples.get(i));
		}
		writer.finalize();
		writer.close();
	}
}
