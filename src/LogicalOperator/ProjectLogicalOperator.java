package LogicalOperator;

import java.util.ArrayList;

import Operators.PhysicalPlanBuilder;
import net.sf.jsqlparser.statement.select.SelectItem;

/**
 * Logical operator for projection
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class ProjectLogicalOperator extends LogicalOperator {

	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	private LogicalOperator child; // child operator
	private ArrayList<SelectItem> items; // projection attributes
	
	/*
	 * ================================== 
	 * Constructor
	 * ==================================
	 */
	/**
	 * Constructor
	 * @param child - child operator
	 * @param items - projection attributes
	 */
	public ProjectLogicalOperator(LogicalOperator child, ArrayList<SelectItem> items) {
		this.child = child;
		this.items = items;
	}

	/*
	 * ================================== 
	 * Methids
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
	 * @return - projection attributes
	 */
	public ArrayList<SelectItem> getItems() {
		return items;
	}
	
	@Override
	public void accept(PhysicalPlanBuilder ppb){
		ppb.visit(this);	
	}
}
