package Operators;

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
import Project.ColumnInfo;
import Project.JoinExp2OrderByVisitor;
import Project.TableInfo;
import Project.UnionFind;
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
	private final int BLOCK_SIZE = 5;
	private final int PAGE_SIZE = 4096;
	private final int SORT_TYPE = 1;
	
	private Stack<Operator> pStack; // keeps track of physical operators
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
	public PhysicalPlanBuilder(LogicalOperator root,  String tmp_dir) {
		this.pStack = new Stack<Operator>();
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
		int joinType;
		JoinOperator j = null;
		
		switch (joinType) {
		case 0:
			j = new TNLJoinOperator(left, right, exp);
			break;
		case 1:
			j = new BNLJoinOperator(left, right, exp, BLOCK_SIZE);
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
		SortOperator s = null;
		
		switch (SORT_TYPE) {
		case 0:
			s = new InMemSortOperator(o, ob);
			break;
		case 1:
			s = new ExtSortOperator(o, ob, this.tmp_dir,BLOCK_SIZE);
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
		SortOperator s = null;
		
		switch (SORT_TYPE) {
		case 0:
			s = new InMemSortOperator(o, so);
			break;
		case 1:
			s = new ExtSortOperator(o, so, this.tmp_dir, BLOCK_SIZE);
			break;
		}
		
		return s;
	}
	
	private Operator detSelect(Operator o, Expression exp, UnionFind union) {
		if (! (o instanceof ScanOperator))
			return new SelectOperator(o, exp, union);
		
		ScanOperator scan = (ScanOperator) o;
		TableInfo tableInfo = scan.getTableInfo();
			
		// calculate cost of scan
		int min = scan.getRelationSize(); 
		ColumnInfo bestCol = null;
		int lowkey = 0;
		int highkey = 0;
		Expression slct = null;
		
		// iterate through columns
		for (ColumnInfo column : tableInfo.getColumns().values()) {
			 if (column.getIndexInfo() != null) {
				 IndexExpressionVisitor indexVisitor = new IndexExpressionVisitor(exp, column);
				 if (indexVisitor.canUseIndex()) {							 
					IndexScanOperator iso;
					 if (bestCol.isClustered()) {
						 iso = new ClusteredIndexScanOperator(tableInfo, scan.getTableID(), bestCol, lowkey, highkey);
					 }
					 else {
						 iso = new UnclusteredIndexScanOperator(tableInfo, scan.getTableID(), bestCol, lowkey, highkey);
					 }
					 int cost = iso.getRelationSize();					 
					 if (cost < min) {
						 min = cost;
						 bestCol = column;
						 lowkey = indexVisitor.getLowkey();
						 highkey = indexVisitor.getHighkey();
						 slct = indexVisitor.getOtherSlctExps();
					 }
				 }
			 }
		}
		
		if (bestCol == null) {
			return new SelectOperator(o, exp, union);
		}
		else {
			IndexScanOperator iso;
			if (bestCol.isClustered()) {
				iso = new ClusteredIndexScanOperator(tableInfo, scan.getTableID(), bestCol, lowkey, highkey);
			}
			else {
				iso = new UnclusteredIndexScanOperator(tableInfo, scan.getTableID(), bestCol, lowkey, highkey);
			}
			scan.close();

			if (slct != null) // add select operator if select conditions index can't handle
				return new SelectOperator(iso, slct, union);
			else
				return iso;
		}
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
		Operator slct = detSelect(o, lo.getExp(), lo.getUnionFind());
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
