package Project;

import java.util.ArrayList;

public class TableInfo {
	
	private String fileName;
	private String tableName;
	private ArrayList<String> columns;
	
	public TableInfo(String fileName, String tableName) {
		this.fileName = fileName;
		this.tableName = tableName;
		this.columns = new ArrayList<String>();
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public ArrayList<String> getColumns() {
		return columns;
	}	
	
}
