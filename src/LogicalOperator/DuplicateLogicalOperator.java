/**
 * 
 */
package LogicalOperator;

import Operators.PhysicalPlanBuilder;

/**
 * Logical operator for duplicate elimination
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */

public class DuplicateLogicalOperator extends LogicalOperator {

	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	private LogicalOperator child; // child of operator
	
	/*
	 * ================================== 
	 * Constructors
	 * ==================================
	 */
	/**
	 * Constructor
	 * @param child - child of operator
	 */
	public DuplicateLogicalOperator(LogicalOperator child) {
		this.child = child;
	}
	
	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */
	/**
	 * @return child
	 */
	public LogicalOperator getChild() {
		return child;
	}
	
	/**
	 * sets child
	 * @param child - new child
	 */
	public void setChild(LogicalOperator child) {
		this.child = child;
	}
	
	public String prettyPrint(int depth) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < depth; i++) 
			sb.append("-");
		sb.append("DupElim\n");
		
		sb.append(this.child.prettyPrint(depth + 1));		
		return sb.toString();
	}

	@Override
	public void accept(PhysicalPlanBuilder ppb){
		ppb.visit(this);	
	}
}
