package IO;

import java.util.Random;

import Project.Tuple;

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
	 * Fields
	 * ==================================
	 */
	private String output; // name of output file we are writing to
	private int numTuples; // number of tuples we want to generate
	private int numCols; // number of columns these tuples chould have
	
	/*
	 * ================================== 
	 * Constructor
	 * ==================================
	 */
	/**
	 * Constructor
	 * @param output - name of output file
	 * @param numTuples - number of tuples we want in file
	 * @param numCols - number of columns these tuples should have
	 */
	public RandomTupleGenerator(String output, int numTuples, int numCols) {
		this.output = output;
		this.numTuples = numTuples;
		this.numCols = numCols;
	}
	
	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */
	/**
	 * generates the file
	 */
	public void genTuples() {
		BinaryTupleWriter writer = new BinaryTupleWriter(output);
		Random rand = new Random();
		
		for (int i = 0; i < numTuples; i++) {
			Tuple t = new Tuple(numCols);
			t.add(i, 0);
			for (int j = 1; j < numCols; j++) {
				t.add(rand.nextInt(1000), j);
			}
			writer.write(t);
		}
		
		writer.finalize();
		writer.close();
	}
}
