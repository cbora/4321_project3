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
	
	public TableSingle(String table, OneTableOperator op, HashMap<vWrapper, Long> vVals, UnionFind union) {
		super(vVals, union);
		
		this.cost = 0;
		this.tables = new LinkedHashSet<String>();
		this.tables.add(table);
		
		this.op = op;
		
		this.nTuples = computeSize();
	}
	
	@Override
	public long vValCompute(String attr) {
		vWrapper key = new vWrapper(this,attr);
		if (vVals.containsKey(key))
			return vVals.get(key);

		long v;
		if (op instanceof ScanOperator)
			v = vValBase(attr, (ScanOperator) op);
		else if (op instanceof IndexScanOperator)
			v =  vValSelect(attr, (IndexScanOperator) op);
		else
			v = vValSelect(attr, (SelectOperator) op);
		vVals.put(key, v);
		return v;
	}
	
	private long computeSize() {
		if (op instanceof ScanOperator)
			return sizeBase((ScanOperator) op);
		else if (op instanceof IndexScanOperator)
			return sizeSelect((IndexScanOperator) op);
		else
			return sizeSelect((SelectOperator) op);
	}
	
	private long sizeBase(ScanOperator scan) {
		//System.out.println(this.getTables() + " cost: " + this.getCost() + ", size: " + scan.getTableInfo().getNumTuples());
		return Math.max(scan.getTableInfo().getNumTuples(), 1);
	}
	
	private long sizeSelect(SelectOperator slct) {
		TableInfo tableInfo = slct.getTableInfo();
		
		long tableSize;
		if (slct.getChild() instanceof IndexScanOperator)
			tableSize = sizeSelect((IndexScanOperator) slct.getChild());
		else
			tableSize = tableInfo.getNumTuples();
		
		HashMap<String, Pair> selectRange = slct.getSelectRange();
		
		double reduction = 1;
		for (Entry<String, Pair> entry : selectRange.entrySet()) {
			String attr = entry.getKey();
			String col = attr.substring(attr.indexOf('.') + 1);
			long max = tableInfo.getColumns().get(col).max;
			long min = tableInfo.getColumns().get(col).min;
			
			long range = Math.min(max, entry.getValue().high) - Math.max(min, entry.getValue().low) + 1;
			
			reduction *= ((double) range) / (max - min + 1);
		}
		
		//System.out.println(this.getTables() + " cost: " + this.getCost() + ", size: " + tableInfo.getNumTuples() + " * " + reduction + " = " + Math.max(1, (long) (tableInfo.getNumTuples() * reduction)));
		return Math.max(1, (long) (tableSize * reduction));
	}
	
	private long sizeSelect(IndexScanOperator idx) {
		String attr = idx.indexAttribute();
		String col = attr.substring(attr.indexOf('.') + 1);
		
		TableInfo tableInfo = idx.getTableInfo();
		long max = tableInfo.getColumns().get(col).max;
		long min = tableInfo.getColumns().get(col).min;
		
		long range = Math.min(max, idx.getHighkey()) - Math.max(min, idx.getLowkey()) + 1;
		double reduction = ((double) range) / (max - min + 1);
		
		//System.out.println(this.getTables() + " cost: " + this.getCost() + ", size: " + tableInfo.getNumTuples() + " * " + reduction + " = " + Math.max(1, (long) (tableInfo.getNumTuples() * reduction)));
		return Math.max(1, (long) (tableInfo.getNumTuples() * reduction));
	}
	
	private long vValBase(String attr, ScanOperator scan) {
		TableInfo tableInfo = scan.getTableInfo();
		String col = attr.substring(attr.indexOf('.') + 1);
		
		long max = tableInfo.getColumns().get(col).max;
		long min = tableInfo.getColumns().get(col).min;
		
		long result = max - min + 1;
		
		return Math.max(Math.min(result, tableInfo.getNumTuples()), 1);
	}
	
	private long vValSelect(String attr, SelectOperator slct) {
		TableInfo tableInfo = slct.getTableInfo();
		String col = attr.substring(attr.indexOf('.') + 1);
		long max = tableInfo.getColumns().get(col).max;
		long min = tableInfo.getColumns().get(col).min;
		
		long result;
		if (slct.getSelectRange().get(attr) != null)
			result = Math.min(max, slct.getSelectRange().get(attr).high) - Math.max(min, slct.getSelectRange().get(attr).low) + 1;
		else
			result = max - min + 1;
		
		return Math.max(Math.min(result, tableInfo.getNumTuples()), 1);
	}
	
	private long vValSelect(String attr, IndexScanOperator idx) {
		TableInfo tableInfo = idx.getTableInfo();
		String col = attr.substring(attr.indexOf('.') + 1);
		long max = tableInfo.getColumns().get(col).max;
		long min = tableInfo.getColumns().get(col).min;
		
		long result;
		if (idx.indexAttribute().equals(attr))
			result = Math.min(max, idx.getHighkey()) - Math.max(min, idx.getLowkey()) + 1;
		else
			result = max - min + 1;
		
		return Math.max(Math.min(result, tableInfo.getNumTuples()), 1);
	}
}
