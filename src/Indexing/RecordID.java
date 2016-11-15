package Indexing;

/**
 * B+ Tree
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class RecordID implements Comparable<RecordID> {

	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	public int pageid; // page in table
	public int tupleid; // index into page
	
	/*
	 * ================================== 
	 * Constructor
	 * ==================================
	 */
	/**
	 * Constructor
	 * @param pageid
	 * @param tupleid
	 */
	public RecordID(int pageid, int tupleid) {
		this.pageid = pageid;
		this.tupleid = tupleid;
	}

	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */
	/**
	 * compareTo for record id. pageid has higher priority that tupleid
	 * @param oo - record id I am comparing myself to
	 * @return -1 if oo < this, 0 if equal, 1 if greater thna
	 */
	@Override
	public int compareTo(RecordID oo) {			
		if(pageid < oo.pageid)
			return -1;
		else if (pageid > oo.pageid)
			return 1;
		else if(tupleid < oo.tupleid)
			return -1;
		else if(tupleid > oo.tupleid)
			return 1;
		else 
			return 0;						
	}
	
	/**
	 * toString for record id
	 * @return (pid: pageid, tid: tupleid)
	 */
	@Override
	public String toString() {
		return ("(pid: " + pageid + ", tid: " + tupleid + ")");
	}

}
