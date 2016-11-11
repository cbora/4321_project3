package Indexing;

import java.util.ArrayList;

public class DataEntry implements Comparable<DataEntry> {

	public int k;
	public ArrayList<RecordID> rid;
	
	public DataEntry(int key, ArrayList<RecordID> rid) {
		this.k = key;
		this.rid = rid;
	}

	@Override
	public int compareTo(DataEntry d) {
		if ( k < d.k)
			return -1;
		else if(k > d.k)
			return 1;
		else 
			return 0;
	}
	

}
