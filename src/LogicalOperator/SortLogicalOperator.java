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
	
	@Override
	public void accept(PhysicalPlanBuilder ppb){
		ppb.visit(this);	
	}
	
}

