package Indexing;
import java.util.ArrayList;

/**
 * B+ Tree Node
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class Node {
	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	protected boolean isLeafNode; // am I a leaf node
	protected ArrayList<Integer> keys; // list of my keys
	protected int pos; // page I appear on
	
	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */
	/**
	 * retrieves list of my keys
	 * @return keys
	 */
	public ArrayList<Integer> getKeys() {
		return keys;
	}
	
	/**
	 * retrieves the page I appear on
	 * @return pos
	 */
	public int getPos() {
		return pos;
	}

}
