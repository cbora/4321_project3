package Project;

import java.io.StringReader;
import java.sql.Statement;
import java.util.ArrayList;

import Operators.ProjectOperator;
import Operators.ScanOperator;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;

public class ExampleProjection {

	public static void main(String[] args) {
		
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("A");
		ti.getColumns().add("B");
		ti.getColumns().add("D");
		
		ScanOperator so = new ScanOperator(ti);		
		
		String query = "SELECT Boats.D, Boats.A FROM Boats";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
		
		
		PlainSelect body = (PlainSelect) s.getSelectBody();
		ArrayList<SelectItem> items = (ArrayList<SelectItem>) body.getSelectItems();
		System.out.println("Projection columns: " + items);
		ProjectOperator po = new ProjectOperator(so, items);
		
		Tuple t;
		while ((t = po.getNextTuple()) != null) {
			System.out.println(t);
		}
		
	}
}
