package Indexing;
import java.util.ArrayList;
import java.util.List;


public class IndexNode extends Node {

	// m nodes
	protected ArrayList<Integer> children; // m+1 children

	public IndexNode(int pos) {
		isLeafNode = false;
		keys = new ArrayList<Integer>();
		children = new ArrayList<Integer>();
		this.pos = pos;
	}

	public IndexNode(Integer key, Integer child0, Integer child1, int pos) {
		isLeafNode = false;
		keys = new ArrayList<Integer>();
		keys.add(key);
		children = new ArrayList<Integer>();
		children.add(child0);
		children.add(child1);
		this.pos = pos;
	}
		
	
	public IndexNode(List<Integer> newKeys, List<Integer> newChildren, int pos) {
		isLeafNode = false;

		keys = new ArrayList<Integer>(newKeys);
		children = new ArrayList<Integer>(newChildren);
		this.pos = pos;
	}

	public void insertKey(Integer key) {
		keys.add(key);
	}
	
	public void insertChild(Integer child) {
		children.add(child);
	}
	
	public void insert(Integer key, Integer child) {
		insertKey(key);
		insertChild(child);
	}

	public ArrayList<Integer> getChildren() {
		return this.children;
	}
}
