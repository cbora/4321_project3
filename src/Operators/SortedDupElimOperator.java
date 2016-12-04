package Operators;

import java.util.HashMap;

import Project.Tuple;

/**
 * Operator for eliminating duplicated from input that is sorted
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class SortedDupElimOperator extends Operator {
	
	/* ================================== 
	 * Fields
	 * ================================== */
	private Operator child; // child operator in operator tree
	private HashMap<String, Integer> schema; // schema of tuples returned by operator
	private Tuple nextUnique; // next tuple to be returned by getNextTuple
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param child
	 */
	public SortedDupElimOperator(Operator child) {
		this.child = child;
		this.schema = this.child.getSchema();
		this.nextUnique = this.child.getNextTuple();
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
		Tuple temp = this.nextUnique;
		while (this.nextUnique != null && this.nextUnique.equals(temp)) {
			this.nextUnique = this.child.getNextTuple();
		}
		return temp;
	}

	@Override
	public void reset() {
		this.child.reset();
		this.nextUnique = this.child.getNextTuple();
	}

	@Override
	public void close() {
		this.child.close();
	}
	
	public int getRelationSize() {
		return this.child.getRelationSize();
	}
	
	@Override
	public void reset(int index){}

}
