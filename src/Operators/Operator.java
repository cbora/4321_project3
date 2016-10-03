package Operators;

import java.util.HashMap;

import Project.Tuple;

/**
 * Operator abstract class
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public abstract class Operator {
	
	/**
	 * Retrieves schema of tuples that getNextTuple returns
	 * @return HashMap that maps columns to their position in the tuples returned
	 * 			by getNextTuple. A column is represented as (alias) + "." + (colName)
	 * 			if alias exists, (table name) + "." + (colName) otherwise
	 */
	public abstract HashMap<String, Integer> getSchema();
	
	/**
	 * @return next tuple if it exists, null at eof, undefined if call again after reaching eof
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
	 * no more tuples remain
	 */
	public void dump() {
		Tuple t = getNextTuple();
		while (t != null) {
			System.out.println(t);
			t = getNextTuple();
		}
	}

}
