package CostPlan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class JoinOrder {

	private ArrayList<String> relations;	
	private ArrayList<TableSet> sets = new ArrayList<TableSet>();
	
	
	public JoinOrder() {
	}
	
	
	
	public void loop() {
		int n = relations.size();
		
		ArrayList<TableSet> prevSet = new ArrayList<TableSet>();
		for(int i=0; i<n; i++){
			prevSet.add(new TableSet(relations.get(i)));
		}
		ArrayList<TableSet> nextset = new ArrayList<TableSet>();
		
		while(true){
			for(TableSet ts : prevSet){
				for(String s : relations) {
					if(!(ts.getSubset().contains(s))){
						TableSet newts = new TableSet(ts, s);
						eliminate(nextset, newts);
						nextset.add(newts);
					}
				}				
			}
			prevSet = nextset;
			nextset = new ArrayList<TableSet>();
			if(prevSet.size() == 1)
				break;
		}
	}
	
	public void eliminate(ArrayList<TableSet> newts, TableSet ts) {
		
		for(int i=0; i<newts.size(); i++){
			if(ts.equals(newts.get(i))){
				if(ts.getCost() < newts.get(i).getCost()){
					newts.set(i, ts);					
				}
				return;
			}
		}
		newts.add(ts);
	}


}


