package Operators;

import Project.TableInfo;

/**
 * Abstract class to map TableInfo to a table ID
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public abstract class OneTableOperator extends Operator {

	public abstract TableInfo getTableInfo();
	
	public abstract String getTableID();

}
