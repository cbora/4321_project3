package Indexing;

/**
 * container for index information
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class IndexInfo {

	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	private String indexPath; // path to index
	private String attribute; // column name of column index is on
	private boolean clustered; // is it clustered
	private int D; // order of tree
	
	/*
	 * ================================== 
	 * Constructors
	 * ==================================
	 */
	/**
	 * Constructor
	 * @param indexPath - path to index
	 * @param attribute - column index is on
	 * @param clustered - is index clustered
	 * @param order - order of tree index
	 */
	public IndexInfo(String indexPath, String attribute, boolean clustered, int order) {
		this.indexPath = indexPath;
		this.attribute = attribute;
		this.clustered = clustered;
		this.D = order;
	}
	
	/**
	 * Constructor
	 * @param indexPath - path to index
	 * @param attribute - column index is on
	 * @param clustered - is index clustered
	 * @param order - order of tree index
	 */
	public IndexInfo(String indexPath, String attribute,String clustered, String order) {
		boolean c = clustered.equals("0") ? false : true;
		int o = Integer.parseInt(order);
		this.indexPath= indexPath;
		this.attribute  = attribute;
		this.clustered = c;
		this.D = o;
		
	}

	/*
	 * ================================== 
	 * Constructors
	 * ==================================
	 */
	
	/**
	 * 
	 * @return indexPath
	 */
	public String getIndexPath() {
		return indexPath;
	}

	/**
	 * 
	 * @param indexPath
	 */
	public void setIndexPath(String indexPath) {
		this.indexPath = indexPath;
	}

	/**
	 * 
	 * @return attribute (i.e. column name)
	 */
	public String getAttribute() {
		return attribute;
	}

	/**
	 * 
	 * @param attribute
	 */
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}

	/**
	 * 
	 * @return clustered
	 */
	public boolean isClustered() {
		return clustered;
	}

	/**
	 * 
	 * @param clustered
	 */
	public void setClustered(boolean clustered) {
		this.clustered = clustered;
	}

	/**
	 * 
	 * @return D (i.e. order of tree)
	 */
	public int getD() {
		return D;
	}

	/**
	 * 
	 * @param D
	 */
	public void setD(int D) {
		this.D = D;
	}

	
}
