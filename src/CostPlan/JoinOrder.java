package CostPlan;

import java.util.ArrayList;
import java.util.HashMap;

import Operators.OneTableOperator;
import Project.UnionFind;

/**
 * 
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class JoinOrder {

	/* ================================== 
	 * Fields
	 * ================================== */
	private ArrayList<OneTableOperator> children; //children
	private UnionFind union; // Unionfind object
	private ArrayList<TableSingle> baseTables; // leaf tables 
	private HashMap<vWrapper, Integer> vVals; // V-values
	private HashMap<String, Integer> table_mapping; // hashmap for mapping table names to indexes
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param children
	 * @param union
	 */
	public JoinOrder(ArrayList<OneTableOperator> children, UnionFind union) {
		this.union = union;
		this.children = children;
		this.vVals = new HashMap<vWrapper, Integer>();
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
				for (int i = 0; i < children.size(); i++) {
					String s = children.get(i).getTableID();
					if(!(ts.getTables().contains(s))){
						TableSet2 newts = new TableMulti(ts, baseTables.get(i), this.vVals, this.union);
						eliminate(nextset, newts);
					}
				}	
			}
			prevSet = nextset;
			nextset = new ArrayList<TableSet2>();			
		}
		
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


