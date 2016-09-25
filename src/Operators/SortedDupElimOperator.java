package Operators;

import java.util.HashMap;

import Project.Tuple;

public class SortedDupElimOperator extends Operator {
	
	private Operator child;
	private HashMap<String, Integer> schema;

	public SortedDupElimOperator(Operator child) {
		this.child = child;
		this.schema = this.child.getSchema();
	}
	
	@Override
	public HashMap<String, Integer> getSchema() {
		return this.schema;
	}

	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() {
		this.child.reset();
	}

	@Override
	public void close() {
		this.child.close();
	}

}
