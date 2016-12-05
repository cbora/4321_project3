package CostPlan;

public class TableSet {

//	private LinkedHashMap<String, OneTableOperator> subset;
//	private int cost;
//	private int nTuples;
//	private HashMap<vWrapper, Integer> vVals;
//
//	//////////////////////////////////////////////////////////////////
//	
//	public TableSet(String s, OneTableOperator op) {
//		this.subset = new LinkedHashMap<String, OneTableOperator>();
//		this.subset.put(s, op);
//		
//		this.vVals = new HashMap<vWrapper,Integer>();
//		vValLeaf(s, op);
//		
//		this.cost = 0;
//		this.nTuples = op.getTableInfo().getNumTuples();
//	}
//	
//	public TableSet(TableSet ts, String s,OneTableOperator op){
//		this.subset = new LinkedHashMap<String, OneTableOperator>();
//		this.subset.putAll(ts.getSubset());
//		this.subset.put(s, op);
//		
//		this.vVals = new HashMap<vWrapper, Integer>();
//		this.vVals.putAll(ts.getvVals());
//		vValLeaf(s, op);
//		
//		if (ts.getSize() > 1) {
//			this.cost = ts.getCost() + ts.getnTuples();
//		}
//		else{
//			this.cost = 0;
//		}
//		
//		this.nTuples = computeSize(ts, s, op);	
//	}
//	
//	///////////////////////////////////////////////////////////////////////////////
//	
//	private void vValLeaf(String s, OneTableOperator op) {		
//		for (ColumnInfo colInfo : op.getTableInfo().getColumns().values()) {
//			String attr = s + "." + colInfo.column;
//			
//			int v, size;
//			if (op instanceof ScanOperator) {
//				v = vValBase(attr, (ScanOperator) op);
//				size = sizeBase((ScanOperator) op);
//			}
//			else if (op instanceof SelectOperator) {
//				v = vValSelect(attr, (SelectOperator) op);
//				size = sizeSelect((SelectOperator) op);
//			}
//			else {
//				v = vValSelect(attr, (IndexScanOperator) op);
//				size = sizeSelect((IndexScanOperator) op);
//			}
//			
//			v = Math.min(v, size);
//			
//			this.vVals.put(new vWrapper(this, attr), v);
//		}
//	}
//	
//	private int vValBase(String attr, ScanOperator scan) {
//		TableInfo tableInfo = scan.getTableInfo();
//		int max = tableInfo.getColumns().get(attr).max;
//		int min = tableInfo.getColumns().get(attr).min;
//		
//		return max - min + 1;
//	}
//	
//	private int sizeBase(ScanOperator scan) {
//		return scan.getTableInfo().getNumTuples();
//	}
//	
//	private int vValSelect(String attr, SelectOperator slct) {
//		TableInfo tableInfo = slct.getTableInfo();
//		int max = tableInfo.getColumns().get(attr).max;
//		int min = tableInfo.getColumns().get(attr).min;
//		
//		if (slct.getSelectRange().get(attr) != null)
//			return Math.min(max, slct.getSelectRange().get(attr).high) - Math.max(min, slct.getSelectRange().get(attr).low) + 1;
//		else
//			return max - min + 1;
//	}
//	
//	private int sizeSelect(SelectOperator slct) {
//		TableInfo tableInfo = slct.getTableInfo();
//		
//		HashMap<String, Pair> selectRange = slct.getSelectRange();
//		
//		double reduction = 1;
//		for (Entry<String, Pair> entry : selectRange.entrySet()) {
//			String attr = entry.getKey();
//			int max = tableInfo.getColumns().get(attr).max;
//			int min = tableInfo.getColumns().get(attr).min;
//			
//			int range = Math.min(max, entry.getValue().high) - Math.max(min, entry.getValue().low) + 1;
//			
//			reduction *= ((double) range) / (max - min + 1);
//		}
//		
//		return Math.max(1, (int) (tableInfo.getNumTuples() * reduction));
//	}
//	
//	private int vValSelect(String attr, IndexScanOperator idx) {
//		TableInfo tableInfo = idx.getTableInfo();
//		int max = tableInfo.getColumns().get(attr).max;
//		int min = tableInfo.getColumns().get(attr).min;
//		
//		if (idx.indexAttribute().equals(attr))
//			return Math.min(max, idx.getHighkey()) - Math.max(min, idx.getLowkey()) + 1;
//		else
//			return max - min + 1;
//	}
//	
//	private int sizeSelect(IndexScanOperator idx) {
//		String attr = idx.indexAttribute();
//		
//		TableInfo tableInfo = idx.getTableInfo();
//		int max = tableInfo.getColumns().get(attr).max;
//		int min = tableInfo.getColumns().get(attr).min;
//		
//		int range = Math.min(max, idx.getHighkey()) - Math.max(min, idx.getLowkey()) + 1;
//		double reduction = ((double) range) / (max - min + 1);
//		
//		return Math.max(1, (int) (tableInfo.getNumTuples() * reduction));
//	}
//	
//	////////////////////////////////////////////////////////////////////////////////
//	
//	private int computeSize(TableSet ts, String s, OneTableOperator op) {
//		int leftSize = ts.getnTuples();
//		int rightSize;
//		if (op instanceof ScanOperator)
//			rightSize = sizeBase((ScanOperator) op);
//		else if (op instanceof SelectOperator)
//			rightSize = sizeSelect((SelectOperator) op);
//		else
//			rightSize = sizeSelect((IndexScanOperator) op);
//		
//		ArrayList<AttrPair> joinAttrs; // populate
//		
//		for (AttrPair p : joinAttrs) {
//			String leftAttr = p.left;
//			String rightAttr = p.right;
//			
//			int leftV = this.vVals.get(new vWrapper(ts, leftAttr));
//			int rightV = this.vVals.get(new vWrapper(s, rightAttr));
//			
//			p.leftV = leftV;
//			p.rightV = rightV;
//		}
//		
//		
//		int numerator = leftSize * rightSize;
//		int denom = 1;
//		for (AttrPair p : joinAttrs)
//			denom *= Math.max(p.leftV, p.rightV);
//		
//		return Math.max(numerator / denom, 1);
//		
//	}
//	
////	private int computeSize(TableSet ts, String s, OneTableOperator op) {
////		int leftSize = ts.getnTuples();
////		//rightSize = table size for scan
////		//rightSize = table size * reduction for select
////			
////		ArrayList<AttrPair> joinAttrs; // populate
////
////		for(AttrPair p : joinAttrs) {
////			String leftAttr = p.left;
////			String rightAttr = p.right;
////			
////			int leftV;
////			if (ts.getSize() == 1) {
////				OneTableOperator op2 = null;
////				
////				for (OneTableOperator x : ts.getSubset().values()) {
////					op2 = x;
////				}
////				
////				if (op2 instanceof ScanOperator) { // just base table
////					ScanOperator scan = (ScanOperator) op2;
////					TableInfo tableInfo = scan.getTableInfo();
////					int max = tableInfo.getColumns().get(leftAttr).max;
////					int min = tableInfo.getColumns().get(leftAttr).min;
////					leftV = Math.min(max - min + 1, tableInfo.getNumTuples());
////				}
////				else {
////					TableInfo tableInfo;
////					if (op2 instanceof SelectOperator) {
////						SelectOperator slct = (SelectOperator) op2;
////						tableInfo = slct.getTableInfo();
////						leftV = vValSelect(leftAttr, slct);
////					}
////					else {
////						IndexScanOperator idx = (IndexScanOperator) op2;
////						tableInfo = idx.getTableInfo();
////						leftV = vValSelect(leftAttr, idx);
////					}
////					leftV = Math.min(leftV, leftSize);
////				}
////			}
////			else {
////				// else (join)
////				//leftV = vValJoin()
////			}
////			
////			int rightV;
////			if (op instanceof ScanOperator) {
////				ScanOperator scan = (ScanOperator) op;
////				TableInfo tableInfo = scan.getTableInfo();
////				int max = tableInfo.getColumns().get(rightAttr).max;
////				int min = tableInfo.getColumns().get(rightAttr).min;
////				rightV = Math.min(max - min + 1, tableInfo.getNumTuples());
////			}
////			else {
////				TableInfo tableInfo;
////				if (op instanceof SelectOperator) {
////					SelectOperator slct = (SelectOperator) op;
////					tableInfo = slct.getTableInfo();
////					rightV = vValSelect(rightAttr, slct);
////				}
////				else {
////					IndexScanOperator idx = (IndexScanOperator) op;
////					tableInfo = idx.getTableInfo();
////					rightV = vValSelect(leftAttr, idx);
////				}
////				rightV = Math.min(rightV, rightSize);
////			}
////			
////			p.leftV = leftV;
////			p.rightV = rightV;
////		}
////		
////		
////		int numerator = leftSize * rightSize;
////		int denom = 1;
////		for (AttrPair p : joinAttrs)
////			denom *= Math.max(p.leftV, p.rightV);
////		
////		return Math.max(numerator / denom, 1);
////	}
//	
//	private int selectComputeSize() {
//		return 0;
//	}
//	
//	private int vValJoin() {
//		// let ts = {R S}
//		// if R and S are joined on A
//			// find all attributes equated to A
//			// return min of all vvals
//		// else
//			// return vVal of whichever relation it comes from
//		return 0;
//	}
//	
//	/////////////////////////////////////////////////////////////////////////////////
//	
//	public int getSize() {
//		return subset.size();
//	}
//	
//	public int compareTo(TableSet other) {
//		return this.getSubset().size() - other.getSubset().size();
//	}
//
//	/**
//	 * @return the subset
//	 */
//	public LinkedHashMap<String, OneTableOperator> getSubset() {
//		return subset;
//	}
//
//	/**
//	 * @param subset the subset to set
//	 */
//	public void setSubset(LinkedHashMap<String, OneTableOperator> subset) {
//		this.subset = subset;
//	}
//
//	/**
//	 * @return the cost
//	 */
//	public int getCost() {
//		return cost;
//	}
//
//	/**
//	 * @param cost the cost to set
//	 */
//	public void setCost(int cost) {
//		this.cost = cost;
//	}
//
//	/**
//	 * @return the nTuples
//	 */
//	public int getnTuples() {
//		return nTuples;
//	}
//
//	/**
//	 * @param nTuples the nTuples to set
//	 */
//	public void setnTuples(int nTuples) {
//		this.nTuples = nTuples;
//	}
//	
//	public HashMap<vWrapper, Integer> getvVals() {
//		return this.vVals;
//	}
//
//	///////////////////////////////////////////////////////////////////////////////////
//	
//	private class AttrPair {
//		String left;
//		String right;
//		int leftV;
//		int rightV;
//		
//		public AttrPair(String  left, String right) {
//			this.left = left;
//			this.right = right;
//			this.leftV = 0;
//			this.rightV = 0;
//		}
//	}
//	
//	protected class vWrapper {
//		//public TableSet tableSet;
//		//public String attribute;
//		public String encoding;
//		
//		public vWrapper(TableSet tableSet, String attribute) {
//			//this.tableSet = tableSet;
//			//this.attribute = attribute;
//			
//			ArrayList<String> tables = new ArrayList<String>(tableSet.getSubset().keySet());
//			Collections.sort(tables);
//			
//			StringBuffer sb = new StringBuffer();
//			sb.append("[");
//			for (String table : tables)
//				sb.append("/" + table + "/");
//			sb.append("]");
//			sb.append(attribute);
//			
//			encoding = sb.toString();
//		}
//		
//		public vWrapper(String tbl, String attribute) {
//			encoding = "[/" + tbl + "/]" + attribute;
//		}
//		
//		@Override
//		public int hashCode() {
//			return encoding.hashCode();
//		}
//		
//		@Override 
//		public boolean equals(Object o) {
//			if (o instanceof vWrapper) {
//				vWrapper v = (vWrapper) o;
//				//return v.tableSet.equals(tableSet) && v.attribute.equals(attribute);
//				return this.encoding.equals(v.encoding);
//			}
//			return false;				
//		}
//	}
}
