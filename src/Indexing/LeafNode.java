package Indexing;
import java.util.ArrayList;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

public class LeafNode extends Node {
	protected ArrayList<ArrayList<RecordID>> values;

  	public LeafNode(int pos) {
  		isLeafNode = true;
  		keys = new ArrayList<Integer>();
		values = new ArrayList<ArrayList<RecordID>>();
		this.pos = pos;
  	}
  	
	public LeafNode(Integer firstKey, ArrayList<RecordID> firstValue, int pos) {
		isLeafNode = true;
		keys = new ArrayList<Integer>();
		values = new ArrayList<ArrayList<RecordID>>();
		keys.add(firstKey);
		values.add(firstValue);
		this.pos = pos;
	}
	
	public LeafNode(List<Integer> newKeys, List<ArrayList<RecordID>> newValues, int pos) {
		isLeafNode = true;
		keys = new ArrayList<Integer>(newKeys);
		values = new ArrayList<ArrayList<RecordID>>(newValues);
		this.pos = pos;
	}
	
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
