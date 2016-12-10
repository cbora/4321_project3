package LogicalOperator;

import java.util.HashMap;

import Operators.PhysicalPlanBuilder;
import Project.Pair;
import net.sf.jsqlparser.expression.Expression;

/**
 * Logical operator for selection
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class SelectLogicalOperator extends LogicalOperator {
	
	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	private LogicalOperator child; // child operator
	private Expression exp; // selection condition
	private HashMap<String, Pair> selectRange;
	
	/*
	 * ================================== 
	 * Constructors
	 * ==================================
	 */
	/**
	 * Constructor
	 * @param child - child operator
	 * @param ex - selection condition
	 */
	public SelectLogicalOperator(LogicalOperator child, Expression ex, HashMap<String, Pair> selectRange) {
		this.child = child;
		this.exp = ex;
		this.selectRange = selectRange;
	}

	/*
	 * ================================== o
	 * Methods
	 * ==================================
	 */
	
	/**
	 * 
	 * @return - child operator
	 */
	public LogicalOperator getChild() {
		return child;
	}

	/**
	 * 
	 * @param child - new chile
	 */
	public void setChild(LogicalOperator child) {
		this.child = child;
	}

	/**
	 * 
	 * @return selection condition
	 */
	public Expression getExp() {
		return exp;
	}
	
	public HashMap<String, Pair> getSelectRange() {
		return this.selectRange;
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
		sb.append("Select[");
		sb.append(this.exp);
		sb.append("]\n");
		
		sb.append(this.child.prettyPrint(depth + 1));
		return sb.toString();
	}
	
	@Override
	public void accept(PhysicalPlanBuilder ppb){
		ppb.visit(this);	
	}
}
