package Operators;

import java.util.HashMap;

import Project.Tuple;
import net.sf.jsqlparser.expression.Expression;

public class BNLJoinOperator extends JoinOperator {
	
	/* ================================== 
	 * Fields
	 * ================================== */
	private static int PAGE_SIZE = 4096; // bytes per page
	private static int INT_SIZE = Integer.SIZE / Byte.SIZE; // bytes per int
	private Tuple[] buffer; // tuple buffer
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param leftChild
	 * @param rightChild
	 * @param exp - expression for the condition we are joining on
	 * @param bSize - buffer size in pages
	 */
	public BNLJoinOperator(Operator leftChild, Operator rightChild, Expression exp, int bSize) {
		super(leftChild, rightChild, exp);
		buffer = new Tuple[(bSize * PAGE_SIZE) / (INT_SIZE * schema.size())];
	}
	
	/**
	 * Cartesian product constructor - set join condition to null
	 * @param leftChild
	 * @param rightChild
	 * @param bSize - buffer siz in pages
	 */
	public BNLJoinOperator(Operator leftChild, Operator rightChild, int bSize) {
		this(leftChild, rightChild, null, bSize);
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

	/**
	 * Fills buffer with next round of tuples from left child
	 */
	private void fillBuffer() {
		Tuple t;
		int i = 0;
		
		while ((t = leftChild.getNextTuple()) != null && i < buffer.length) {
			buffer[i] = t;
			i++;
		}
		for (; i < buffer.length; i++) {
			buffer[i] = null;
		}
	}
}
