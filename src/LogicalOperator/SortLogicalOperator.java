package LogicalOperator;

import java.util.ArrayList;

import Operators.PhysicalPlanBuilder;
import net.sf.jsqlparser.statement.select.OrderByElement;

/**
 * Logical operator for sorting
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class SortLogicalOperator extends LogicalOperator {

	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	private LogicalOperator child; // child operator
	private ArrayList<OrderByElement> order_by; // order by clause
	
	/*
	 * ================================== 
	 * Constructors
	 * ==================================
	 */
	/**
	 * Constructor
	 * @param child - child operator
	 * @param order_by - order by clause
	 */
	public SortLogicalOperator(LogicalOperator child, ArrayList<OrderByElement> order_by) {
		this.child = child;
		this.order_by = order_by;
	}

	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */
	/**
	 * 
	 * @return child
	 */
	public LogicalOperator getChild() {
		return child;
	}

	/**
	 * 
	 * @param child - new child
	 */
	public void setChild(LogicalOperator child) {
		this.child = child;
	}

	/**
	 * 
	 * @return order by clause
	 */
	public ArrayList<OrderByElement> getOrder_by() {
		return order_by;
	}
	
	/**
	 * pretty print method
	 * @param depth
	 * @return this method's name
	 */
	public String prettyPrint(int depth) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < depth; i++) 
			sb.append("-");
		sb.append("Sort");
		sb.append(order_by);
		sb.append("\n");
		
		sb.append(this.child.prettyPrint(depth + 1));
		return sb.toString();
	}
	
	@Override
	public void accept(PhysicalPlanBuilder ppb){
		ppb.visit(this);	
	}
	
}

