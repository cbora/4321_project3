package LogicalOperator;

import java.util.ArrayList;

import net.sf.jsqlparser.statement.select.OrderByElement;

public class SortLogicalOperator extends LogicalOperator {

	private LogicalOperator child;
	private ArrayList<OrderByElement> order_by;
	
	public SortLogicalOperator(LogicalOperator child, ArrayList<OrderByElement> order_by) {
		this.child = child;
		this.order_by = order_by;
	}

	public LogicalOperator getChild() {
		return child;
	}

	public void setChild(LogicalOperator child) {
		this.child = child;
	}

	public ArrayList<OrderByElement> getOrder_by() {
		return order_by;
	}

	public void setOrder_by(ArrayList<OrderByElement> order_by) {
		this.order_by = order_by;
	}
	
	public void accept(PhysicalPlanBuilder ppb){
		ppb.visit(this);	
	}
	
}

