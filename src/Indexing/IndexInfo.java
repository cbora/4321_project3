package Indexing;

public class IndexInfo {

	private String indexPath;
	private String attribute;
	private boolean clustered;
	private int D;

	public IndexInfo(String indexPath, String attribute, boolean clustered, int order) {
		this.indexPath = indexPath;
		this.attribute = attribute;
		this.clustered = clustered;
		this.D = order;
	}
	
	public IndexInfo(String indexPath, String attribute,String clustered, String order) {
		boolean c = clustered.equals("0") ? false : true;
		int o = Integer.parseInt(order);
		this.indexPath= indexPath;
		this.attribute  = attribute;
		this.clustered = c;
		this.D = o;
		
	}

	public String getIndexPath() {
		return indexPath;
	}

	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}

	public String getAttribute() {
		return attribute;
	}

	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	public boolean isClustered() {
		return clustered;
	}

	public void setClustered(boolean clustered) {
		this.clustered = clustered;
	}

	public int getD() {
		return D;
	}

	public void setD(int d) {
		D = d;
	}

	
}
