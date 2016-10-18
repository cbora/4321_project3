package LogicalOperator;

public abstract class LogicalOperator {

	/**
	 * method for accepting visitor
	 */
	public abstract void accept(PhysicalPlanBuilder ppb);
}
