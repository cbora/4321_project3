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
	private HashMap<String, Integer> projection; // projected schema, and use linkedHashMap to preserve insertion order
	private int index;
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	public EvalSelectItemVisitor(ArrayList<SelectItem> items, HashMap<String, Integer> schema) {
		this.schema = schema;
		this.projection = new LinkedHashMap<String, Integer>();
		for (this.index=0; this.index < items.size(); this.index++) {
			items.get(this.index).accept(this);	
		}
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	public HashMap<String, Integer> getResult() {
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
		String tbl = col.getTable().getAlias() == null ? col.getTable().getName() : col.getTable().getAlias();
		projection.put(tbl + "." + col.getColumnName(), this.index);
	}
	
	
	// NOT TO IMPLEMENT
	@Override
	public void visit(AllTableColumns allTableColumns){
		// not required to implement
	}
	

}
