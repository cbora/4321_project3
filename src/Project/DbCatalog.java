package Project;

import java.util.HashMap;

public class DbCatalog {
	
	private static DbCatalog instance = null;
	private HashMap<String, TableInfo> hash;
	
	private DbCatalog() {
		hash = new HashMap<String, TableInfo>();
	}
	
	public static DbCatalog getInstance() {
		if (instance == null)
			instance = new DbCatalog();
		return instance;
	}
	
	public void addTable(String tableName, TableInfo t) {
		hash.put(tableName, t);
	}
	
	public TableInfo get(String tableName) {
		return hash.get(tableName);
	}
}
