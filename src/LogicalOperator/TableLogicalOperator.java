/**
 * 
 */
package LogicalOperator;

import Operators.PhysicalPlanBuilder;
import Project.TableInfo;
import net.sf.jsqlparser.schema.Table;

/**
 * Logical operator that represents a table
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class TableLogicalOperator extends LogicalOperator {


	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	private TableInfo info; // info about table
	private String tableID; // identifier to be used for table
	
	/*
	 * ================================== 
	 * Constructor
	 * ==================================
	 */
	/**
	 * tableID Constructor
	 * @param info - info about table
	 * @param tableID - identifier to be used for table
	 */
	public TableLogicalOperator(TableInfo info, String tableID) {
		this.info = info;
		this.tableID = tableID;
	}
	
	/**
	 * table constructor
	 * @param info - info about table
	 * @param tbl - table object containing name of table (used alias if it exists, else use table name)
	 */
	public TableLogicalOperator(TableInfo info, Table tbl){
		this(info, tbl.getAlias() == null ? tbl.getName() : tbl.getAlias());
	}
	
	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */
	/**
	 * getter for tableinfo
	 * @return tableinfo
	 */
	public TableInfo getInfo() {
		return info;
	}

	/**
	 * setter for tableinfo
	 * @param info
	 */
	public void setInfo(TableInfo info) {
		this.info = info;
	}

	/**
	 * getter for table identifier
	 * @return table identifier
	 */
	public String getTableID() {
		return tableID;
	}

	/**
	 * setter for table identifier
	 * @param tableID
	 */
	public void setTableID(String tableID) {
		this.tableID = tableID;
	}

	@Override
	public void accept(PhysicalPlanBuilder ppb){
		ppb.visit(this);	
	}

}
