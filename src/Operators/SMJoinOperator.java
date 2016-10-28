package Operators;

import java.util.HashMap;

import Project.Tuple;
import net.sf.jsqlparser.expression.Expression;

public class SMJoinOperator extends JoinOperator {

	/* ================================== 
	 * Fields
	 * ================================== */
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param leftChild
	 * @param rightChild
	 * @param exp - expression for the condition we are joining on
	 */
	public SMJoinOperator(Operator leftChild, Operator rightChild, Expression exp) {
		super(leftChild, rightChild, exp);
	}
	
	/**
	 * Cartesian product constructor - set join condition to null
	 * @param leftChild
	 * @param rightChild
	 */
	public SMJoinOperator(Operator leftChild, Operator rightChild) {
		this(leftChild, rightChild, null);
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

}
