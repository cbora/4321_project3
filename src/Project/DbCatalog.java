package Project;

public class DbCatalog {
	
	private static DbCatalog instance = null;
	
	private DbCatalog() {}
	
	public DbCatalog getInstance() {
		if (instance == null)
			instance = new DbCatalog();
		return instance;
	}
}
