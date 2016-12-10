package Operators;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import IO.BinaryTupleReader;
import Indexing.BPlusTree;
import Indexing.LeafNode;
import Project.ColumnInfo;
import Project.TableInfo;
import Project.Tuple;
import net.sf.jsqlparser.schema.Table;

/**
 * Index Scan Operator abstract class
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public abstract class IndexScanOperator extends OneTableOperator {

	/* =====================================
	 * Fields
	 * ===================================== */
	protected int lowkey; // lowkey of range we care about
	protected int highkey; // highkey of range we care about
	protected BPlusTree bTree; // b+ tree index
	protected LeafNode currLeaf; // leaf node we are on right now
	
	protected HashMap<String, Integer> schema; // schema of the tuples we are reading
	protected TableInfo tableInfo; // info about table we are reading from
	protected ColumnInfo colInfo;
	protected String tableID; // id we are using for table
	protected BinaryTupleReader reader; // reads from table file

	protected final static int PAGE_SIZE = 4096;
	//protected int cost;
	/* =====================================
	 * Constructors
	 * ===================================== */
	/**
	 * Constructor
	 * @param tableInfo - info about table we are reading from
	 * @param tableID - id of table we are reading from
	 * @param lowkey - lowkey of range we want
	 * @param highkey - highkey of range we want
	 */
	public IndexScanOperator(TableInfo tableInfo, String tableID, ColumnInfo colInfo, int lowkey, int highkey) {
		super();
		
		this.tableInfo = tableInfo;
		this.colInfo = colInfo;
		this.tableID = tableID;

		LinkedHashMap<String, ColumnInfo> columns = tableInfo.getColumns();
		this.schema = new HashMap<String, Integer>();
		
		this.reader = new BinaryTupleReader(this.tableInfo.getFilePath());
		
		// Read from columns in tableInfo
		// Add (<alias> + "." + <name of column i>, i) to hash map
		for (Map.Entry<String, ColumnInfo> entry : columns.entrySet()) {
			this.schema.put(this.tableID + "." + entry.getKey(), entry.getValue().pos);
		}
		
		String indexFile = colInfo.getIndexPath();
		this.bTree = new BPlusTree(indexFile);
		this.lowkey = lowkey;
		this.highkey = highkey;	
		this.currLeaf = this.bTree.search(lowkey);
	}
	
	/**
	 * Constructor
	 * @param tableInfo - info about table we are reading from
	 * @param tableID - id of table we are reading from
	 * @param lowkey - lowkey of range we want
	 * @param highkey - highkey of range we want
	 */
	public IndexScanOperator(TableInfo tableInfo, ColumnInfo colInfo, int lowkey, int highkey) {
		this(tableInfo, tableInfo.getTableName(), colInfo, lowkey, highkey);
	}
	
	/**
	 * Constructor
	 * @param tableInfo - info about table we are reading from
	 * @param tableID - id of table we are reading from
	 * @param lowkey - lowkey of range we want
	 * @param highkey - highkey of range we want
	 */
	public IndexScanOperator(TableInfo tableInfo, ColumnInfo colInfo, Table tbl, int lowkey, int highkey) {
		this(tableInfo, tbl.getAlias() == null ? tbl.getName() : tbl.getAlias(), colInfo, lowkey, highkey);
	}
	
	/**
	 * pretty print method
	 * @param depth
	 * @return this method's name
	 */
	public String prettyPrint(int depth){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<depth; i++)
			sb.append("-");
		sb.append("IndexScan");
		sb.append("[");
		sb.append(this.tableInfo.getTableName());
		sb.append("]\n");
		return sb.toString();
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
	
	public TableInfo getTableInfo() {
		return this.tableInfo;
	}
	
	public String getTableID() {
		return this.tableID;
	}
	
	public int getLowkey() {
		return lowkey;
	}
	
	public int getHighkey() {
		return highkey;
	}
	
	public String indexAttribute() {
		return this.tableID + "." + this.colInfo.column;
	}
	
//	public int getRelationSize() {
//		return this.cost;
//	}
	
//	protected void calculateIndexSize() {
//		double r = ((double) (Math.min(highkey, colInfo.max) - Math.max(lowkey, colInfo.min) +1)) / (colInfo.max - colInfo.min + 1);
//		this.cost = (int) (tableInfo.getNumTuples() * r);
//	}
}
