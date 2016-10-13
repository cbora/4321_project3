package LogicalOperator;

import net.sf.jsqlparser.expression.Expression;

public class SelectLogicalOperator extends LogicalOperator {
	
	private LogicalOperator child;
	private Expression ex;
	
	public SelectLogicalOperator(LogicalOperator child, Expression ex) {
		this.child = child;
		this.ex = ex;
	}

	public LogicalOperator getChild() {
		return child;
	}

	public void setChild(LogicalOperator child) {
		this.child = child;
	}

	public Expression getEx() {
		return ex;
	}

	public void setEx(Expression ex) {
		this.ex = ex;
	}
	
	public void accept(PhysicalPlanBuilder ppb){
		ppb.visit(this);	
	}
}
