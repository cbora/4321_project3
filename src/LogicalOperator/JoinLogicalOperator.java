package LogicalOperator;

import java.util.ArrayList;

import Operators.PhysicalPlanBuilder;
import Project.UnionFind;
import net.sf.jsqlparser.expression.Expression;

/**
 * Logical operator for joins
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class JoinLogicalOperator extends LogicalOperator {

	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	//private LogicalOperator left; // left child
	//private LogicalOperator right; // right child
	
	private ArrayList<LogicalOperator> children;
	private UnionFind union;
	private Expression exp; // join expression
	private Expression weird_exp;
	
	/*
	 * ================================== 
	 * Constructors
	 * ==================================
	 */
	/**
	 * Constructor
	 * @param child1 - left child
	 * @param child2 - right child
	 * @param exp - join expression
	 */
	public JoinLogicalOperator(ArrayList<LogicalOperator> children, Expression exp, Expression weird_exp, UnionFind union) {
		this.children = children;
		this.exp = exp;
		this.weird_exp = weird_exp;
		this.union = union;
	}

	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */

	public ArrayList<LogicalOperator> getChildren() {
		return this.children;
	}

	/**
	 * @return - join expression
	 */
	public Expression getExp() {
		return exp;
	}
	
	public UnionFind getUnionFind() {
		return union;
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
		sb.append("Join[");
		sb.append(weird_exp);
		sb.append("]\n");
		sb.append(this.union.toString());
		for (LogicalOperator child : children) {
			sb.append(child.prettyPrint(depth + 1));
		}
		
		return sb.toString();
	}
	
	@Override
	public void accept(PhysicalPlanBuilder ppb){
		ppb.visit(this);	
	}
}
