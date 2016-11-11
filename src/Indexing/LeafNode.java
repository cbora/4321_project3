package Indexing;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class LeafNode extends Node {
	protected ArrayList<LinkedList<RecordID>> values;

  	public LeafNode(int pos) {
  		isLeafNode = true;
  		keys = new ArrayList<Integer>();
		values = new ArrayList<LinkedList<RecordID>>();
		this.pos = pos;
  	}
  	
	public LeafNode(Integer firstKey, LinkedList<RecordID> firstValue, int pos) {
		isLeafNode = true;
		keys = new ArrayList<Integer>();
		values = new ArrayList<LinkedList<RecordID>>();
		keys.add(firstKey);
		values.add(firstValue);
		this.pos = pos;
	}
	
	public LeafNode(List<Integer> newKeys, List<LinkedList<RecordID>> newValues, int pos) {
		isLeafNode = true;
		keys = new ArrayList<Integer>(newKeys);
		values = new ArrayList<LinkedList<RecordID>>(newValues);
		this.pos = pos;
	}
	
	public ArrayList<LinkedList<RecordID>> getValues() {
		return this.values;
	}

	/**
	 * insert key/value into this node so that it still remains sorted
	 * 
	 * @param key
	 * @param value
	 */
	public void insertSorted(Integer key, LinkedList<RecordID> value) {
		if (key.compareTo(keys.get(0)) < 0) {
			keys.add(0, key);
			values.add(0, value);
		} else if (key.compareTo(keys.get(keys.size() - 1)) > 0) {
			keys.add(key);
			values.add(value);
		} else {
			ListIterator<Integer> iterator = keys.listIterator();
			while (iterator.hasNext()) {
				if (iterator.next().compareTo(key) > 0) {
					int position = iterator.previousIndex();
					keys.add(position, key);
					values.add(position, value);
					break;
				}
			}

		}
	}

}
