package Operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import Project.Tuple;
import net.sf.jsqlparser.statement.select.OrderByElement;

public abstract class SortOperator extends Operator {
	
	/* ================================== 
	 * Fields
	 * ================================== */
	protected Operator child; // child operator in operator tree
	protected ArrayList<OrderByElement> order_by; // order by expression
	protected HashMap<String, Integer> schema; // schema of tuples returned by operator
	protected int[] sort_order; // priority of the columns in sort (so [1,0] means col1 has priority over col0)
	
	/* ================================== 
	 * Constructor
	 * ================================== */
	public SortOperator(Operator child, ArrayList<OrderByElement> order_by) {
		this.child = child;
		this.order_by = order_by;
		this.schema = child.getSchema();
		this.sort_order = new int[this.schema.size()];
		makeSortOrder(); // make the sort order array
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
	
	/**
	 * Construct sort_order array using order by expression
	 */
	protected void makeSortOrder() {
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
