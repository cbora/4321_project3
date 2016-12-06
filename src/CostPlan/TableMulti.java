package CostPlan;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import Project.UnionFind;
import Project.UnionFindElement;

public class TableMulti extends TableSet2 {

	private TableSet2 left;
	private TableSingle right;
	
	public TableMulti(TableSet2 left, TableSingle right, HashMap<vWrapper, Integer> vVals, UnionFind union) {
		super(vVals, union);
		
		this.left = left;
		this.right = right;
		
		this.tables = new LinkedHashSet<String>();
		this.tables.addAll(left.getTables());
		this.tables.addAll(right.getTables());
		
		if (left instanceof TableSingle) {
			if (left.getnTuples() <= right.getnTuples())
				this.cost = 0;
			else 
				this.cost = Integer.MAX_VALUE;
		}
		else {
			this.cost = this.left.getCost() + this.left.getnTuples();
		}
		
		this.nTuples = computeNumTuples();
		
	}
	
	public int vValCompute(String attr) {
		vWrapper key = new vWrapper(this,attr);
		if (vVals.containsKey(key))
			return vVals.get(key);
		
		// get all attrs equal to attr in union find
		UnionFindElement elem = this.union.find(attr);
		
		Set<String> equiAttrs = elem.getAttributes();
		int min = Integer.MAX_VALUE;
		for (String equiAttr : equiAttrs) {
			String tbl = equiAttr.substring(0, equiAttr.indexOf('.'));
			if (left.getTables().contains(tbl)) {
				int tmp = left.vValCompute(equiAttr);
				min = Math.min(tmp, min);
			}
			else if (right.getTables().contains(tbl)) {
				int tmp = right.vValCompute(equiAttr);
				min = Math.min(tmp, min);
			}
		}
		int result = Math.max(Math.min(min, this.nTuples), 1);
		
		vVals.put(key, result);
		return result;
	}
	
	private int computeNumTuples() {
		int leftSize = this.left.getnTuples();
		int rightSize = this.right.getnTuples();
		
		// find which union find elements contain info about join
		HashSet<UnionFindElement> goodPartitions = new HashSet<UnionFindElement>();
		HashSet<UnionFindElement> badPartitions = new HashSet<UnionFindElement>();
		for (String attr : this.union.getAttributes()) {
			if (badPartitions.contains(this.union.find(attr)) || goodPartitions.contains(this.union.find(attr)))
				continue;
			
			boolean inLeft = false;
			boolean inRight = false;
			for (String attr2 : this.union.find(attr).getAttributes()) {
				String tbl = attr2.substring(0, attr2.indexOf('.'));
				inLeft = this.left.getTables().contains(tbl) ? true : inLeft;
				inRight = this.right.getTables().contains(tbl) ? true : inRight;
			}
			
			if (inLeft && inRight)
				goodPartitions.add(this.union.find(attr));
			else
				badPartitions.add(this.union.find(attr));
		}
		
		// hold vVals maxes to be multiplied together
		ArrayList<Integer> vVals = new ArrayList<Integer>();
		
		for (UnionFindElement condList : goodPartitions) {
			int max = 1;
			
			for (String attr : condList.getAttributes()) {
				String tbl = attr.substring(0, attr.indexOf('.'));
				if (left.getTables().contains(tbl)) {
					int tmp = left.vValCompute(attr);
					max = Math.max(tmp, max);
				}
				else if (right.getTables().contains(tbl)) {
					int tmp = right.vValCompute(attr);
					max = Math.max(tmp, max);
				}
			}
			
			vVals.add(max);
		}
		
		int numer = leftSize * rightSize;
		int denom = 1;
		for (Integer n : vVals)
			denom *= n;
		
		return Math.max(numer / denom, 1);
	}
}
