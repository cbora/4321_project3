package CostPlan;

import java.util.HashMap;
import java.util.LinkedHashSet;

import Project.UnionFind;

/**
 * Represents node in left deep join plan
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public abstract class TableSet2 {

	/* ================================== 
	 * Fields
	 * ================================== */
	protected long cost; // cost of plan
	protected long nTuples; // size of plan
	protected LinkedHashSet<String> tables; // tables in plan
	protected HashMap<vWrapper, Long> vVals; // vval map
	protected UnionFind union; // union find structor
	

	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param vVals - vval map
	 * @param union - union find structure
	 */
	public TableSet2(HashMap<vWrapper, Long> vVals, UnionFind union) {
		this.vVals = vVals;
		this.union = union;
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	
	/**
	 * computes the vval associates with attr
	 * @param attr - attribute we want the vval of
	 * @return v value 
	 */
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
