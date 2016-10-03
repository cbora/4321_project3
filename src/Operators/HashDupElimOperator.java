package Operators;

import java.util.HashMap;
import java.util.HashSet;

import Project.Tuple;
/**
 * Operator for eliminating duplicates from unsorted data
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class HashDupElimOperator extends Operator {

	/* ================================== 
	 * Fields
	 * ================================== */
	private Operator child;	// child operator in operator tree
	private HashMap<String, Integer> schema; // schema of tuples returned by this operator
	private HashSet<Tuple> seenTuples;	// hash set of tuples we have encountered already
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param child - child node for operator
	 */
	public HashDupElimOperator(Operator child) {
		this.child = child;
		this.schema = this.child.getSchema();
		this.seenTuples = new HashSet<Tuple>();
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
		Tuple t = this.child.getNextTuple();
		if (t == null) {
			return null;
		}
		else if (this.seenTuples.contains(t)) { // if tuple has been seen, try again
			return getNextTuple();
		}
		else {
			this.seenTuples.add(t);
			return t;
		}
	}

	@Override
	public void reset() {
		this.seenTuples = new HashSet<Tuple>(); // clear set of seen tuples
		this.child.reset();
	}
	
	@Override
	public void close() {
		this.child.close();
	}	
	
}
