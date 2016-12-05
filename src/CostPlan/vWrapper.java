package CostPlan;

import java.util.ArrayList;

public class vWrapper {
	
	public String encoding;
	
	public vWrapper(TableSet2 tableSet, String attribute) {
		ArrayList<String> tables = new ArrayList<String>(tableSet.getTables());
		
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (String table : tables)
			sb.append("/" + table + "/");
		sb.append("]");
		sb.append(attribute);
		
		this.encoding = sb.toString();
	}
	
	@Override
	public int hashCode() {
		return this.encoding.hashCode();
	}
	
	@Override 
	public boolean equals(Object o) {
		if (o instanceof vWrapper) {
			vWrapper v = (vWrapper) o;
			return this.encoding.equals(v.encoding);
		}
		return false;				
	}
}
