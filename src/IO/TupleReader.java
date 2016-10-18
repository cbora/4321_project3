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
	 * closes I/O services
	 */
	public abstract void close();
	
}
