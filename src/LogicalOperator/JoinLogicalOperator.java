package LogicalOperator;

import Operators.PhysicalPlanBuilder;
import net.sf.jsqlparser.expression.Expression;

/**
 * Logical operator for joins
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class JoinLogicalOperator extends LogicalOperator {

	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	private LogicalOperator left; // left child
	private LogicalOperator right; // right child
	private Expression exp; // join expression
	
	/*
	 * ================================== 
	 * Constructors
	 * ==================================
	 */
	/**
	 * Constructor
	 * @param child1 - left child
	 * @param child2 - right child
	 * @param exp - join expression
	 */
	public JoinLogicalOperator(LogicalOperator left, LogicalOperator right, Expression exp) {
		this.left = left;
		this.right = right;
		this.exp = exp;
	}

	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */
	/**
	 * @return left child
	 */
	public LogicalOperator getLeft() {
		return left;
	}

	/**
	 * @param child - new left child
	 */
	public void setLeft(LogicalOperator child) {
		this.left = child;
	}

	/**
	 * @return right child
	 */
	public LogicalOperator getRight() {
		return right;
	}

	/**
	 * @param child - new right child
	 */
	public void setRight(LogicalOperator child) {
		this.right = child;
	}

	/**
	 * @return - join expression
	 */
	public Expression getExp() {
		return exp;
	}
	
	@Override
	public void accept(PhysicalPlanBuilder ppb){
		ppb.visit(this);	
	}
}
