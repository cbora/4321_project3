package Indexing;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * B+ Tree Leaf Node
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class LeafNode extends Node {
	
	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	protected ArrayList<ArrayList<RecordID>> values; // values

	/*
	 * ================================== 
	 * Constructor
	 * ==================================
	 */
	/**
	 * Constructor
	 * @param pos - page I appear on
	 */
  	public LeafNode(int pos) {
  		isLeafNode = true;
  		keys = new ArrayList<Integer>();
		values = new ArrayList<ArrayList<RecordID>>();
		this.pos = pos;
  	}
  	
  	/**
  	 * Constructor
  	 * @param firstKey
  	 * @param firstValue
  	 * @param pos - page I appear on
  	 */
	public LeafNode(Integer firstKey, ArrayList<RecordID> firstValue, int pos) {
		isLeafNode = true;
		keys = new ArrayList<Integer>();
		values = new ArrayList<ArrayList<RecordID>>();
		keys.add(firstKey);
		values.add(firstValue);
		this.pos = pos;
	}
	
	/**
	 * Constructor
	 * @param newKeys
	 * @param newValues
	 * @param pos - page I appear on
	 */
	public LeafNode(List<Integer> newKeys, List<ArrayList<RecordID>> newValues, int pos) {
		isLeafNode = true;
		keys = new ArrayList<Integer>(newKeys);
		values = new ArrayList<ArrayList<RecordID>>(newValues);
		this.pos = pos;
	}
	
	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */
	/**
	 * List of record id's I contain
	 * @return values
	 */
	public ArrayList<ArrayList<RecordID>> getValues() {
		return this.values;
	}

	/**
	 * insert key/value into this node so that it still remains sorted
	 * 
	 * @param key
	 * @param value
	 */
	public void insert(Integer key, ArrayList<RecordID> value) {
		keys.add(key);
		values.add(value);
	}

}
