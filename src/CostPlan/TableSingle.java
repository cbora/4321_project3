package CostPlan;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map.Entry;

import Operators.IndexScanOperator;
import Operators.OneTableOperator;
import Operators.ScanOperator;
import Operators.SelectOperator;
import Project.Pair;
import Project.TableInfo;
import Project.UnionFind;

public class TableSingle extends TableSet2 {

	private OneTableOperator op;
	
	public TableSingle(String table, OneTableOperator op, HashMap<vWrapper, Integer> vVals, UnionFind union) {
		super(vVals, union);
		
		this.cost = 0;
		this.tables = new LinkedHashSet<String>();
		this.tables.add(table);
		
		this.op = op;
		
		this.nTuples = computeSize();
	}
	
	@Override
	public int vValCompute(String attr) {
		vWrapper key = new vWrapper(this,attr);
		if (vVals.containsKey(key))
			return vVals.get(key);
		
		int v;
		if (op instanceof ScanOperator)
			v = vValBase(attr, (ScanOperator) op);
		else if (op instanceof IndexScanOperator)
			v =  vValSelect(attr, (IndexScanOperator) op);
		else
			v = vValSelect(attr, (SelectOperator) op);
		vVals.put(key, v);
		return v;
	}
	
	private int computeSize() {
		if (op instanceof ScanOperator)
			return sizeBase((ScanOperator) op);
		else if (op instanceof IndexScanOperator)
			return sizeSelect((IndexScanOperator) op);
		else
			return sizeSelect((SelectOperator) op);
	}
	
	private int sizeBase(ScanOperator scan) {
		return Math.max(scan.getTableInfo().getNumTuples(), 1);
	}
	
	private int sizeSelect(SelectOperator slct) {
		TableInfo tableInfo = slct.getTableInfo();
		
		HashMap<String, Pair> selectRange = slct.getSelectRange();
		
		double reduction = 1;
		for (Entry<String, Pair> entry : selectRange.entrySet()) {
			String attr = entry.getKey();
			String col = attr.substring(attr.indexOf('.') + 1);
			int max = tableInfo.getColumns().get(col).max;
			int min = tableInfo.getColumns().get(col).min;
			
			int range = Math.min(max, entry.getValue().high) - Math.max(min, entry.getValue().low) + 1;
			
			reduction *= ((double) range) / (max - min + 1);
		}
		
		return Math.max(1, (int) (tableInfo.getNumTuples() * reduction));
	}
	
	private int sizeSelect(IndexScanOperator idx) {
		String attr = idx.indexAttribute();
		String col = attr.substring(attr.indexOf('.') + 1);
		
		TableInfo tableInfo = idx.getTableInfo();
		int max = tableInfo.getColumns().get(col).max;
		int min = tableInfo.getColumns().get(col).min;
		
		int range = Math.min(max, idx.getHighkey()) - Math.max(min, idx.getLowkey()) + 1;
		double reduction = ((double) range) / (max - min + 1);
		
		return Math.max(1, (int) (tableInfo.getNumTuples() * reduction));
	}
	
	private int vValBase(String attr, ScanOperator scan) {
		TableInfo tableInfo = scan.getTableInfo();
		String col = attr.substring(attr.indexOf('.') + 1);
		
		int max = tableInfo.getColumns().get(col).max;
		int min = tableInfo.getColumns().get(col).min;
		
		int result = max - min + 1;
		
		return Math.max(Math.min(result, tableInfo.getNumTuples()), 1);
	}
	
	private int vValSelect(String attr, SelectOperator slct) {
		TableInfo tableInfo = slct.getTableInfo();
		String col = attr.substring(attr.indexOf('.') + 1);
		int max = tableInfo.getColumns().get(col).max;
		int min = tableInfo.getColumns().get(col).min;
		
		int result;
		if (slct.getSelectRange().get(attr) != null)
			result = Math.min(max, slct.getSelectRange().get(attr).high) - Math.max(min, slct.getSelectRange().get(attr).low) + 1;
		else
			result = max - min + 1;
		
		return Math.max(Math.min(result, tableInfo.getNumTuples()), 1);
	}
	
	private int vValSelect(String attr, IndexScanOperator idx) {
		TableInfo tableInfo = idx.getTableInfo();
		String col = attr.substring(attr.indexOf('.') + 1);
		int max = tableInfo.getColumns().get(col).max;
		int min = tableInfo.getColumns().get(col).min;
		
		int result;
		if (idx.indexAttribute().equals(attr))
			result = Math.min(max, idx.getHighkey()) - Math.max(min, idx.getLowkey()) + 1;
		else
			result = max - min + 1;
		
		return Math.max(Math.min(result, tableInfo.getNumTuples()), 1);
	}
}
