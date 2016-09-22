package Operators;

import java.util.HashMap;
import java.util.Set;

import Project.Tuple;

public class JoinOperator extends Operator {
	private Operator leftChild;
	private Operator rightChild;
	private HashMap<String, Integer> schema;
	private Tuple lastLeft;
	
	public JoinOperator(Operator leftChild, Operator rightChild) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		this.schema = new HashMap<String, Integer>();
		this.lastLeft = this.leftChild.getNextTuple();
		
		HashMap<String, Integer> s1 = this.leftChild.getSchema();
		this.schema.putAll(s1); // add all the left child's columns to schema
		
		HashMap<String, Integer> s2 = this.rightChild.getSchema();
		Set<String> keys = s2.keySet();
		// add all right child's columns to schema, but increment index
		// to reflect the fact that they now are located after left child's columns
		for (String key : keys) {
			this.schema.put(key, s2.get(key) + s1.size());
		}
	}
	
	@Override
	public HashMap<String, Integer> getSchema() {
		return this.schema;
	}

	@Override
	public Tuple getNextTuple() {
		Tuple right = this.rightChild.getNextTuple();
		// if rightChild returns null, we should reset it and increment left
		if (right == null) { 
			this.rightChild.reset();
			lastLeft = this.leftChild.getNextTuple();
			right = this.rightChild.getNextTuple();
			// if left null, we've checked all tuples. if right still null, right is empty relation
			if (lastLeft == null || right == null)
				return null;
		}
		return Tuple.concat(lastLeft, right);
	}

	@Override
	public void reset() {
		this.leftChild.reset();
		this.lastLeft = leftChild.getNextTuple();
		this.rightChild.reset();
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
