package Project;

import java.util.ArrayList;

public class TableInfo {
	
	private String filePath; // path to file containing table
	private String tableName; // name of table
	private ArrayList<String> columns; // name of the columns in order
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	public TableInfo(String fileName, String tableName) {
		this.filePath = fileName;
		this.tableName = tableName;
		this.columns = new ArrayList<String>();
	}

	/* ================================== 
	 * Methods
	 * ================================== */
	/**
	 * getter for fileName
	 * @return this.fileName
	 */
	public String getFileName() {
		return filePath;
	}

	/**
	 * setter for fileName
	 * @param fileName
	 */
	public void setFileName(String fileName) {
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
	public ArrayList<String> getColumns() {
		return columns;
	}	
	
}
