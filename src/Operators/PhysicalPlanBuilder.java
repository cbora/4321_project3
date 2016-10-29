package Operators;

import java.util.ArrayList;
import java.util.Stack;

import LogicalOperator.DuplicateLogicalOperator;
import LogicalOperator.JoinLogicalOperator;
import LogicalOperator.LogicalOperator;
import LogicalOperator.ProjectLogicalOperator;
import LogicalOperator.SelectLogicalOperator;
import LogicalOperator.SortLogicalOperator;
import LogicalOperator.TableLogicalOperator;
import Project.JoinExp2OrderByVisitor;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.statement.select.OrderByElement;

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
	private String[] joinPlan; // Strings that indicate which join plan to use
	private String[] sortPlan; // String which indicate which sort type to use
	private String tmp_dir; // Arg temp directory
	/*
	 * ================================== 
	 * Constructors
	 * ==================================
	 */
	/**
	 * Constructor
	 * 
	 * @param root
	 *            - root of logical operator tree
	 */
	public PhysicalPlanBuilder(LogicalOperator root, String[] joinPlan, String[] sortPlan, String tmp_dir) {
		this.pStack = new Stack<Operator>();
		this.joinPlan = joinPlan;
		this.sortPlan = sortPlan;
		this.tmp_dir = tmp_dir;
		root.accept(this);
	}

	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */
	/**
	 * Retrieves physical query plan
	 * 
	 * @return root of physical query plan
	 */
	public Operator getResult() {
		return this.pStack.peek();
	}
	
	/**
	 * Returns appropriate join operator based on joinPlan
	 * @param left - left side of join
	 * @param right - right side of join
	 * @param exp - join expression
	 * @return JoinOperator
	 */
	private JoinOperator detJoin(Operator left, Operator right, Expression exp) {
		int joinType = Integer.parseInt(joinPlan[0]);
		JoinOperator j = null;
		
		switch (joinType) {
		case 0:
			j = new TNLJoinOperator(left, right, exp);
			break;
		case 1:
			j = new BNLJoinOperator(left, right, exp, Integer.parseInt(joinPlan[1]));
			break;
		case 2:
			JoinExp2OrderByVisitor je2ob = new JoinExp2OrderByVisitor(left, right, exp);
			left = detSort(left, je2ob.getLeft());
			right = detSort(right, je2ob.getRight());
			j = new SMJoinOperator(left, right, exp);
			break;
		}
		
		return j;
	}
	
	/**
	 * Constructs appropriate SortOperator base don sortPlan
	 * @param o - child operator
	 * @param ob - order by expression
	 * @return SortOperator
	 */
	private SortOperator detSort(Operator o, ArrayList<OrderByElement> ob) {
		int sortType = Integer.parseInt(sortPlan[0]);
		SortOperator s = null;
		
		switch (sortType) {
		case 0:
			s = new InMemSortOperator(o, ob);
			break;
		case 1:
			s = new ExtSortOperator(o, ob, this.tmp_dir,Integer.parseInt(sortPlan[1]));
			break;
		}
		
		return s;
	}
	
	/**
	 * Constructs appropriate SortOperator base don sortPlan
	 * @param o - child operator
	 * @param so - sort order
	 * @return SortOperator
	 */
	private SortOperator detSort(Operator o, int[] so) {
		int sortType = Integer.parseInt(sortPlan[0]);
		SortOperator s = null;
		
		switch (sortType) {
		case 0:
			s = new InMemSortOperator(o, so);
			break;
		case 1:
			s = new ExtSortOperator(o, so, this.tmp_dir,Integer.parseInt(sortPlan[1]));
			break;
		}
		
		return s;
	}

	/**
	 * @param lo
	 *            - node to visit
	 */
	public void visit(TableLogicalOperator lo) {
		ScanOperator s = new ScanOperator(lo.getInfo(), lo.getTableID());
		pStack.push(s);
	}
	
	/**
	 * @param lo
	 *            - node to visit
	 */
	public void visit(SelectLogicalOperator lo) {
		lo.getChild().accept(this);
		Operator o = pStack.pop();
		SelectOperator s = new SelectOperator(o, lo.getExp());
		pStack.push(s);
	}
	
	/**
	 * @param lo
	 *            - node to visit
	 */
	public void visit(ProjectLogicalOperator lo) {
		lo.getChild().accept(this);
		Operator o = pStack.pop();
		ProjectOperator p = new ProjectOperator(o, lo.getItems());
		pStack.push(p);
	}

	/**
	 * @param lo
	 *            - node to visit
	 */
	public void visit(JoinLogicalOperator lo) {
		lo.getLeft().accept(this);
		lo.getRight().accept(this);
		Operator o = pStack.pop();
		Operator o2 = pStack.pop();

		JoinOperator j = detJoin(o2, o, lo.getExp());

		pStack.push(j);
	}
	
	/**
	 * @param lo
	 *            - node to visit
	 */
	public void visit(SortLogicalOperator lo) {
		lo.getChild().accept(this);
		Operator o = pStack.pop();
		
		SortOperator s = detSort(o, lo.getOrder_by());
		
		pStack.push(s);
	}

	/**
	 * @param lo
	 *            - node to visit
	 */
	public void visit(DuplicateLogicalOperator lo) {
		lo.getChild().accept(this);
		Operator o = pStack.pop();
		if (!(o instanceof SortOperator)) {
			SortOperator s = detSort(o, (ArrayList<OrderByElement>) null);
			o = s;
		}
		SortedDupElimOperator s = new SortedDupElimOperator(o);
		pStack.push(s);
	}
	
}
