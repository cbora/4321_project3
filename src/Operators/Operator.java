package Operators;

import java.util.HashMap;

import Project.Tuple;

public abstract class Operator {
	
	/**
	 * Retrieves schema of tuples that getNextTuple returns
	 * @return HashMap that maps columns to their position in the tuples returned
	 * 			by getNextTuple. A column is represented as <alias> + "." + <colName>
	 */
	public abstract HashMap<String, Integer> getSchema();
	
	/**
	 * @return next tuple if it exists, null otherwise
	 */
	public abstract Tuple getNextTuple();
	
	/**
	 * resets getNextTuple() so that it reads from the beginning
	 */
	public abstract void reset();
	
	/**
	 * closes any open streams
	 */
	public abstract void close();
	
	/**
	 * Repeatedly calls getNextTuple(), printing the results, until
	 * no more tuple remain
	 */
	public void dump() {
		Tuple t = getNextTuple();
		while (t != null) {
			System.out.println(t);
			t = getNextTuple();
		}
	}

}
