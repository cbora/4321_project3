package Indexing;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import Operators.ScanOperator;
import Project.Tuple;

public class BPlusTree {

	private String indexFile;
	private int D;
	private int nLeaves;
	private int rootIndex;
	private BinaryNodeReader reader;
	
	public BPlusTree(int D, ScanOperator scan, int tuplePos, String indexFile) {
		// build constructor
		this.indexFile = indexFile;
		this.D = D;
		this.nLeaves = 0;
		
		bulkLoad(scan, tuplePos);
		
		this.reader = new BinaryNodeReader(indexFile);
	}
	
	public BPlusTree(String indexFile) {
		// already exists
		this.indexFile = indexFile;
		this.reader = new BinaryNodeReader(indexFile);
		
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
	public LeafNode search(Integer key) {
		IndexNode root = (IndexNode) reader.read(rootIndex);
		return tree_search(root, key);
	}
	
	public LeafNode tree_search(Node root, Integer key){
		if (root.isLeafNode){
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
	
	public LeafNode readNextLeaf(LeafNode leaf) {
		int pos = leaf.getPos() + 1;
		if (pos > reader.getNumLeaves()) {
			return null;
		}
		return (LeafNode) reader.read(pos);
	}
	
	public void close() {
		this.reader.close();
	}
	
	/**
	 * Bulk loading function 
	 * @param
	 * 
	 */
	private void bulkLoad(ScanOperator scan, int tuplePos) {
		ArrayList<DataEntry> entries = formatEntries(scan, tuplePos);
		
		ArrayList<Node> nodes = new ArrayList<Node>();
		ArrayList<Node> nodesFromLastRound = makeLeafNodes(entries, nodes);
		this.nLeaves = nodesFromLastRound.size();
		
		nodesFromLastRound = makeIndexNodes(nodesFromLastRound, nodes);
		while (nodesFromLastRound.size() > 1){
			nodesFromLastRound = makeIndexNodes(nodesFromLastRound, nodes);
		}
		this.rootIndex = nodes.size();
		
		BinaryNodeWriter writer = new BinaryNodeWriter(indexFile, nodes, this.nLeaves, this.D);
		writer.write();
		writer.close();
	}
	
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
		
		boolean isUnderflow = (lastRound.size() % (2 * D + 1)) - 1 < D/2;	
		
		int k = 0;
		for(int i=0; i<indexNodes; i++){
			IndexNode index = new IndexNode(nodes.size() + 1);
			
			if ( i >= indexNodes-2 && isUnderflow) {
				int remaining = lastRound.size() - k;
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
			
			else {
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
	
	private ArrayList<Node> makeLeafNodes(ArrayList<DataEntry> entries, ArrayList<Node> nodes) {
		int nLeaves = entries.size() / (2*D);
		if(entries.size()%(2*D) != 0)
			nLeaves++;
		boolean isUnderflow = entries.size()%(2*D) < D/2;	
		
		ArrayList<Node> leaves = new ArrayList<Node>();
		
		int k = 0;
		for(int i = 0; i < nLeaves; i++){
			LeafNode leaf = new LeafNode(nodes.size() + 1);
		
			if(i >= nLeaves - 2 && isUnderflow && nLeaves > 1){
				int remaining = entries.size() - k;
			
				for( int j=0; j<remaining/2 && k < entries.size(); j++, k++){					
					leaf.insert(entries.get(k).k, entries.get(k).rid);
				}
				leaves.add(leaf);
				nodes.add(leaf);
				
				LeafNode leaf2 = new LeafNode(nodes.size() + 1);
				for(; k < entries.size(); k++){					
					leaf2.insert(entries.get(k).k, entries.get(k).rid);
				}
				leaves.add(leaf2);
				nodes.add(leaf2);
				
				break;
			}
			else {
				for( int j=0; j<2*D && k < entries.size(); j++, k++){					
					leaf.insert(entries.get(k).k, entries.get(k).rid);
				}
			}
			leaves.add(leaf);
			nodes.add(leaf);
		}		
		return leaves;
	}
		
	
	private ArrayList<DataEntry> formatEntries(ScanOperator scan, int tuplePos) {
		Tuple t;
		HashMap<Integer, ArrayList<RecordID>> map = new HashMap<Integer, ArrayList<RecordID>>();
		ArrayList<DataEntry> entries= new ArrayList<DataEntry>();
		int pageid = 0;
		int tupleid = 0;
		
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

		for (Map.Entry<Integer, ArrayList<RecordID>> m : map.entrySet()) {
			entries.add(new DataEntry(m.getKey(), m.getValue()));
		}
		Collections.sort(entries);
		
		return entries;
	}
	
	private Integer makeKey(Node node, ArrayList<Node> nodes) {
		if (node.isLeafNode){
			return node.getKeys().get(0);
		}
		IndexNode index = (IndexNode) node;
		return makeKey(nodes.get(index.getChildren().get(0) - 1), nodes);
	}

}
