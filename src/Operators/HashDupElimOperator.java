package Operators;

import java.util.HashMap;
import java.util.HashSet;

import Project.Tuple;

public class HashDupElimOperator extends Operator {

	private Operator child;
	private HashMap<String, Integer> schema;
	private HashSet<Tuple> seenTuples;
	
	public HashDupElimOperator(Operator child) {
		this.child = child;
		this.schema = this.child.getSchema();
		this.seenTuples = new HashSet<Tuple>();
	}
	
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
		else if (this.seenTuples.contains(t)) {
			return getNextTuple();
		}
		else {
			this.seenTuples.add(t);
			return t;
		}
	}

	@Override
	public void reset() {
		this.seenTuples = new HashSet<Tuple>();
		this.child.reset();
	}
	
	@Override
	public void close() {
		this.child.close();
	}	
	
}
