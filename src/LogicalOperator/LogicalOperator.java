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

	public abstract String prettyPrint();
	
	/**
	 * method for accepting visitor
	 */
	public abstract void accept(PhysicalPlanBuilder ppb);
}
