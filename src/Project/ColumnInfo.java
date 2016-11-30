package Project;

import Indexing.IndexInfo;

public class ColumnInfo {
	
	public String column; // col name
	public int pos;
	public int min; // min value
	public int max; // max value
	private IndexInfo indexInfo; // index info for this column
	
	public ColumnInfo(String column, int pos) {
		this.column = column;
		this.min = Integer.MAX_VALUE;
		this.max = Integer.MIN_VALUE;
		this.indexInfo = null;
		this.pos = pos;
	}
	
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
