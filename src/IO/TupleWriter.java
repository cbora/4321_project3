package IO;

import Project.Tuple;

public abstract class TupleWriter {

	/**
	 * writes tuple to disk
	 * @param t
	 */
	public abstract void write(Tuple t);
		
	/**
	 * closes I/O
	 */
	public abstract void close();
	
	/**
	 * finalizes write
	 */
	public abstract void finalize();
}
