package Indexing;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import Operators.ScanOperator;
import Project.Tuple;

/**
 * BPlusTree Class Assumptions: 1. No duplicate keys inserted 2. Order D:
 * D<=number of keys in a node <=2*D 3. All keys are non-negative
 */
public class BPlusTree {

	private String outputFile;
	private int D;
	private int nLeaves;
	private int rootIndex;
	private BinaryNodeReader reader;
	
	public BPlusTree(int D, ScanOperator scan, String outputFile) {
		this.outputFile = outputFile;
		this.D = D;
		this.nLeaves = 0;
		
		bulkLoad(scan);
		
		this.reader = new BinaryNodeReader(outputFile);
	}
	
	public BPlusTree(String outputFile) {
		this.reader = new BinaryNodeReader(outputFile);
		
		this.rootIndex = this.reader.getRootPage();
		this.nLeaves = this.reader.getNumLeaves();
		this.D = this.reader.getOrder();
	}
	
	/**
	 * Search the value for a specific key
	 * 
	 * @param key
	 * @return value
	 */
	public Integer search(Integer key) {
		IndexNode<Integer> root = (IndexNode<Integer>) reader.read(reader.getRootPage());
		return tree_search(root, key);
	}
	
	public Integer tree_search(Node root, Integer key){
		if (root.isLeafNode){
			LeafNode leaf = (LeafNode) root;
			
			ArrayList<LinkedList<RecordID>> values = leaf.getValues();
			return leaf.getPos();
		}
		else{
			IndexNode<Integer> index = (IndexNode<Integer>) root;
			
			ArrayList<Integer> index_keys = index.getKeys();
			ArrayList<Integer> index_children = index.getChildren();
			
			//find which pointer to follow and make recursive call to search
			for (int i=0; i<index_keys.size(); i++){
				if( key.compareTo(index_keys.get(i)) < 0){					
					Node new_node = reader.read(index_children.get(i)); 
					return tree_search(new_node, key);
				}
			}				
			Node new_node = reader.read(index_children.get(index_children.size()-1));
			return tree_search(new_node, key);
		}
	}
	
	public void close() {
		this.reader.close();
	}
	
	/**
	 * Bulk loading function 
	 * @param
	 * 
	 */
	private void bulkLoad(ScanOperator scan) {
		ArrayList<DataEntry> entries = formatEntries(scan);
		
		ArrayList<Node> nodes = new ArrayList<Node>();
		ArrayList<Node> nodesFromLastRound = makeLeafNodes(entries, nodes);
		this.nLeaves = nodesFromLastRound.size();
		
		nodesFromLastRound = makeIndexNodes(nodesFromLastRound, nodes);
		while (nodesFromLastRound.size() > 1){
			nodesFromLastRound = makeIndexNodes(nodesFromLastRound, nodes);
		}
		this.rootIndex = nodes.size();
		
		BinaryNodeWriter writer = new BinaryNodeWriter(outputFile, nodes, this.nLeaves, this.D);
		writer.write();
		writer.close();
	}
	
	private IndexNode<Node> makeRoot(ArrayList<Node> lastRound, ArrayList<Node> nodes) {
		IndexNode<Node> index = new IndexNode<Node>(nodes.size() + 1);		
		for(int j=0; j < lastRound.size(); j++){
			if ( index.getChildren().size() == 0){
				index.insertChild(lastRound.get(j));
			}
			else {
				index.insert(makeKey(lastRound.get(j)), lastRound.get(j));
			}
		}		
		return index;
	}
	
	private ArrayList<Node> makeIndexNodes(ArrayList<Node> lastRound, ArrayList<Node> nodes) {
		int indexNodes = lastRound.size() / 2*D;
		if(lastRound.size()%(2*D) != 0)
			indexNodes++;
		ArrayList<Node> indexes = new ArrayList<Node>();
		
		if (indexNodes == 1){
			indexes.add(makeRoot(lastRound, nodes));
			return indexes ;
		}
		
		boolean isUnderflow = lastRound.size()%(2*D) < D/2;	
		
		int k = 0;
		for(int i=0; i<indexNodes; i++){
			IndexNode<Node> index = new IndexNode<Node>(nodes.size() + 1);
			
			if ( i >= indexNodes-2 && isUnderflow) {
				int remaining = lastRound.size() - k;
				
				for (int j=0; j<remaining/2 && k < lastRound.size(); j++, k++){
					if (index.getChildren().size() == 0){
						index.insertChild(lastRound.get(k));
					}
					else {	
						index.insert(makeKey(lastRound.get(k)), lastRound.get(k));
					}
				}
				indexes.add(index);
				nodes.add(index);
				
				IndexNode<Node> index2 = new IndexNode<Node>(nodes.size() + 1);
				for(; k < lastRound.size(); k++){
					if ( index2.getChildren().size() == 0){
						index2.insertChild(lastRound.get(k));
					}
					else {
						index.insert(makeKey(lastRound.get(k)), lastRound.get(k));
					}
				}
				indexes.add(index2);
				nodes.add(index2);
				break;	
			}
			
			else {
				for (int j=0; j<2*D; j++, k++){
					if (index.getChildren().size() == 0) {
						index.insertChild(lastRound.get(k));
					}
					else {
						index.insert(makeKey(lastRound.get(k)), lastRound.get(k));
					}
				}
				indexes.add(index);
				nodes.add(index);
			}
		}
		return indexes ;
	}
	
	private ArrayList<Node> makeLeafNodes(ArrayList<DataEntry> entries, ArrayList<Node> nodes) {
		int nLeaves = entries.size() / (2*D);
		if(entries.size()%(2*D) != 0)
			nLeaves++;
		boolean isUnderflow = entries.size()%(2*D) < D/2;	
		
		ArrayList<Node> leaves = new ArrayList<Node>();
		
		int k = 0;
		for(int i = 0; i < nLeaves; i++){
			LeafNode leaf = new LeafNode(nodes.size() + 1);
		
			if(i >= nLeaves - 2 && isUnderflow){
				int remaining = entries.size() - k;
			
				for( int j=0; j<remaining/2 && k < entries.size(); j++, k++){					
					leaf.insertSorted(entries.get(k).k, entries.get(k).rid);
				}
				leaves.add(leaf);
				nodes.add(leaf);
				
				LeafNode leaf2 = new LeafNode(nodes.size() + 1);
				for(; k < entries.size(); k++){					
					leaf2.insertSorted(entries.get(k).k, entries.get(k).rid);
				}
				leaves.add(leaf2);
				nodes.add(leaf2);
				
				break;
			}
			else {
				for( int j=0; j<2*D && k < entries.size(); j++, k++){					
					leaf.insertSorted(entries.get(k).k, entries.get(k).rid);
				}
			}
			leaves.add(leaf);
			nodes.add(leaf);
		}		
		return leaves;
	}
		
	
	private ArrayList<DataEntry> formatEntries(ScanOperator scan) {
		Tuple t;
		HashMap<Integer, LinkedList<RecordID>> map = new HashMap<Integer, LinkedList<RecordID>>();
		ArrayList<DataEntry> entries= new ArrayList<DataEntry>();
		int pageid = 0;
		int tupleid = 0;
		
		while((t=scan.getNextTuple())!=null){						
			RecordID rid = new RecordID(pageid, tupleid);
			tupleid++;
			if (!map.containsKey(0)){
				map.put(0, new LinkedList<RecordID>());				
			}
			map.get(0).push(rid);
			if(scan.pageStatus()) {
				tupleid=0;
				pageid++;
			}
		} 

		for (Map.Entry<Integer, LinkedList<RecordID>> m : map.entrySet()) {
			entries.add(new DataEntry(m.getKey(), m.getValue()));
		}
		Collections.sort(entries);
		
		return entries;
	}
	
	private Integer makeKey(Node node) {
		if (node.isLeafNode){
			return node.getKeys().get(0);
		}
		return makeKey(((IndexNode<Node>) node).getChildren().get(0));
	}

}
