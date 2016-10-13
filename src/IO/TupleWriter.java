package IO;

import Project.Tuple;

public abstract class TupleWriter {

	public abstract void write(Tuple t);
	
	public abstract void reset();
	
	public abstract void close();
}
