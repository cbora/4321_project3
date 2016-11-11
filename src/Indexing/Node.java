package Indexing;
import java.util.ArrayList;

public class Node {
	protected boolean isLeafNode;
	protected ArrayList<Integer> keys;
	protected int pos;
	
	public ArrayList<Integer> getKeys() {
		return keys;
	}
	
	public int getPos() {
		return pos;
	}

}
