/**
 * 
 */
package LogicalOperator;

/**
 * @author cbora
 *
 */
public class DuplicateLogicalOperator extends LogicalOperator {

	/**
	 * 
	 */
	private LogicalOperator child;
	public DuplicateLogicalOperator(LogicalOperator child) {
		// TODO Auto-generated constructor stub
		this.child = child;
	}
	public LogicalOperator getChild() {
		return child;
	}
	public void setChild(LogicalOperator child) {
		this.child = child;
	}

	public void accept(PhysicalPlanBuilder ppb){
		ppb.visit(this);	
	}
}
