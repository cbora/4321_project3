package CostPlan;

import java.util.ArrayList;
import java.util.HashMap;

import Operators.OneTableOperator;
import Project.UnionFind;

public class JoinOrder {

	private ArrayList<OneTableOperator> children;
	private UnionFind union;
	private ArrayList<TableSingle> baseTables;
	private HashMap<vWrapper, Long> vVals;
	private HashMap<String, Integer> table_mapping;
	
	public JoinOrder(ArrayList<OneTableOperator> children, UnionFind union) {
		this.union = union;
		this.children = children;
		this.vVals = new HashMap<vWrapper, Long>();
		this.table_mapping = new HashMap<String, Integer>();
		this.baseTables = new ArrayList<TableSingle>();
		loop();
	}
	
	public HashMap<String, Integer> getTableMapping() {
		return this.table_mapping;
	}
	
	private void loop() {
		int n = children.size();
		
		ArrayList<TableSet2> prevSet = new ArrayList<TableSet2>();
		for(int i=0; i<n; i++){
			TableSingle base = new TableSingle(children.get(i).getTableID(), children.get(i), this.vVals, this.union);
			prevSet.add(base);
			baseTables.add(base);
		}
		
		ArrayList<TableSet2> nextset = new ArrayList<TableSet2>();

		while(prevSet.size() > 1){
		
			for(TableSet2 ts : prevSet){
				//System.out.println(ts.getTables());
				for (int i = 0; i < children.size(); i++) {
					String s = children.get(i).getTableID();
					//System.out.println(s);
					if(!(ts.getTables().contains(s))){
						//System.out.println("inside if: " + s);
						TableSet2 newts = new TableMulti(ts, baseTables.get(i), this.vVals, this.union);
						System.out.println(newts.getTables() + " cost: " + newts.getCost() + ", size: " + newts.getnTuples());
						eliminate(nextset, newts);
					}
				}	
				//System.out.println("--");
			}
			
			prevSet = nextset;
			nextset = new ArrayList<TableSet2>();
			
			//System.out.println("after: " + prevSet.size());
			//for (TableSet2 ts : prevSet)
				//System.out.println(ts.getTables());
			//System.out.println("--");
			
		}
		
		System.out.println("Cost of plan: " + prevSet.get(0).getCost());
		
		for (TableSet2 result : prevSet) {
			int i = 0;
			for (String table : result.getTables()) {
				table_mapping.put(table, i);
				i++;
			}
		}
	}
	
	private void eliminate(ArrayList<TableSet2> newts, TableSet2 ts) {
		
		for(int i=0; i<newts.size(); i++){
			if(ts.getTables().containsAll(newts.get(i).getTables())){
				if(ts.getCost() < newts.get(i).getCost()){
					newts.set(i, ts);					
				}
				return;
			}
		}
		newts.add(ts);
	}


}


