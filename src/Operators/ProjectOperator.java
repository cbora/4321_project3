package Operators;

import Project.Tuple;

public class ProjectOperator extends Operator {

	private Operator child;

	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}
	
	public Operator getChild() {
		return child;
	}

	public void setChild(Operator child) {
		this.child = child;
	}

}
