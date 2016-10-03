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

/**
 * Provided old schema, produces what new schema is after projection
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class EvalSelectItemVisitor implements SelectItemVisitor {

	/* ================================== 
	 * Fields
	 * ================================== */
	private HashMap<String, Integer> oldSchema; // original schema
	private HashMap<String, Integer> projection; // schema after projection
	private int index;
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param items - projection items
	 * @param oldSchema - original schema
	 */
	public EvalSelectItemVisitor(ArrayList<SelectItem> items, HashMap<String, Integer> oldSchema) {
		this.oldSchema = oldSchema;
		this.projection = new LinkedHashMap<String, Integer>();
		for (this.index=0; this.index < items.size(); this.index++) {
			items.get(this.index).accept(this);	
		}
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	/**
	 * @return new schema after projection applies to original schema
	 */
	public HashMap<String, Integer> getResult() {
		return projection;
	}
	
	/**
	 * Simply copy over all columns from original schema
	 * @param allColumns
	 */
	@Override
	public void visit(AllColumns allColumns){
		projection.putAll(oldSchema);
	}
	
	/**
	 * Add ([alias == null ? tableName : alias) + "." + colName], position in Select List) to new schema
	 * @param s - column to be projected
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
