package Operators;

import Project.Tuple;

public abstract class Operator {
	public abstract Tuple getNextTuple();
	
	public abstract void reset();
	
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
