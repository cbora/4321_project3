package Project;

import Operators.ScanOperator;
import Operators.SelectOperator;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

public class Main {

	public static void main(String[] args) {
		TableInfo tableInfo = new TableInfo("src/Boats","Boats");
		tableInfo.getColumns().add("A");
		tableInfo.getColumns().add("B");
		tableInfo.getColumns().add("C");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		
		GreaterThan exp = new GreaterThan();
		Table tbl = new Table();
		tbl.setAlias("Boats");
		tbl.setName("Boats");
		Column col = new Column(tbl, "C");
		exp.setLeftExpression(col);
		exp.setRightExpression(new LongValue((long) 5));
		
		SelectOperator slct = new SelectOperator(scn, exp);
		
		Tuple t;
		while ((t = slct.getNextTuple()) != null) {
			System.out.println(t);
		}
	}

}
