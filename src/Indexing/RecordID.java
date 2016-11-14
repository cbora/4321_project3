package Indexing;

public class RecordID implements Comparable<RecordID> {

	public int pageid;
	public int tupleid; 
	
	public RecordID(int pageid, int tupleid) {
		this.pageid = pageid;
		this.tupleid = tupleid;
	}

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
	
	@Override
	public String toString() {
		return ("(pid: " + pageid + ", tid: " + tupleid + ")");
	}

}
