package Project;

import java.util.HashMap;

/**
 * Singleton that maps table name to info about said table
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class DbCatalog {
	
	/* ================================== 
	 * Fields
	 * ================================== */
	private static DbCatalog instance = null; // singleton instance
	private HashMap<String, TableInfo> hash; // maps tableName to info about that table
	
	/* ================================== 
	 * Constructor
	 * ================================== */
	private DbCatalog() {
		hash = new HashMap<String, TableInfo>();
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	/**
	 * retrieves singleton instance of DbCatalog
	 * @return instance of DbCatalog
	 */
	public static DbCatalog getInstance() {
		if (instance == null)
			instance = new DbCatalog();
		return instance;
	}
	
	/**
	 * Adds table to catalog
	 * @param tableName - name of table we are adding
	 * @param t - info about that table
	 */
	public void addTable(String tableName, TableInfo t) {
		hash.put(tableName, t);
	}
	
	/**
	 * Looks up table with name "tableName" in DbCatalog
	 * @param tableName - name of table we are looking up
	 * @return - info about that table if it exists, null otherwise
	 */
	public TableInfo get(String tableName) {
		return hash.get(tableName);
	}
}
