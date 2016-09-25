package Operators;

import java.util.HashMap;

import Project.Tuple;

public class SortOperator extends Operator {

	private Operator child;
	
	
	@Override
	public HashMap<String, Integer> getSchema() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void close() {
		child.close();
	}

}
