package Indexing;

public class RecordID implements Comparable<RecordID> {

	public int pageid;
	public int tupleid; 
	
	public RecordID(int pageid, int tupleid) {
		// TODO Auto-generated constructor stub
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

}
