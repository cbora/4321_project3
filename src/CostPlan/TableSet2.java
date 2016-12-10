package CostPlan;

import java.util.HashMap;
import java.util.LinkedHashSet;

import Project.UnionFind;

public abstract class TableSet2 {

	/* ================================== 
	 * Fields
	 * ================================== */
	protected long cost;
	protected long nTuples;
	protected LinkedHashSet<String> tables;
	protected HashMap<vWrapper, Long> vVals;
	protected UnionFind union;
	

	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param vVals
	 * @param union
	 */
	public TableSet2(HashMap<vWrapper, Long> vVals, UnionFind union) {
		this.vVals = vVals;
		this.union = union;
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	
	public abstract long vValCompute(String attr);
	
	/**
	 * 
	 * @return cost
	 */
	public long getCost() {
		return this.cost;
	}
	
	/**
	 * 
	 * @return number of tuples
	 */
	public long getnTuples() {
		return this.nTuples;
	}
	
	/**
	 * 
	 * @return table size
	 */
	public long getSetSize() {
		return this.tables.size();
	}
	
	/**
	 * 
	 * @return hashset of tables
	 */
	public LinkedHashSet<String> getTables() {
		return this.tables;
	}
}
