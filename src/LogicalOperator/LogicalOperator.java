package LogicalOperator;

import Operators.PhysicalPlanBuilder;

/**
 * Logical operator abstract class
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public abstract class LogicalOperator {

	/**
	 * string representation of plan
	 * @param depth - depth of current node
	 * @return string representation of plan
	 */
	public abstract String prettyPrint(int depth);
	
	/**
	 * method for accepting visitor
	 */
	public abstract void accept(PhysicalPlanBuilder ppb);
}
