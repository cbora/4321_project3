package Operators;

import java.util.ArrayList;
import java.util.HashMap;

import IO.BinaryTupleReader;
import Indexing.BPlusTree;
import Indexing.LeafNode;
import Indexing.RecordID;
import Project.TableInfo;
import Project.Tuple;
import net.sf.jsqlparser.schema.Table;

public class IndexScanOperator extends Operator {
	
	/* =====================================
	 * Fields
	 * ===================================== */
	private int lowkey;
	private int highkey;
	private BPlusTree bTree;
	private LeafNode currLeaf;
	private int leafIndex;
	private int listIndex;
	
	private HashMap<String, Integer> schema;
	private TableInfo tableInfo;
	private String tableID;
	private BinaryTupleReader reader;

	/* =====================================
	 * Constructors
	 * ===================================== */
	public IndexScanOperator(TableInfo tableInfo, String tableID, String indexFile, int lowkey, int highkey) {
		super();
		
		this.tableInfo = tableInfo;
		this.tableID = tableID;

		ArrayList<String> columns = tableInfo.getColumns();
		this.schema = new HashMap<String, Integer>();
		
		this.reader = new BinaryTupleReader(this.tableInfo.getFilePath());
		
		// Read from columns in tableInfo
		// Add (<alias> + "." + <name of column i>, i) to hash map
		for (int i = 0; i < columns.size(); i++) {
			this.schema.put(this.tableID + "." + columns.get(i), i);
		}
		
		this.bTree = new BPlusTree(indexFile);
		this.lowkey = lowkey;
		this.highkey = highkey;	
		this.currLeaf = this.bTree.search(lowkey);
		
		int i = 0;
		while(i < currLeaf.getKeys().size() && lowkey < currLeaf.getKeys().get(i)) {
			i++;
		}
		this.leafIndex = i;
		this.listIndex = 0;
	}
	
	public IndexScanOperator(TableInfo tableInfo, String indexFile, int lowkey, int highkey) {
		this(tableInfo, tableInfo.getTableName(), indexFile, lowkey, highkey);
	}
	
	public IndexScanOperator(TableInfo tableInfo, Table tbl, String indexFile, int lowkey, int highkey) {
		this(tableInfo, tbl.getAlias() == null ? tbl.getName() : tbl.getAlias(), indexFile, lowkey, highkey);
	}

	/* ===============================================
	 * Methods
	 * =============================================== */
	@Override
	public HashMap<String, Integer> getSchema() {
		return schema;
	}

	@Override
	public Tuple getNextTuple() {
		if (listIndex >= currLeaf.getValues().size()) {
			leafIndex++;
			listIndex = 0;
		}
		if (leafIndex >= currLeaf.getKeys().size()) {
			currLeaf = bTree.readNextLeaf(currLeaf);
			leafIndex = 0;
		}
		if (currLeaf == null || currLeaf.getKeys().get(leafIndex) > highkey) {
			return null;
		}
		
		RecordID rid = currLeaf.getValues().get(leafIndex).get(listIndex);
		listIndex++;
		reader.reset(rid.pageid, rid.tupleid);
		return reader.read();
	}

	@Override
	public void reset() {
		this.currLeaf = bTree.search(lowkey);
	}

	@Override
	public void reset(int index) {
		
	}

	@Override
	public void close() {
		reader.close();
		bTree.close();
	}

}
