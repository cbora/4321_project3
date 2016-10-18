package LogicalOperator;

import java.util.Stack;

import Operators.JoinOperator;
import Operators.Operator;
import Operators.ProjectOperator;
import Operators.ScanOperator;
import Operators.SelectOperator;
import Operators.SortOperator;
import Operators.SortedDupElimOperator;

/**
 * converts logical plan to physical plan
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class PhysicalPlanBuilder {

	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	private Stack<Operator> pStack; // keeps track of physical operators
	
	/*
	 * ================================== 
	 * Constructors
	 * ==================================
	 */
	/**
	 * Constructor
	 * @param root - root of logical operator tree
	 */
	public PhysicalPlanBuilder(LogicalOperator root) {
		this.pStack = new Stack<Operator>();
		root.accept(this);
	}
	
	/*
	 * ================================== 
	 * CMethods
	 * ==================================
	 */
	/**
	 * Retrieves physical query plan
	 * @return root of physical query plan
	 */
	public Operator getResult() {
		return this.pStack.peek();
	}
	
	/**
	 * @param lo - node to visit
	 */
	public void visit(DuplicateLogicalOperator lo) {
		lo.getChild().accept(this);
		Operator o = pStack.pop();
		if ( !(o instanceof SortOperator) ){
			SortOperator s = new SortOperator(o, null);
			o = s;
		}
		SortedDupElimOperator s = new SortedDupElimOperator(o);
		pStack.push(s);
	}
	
	/**
	 * @param lo - node to visit
	 */
	public void visit(JoinLogicalOperator lo) {
		lo.getLeft().accept(this);
		lo.getRight().accept(this);
		Operator o = pStack.pop();
		Operator o2 = pStack.pop();
		JoinOperator j = new JoinOperator(o2, o, lo.getExp());
		pStack.push(j);
	}
	
	/**
	 * @param lo - node to visit
	 */
	public void visit(ProjectLogicalOperator lo) {
		lo.getChild().accept(this);
		Operator o = pStack.pop();
		ProjectOperator p = new ProjectOperator(o, lo.getItems());
		pStack.push(p);
	}
	
	/**
	 * @param lo - node to visit
	 */
	public void visit(SelectLogicalOperator lo) {
		lo.getChild().accept(this);
		Operator o = pStack.pop();
		SelectOperator s = new SelectOperator(o, lo.getExp());
		pStack.push(s);
	}
	/**
	 * @param lo - node to visit
	 */
	public void visit(SortLogicalOperator lo) {
		lo.getChild().accept(this);
		Operator o = pStack.pop();
		SortOperator s = new SortOperator(o, lo.getOrder_by());
		pStack.push(s); 
	}	
	
	/**
	 * @param lo - node to visit
	 */
	public void visit(TableLogicalOperator lo) {
		ScanOperator s = new ScanOperator(lo.getInfo(), lo.getTableID());
		pStack.push(s);
	}
}
