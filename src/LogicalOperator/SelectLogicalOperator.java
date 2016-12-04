package LogicalOperator;

import Operators.PhysicalPlanBuilder;
import Project.UnionFind;
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
	private UnionFind union;
	
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
	public SelectLogicalOperator(LogicalOperator child, Expression ex, UnionFind union) {
		this.child = child;
		this.exp = ex;
		this.union = union;
	}

	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */
	
	public UnionFind getUnionFind() {
		return union;
	}
	
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
	
	@Override
	public void accept(PhysicalPlanBuilder ppb){
		ppb.visit(this);	
	}
}
