package Operators;

import Project.Tuple;
import net.sf.jsqlparser.expression.Expression;

public class BNLJoinOperator extends JoinOperator {
	
	/* ================================== 
	 * Fields
	 * ================================== */
	private static final int PAGE_SIZE = 4096; // bytes per page
	private static final int INT_SIZE = Integer.SIZE / Byte.SIZE; // bytes per int
	private Tuple[] buffer; // tuple buffer
	private int bIndex; // position in tuple buffer
	private Tuple lastRight; // last tuple returned by rightChild
	private boolean isEmpty; // flags if either relation is empty
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param leftChild
	 * @param rightChild
	 * @param exp - expression for the condition we are joining on
	 * @param bSize - buffer size in pages (must be >0 otherwise defaults to 1)
	 */
	public BNLJoinOperator(Operator leftChild, Operator rightChild, Expression exp, int bSize) {
		super(leftChild, rightChild, exp);
		
		bSize = (bSize > 0) ? bSize : 1;
		this.buffer = new Tuple[(bSize * PAGE_SIZE) / (INT_SIZE * schema.size())];
		this.bIndex = 0;
		fillBuffer();
		this.lastRight = this.rightChild.getNextTuple();
		this.isEmpty = this.lastRight == null || buffer[0] == null;
	}
	
	/**
	 * Cartesian product constructor - set join condition to null
	 * @param leftChild
	 * @param rightChild
	 * @param bSize - buffer size in pages (must be >0 otherwise defaults to 1)
	 */
	public BNLJoinOperator(Operator leftChild, Operator rightChild, int bSize) {
		this(leftChild, rightChild, null, bSize);
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	@Override
	public Tuple getNextTuple() {
		// if either relation is empty, return null
		if (isEmpty)
			return null;
		
		// If reached end of buffer or reached null Tuple, increment lastRight and proceed
		if (bIndex >= buffer.length || buffer[bIndex] == null) {
			// increment lastRight
			lastRight = rightChild.getNextTuple();
			
			// if current spot in buffer is null and lastRight is null, we are done
			if (bIndex < buffer.length && buffer[bIndex] == null && lastRight == null) {
				return null;
			}
			// else if lastRight is null, we have finished with the current buffer. Refill buffer.
			else if (lastRight == null) {
				rightChild.reset();
				lastRight = rightChild.getNextTuple();
				
				fillBuffer();
				
				// if new buffer is empty, return null
				if (buffer[0] == null)
					return null;
			}
			
			// reset to start of buffer
			bIndex = 0;
		}
						
		Tuple result = Tuple.concat(buffer[bIndex], lastRight);
		bIndex++;
		
		return passesCondition(result) ? result : getNextTuple();
	}

	@Override
	public void reset() {
		leftChild.reset();
		rightChild.reset();
		lastRight = rightChild.getNextTuple();
		bIndex = 0;
		fillBuffer();
	}
	
	@Override
	public void reset(int index){}

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
