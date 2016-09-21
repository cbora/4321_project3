package Project;

public class Tuple {
	/* --------------------------------------------------------------------------------- *
	 * May not need to store colName AND value											 *
	 * We may just need value...not sure yet, so included both. Adjust later if needed.  *
	 * --------------------------------------------------------------------------------- */
	
	private Pair[] values; // represents tuple as array of (colName, value) pairs
	
	public Tuple(int nCols) {
		values = new Pair[nCols];
	}
	
	/**
	 * Retrieves (colName, value) pair at index in tuple
	 * @param index - where we are retrieving from
	 * @return pair at index
	 */
	public Pair getPair(int index) {
		return values[index];
	}
	
	/**
	 * Retrieves value of pair at index in tuple
	 * @param index - where we are retrieving from
	 * @return value of pair at index
	 */
	public int getVal(int index) {
		return values[index].value;
	}
	
	/**
	 * Retrieves colName of pair at index in tuple
	 * @param index - where we are retrieving from
	 * @return colName of pair at index
	 */
	public String getColName(int index) {
		return values[index].colName;
	}
	
	/**
	 * Inserts pair at index in tuple
	 * @param p - pair we are inserting
	 * @param index - where we want to insert
	 */
	public void add(Pair p, int index) {
		values[index] = p;
	}
	
	/**
	 * retrieves length of tuple
	 * @return length of tuple
	 */
	public int length() {
		return values.length;
	}
	
	/**
	 * Returns string representation of the tuple
	 * @return values in the tuple separated by commas
	 */
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < values.length - 1; i++) {
			sb.append(getVal(i));
			sb.append(",");
		}
		sb.append(getVal(values.length - 1));
		return sb.toString();
	}
}
