package Operators;

import java.util.ArrayList;
import java.util.Collections;

import Project.Tuple;
import Project.TupleComparator;
import net.sf.jsqlparser.statement.select.OrderByElement;

/**
 * Operator for sorting tuples in memory
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class InMemSortOperator extends SortOperator {

	/* ================================== 
	 * Fields
	 * ================================== */
	private int index; // where we are in sorted_tuples
	private ArrayList<Tuple> sorted_tuples; // sorted list of tuples retrieved from child

	
	/* ================================== 
	 * Constructor
	 * ================================== */
	/**
	 * Order By Constructor
	 * @param child - child operator
	 * @param order_by - order by expression
	 */
	public InMemSortOperator(Operator child, ArrayList<OrderByElement> order_by){
		super(child, order_by);
		this.sorted_tuples = new ArrayList<Tuple>();
		this.index = 0;
		preFetch(); // fetch all the tuples and sort them
	}
	
	/**
	 * Sort Order Constructor
	 * @param child - child operator
	 * @param sort_order - priority ordering of cols
	 */
	public InMemSortOperator(Operator child, int[] sort_order){
		super(child, sort_order);
		this.sorted_tuples = new ArrayList<Tuple>();
		this.index = 0;
		preFetch(); // fetch all the tuples and sort them
	}
	
	
	/* ================================== 
	 * pretty print of method
	 * ================================== */
	public String prettyPrint(int depth){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<depth; i++)
			sb.append("-");
		sb.append("Sort");
		sb.append(this.order_by);
		sb.append("\n");
		sb.append(this.child.prettyPrint(depth + 1));
		return sb.toString();
	}
	
	
	/* ================================== 
	 * Methods
	 * ================================== */
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
	public void reset(int index) {
		this.index = index;  
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
