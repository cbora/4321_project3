package Indexing;
import java.util.ArrayList;
import java.util.List;

/**
 * B+ Tree Index Node
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class IndexNode extends Node {

	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	protected ArrayList<Integer> children; // m+1 integers pointing to pages of children

	/*
	 * ================================== 
	 * Constructor
	 * ==================================
	 */
	/**
	 * Constructor
	 * @param pos - page I will appear on
	 */
	public IndexNode(int pos) {
		isLeafNode = false;
		keys = new ArrayList<Integer>();
		children = new ArrayList<Integer>();
		this.pos = pos;
	}

	/**
	 * Constructor
	 * @param key - first key
	 * @param child0 - first child
	 * @param child1 - second child
	 * @param pos - page I will appear on
	 */
	public IndexNode(Integer key, Integer child0, Integer child1, int pos) {
		isLeafNode = false;
		keys = new ArrayList<Integer>();
		keys.add(key);
		children = new ArrayList<Integer>();
		children.add(child0);
		children.add(child1);
		this.pos = pos;
	}
		
	/**
	 * Constructor
	 * @param newKeys - list of keys
	 * @param newChildren - list of children
	 * @param pos - page I will appear on
	 */
	public IndexNode(List<Integer> newKeys, List<Integer> newChildren, int pos) {
		isLeafNode = false;

		keys = new ArrayList<Integer>(newKeys);
		children = new ArrayList<Integer>(newChildren);
		this.pos = pos;
	}

	/*
	 * ================================== 
	 * Method
	 * ==================================
	 */
	/**
	 * inserts new key
	 * @param key
	 */
	public void insertKey(Integer key) {
		keys.add(key);
	}
	
	/**
	 * inserts new child
	 * @param child
	 */
	public void insertChild(Integer child) {
		children.add(child);
	}
	
	/**
	 * inserts key/child pair
	 * @param key
	 * @param child
	 */
	public void insert(Integer key, Integer child) {
		insertKey(key);
		insertChild(child);
	}

	/**
	 * retrieves all children
	 * @return children
	 */
	public ArrayList<Integer> getChildren() {
		return this.children;
	}
}
