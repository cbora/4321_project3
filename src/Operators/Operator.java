package Operators;

import java.util.HashMap;

import Project.Tuple;

public abstract class Operator {
	public abstract Tuple getNextTuple();
	
	public abstract void reset();
	
	public abstract HashMap<String, Integer> getSchema();
	
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
	
	public abstract void close();

}
