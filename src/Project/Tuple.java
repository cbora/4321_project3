package Project;

import net.sf.jsqlparser.schema.Column;

public class Tuple {

	private int[] values;
	
	public Tuple(int nCols) {
		values = new int[nCols];
	}
	
	/**
	 * Retrieves value of pair at index in tuple
	 * @param index - where we are retrieving from
	 * @return value of pair at index
	 */
	public int getVal(int index) {
		return values[index];
	}
	
	/**
	 * Inserts pair at index in tuple
	 * @param p - pair we are inserting
	 * @param index - where we want to insert
	 */
	public void add(int value, int index) {
		values[index] = value;
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
	
	/**
	 * concatenates two tuples
	 * @param t1 - tuple to be on left side in concatenation
	 * @param t2 - tuple to be one right side in concatenation
	 * @return concatenation of t1 and t2
	 */
	public static Tuple concat(Tuple t1, Tuple t2) {
		Tuple t = new Tuple(t1.length() + t2.length());
		for (int i = 0; i < t1.length() + t2.length(); i++) {
			if (i < t1.length())
				t.add(t1.getVal(i), i);
			else 
				t.add(t2.getVal(i - t1.length()), i);
		}
		return t;
	}
}










