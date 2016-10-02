package Operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import Project.Tuple;
import Project.TupleComparator;
import net.sf.jsqlparser.statement.select.OrderByElement;

public class SortOperator extends Operator {

	private Operator child;
	private ArrayList<Tuple> sorted_tuples;
	private ArrayList<OrderByElement> order_by;
	private HashMap<String, Integer> schema;
	private int[] sort_order;
	private int index;
	
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
	
	public ArrayList<Tuple> getSorted() {
		return sorted_tuples;
	}

	public void preFetch() {
		Tuple t;
		while ( (t = child.getNextTuple()) != null ){
			sorted_tuples.add(t);
		}
		// sort the tuples
		Collections.sort(sorted_tuples, new TupleComparator(sort_order));		
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
	
	public void makeSortOrder() {
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

	@Override
	public void reset() {
		index = 0;
	}
	
	@Override
	public void close() {
		child.close();
	}
}
