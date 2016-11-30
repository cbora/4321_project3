package Operators;

import Indexing.LeafNode;
import Indexing.RecordID;
import Project.ColumnInfo;
import Project.TableInfo;
import Project.Tuple;
import net.sf.jsqlparser.schema.Table;

/**
 * Index Scan Operator for unclustered index
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class UnclusteredIndexScanOperator extends IndexScanOperator {
	
	/* =====================================
	 * Fields
	 * ===================================== */
	private int leafIndex; // which entry we are at in current leaf
	private int listIndex; // which list position we are at in current entry
	
	private LeafNode firstLeaf; // first leaf we care about
	private int firstLeafIndex; // first leaf index we care about
	
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
	public UnclusteredIndexScanOperator(TableInfo tableInfo, String tableID, ColumnInfo colInfo, int lowkey, int highkey) {
		super(tableInfo, tableID, colInfo, lowkey, highkey);
		
		this.firstLeaf = this.currLeaf;
		this.leafIndex = -1;
		this.firstLeafIndex = -1;
		this.listIndex = 0;
	}
	
	/**
	 * Constructor
	 * @param tableInfo - info about table we are reading from
	 * @param tableID - id of table we are reading from
	 * @param lowkey - lowkey of range we want
	 * @param highkey - highkey of range we want
	 */
	public UnclusteredIndexScanOperator(TableInfo tableInfo, ColumnInfo colInfo, int lowkey, int highkey) {
		super(tableInfo, tableInfo.getTableName(), colInfo, lowkey, highkey);
	}
	
	/**
	 * Constructor
	 * @param tableInfo - info about table we are reading from
	 * @param tableID - id of table we are reading from
	 * @param lowkey - lowkey of range we want
	 * @param highkey - highkey of range we want
	 */
	public UnclusteredIndexScanOperator(TableInfo tableInfo, Table tbl, ColumnInfo colInfo, int lowkey, int highkey) {
		super(tableInfo, tbl.getAlias() == null ? tbl.getName() : tbl.getAlias(), colInfo, lowkey, highkey);
	}

	/* ===============================================
	 * Methods
	 * =============================================== */
	@Override
	public Tuple getNextTuple() {
		// if we have not determined leafIndex yet, determine it
		if (this.leafIndex == -1) {
			int i = 0;

			while(i < currLeaf.getKeys().size() && lowkey > currLeaf.getKeys().get(i)) {
				i++;
			}
			if (i == currLeaf.getKeys().size()) {
				return null;
			}
			else {
				this.leafIndex = i;
				this.firstLeafIndex = this.leafIndex;
				this.listIndex = 0;
			}
		}
		
		// if we have finished all list items in current entry, increment leaf index
		if (listIndex >= currLeaf.getValues().get(leafIndex).size()) {
			leafIndex++;
			listIndex = 0;
		}
		
		// if we have read all items in current leaf, increment leaf
		if (leafIndex >= currLeaf.getKeys().size()) {
			currLeaf = bTree.readNextLeaf(currLeaf);
			leafIndex = 0;
			listIndex = 0;
		}
		
		// if we have read all leaves or have passed high key, return null
		if (currLeaf == null || currLeaf.getKeys().get(leafIndex) > highkey) {
			return null;
		}
		
		// get the next tuple using the recordid and increment listIndex
		RecordID rid = currLeaf.getValues().get(leafIndex).get(listIndex);
		listIndex++;
		reader.reset(rid.pageid, rid.tupleid);
		return reader.read();
	}

	@Override
	public void reset() {
		this.currLeaf = this.firstLeaf;
		this.leafIndex = this.firstLeafIndex;
		this.listIndex = 0;
	}
	
	@Override
	public void reset(int index) {
		reset();
		for (int i = 0; i < index; i++) {
			if (getNextTuple() == null)
				break;
		}
	}

}
