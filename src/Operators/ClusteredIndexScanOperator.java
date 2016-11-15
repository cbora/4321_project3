package Operators;

import Project.TableInfo;
import Project.Tuple;
import net.sf.jsqlparser.schema.Table;

/**
 * Index Scan Operator for clustered indexes
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class ClusteredIndexScanOperator extends IndexScanOperator {

	/* =====================================
	 * Fields
	 * ===================================== */
	private int pageid; // starting page id
	private int tupleid; // starting tuple id
	
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
	public ClusteredIndexScanOperator(TableInfo tableInfo, String tableID, int lowkey, int highkey) {
		super(tableInfo, tableID, lowkey, highkey);
		this.pageid = -1;
		this.tupleid = -1;
	}
	
	/**
	 * Constructor
	 * @param tableInfo - info about table we are reading from, uses table name as table id
	 * @param lowkey - lowkey of range we want
	 * @param highkey - highkey of range we want
	 */
	public ClusteredIndexScanOperator(TableInfo tableInfo, int lowkey, int highkey) {
		super(tableInfo, tableInfo.getTableName(), lowkey, highkey);
	}
	
	/**
	 * Constructor
	 * @param tableInfo - info about table we are reading from
	 * @param table - table we are reading from. uses alias as id if it exists, name otherwise
	 * @param lowkey - lowkey of range we want
	 * @param highkey - highkey of range we want
	 */
	public ClusteredIndexScanOperator(TableInfo tableInfo, Table tbl, int lowkey, int highkey) {
		super(tableInfo, tbl.getAlias() == null ? tbl.getName() : tbl.getAlias(), lowkey, highkey);
	}
	
	/* ===============================================
	 * Methods
	 * =============================================== */
	@Override
	public Tuple getNextTuple() {
		// if we have not determined pageid, tupleid of first tuple in range, 
		// determine them and point reader there
		if (this.pageid == -1 && this.tupleid == -1) {
			int i = 0;
			while (i < currLeaf.getKeys().size() && currLeaf.getKeys().get(i) < lowkey)
				i++;
			if (i == currLeaf.getKeys().size()) {
				return null;
			}
			else {
				this.pageid = currLeaf.getValues().get(i).get(0).pageid;
				this.tupleid = currLeaf.getValues().get(i).get(0).tupleid;
				this.reader.reset(this.pageid, this.tupleid);
			}
		}
		
		// read next value from reader. if we pass highkey or finish reading file, return null
		Tuple t = reader.read();
		return (t == null || t.getVal(0) > highkey) ? null : t;
	}

	@Override
	public void reset() {
		reader.reset(pageid, tupleid);
	}
	
	@Override
	public void reset(int index) {
		reader.reset();
		for (int i = 0; i < index; i++) {
			if (getNextTuple() == null)
				break;
		}
	}
}
