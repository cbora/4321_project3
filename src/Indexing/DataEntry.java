package Indexing;

import java.util.ArrayList;

/**
 * Data Entry for LeafNode
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class DataEntry implements Comparable<DataEntry> {

	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	public int k; // key
	public ArrayList<RecordID> rid; // record id
	
	/*
	 * ================================== 
	 * Constructor
	 * ==================================
	 */
	/**
	 * Constructor
	 * @param key
	 * @param rid
	 */
	public DataEntry(int key, ArrayList<RecordID> rid) {
		this.k = key;
		this.rid = rid;
	}

	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */
	/**
	 * compareTo method for data entries (compares using just key)
	 * @param d - data entry we are comparing with
	 * @return -1 if I am less than d, 0 if equal, 1 if greater
	 */
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
