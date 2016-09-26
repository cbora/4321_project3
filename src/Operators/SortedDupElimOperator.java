package Operators;

import java.util.HashMap;

import Project.Tuple;

public class SortedDupElimOperator extends Operator {
	
	private Operator child; // child operator
	private HashMap<String, Integer> schema; // schema of tuples returned by operator
	private Tuple lastUnique; // next tuple to be returned by getNextTuple
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	public SortedDupElimOperator(Operator child) {
		this.child = child;
		this.schema = this.child.getSchema();
		this.lastUnique = this.child.getNextTuple();
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	@Override
	public HashMap<String, Integer> getSchema() {
		return this.schema;
	}

	@Override
	public Tuple getNextTuple() {
		Tuple temp = this.lastUnique;
		while (this.lastUnique != null && this.lastUnique.equals(temp)) {
			this.lastUnique = this.child.getNextTuple();
		}
		return temp;
	}

	@Override
	public void reset() {
		this.child.reset();
		this.lastUnique = this.child.getNextTuple();
	}

	@Override
	public void close() {
		this.child.close();
	}

}
