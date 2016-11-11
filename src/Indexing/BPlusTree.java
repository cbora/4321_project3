package Indexing;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import Operators.ScanOperator;
import Project.Tuple;

/**
 * BPlusTree Class Assumptions: 1. No duplicate keys inserted 2. Order D:
 * D<=number of keys in a node <=2*D 3. All keys are non-negative
 * TODO: Rename to BPlusTree
 */
public class BPlusTree<K extends Comparable<K>, T> {

	public Node<Integer, LinkedList<RecordID>> root;
	public int D;
	private ScanOperator op;
	private HashMap<Integer, LinkedList<RecordID>> ln;
	//private ArrayList<Node<Integer, LinkedList<RecordID>>> rids;
	private ArrayList<DataEntry> entries;
	private boolean sort;
	
	public BPlusTree(int D, ScanOperator op, boolean sort) {
		this.D = D;
		root = null;
		this.op = op;
		this.sort = sort;
	}
	
	
	/**
	 * Bulk loading function 
	 * @param
	 * 
	 */
	public void bulkLoading() {
		ArrayList<Node<Integer, LinkedList<RecordID>>> l = makeLeafNodes();
		ArrayList<Node<Integer, LinkedList<RecordID>>> ins = makeIndexNodes(l);
		while (ins.size() > 0){
			ins = makeIndexNodes(ins);
		}
	}
	
	public void makeRoot(ArrayList<Node<Integer, LinkedList<RecordID>>> lns) {
		IndexNode<Integer, LinkedList<RecordID>> nodes = null;		
		for(int j=0; j < lns.size(); j++){
			if ( nodes == null){
				nodes = new IndexNode<Integer, LinkedList<RecordID>>(makeKey(lns.get(j+1)), lns.get(j), lns.get(j+1));
				j++;				
			}
			else {
				nodes.insertSorted(new AbstractMap.SimpleEntry<Integer, Node<Integer, LinkedList<RecordID>>>(makeKey(lns.get(j+1)), lns.get(j)), j);
			}
		}		
		this.root = nodes;
	}
	
	
	public ArrayList<Node<Integer, LinkedList<RecordID>>> makeIndexNodes(ArrayList<Node<Integer, LinkedList<RecordID>>> lns) {
		int indexNodes = lns.size() / 2*D;
		
		if(lns.size()%(2*D) != 0)
			indexNodes++;
		ArrayList<Node<Integer, LinkedList<RecordID>>> ins= new ArrayList<Node<Integer, LinkedList<RecordID>>>();
		if (indexNodes == 1){
			makeRoot(lns);
			return ins;
		}
		boolean b = lns.size()%(2*D) < D/2;		
		int k = 0;
		
		for(int i=0; i<indexNodes; i++){
			IndexNode<Integer, LinkedList<RecordID>> nodes = null;
			if ( i >= indexNodes-2 && b) {
				int remaining = lns.size() - k;
				for (int j=0; j<remaining/2 && k < lns.size(); j++, k++){
					if ( nodes == null){
						nodes = new IndexNode<Integer, LinkedList<RecordID>>(makeKey(lns.get(k+1)), lns.get(k), lns.get(k+1));
						k++;
						j++;
					}
					else {	
						nodes.insertSorted(new AbstractMap.SimpleEntry<Integer, Node<Integer, LinkedList<RecordID>>>(makeKey(lns.get(k+1)), lns.get(k)), j);
					}
				}
				IndexNode<Integer, LinkedList<RecordID>> nodes2 = null;
				for(int j=0; k < lns.size(); k++, j++){
					if ( nodes2 == null){
						nodes2 = new IndexNode<Integer, LinkedList<RecordID>>(makeKey(lns.get(k+1)), lns.get(k), lns.get(k+1));
						k++;
						j++;
					}
					else {
						nodes.insertSorted(new AbstractMap.SimpleEntry<Integer, Node<Integer, LinkedList<RecordID>>>(makeKey(lns.get(k+1)), lns.get(k)), j);
					}
				}
			ins.add(nodes);
			ins.add(nodes2);
			break;
				
			}
			for (int j=0; j<2*D; j++, k++){
				if (nodes == null) {
					nodes = new IndexNode<Integer, LinkedList<RecordID>>(makeKey(lns.get(k+1)), lns.get(k), lns.get(k+1));
					k++;
					j++;
				}
				else {
					nodes.insertSorted(new AbstractMap.SimpleEntry<Integer, Node<Integer, LinkedList<RecordID>>>(makeKey(lns.get(k+1)),  lns.get(k)), j);
				}
			}
			ins.add(nodes);
		}
		return ins;
	}
	
	public ArrayList<Node<Integer, LinkedList<RecordID>>> makeLeafNodes() {
		int leaves = entries.size() / (2*D);
		if(entries.size()%(2*D) != 0)
			leaves++;
		
		boolean b = entries.size()%(2*D) < D/2;		
		ArrayList<Node<Integer, LinkedList<RecordID>>> lNodes = new ArrayList<Node<Integer, LinkedList<RecordID>>>();
		int k = 0;
		for(int i=0; i<leaves; i++){
			LeafNode<Integer, LinkedList<RecordID>> ln = new LeafNode<Integer, LinkedList<RecordID>>();
			if(i >= leaves - 2 && b){
				int remaining = entries.size() - k;
				for( int j=0; j<remaining/2 && k < entries.size(); j++, k++){					
					ln.insertSorted(entries.get(k).k, entries.get(k).rid);
				}
				LeafNode<Integer, LinkedList<RecordID>> ln2 = new LeafNode<Integer, LinkedList<RecordID>>();
				for(; k < entries.size(); k++){					
					ln2.insertSorted(entries.get(k).k, entries.get(k).rid);
				}
				lNodes.add(ln);
				lNodes.add(ln2);
				break;
			}
			else {
				for( int j=0; j<2*D && k < entries.size(); j++, k++){					
					ln.insertSorted(entries.get(k).k, entries.get(k).rid);
				}
			}
			lNodes.add(ln);			
		}		
		
		return lNodes;
	}
		
	
	public void formatEntries() {
		Tuple t;
		int pageid = 0;
		int tupleid = 0;
		
		while((t=op.getNextTuple())!=null){						
			RecordID rid = new RecordID(pageid, tupleid);
			tupleid++;
			if (!ln.containsKey(0)){
				ln.put(0, new LinkedList<RecordID>());				
			}
			ln.get(0).push(rid);
			if(op.pageStatus()) {
				tupleid=0;
				pageid++;
			}
		} 

		for (Map.Entry<Integer, LinkedList<RecordID>> m : ln.entrySet()) {
			entries.add(new DataEntry(m.getKey(), m.getValue()));
		}
		Collections.sort((List) entries);			
	}
	
	/**
	 * Search the value for a specific key
	 * 
	 * @param key
	 * @return value
	 */
	public LinkedList<RecordID> search(Integer key) {
		return tree_search(this.root, key);
	}
	
	public LinkedList<RecordID> tree_search(Node<Integer, LinkedList<RecordID>> root, Integer key){
		if (root.isLeafNode){
			LeafNode<Integer, LinkedList<RecordID>> leaf = (LeafNode<Integer, LinkedList<RecordID>>) root;
			
			//find if and where key is located
			ArrayList<Integer> keys = leaf.getKeys();
			int pos = keys.indexOf(key);
			if (pos < 0)
				return null;
			
			//if key is in leaf, return corresponding value
			ArrayList<LinkedList<RecordID>> values = leaf.getValues();
			return values.get(pos);
		}
		else{
			IndexNode<Integer, LinkedList<RecordID>> index = (IndexNode<Integer, LinkedList<RecordID>>) root;
			
			ArrayList<Integer> index_keys = index.getKeys();
			ArrayList<Node<Integer, LinkedList<RecordID>>> index_children = index.getChildren();
			
			//find which pointer to follow and make recursive call to search
			for (int i=0; i<index_keys.size(); i++){
				if( key.compareTo(index_keys.get(i)) < 0){					
					Node<Integer, LinkedList<RecordID>> new_node = index_children.get(i); 
					return tree_search(new_node, key);
				}
			}				
			Node<Integer, LinkedList<RecordID>> new_node = index_children.get(index_children.size()-1);
			return tree_search(new_node, key);
		}
	}
	
	public Integer makeKey(Node<Integer, LinkedList<RecordID>> node) {
		if (node.isLeafNode){
			return node.getKeys().get(0);
		}
		return makeKey(((IndexNode<Integer, LinkedList<RecordID>>) node).getChildren().get(0));
		return 0;
		
	}
}
