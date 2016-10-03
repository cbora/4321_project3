package Project;

import java.util.Comparator;

/**
 * Comparator that allows for comparing tuples based on specified column order
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class TupleComparator implements Comparator<Tuple> {
	
	/* ================================== 
	 * Fields
	 * ================================== */
	// order of columns we wish to compare
	// so if sortOrder = [2,0,1], col2 gets highest priority, col0 next highest priority, col1 next priority
	private final int[] sortOrder; 
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param sortOrder - order of columns we are sorting on
	 */
	public TupleComparator(int[] sortOrder) {
		this.sortOrder = sortOrder;
	}

	/* ================================== 
	 * Methods
	 * ================================== */
	/**
	 * compares tuples in order of the columns in sortOrder
	 * @param t1 - first tuple to compare
	 * @param t2 - second tuple to compare
	 * @return -1 if t1 < t2 in first column in sortOrder that they differ
	 * 			0 if t1 equals t2 over all columns in sortOrder
	 * 			1 if t1 > t2 in first column in sortOrder that they differ
	 */
	@Override
	public int compare(Tuple t1, Tuple t2) {
		int i = 0;
		int result = 0;
		while (result == 0 && i < this.sortOrder.length) {
			int val1 = t1.getVal(this.sortOrder[i]);
			int val2 = t2.getVal(this.sortOrder[i]);
			result = ((Integer) val1).compareTo(val2);
			i++;
		}
		return result;
	}

}
