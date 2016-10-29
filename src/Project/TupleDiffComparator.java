package Project;

import java.util.Comparator;

public class TupleDiffComparator implements Comparator<Tuple> {
	/* ================================== 
	 * Fields
	 * ================================== */
	private final int[] leftSortOrder; // priority order of cols in left relation
	private final int[] rightSortOrder; // priority order of cols in right relation
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param leftSortOrder - priority order of cols in left relation
	 * @param rightSortOrder - priority order of cols in right relation
	 */
	public TupleDiffComparator(int[] leftSortOrder, int[] rightSortOrder) {
		this.leftSortOrder = leftSortOrder;
		this.rightSortOrder = rightSortOrder;

	}

	/* ================================== 
	 * Methods
	 * ================================== */
	/**
	 * compares tuples in order of the columns in leftSortOrder and rightSortOrder respectively
	 * note: if rightSortOrder and leftSortOrder have different lengths, compares up until the smaller of their length
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
