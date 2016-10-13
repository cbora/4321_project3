package LogicalOperator;

import net.sf.jsqlparser.expression.Expression;

public class JoinLogicalOperator extends LogicalOperator {

	private LogicalOperator child1;
	private LogicalOperator child2;
	private Expression exp;
	
	public JoinLogicalOperator(LogicalOperator child1, LogicalOperator child2, Expression exp) {
		this.child1 = child1;
		this.child2 = child2;
		this.exp = exp;
	}

	public LogicalOperator getChild1() {
		return child1;
	}

	public void setChild1(LogicalOperator child1) {
		this.child1 = child1;
	}

	public LogicalOperator getChild2() {
		return child2;
	}

	public void setChild2(LogicalOperator child2) {
		this.child2 = child2;
	}

	public Expression getExp() {
		return exp;
	}

	public void setExp(Expression exp) {
		this.exp = exp;
	}
	public void accept(PhysicalPlanBuilder ppb){
		ppb.visit(this);	
	}
}
