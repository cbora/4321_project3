package Project;

/**
 * Tuple object that represents a row in a table
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class Tuple {

	/* ================================== 
	 * Fields
	 * ================================== */
	private int[] values; // values in tuple
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param nCols - number of columns in tuple
	 */
	public Tuple(int nCols) {
		values = new int[nCols];
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
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
	 * equals method for tuples
	 * @param o - object we wish to compare to our tuple
	 * @return true if the tuples have the same length and all columns have 
	 * 			equal value. False if the parameter is not a tuple, is a tuple but has 
	 * 			different length, or is a tuple but has differing colum values
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Tuple) {
			Tuple t = (Tuple) o;
			if (t.length() != this.values.length)
				return false;
			for (int i = 0; i < this.values.length; i++) {
				if (t.getVal(i) != this.getVal(i))
					return false;
			}
			return true;
		}
		return false;
	}
	
	/**
	 * Returns string representation of the tuple
	 * @return values in the tuple separated by commas
	 */
	@Override
	public String toString() {
		if (this.length() == 0)
			return "";
		
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < this.values.length - 1; i++) {
			sb.append(getVal(i));
			sb.append(",");
		}
		sb.append(getVal(this.values.length - 1));
		return sb.toString();
	}
	
	/**
	 * hash code method for tuples
	 * @return a hash equal to the hash code of the tuple's toString()
	 */
	@Override
	public int hashCode() {
		return this.toString().hashCode();
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
