package CostPlan;

import java.util.HashMap;
import java.util.LinkedHashSet;

import Project.UnionFind;

public abstract class TableSet2 {

	/* ================================== 
	 * Fields
	 * ================================== */
	protected int cost;
	protected int nTuples;
	protected LinkedHashSet<String> tables;
	protected HashMap<vWrapper, Integer> vVals;
	protected UnionFind union;
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param vVals
	 * @param union
	 */
	public TableSet2(HashMap<vWrapper, Integer> vVals, UnionFind union) {
		this.vVals = vVals;
		this.union = union;
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	
	public abstract int vValCompute(String attr);
	
	/**
	 * 
	 * @return cost
	 */
	public int getCost() {
		return this.cost;
	}
	
	/**
	 * 
	 * @return number of tuples
	 */
	public int getnTuples() {
		return this.nTuples;
	}
	
	/**
	 * 
	 * @return table size
	 */
	public int getSetSize() {
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
