package Operators;

import Project.Tuple;
import net.sf.jsqlparser.expression.Expression;

/**
 * Operator for joining tuples from two child operators
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class TNLJoinOperator extends JoinOperator {
	
	/* ================================== 
	 * Fields
	 * ================================== */
	private Tuple lastLeft; // left tuple we have seen from leftChild
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param leftChild
	 * @param rightChild
	 * @param exp - expression for the condition we are joining on
	 */
	public TNLJoinOperator(Operator leftChild, Operator rightChild, Expression exp) {
		super(leftChild, rightChild, exp);
		this.lastLeft = this.leftChild.getNextTuple();
	}
	
	/**
	 * Cartesian product constructor - set join condition to null
	 * @param leftChild
	 * @param rightChild
	 */
	public TNLJoinOperator(Operator leftChild, Operator rightChild) {
		this(leftChild, rightChild, null);
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */

	@Override
	public Tuple getNextTuple() {
		Tuple right = this.rightChild.getNextTuple();
		if (lastLeft == null)
			return null;
		// if rightChild returns null, we should reset it and increment left
		if (right == null) { 
			this.rightChild.reset();
			lastLeft = this.leftChild.getNextTuple();
			right = this.rightChild.getNextTuple();
			// if left null, we've checked all tuples. if right still null, right is empty relation
			if (lastLeft == null || right == null)
				return null;
		}
		Tuple result = Tuple.concat(lastLeft, right);
		if (passesCondition(result))
			return result;
		else
			return getNextTuple();
	}

	@Override
	public void reset() {
		this.leftChild.reset();
		this.lastLeft = leftChild.getNextTuple();
		this.rightChild.reset();
	}
	
	@Override
	public void reset(int index){}

}
