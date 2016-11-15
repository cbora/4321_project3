package Indexing;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import Operators.ScanOperator;
import Project.Tuple;

/**
 * B+ Tree
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class BPlusTree {

	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	private String indexFile; // where tree is stored
	private int D; // order of tree
	private int nLeaves; // number of leaves
	private int rootIndex; // page root is at
	private BinaryNodeReader reader; // reader that reads from indexFile
	
	/*
	 * ================================== 
	 * Constructors
	 * ==================================
	 */
	/**
	 * Constructor that allows us to  build a tree
	 * @param D - order of tree
	 * @param scan - scan operator reading from table we want to index
	 * @param tuplePos - which column are we indexing
	 * @param indexFile - where tree is stored
	 */
	public BPlusTree(int D, ScanOperator scan, int tuplePos, String indexFile) {
		// build constructor
		this.indexFile = indexFile;
		this.D = D;
		this.nLeaves = 0;
		
		bulkLoad(scan, tuplePos);
		
		this.reader = new BinaryNodeReader(indexFile);
	}
	
	/**
	 * Constructor that allows us to read from index that already exists
	 * @param indexFile - location of tree
	 */
	public BPlusTree(String indexFile) {
		// already exists
		this.indexFile = indexFile;
		this.reader = new BinaryNodeReader(indexFile);
		
		this.rootIndex = this.reader.getRootPage();
		this.nLeaves = this.reader.getNumLeaves();
		this.D = this.reader.getOrder();
	}
	
	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */
	/**
	 * Search the value for a specific key
	 * 
	 * @param key
	 * @return value
	 */
	public LeafNode search(Integer key) {
		IndexNode root = (IndexNode) reader.read(rootIndex);
		return tree_search(root, key);
	}
	
	public LeafNode tree_search(Node root, Integer key){
		if (root.isLeafNode){ // if we are at a leaf node, return
			LeafNode leaf = (LeafNode) root;
			return leaf;
		}
		else{
			IndexNode index = (IndexNode) root;
			
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
	
	/**
	 * Reads right sibling of leaf
	 * @param leaf - leaf we want to the sibling of
	 * @return right sibling of leaf or null if it does not exist
	 */
	public LeafNode readNextLeaf(LeafNode leaf) {
		int pos = leaf.getPos() + 1;
		if (pos > reader.getNumLeaves()) { // if there is no right sibling, return null
			return null;
		}
		
		LeafNode newLeaf = (LeafNode) reader.read(pos);

		return newLeaf;
	}
	
	/**
	 * closes any open I/O
	 */
	public void close() {
		this.reader.close();
	}
	
	/**
	 * Bulk loading function 
	 * @param scan - scan operator reading from table we want to index
	 * @param tuplePos - column we are indexing 
	 */
	private void bulkLoad(ScanOperator scan, int tuplePos) {
		// get list of data entries to put in leaf layer
		ArrayList<DataEntry> entries = formatEntries(scan, tuplePos);
		
		ArrayList<Node> nodes = new ArrayList<Node>(); // will hold all nodes in tree
		
		// add leaves to nodes array
		ArrayList<Node> nodesFromLastRound = makeLeafNodes(entries, nodes); 
		this.nLeaves = nodesFromLastRound.size();
		
		// create first index layer
		nodesFromLastRound = makeIndexNodes(nodesFromLastRound, nodes);
		
		// keep going until we only added one node last round (at this point we've reached the root
		while (nodesFromLastRound.size() > 1){
			nodesFromLastRound = makeIndexNodes(nodesFromLastRound, nodes);
		}
		this.rootIndex = nodes.size();
		
		// write all nodes to disk
		BinaryNodeWriter writer = new BinaryNodeWriter(indexFile, nodes, this.nLeaves, this.D);
		writer.write();
		writer.close();
	}
	
	/**
	 * create root node
	 * @param lastRound - nodes we produced last iteration
	 * @param nodes - all nodes so far
	 * @return - root
	 */
	private IndexNode makeRoot(ArrayList<Node> lastRound, ArrayList<Node> nodes) {
		IndexNode index = new IndexNode(nodes.size() + 1);		
		for(int j=0; j < lastRound.size(); j++){
			if ( index.getChildren().size() == 0){
				index.insertChild(lastRound.get(j).getPos());
			}
			else {
				index.insert(makeKey(lastRound.get(j), nodes), lastRound.get(j).getPos());
			}
		}		
		return index;
	}
	
	/**
	 * creates index nodes 
	 * @param lastRound - nodes produced last iteration
	 * @param nodes - nodes produced so far
	 * @return - index nodes from this round
	 */
	private ArrayList<Node> makeIndexNodes(ArrayList<Node> lastRound, ArrayList<Node> nodes) {
		int indexNodes = lastRound.size() / (2 * D + 1);
		if(lastRound.size() % (2 * D + 1) != 0)
			indexNodes++;
		ArrayList<Node> indexes = new ArrayList<Node>();
		
		if (indexNodes == 1){
			IndexNode root = makeRoot(lastRound, nodes);
			indexes.add(root);
			nodes.add(root);
			return indexes ;
		}
		
		boolean isUnderflow = (lastRound.size() % (2 * D + 1)) - 1 < D;	
		
		int k = 0;
		for(int i=0; i<indexNodes; i++){
			IndexNode index = new IndexNode(nodes.size() + 1);
			
			if ( i >= indexNodes-2 && isUnderflow) { // if we are at the end and there will be underflow in last two nodes
				int remaining = lastRound.size() - k;
				
				// handle second to last node
				for (int j=0; j < remaining/2; j++, k++){
					if (index.getChildren().size() == 0){
						index.insertChild(lastRound.get(k).getPos());
					}
					else {	
						index.insert(makeKey(lastRound.get(k), nodes), lastRound.get(k).getPos());
					}
				}
				indexes.add(index);
				nodes.add(index);
				
				// handle last node
				IndexNode index2 = new IndexNode(nodes.size() + 1);
				for(; k < lastRound.size(); k++){
					if ( index2.getChildren().size() == 0){
						index2.insertChild(lastRound.get(k).getPos());
					}
					else {
						index2.insert(makeKey(lastRound.get(k), nodes), lastRound.get(k).getPos());
					}
				}
				indexes.add(index2);
				nodes.add(index2);
				break;	
			}
			
			else { // typical case - add 2*D keys to index node
				for (int j=0; j<=2*D && k < lastRound.size(); j++, k++){
					if (index.getChildren().size() == 0) {
						index.insertChild(lastRound.get(k).getPos());
					}
					else {
						index.insert(makeKey(lastRound.get(k), nodes), lastRound.get(k).getPos());
					}
				}
				indexes.add(index);
				nodes.add(index);
			}
		}
		return indexes ;
	}
	
	/**
	 * produces leaf layer
	 * @param entries - data entries we wish to store in leaves
	 * @param nodes - list that will contain all nodes we produce
	 * @return - list of leaf nodes produced
	 */
	private ArrayList<Node> makeLeafNodes(ArrayList<DataEntry> entries, ArrayList<Node> nodes) {
		int nLeaves = entries.size() / (2*D);
		if(entries.size()%(2*D) != 0)
			nLeaves++;
		boolean isUnderflow = entries.size()%(2*D) < D;	
		
		ArrayList<Node> leaves = new ArrayList<Node>();
		
		int k = 0;
		for(int i = 0; i < nLeaves; i++){
			LeafNode leaf = new LeafNode(nodes.size() + 1);
		
			if(i >= nLeaves - 2 && isUnderflow && nLeaves > 1){ // if we are at the end and there will be underflow in last node
				int remaining = entries.size() - k;

				// handle second to last node
				for( int j=0; j<remaining/2 && k < entries.size(); j++, k++){					
					leaf.insert(entries.get(k).k, entries.get(k).rid);
				}
				leaves.add(leaf);
				nodes.add(leaf);
								
				// handle last node
				LeafNode leaf2 = new LeafNode(nodes.size() + 1);
				for(; k < entries.size(); k++){					
					leaf2.insert(entries.get(k).k, entries.get(k).rid);
				}
				leaves.add(leaf2);
				nodes.add(leaf2);
				
				break;
			}
			else { // typical case - add 2*D entries to leaf
				for( int j=0; j<2*D && k < entries.size(); j++, k++){					
					leaf.insert(entries.get(k).k, entries.get(k).rid);
				}
			}
			leaves.add(leaf);
			nodes.add(leaf);
		}		
		return leaves;
	}
		
	/**
	 * creates sorted list of data entries we wish to store in leaves
	 * @param scan - scan operator reading from table we want to index
	 * @param tuplePos - column we want to index
	 * @return - list of data entries to be added to the leaves
	 */
	private ArrayList<DataEntry> formatEntries(ScanOperator scan, int tuplePos) {
		Tuple t;
		HashMap<Integer, ArrayList<RecordID>> map = new HashMap<Integer, ArrayList<RecordID>>();
		ArrayList<DataEntry> entries= new ArrayList<DataEntry>();
		int pageid = 0;
		int tupleid = 0;
		
		// read all tuples from disk and place them in key -> list<record id> hashmap
		while((t=scan.getNextTuple())!=null){						
			RecordID rid = new RecordID(pageid, tupleid);
			tupleid++;
			if (!map.containsKey(t.getVal(tuplePos))){
				map.put(t.getVal(tuplePos), new ArrayList<RecordID>());				
			}
			map.get(t.getVal(tuplePos)).add(rid);
			if(scan.pageStatus()) {
				tupleid=0;
				pageid++;
			}
		} 

		// construct data entries based on hash map
		for (Map.Entry<Integer, ArrayList<RecordID>> m : map.entrySet()) {
			entries.add(new DataEntry(m.getKey(), m.getValue()));
		}
		
		// sort entries on key
		Collections.sort(entries);
		
		return entries;
	}
	
	/**
	 * repeatedly follows the left most child pointer until we reach a leaf and returns leaf's left most key
	 * @param node - node we want to start at
	 * @param nodes - list of all nodes we've created
	 * @return left most key of left most leaf descendant
	 */
	private Integer makeKey(Node node, ArrayList<Node> nodes) {
		if (node.isLeafNode){
			return node.getKeys().get(0);
		}
		IndexNode index = (IndexNode) node;
		return makeKey(nodes.get(index.getChildren().get(0) - 1), nodes);
	}

}
