package Operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import Project.Tuple;
import net.sf.jsqlparser.statement.select.OrderByElement;

/**
 * Sort Operator abstract class
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public abstract class SortOperator extends Operator {
	
	/* ================================== 
	 * Fields
	 * ================================== */
	protected Operator child; // child operator in operator tree
	protected HashMap<String, Integer> schema; // schema of tuples returned by operator
	public int[] sort_order; // priority of the columns in sort (so [1,0] means col1 has priority over col0)
	protected ArrayList<OrderByElement> order_by;
	
	/* ================================== 
	 * Constructor
	 * ================================== */
	/**
	 * OrderBy Expression constructor
	 * @param child - child operator
	 * @param order_by - order_by expression
	 */
	public SortOperator(Operator child, ArrayList<OrderByElement> order_by) {
		this.child = child;
		this.schema = child.getSchema();
		this.sort_order = new int[this.schema.size()];
		this.order_by = new ArrayList<OrderByElement>(order_by);
		makeSortOrder(order_by); // make the sort order array
	}
	
	/**
	 * Sort Order Constructor
	 * @param child - child  operator
	 * @param sort_order - priority ranking of cols to sort on
	 */
	public SortOperator(Operator child, int[] sort_order) {
		this.child = child;
		this.schema = child.getSchema();
		this.sort_order = sort_order;
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	@Override
	public HashMap<String, Integer> getSchema() {
		return this.schema;
	}

	@Override
	public abstract Tuple getNextTuple();

	@Override
	public abstract void reset();

	@Override
	public void close() {
		child.close();
	}
	
//	public int getRelationSize() {
//		return this.child.getRelationSize();
//	}
	
	/**
	 * Construct sort_order array using order by expression
	 */
	protected void makeSortOrder(ArrayList<OrderByElement> order_by) {
		boolean[] seen_keys = new boolean[schema.size()];
		Arrays.fill(seen_keys, false);
		
		int i = 0;
		
		for (; order_by != null && i<order_by.size(); i++){
			String key_name = order_by.get(i).toString();
			sort_order[i] = this.schema.get(key_name);
			seen_keys[this.schema.get(key_name)] = true;
		}
		if (order_by != null && order_by.size() == schema.size())
			return;
		// if the number of sort orders provided are less than # of columns
		for (int j = 0; j < seen_keys.length; j++) {
			if (!seen_keys[j]) {
				sort_order[i] = j;
				i++;
			}
		}
	}

}
