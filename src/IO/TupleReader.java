
package IO;

import Project.Tuple;

/**
 * Abstract class for reading tuples
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */

public abstract class TupleReader {

	/**
	 * reads a single tuple
	 * @return - read tuple
	 */
	public abstract Tuple read();
	
	/**
	 * resets read to read from the beginning of the file again
	 */
	public abstract void reset();
	
	/**
	 * checks if page is finished
	 */
	public abstract boolean pageIsFinished();
	
	/**
	 * resets read to a specific point in file
	 */
	public abstract void reset(int index);
	
	/**
	 * closes I/O services
	 */
	public abstract void close();
	
}
