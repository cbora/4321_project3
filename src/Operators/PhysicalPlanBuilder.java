package Operators;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Stack;

import Indexing.IndexExpressionVisitor;
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
	private String input_dir; 
	private String index; // for index type
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
	public PhysicalPlanBuilder(LogicalOperator root,  String planConfig, String tmp_dir) {
		this.pStack = new Stack<Operator>();
		this.tmp_dir = tmp_dir;
		readPlanConfig(planConfig);				
		root.accept(this);
	}
	
	/**
	 * reads from file to get joinplan, sortplan, and index plan
	 * @param config - filepath
	 */
	public void readPlanConfig(String config) {
		try {
			BufferedReader br = new BufferedReader(new FileReader(config));
			this.joinPlan = br.readLine().split(" ");
			this.sortPlan = br.readLine().split(" ");
			this.index = br.readLine();
			br.close();
		} catch(Exception e) {
			System.err.println("Exception occured during plan reading");
			e.printStackTrace();
		}
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
			j = new SMJoinOperator(left, right, exp, ((SortOperator) left).sort_order, ((SortOperator) right).sort_order);
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
	 * Constructs appropriate Selection plan based on the config file
	 * @param o - child operator
	 * @param exp - select expression
	 * @return operator for handling selection
	 * 
	 */
	private Operator detSelect(Operator o, Expression exp) {
		int indexType = Integer.parseInt(index);
				
		switch (indexType) {
		case 0:	
			return new SelectOperator(o, exp);
		case 1:		
			// if child is not of instance Scan Operator return just SelectOperator
			if (! (o instanceof ScanOperator))
				return new SelectOperator(o, exp);
			ScanOperator scan = (ScanOperator) o;
			
			//make expression visitor to determine low and high key
			IndexExpressionVisitor indexVisitor = new IndexExpressionVisitor(exp, scan.getTableInfo());
			
			if (indexVisitor.canUseIndex()) { // if we can use the index, use it
				int lowkey = indexVisitor.getLowkey();
				int highkey = indexVisitor.getHighkey();
				
				IndexScanOperator iso;
				if (scan.getTableInfo().isClustered()) {
					iso = new ClusteredIndexScanOperator(scan.getTableInfo(), scan.getTableID(), lowkey, highkey);
				}
				else {
					iso = new UnclusteredIndexScanOperator(scan.getTableInfo(), scan.getTableID(), lowkey, highkey);
				}
				scan.close();

				if (indexVisitor.getOtherSlctExps() != null) // add select operator if select conditions index can't handle
					return new SelectOperator(iso, indexVisitor.getOtherSlctExps());
				else
					return iso;
			}
			else {
				return new SelectOperator(scan, exp);
			}
		}
		
		return null;
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
		Operator slct = detSelect(o, lo.getExp());
		pStack.push(slct);
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
