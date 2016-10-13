package Operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import Project.Tuple;
import Project.TupleComparator;
import net.sf.jsqlparser.statement.select.OrderByElement;

/**
 * Operator for sorting tuples
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class SortOperator extends Operator {

	/* ================================== 
	 * Fields
	 * ================================== */
	private Operator child; // child operator in operator tree
	private ArrayList<Tuple> sorted_tuples; // sorted list of tuples retrieved from child
	private ArrayList<OrderByElement> order_by; // order by expression
	private HashMap<String, Integer> schema; // schema of tuples returned by operator
	public int[] sort_order; // priority of the columns in sort (so [1,0] means col1 has priority over col0)
	private int index; // where we are in sorted_tuples
	
	/* ================================== 
	 * Constructor
	 * ================================== */
	/**
	 * Constructor
	 * @param child
	 * @param order_by - order by expression
	 */
	public SortOperator(Operator child, ArrayList<OrderByElement> order_by){
		this.child = child;
		this.order_by = order_by;
		this.sorted_tuples = new ArrayList<Tuple>();
		this.schema = child.getSchema();
		this.sort_order = new int[this.schema.size()];
		this.index = 0;
		makeSortOrder(); // make the sort order array
		preFetch(); // fetch all the tuples and sort them
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	@Override
	public HashMap<String, Integer> getSchema() {
		return this.schema;
	}
	
	@Override
	public Tuple getNextTuple() {	
		if (index < sorted_tuples.size()) {
			Tuple t = sorted_tuples.get(index); 
			index++;
			return t;
		} else {
			return null;
		}		
	}

	@Override
	public void reset() {
		index = 0;
	}
	
	@Override
	public void close() {
		child.close();
	}
	
	/**
	 * Construct sort_order array using order by expression
	 */
	private void makeSortOrder() {
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
	
	/**
	 * Populate sorted_tuples
	 */
	private void preFetch() {
		Tuple t;
		while ( (t = child.getNextTuple()) != null ){
			sorted_tuples.add(t);
		}
		// sort the tuples
		Collections.sort(sorted_tuples, new TupleComparator(sort_order));		
	}

}
