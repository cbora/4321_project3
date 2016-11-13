package Indexing;

public class IndexInfo {

	public String table;
	public String attribute;
	public boolean clustered;
	public int D;

	public IndexInfo(String table, String attribute, boolean clustered, int order) {
		this.table = table;
		this.attribute = attribute;
		this.clustered = clustered;
		this.D = order;
	}
	
	public IndexInfo(String table, String attribute,String clustered, String order) {
		// TODO Auto-generated constructor stub
		boolean c = clustered.equals("0") ? true : false;
		int o = Integer.parseInt(order);
		this.table = table;
		this.attribute  = attribute;
		this.clustered = c;
		this.D = o;
		
	}

	
}
