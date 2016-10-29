package Project;

import java.util.Comparator;

public class TupleDiffComparator implements Comparator<Tuple> {
	/* ================================== 
	 * Fields
	 * ================================== */
	// order of columns we wish to compare
	// so if sortOrder = [2,0,1], col2 gets highest priority, col0 next highest priority, col1 next priority
	private final int[] leftSortOrder; 
	private final int[] rightSortOrder;
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param sortOrder - order of columns we are sorting on
	 */
	public TupleDiffComparator(int[] leftSortOrder, int[] rightSortOrder) {
		this.leftSortOrder = leftSortOrder;
		this.rightSortOrder = rightSortOrder;

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
		int min = Math.min(leftSortOrder.length, rightSortOrder.length);
		
		while (result == 0 && i < min) {
			int val1 = t1.getVal(leftSortOrder[i]);
			int val2 = t2.getVal(rightSortOrder[i]);
			result = ((Integer) val1).compareTo(val2);
			i++;
		}
		return result;
	}
}
