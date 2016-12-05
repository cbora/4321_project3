package CostPlan;

import java.util.HashMap;
import java.util.LinkedHashSet;

import Project.UnionFind;

public abstract class TableSet2 {

	protected int cost;
	protected int nTuples;
	protected LinkedHashSet<String> tables;
	protected HashMap<vWrapper, Integer> vVals;
	protected UnionFind union;
	
	public TableSet2(HashMap<vWrapper, Integer> vVals, UnionFind union) {
		this.vVals = vVals;
		this.union = union;
	}
	
	public abstract int vValCompute(String attr);
	
	public int getCost() {
		return this.cost;
	}
	
	public int getnTuples() {
		return this.nTuples;
	}
	
	public int getSetSize() {
		return this.tables.size();
	}
	
	public LinkedHashSet<String> getTables() {
		return this.tables;
	}
}
