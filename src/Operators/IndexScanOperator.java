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

public abstract class IndexScanOperator extends Operator {

	/* =====================================
	 * Fields
	 * ===================================== */
	protected int lowkey;
	protected int highkey;
	protected BPlusTree bTree;
	protected LeafNode currLeaf;
	
	protected HashMap<String, Integer> schema;
	protected TableInfo tableInfo;
	protected String tableID;
	protected BinaryTupleReader reader;

	/* =====================================
	 * Constructors
	 * ===================================== */
	public IndexScanOperator(TableInfo tableInfo, String tableID, int lowkey, int highkey) {
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
		
		String indexFile = tableInfo.getIndexPath();
		this.bTree = new BPlusTree(indexFile);
		this.lowkey = lowkey;
		this.highkey = highkey;	
		this.currLeaf = this.bTree.search(lowkey);

	}
	
	public IndexScanOperator(TableInfo tableInfo, int lowkey, int highkey) {
		this(tableInfo, tableInfo.getTableName(), lowkey, highkey);
	}
	
	public IndexScanOperator(TableInfo tableInfo, Table tbl, int lowkey, int highkey) {
		this(tableInfo, tbl.getAlias() == null ? tbl.getName() : tbl.getAlias(), lowkey, highkey);
	}
	
	/* ===============================================
	 * Methods
	 * =============================================== */
	@Override
	public HashMap<String, Integer> getSchema() {
		return schema;
	}
	
	@Override
	public abstract Tuple getNextTuple();

	@Override
	public abstract void reset();

	@Override
	public abstract void reset(int index);

	@Override
	public void close() {
		reader.close();
		bTree.close();
	}
}
