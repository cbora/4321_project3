package IO;

import Project.Tuple;

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
	 * resets read to a specific point in file
	 */
	public abstract void reset(int index);
	
	/**
	 * closes I/O services
	 */
	public abstract void close();
	
}
