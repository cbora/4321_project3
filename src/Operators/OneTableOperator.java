package Operators;

import Project.TableInfo;

public abstract class OneTableOperator extends Operator {

	public abstract TableInfo getTableInfo();
	
	public abstract String getTableID();

}
