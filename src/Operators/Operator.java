package Operators;

import java.util.HashMap;

import IO.TupleWriter;
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
	
//	/**
//	 * @return size of the relation
//	 */
//	public abstract int getRelationSize();
	/**
	 * obtain string representation of plan
	 * @param depth - depth of current node
	 * @return string representation of plan
	 */
	public abstract String prettyPrint(int depth);
	
	/**
	 * resets operator to a given index
	 * @param index - index to reset to
	 */
	public abstract void reset(int index);
	
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
	
	/**
	 * Repeatedly calls getNextTuple(), printing results using the TupleWriter, until
	 * no more tuples remain
	 */
	public void dump(TupleWriter writer) {
		Tuple t = getNextTuple();
		
		while (t != null) {
			writer.write(t);
			t = getNextTuple();
		}
		writer.finalize();
	}

}
