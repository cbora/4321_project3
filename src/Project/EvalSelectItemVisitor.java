package Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.AllTableColumns;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.SelectItemVisitor;


public class EvalSelectItemVisitor implements SelectItemVisitor {

	private HashMap<String, Integer> schema;
	private LinkedHashMap<String, Integer> projection; // projected schema, and use linkedHashMap to preserve insertion order
	
	public EvalSelectItemVisitor(ArrayList<SelectItem> items, HashMap<String, Integer> schema) {
		this.schema = schema;
		this.projection = new LinkedHashMap<String, Integer>();
		for (int i=0; i<items.size(); i++) {
			items.get(i).accept(this);	
		}
	}
	
	public LinkedHashMap<String, Integer> getResult() {
		return projection;
	}
	
	/**
	 * @param allColumns
	 * 		simply copy over the original schema
	 */
	@Override
	public void visit(AllColumns allColumns){
		projection.putAll(schema);
	}
	
	/** 
	 * @param s
	 * 
	 * 		
	 */
	@Override
	public void visit(SelectExpressionItem s){
		Column col = (Column) s.getExpression();
		String name = col.getWholeColumnName();
		int index = schema.get(name);
		projection.put(col.getColumnName(), index);
	}
	
	
	// NOT TO IMPLEMENT
	@Override
	public void visit(AllTableColumns allTableColumns){
		// not required to implement
	}
	

}
