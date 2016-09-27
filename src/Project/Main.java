package Project;

import java.io.StringReader;

import Operators.Operator;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

public class Main {

	public static void main(String[] args) {
		
		TableInfo boats = new TableInfo("src/Boats", "Boats");
		boats.getColumns().add("B1");
		boats.getColumns().add("B2");
		boats.getColumns().add("B3");
		
		TableInfo res = new TableInfo("src/Reserves", "Reserves");
		res.getColumns().add("R1");
		res.getColumns().add("R2");
		
		TableInfo sail = new TableInfo("src/Sailors", "Sailors");
		sail.getColumns().add("S1");
		sail.getColumns().add("S2");
		sail.getColumns().add("S3");
	
		
		DbCatalog catalog = DbCatalog.getInstance();
		catalog.addTable("Boats", boats);
		catalog.addTable("Reserves", res);
		catalog.addTable("Sailors", sail);
		
		String query = "SELECT Boats.B1, Reserves.R2 FROM Boats, Reserves WHERE Boats.B1 = Reserves.R2";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
		
		
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		System.out.println(body);
		Join j = (Join) body.getJoins().get(0);
		FromItem item = (FromItem) j.getRightItem();
		System.out.println(j.getRightItem());
		Table tab = ((Table) item);
		System.out.println("tab");
		System.out.println(tab.getWholeTableName());
		
		//System.out.println(x);
		//Column c = (Column) j.getUsingColumns();
		//System.out.println(c.getTable());
		System.out.println("done main");
		Driver d = new Driver(body);
		
		Operator o = d.getRoot();
		
		//o.dump();
		Tuple t;
		while ((t = o.getNextTuple()) != null)
			System.out.println(t);
	}

}
