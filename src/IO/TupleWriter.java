package IO;

import Project.Tuple;

/**
 * Abstract class for writing tuples
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public abstract class TupleWriter {

	/**
	 * writes tuple to disk
	 * @param t
	 */
	public abstract void write(Tuple t);
	
	/**
	 * writes array of tuples to disk
	 * @param t
	 */
	public abstract void write(Tuple[] t);
	
	
		
	/**
	 * closes I/O
	 */
	public abstract void close();
	
	/**
	 * finalizes write
	 */
	public abstract void finalize();
}
