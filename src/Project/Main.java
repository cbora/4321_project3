package Project;

import Operators.JoinOperator;
import Operators.ScanOperator;
import Operators.SelectOperator;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

public class Main {

	public static void main(String[] args) {
		// tables
		TableInfo tableInfo1 = new TableInfo("src/Boats","Boats");
		tableInfo1.getColumns().add("D");
		tableInfo1.getColumns().add("E");
		tableInfo1.getColumns().add("F");
		
		TableInfo tableInfo2 = new TableInfo("src/Reserves","Reserves");
		tableInfo2.getColumns().add("G");
		tableInfo2.getColumns().add("H");	
		
		// scan operators
		ScanOperator scn1 = new ScanOperator(tableInfo1);
		ScanOperator scn2 = new ScanOperator(tableInfo2);
		
		// join operator		
		EqualsTo exp = new EqualsTo();
		
		Table boats = new Table();
		boats.setAlias("Boats");
		boats.setName("Boats");
		Column col1 = new Column(boats, "D");
		
		Table reserves = new Table();
		reserves.setAlias("Reserves");
		reserves.setName("Reserves");
		Column col2 = new Column(reserves, "H");
		
		exp.setLeftExpression(col1);
		exp.setRightExpression(col2);
		
		// join operator
		JoinOperator join = new JoinOperator(scn1, scn2, exp);
		
		// retrieve all tuples
		Tuple t;
		while ((t = join.getNextTuple()) != null) {
			System.out.println(t);
		}
		join.close();
	}

}
