package Operators;

import Indexing.LeafNode;
import Indexing.RecordID;
import Project.TableInfo;
import Project.Tuple;
import net.sf.jsqlparser.schema.Table;

public class UnclusteredIndexScanOperator extends IndexScanOperator {
	
	private int leafIndex;
	private int listIndex;
	
	private LeafNode firstLeaf;
	private int firstLeafIndex;
	
	
	/* =====================================
	 * Constructors
	 * ===================================== */
	public UnclusteredIndexScanOperator(TableInfo tableInfo, String tableID, int lowkey, int highkey) {
		super(tableInfo, tableID, lowkey, highkey);
		
		this.firstLeaf = this.currLeaf;
		this.leafIndex = -1;
		this.firstLeafIndex = -1;
		this.listIndex = 0;
	}
	
	public UnclusteredIndexScanOperator(TableInfo tableInfo, int lowkey, int highkey) {
		super(tableInfo, tableInfo.getTableName(), lowkey, highkey);
	}
	
	public UnclusteredIndexScanOperator(TableInfo tableInfo, Table tbl, int lowkey, int highkey) {
		super(tableInfo, tbl.getAlias() == null ? tbl.getName() : tbl.getAlias(), lowkey, highkey);
	}

	/* ===============================================
	 * Methods
	 * =============================================== */

	@Override
	public Tuple getNextTuple() {
		if (this.leafIndex == -1) {
			int i = 0;
			System.out.println(currLeaf.getKeys().size() + ", " + lowkey + " < " + currLeaf.getKeys().get(i));
			while(i < currLeaf.getKeys().size() && lowkey > currLeaf.getKeys().get(i)) {
				i++;
			}
			if (i == currLeaf.getKeys().size()) {
				return null;
			}
			else {
				this.leafIndex = i;
				System.out.println("set leaf index: " + leafIndex);
				this.firstLeafIndex = this.leafIndex;
				this.listIndex = 0;
			}
		}
		
		if (listIndex >= currLeaf.getValues().get(leafIndex).size()) {
			leafIndex++;
			listIndex = 0;
		}
		if (leafIndex >= currLeaf.getKeys().size()) {
			currLeaf = bTree.readNextLeaf(currLeaf);
			leafIndex = 0;
			listIndex = 0;
		}
		if (currLeaf == null || currLeaf.getKeys().get(leafIndex) > highkey) {
			return null;
		}
		
		RecordID rid = currLeaf.getValues().get(leafIndex).get(listIndex);
		System.out.println(rid);
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
