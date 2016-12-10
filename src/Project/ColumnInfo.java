package Project;

import Indexing.IndexInfo;

/**
 * Holds info of a column including its position, min and max values
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class ColumnInfo {
	
	/* ================================== 
	 * Fields
	 * ================================== */
	public String column; // col name
	public int pos;
	public int min; // min value
	public int max; // max value
	private IndexInfo indexInfo; // index info for this column
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param column
	 * @param pos
	 */
	public ColumnInfo(String column, int pos) {
		this.column = column;
		this.min = Integer.MAX_VALUE;
		this.max = Integer.MIN_VALUE;
		this.indexInfo = null;
		this.pos = pos;
	}
	
	
	/* ================================== 
	 * Methods
	 * ================================== */
	
	/**
	 * 
	 * @param indexInfo
	 */
	public void setIndexInfo(IndexInfo indexInfo) {
		this.indexInfo = indexInfo;
	}
	
	/**
	 * 
	 * @return indexInfo
	 */
	public IndexInfo getIndexInfo() {
		return this.indexInfo;
	}
	
	/**
	 * 
	 * @return index attribute (null if no index)
	 */
	public String getIndexAttribute() {
		if (this.indexInfo == null)
			return null;
		return this.indexInfo.getAttribute();
	}
	
	/**
	 * 
	 * @return path to index file (null if no index)
	 */
	public String getIndexPath() {
		if (this.indexInfo == null)
			return null;
		return this.indexInfo.getIndexPath();
	}
	
	/**
	 * 
	 * @return whether index is clustered (false if no index)
	 */
	public boolean isClustered() {
		if (this.indexInfo == null)
			return false;
		return this.indexInfo.isClustered();
	}

}
