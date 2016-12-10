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

/**
 * Object that holds only one children in the left deep tree
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class TableSingle extends TableSet2 {

	/* ================================== 
	 * Fields
	 * ================================== */
	private OneTableOperator op; // for operator used to access data
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param leftChild
	 * @param rightChild
	 * @param exp - expression for the condition we are joining on
	 * @param bSize - buffer size in pages (must be >0 otherwise defaults to 1)
	 */
	public TableSingle(String table, OneTableOperator op, HashMap<vWrapper, Long> vVals, UnionFind union) {
		super(vVals, union);
		
		this.cost = 0;
		this.tables = new LinkedHashSet<String>();
		this.tables.add(table);
		
		this.op = op;
		
		this.nTuples = computeSize();
	}
	
	/**
	 * determines which vvalue function to call
	 * @return vvalue
	 */
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
	
	/**
	 * determines function to call for computing size
	 * @return size
	 */
	private long computeSize() {
		if (op instanceof ScanOperator)
			return sizeBase((ScanOperator) op);
		else if (op instanceof IndexScanOperator)
			return sizeSelect((IndexScanOperator) op);
		else
			return sizeSelect((SelectOperator) op);
	}
	
	/** 
	 * computes size for simple scan operator
	 * @param scan
	 * @return size
	 */
	private long sizeBase(ScanOperator scan) {
		//System.out.println(this.getTables() + " cost: " + this.getCost() + ", size: " + scan.getTableInfo().getNumTuples());
		return Math.max(scan.getTableInfo().getNumTuples(), 1);
	}
	
	/**
	 * computes size for select operator
	 * @param slct
	 * @return size
	 */
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
	
	/**
	 * computes size for index scan operator
	 * @param idx
	 * @return size
	 */
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
	
	/**
	 * computes vvalue for simple scan operator
	 * @param attr
	 * @param scan
	 * @return vvalue
	 */
	private long vValBase(String attr, ScanOperator scan) {
		TableInfo tableInfo = scan.getTableInfo();
		String col = attr.substring(attr.indexOf('.') + 1);
		
		long max = tableInfo.getColumns().get(col).max;
		long min = tableInfo.getColumns().get(col).min;
		
		long result = max - min + 1;
		
		return Math.max(Math.min(result, tableInfo.getNumTuples()), 1);
	}
	
	/**
	 * computes vvalue for select operator
	 * @param attr
	 * @param slct
	 * @return vvalue
	 */
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
	
	/**
	 * compute vvlaue of index scan operator
	 * @param attr
	 * @param idx
	 * @return vvalue
	 */
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
