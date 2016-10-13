/**
 * 
 */
package LogicalOperator;

import Project.TableInfo;
import net.sf.jsqlparser.schema.Table;

/**
 * @author cbora
 *
 */
public class TableLogicalOperator extends LogicalOperator {

	/**
	 * 
	 */
	private TableInfo info;
	private String tableID;
	
	
	public TableLogicalOperator(TableInfo info, String tableID) {
		// TODO Auto-generated constructor stub
		this.info = info;
		this.tableID = tableID;
	}
	
	public TableLogicalOperator(TableInfo info, Table tbl){
		this(info, tbl.getAlias() == null ? tbl.getName() : tbl.getAlias());
	}
	
	public TableInfo getInfo() {
		return info;
	}

	public void setInfo(TableInfo info) {
		this.info = info;
	}

	public String getTableID() {
		return tableID;
	}

	public void setTableID(String tableID) {
		this.tableID = tableID;
	}

	public void accept(PhysicalPlanBuilder ppb){
		ppb.visit(this);	
	}

}
