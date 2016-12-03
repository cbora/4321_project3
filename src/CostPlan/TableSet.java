package CostPlan;

import java.util.LinkedHashSet;

public class TableSet implements Comparable<TableSet> {

	//private ArrayList<String> subset;
	private LinkedHashSet<String> subset;
	private int cost;
	private int nTuples;

	public TableSet(String s) {
		subset.add(s);
		this.cost = 0;
	}
	
	public TableSet(TableSet ts, String s){
		subset.addAll(ts.getSubset());
		subset.add(s);
		if (ts.getSize() > 1)
			this.cost = ts.getCost() + ts.getnTuples();
		else{
			this.cost = 0;
			  
		}
		this.nTuples = computeSize(ts, s);
		
	}
	
	
	public int computeSize(TableSet ts, String s) {
		
		
		return 0;
	}
	
	
	public void vValues() {
		
	}
	
	public int getSize() {
		return subset.size();
	}
	
	public int compareTo(TableSet other) {
			return this.getSubset().size() - other.getSubset().size();
	}

	/**
	 * @return the subset
	 */
	public LinkedHashSet<String> getSubset() {
		return subset;
	}

	/**
	 * @param subset the subset to set
	 */
	public void setSubset(LinkedHashSet<String> subset) {
		this.subset = subset;
	}

	/**
	 * @return the cost
	 */
	public int getCost() {
		return cost;
	}

	/**
	 * @param cost the cost to set
	 */
	public void setCost(int cost) {
		this.cost = cost;
	}

	/**
	 * @return the nTuples
	 */
	public int getnTuples() {
		return nTuples;
	}

	/**
	 * @param nTuples the nTuples to set
	 */
	public void setnTuples(int nTuples) {
		this.nTuples = nTuples;
	}
	
	public boolean equals(Object o){
		if (o instanceof TableSet) {
			TableSet t = (TableSet) o;
			return this.getSubset().contains(t.getSubset());
		}
		return false;
	}
}
