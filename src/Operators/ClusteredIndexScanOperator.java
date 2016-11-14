package Operators;

import Indexing.RecordID;
import Project.TableInfo;
import Project.Tuple;
import net.sf.jsqlparser.schema.Table;

public class ClusteredIndexScanOperator extends IndexScanOperator {

	private int pageid;
	private int tupleid;
	
	
	/* =====================================
	 * Constructors
	 * ===================================== */
	public ClusteredIndexScanOperator(TableInfo tableInfo, String tableID, int lowkey, int highkey) {
		super(tableInfo, tableID, lowkey, highkey);
		this.pageid = -1;
		this.tupleid = -1;
	}
	
	public ClusteredIndexScanOperator(TableInfo tableInfo, int lowkey, int highkey) {
		super(tableInfo, tableInfo.getTableName(), lowkey, highkey);
	}
	
	public ClusteredIndexScanOperator(TableInfo tableInfo, Table tbl, int lowkey, int highkey) {
		super(tableInfo, tbl.getAlias() == null ? tbl.getName() : tbl.getAlias(), lowkey, highkey);
	}
	
	/* ===============================================
	 * Methods
	 * =============================================== */

	@Override
	public Tuple getNextTuple() {
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
