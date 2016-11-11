package Indexing;
import java.util.ArrayList;
import java.util.List;


public class IndexNode<T> extends Node {

	// m nodes
	protected ArrayList<T> children; // m+1 children

	public IndexNode(int pos) {
		isLeafNode = false;
		keys = new ArrayList<Integer>();
		children = new ArrayList<T>();
		this.pos = pos;
	}

	public IndexNode(Integer key, T child0, T child1, int pos) {
		isLeafNode = false;
		keys = new ArrayList<Integer>();
		keys.add(key);
		children = new ArrayList<T>();
		children.add(child0);
		children.add(child1);
		this.pos = pos;
	}
		
	
	public IndexNode(List<Integer> newKeys, List<T> newChildren, int pos) {
		isLeafNode = false;

		keys = new ArrayList<Integer>(newKeys);
		children = new ArrayList<T>(newChildren);
		this.pos = pos;
	}

	public void insertKey(Integer key) {
		keys.add(key);
	}
	
	public void insertChild(T child) {
		children.add(child);
	}
	
	public void insert(Integer key, T child) {
		insertKey(key);
		insertChild(child);
	}

	public ArrayList<T> getChildren() {
		return this.children;
	}
}
