package Operators;

import Project.Tuple;
import net.sf.jsqlparser.expression.Expression;

public class BNLJoinOperator extends JoinOperator {
	
	/* ================================== 
	 * Fields
	 * ================================== */
	private static int PAGE_SIZE = 4096; // bytes per page
	private static int INT_SIZE = Integer.SIZE / Byte.SIZE; // bytes per int
	private Tuple[] buffer; // tuple buffer
	private int bIndex; // position in tuple buffer
	
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
		this.buffer = new Tuple[(bSize * PAGE_SIZE) / (INT_SIZE * schema.size())];
		this.bIndex = 0;
		fillBuffer();
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
		Tuple right = rightChild.getNextTuple();
		
		// if reached end of right relation, reset right and increment bIndex
		if (right == null) {
			rightChild.reset();
			right = rightChild.getNextTuple();
			
			// if right is still null, then right relation is empty, so return null
			if (right == null)
				return null;
			
			bIndex++;
		}
		
		// if reached end of buffer, refill buffer
		if (bIndex >= buffer.length) {
			bIndex = 0;
			fillBuffer();
		}
		
		Tuple left = buffer[bIndex];

		// if current spot in the buffer is null, we have looked at all tuples in left relation. Return null.
		if (left == null) {
			return null;
		}
		
		// concatenate tuple from left relation with tuple from right relation
		Tuple result = Tuple.concat(left, right);
		
		// return result if it passes join condition. otherwise call getNextTuple() again
		return passesCondition(result) ? result : getNextTuple();
	}

	@Override
	public void reset() {
		leftChild.reset();
		rightChild.reset();
		bIndex = 0;
		fillBuffer();
	}

	/**
	 * Fills buffer with next round of tuples from left child
	 */
	private void fillBuffer() {
		Tuple t;
		int i = 0;
		
		for (; i < buffer.length; i++) {
			if ((t = leftChild.getNextTuple()) == null)
				break;
			buffer[i] = t;
		}
		for (; i < buffer.length; i++) {
			buffer[i] = null;
		}
	}
}
