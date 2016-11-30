package Project;

import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Container to hold info about tables
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class TableInfo {
	
	/* ================================== 
	 * Fields
	 * ================================== */
	private String filePath; // path to file containing table
	private String tableName; // name of table
	private int nTuples;
	private LinkedHashMap<String, ColumnInfo> columns; // name of the columns in order
	private String clusteredIndex;
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param filePath - path to table's data file
	 * @param tableName - name of table
	 */
	public TableInfo(String filePath, String tableName) {
		this.filePath = filePath;
		this.tableName = tableName;
		this.nTuples = 0;
		this.columns = new LinkedHashMap<String, ColumnInfo>();
		this.clusteredIndex = null;
	}

	/* ================================== 
	 * Methods
	 * ================================== */
	/**
	 * getter for filePath
	 * @return this.filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 * setter for filePath
	 * @param filePath
	 */
	public void setFilePath(String fileName) {
		this.filePath = fileName;
	}
	
	/**
	 * getter for tableName
	 * @return tableName
	 */
	public String getTableName() {
		return tableName;
	}

	/**
	 * setter for tableName
	 * @param tableName
	 */
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	/**
	 * getter for list of columns
	 * @return ArrayList of columns in table
	 */
	public LinkedHashMap<String, ColumnInfo> getColumns() {
		return columns;
	}	
	
	public int getNumTuples() {
		return this.nTuples;
	}
	
	public void setNumTuples(int n) {
		this.nTuples = n;
	}
	
	public String getClusteredIndex() {
		return this.clusteredIndex;
	}
	
	public void setClusteredIndex(String clusteredIndex) {
		this.clusteredIndex = clusteredIndex;
	}
}






