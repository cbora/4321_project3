package Operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

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
	private int[] sort_order; // priority of the columns in sort (so [1,0] means col1 has priority over col0)
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
		ArrayList<String> seen_keys = new ArrayList<String>();
		for (int i=0; i<order_by.size(); i++){
			String key_name = order_by.get(i).toString();
			sort_order[i] = this.schema.get(key_name);
			seen_keys.add(key_name);
		}
		if (order_by.size() == schema.size())
			return;
		// if the number of sort orders provided are less than # of columns
		int i = order_by.size();
		for(Entry<String, Integer> entry: schema.entrySet()) {
			String key = entry.getKey();
			if (seen_keys.contains(key))
				continue;
			sort_order[i] = entry.getValue();
			i++;
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
