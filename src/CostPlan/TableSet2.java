package CostPlan;

import java.util.HashMap;
import java.util.LinkedHashSet;

import Project.UnionFind;

public abstract class TableSet2 {

	protected long cost;
	protected long nTuples;
	protected LinkedHashSet<String> tables;
	protected HashMap<vWrapper, Long> vVals;
	protected UnionFind union;
	
	public TableSet2(HashMap<vWrapper, Long> vVals, UnionFind union) {
		this.vVals = vVals;
		this.union = union;
	}
	
	public abstract long vValCompute(String attr);
	
	public long getCost() {
		return this.cost;
	}
	
	public long getnTuples() {
		return this.nTuples;
	}
	
	public long getSetSize() {
		return this.tables.size();
	}
	
	public LinkedHashSet<String> getTables() {
		return this.tables;
	}
}
