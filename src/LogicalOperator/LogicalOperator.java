package LogicalOperator;

import Operators.PhysicalPlanBuilder;

public abstract class LogicalOperator {

	/**
	 * method for accepting visitor
	 */
	public abstract void accept(PhysicalPlanBuilder ppb);
}
