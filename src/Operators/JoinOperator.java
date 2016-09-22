package Operators;

import java.util.HashMap;

import Project.Tuple;

public class JoinOperator extends Operator {
	private Operator leftChild;
	private Operator rightChild;

	
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
		leftChild.close();
		rightChild.close();
	}
	
	public Operator getLeftChild() {
		return leftChild;
	}

	public void setLeftChild(Operator leftChild) {
		this.leftChild = leftChild;
	}

	public Operator getRightChild() {
		return rightChild;
	}

	public void setRightChild(Operator rightChild) {
		this.rightChild = rightChild;
	}

}
