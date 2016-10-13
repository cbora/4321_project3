package IO;

import Project.Tuple;

public abstract class TupleReader {

	public abstract Tuple read();
	
	public abstract void reset();
	
	public abstract void close();
	
}
