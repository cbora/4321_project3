package Project;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import LogicalOperator.LogicalOperator;
import LogicalOperator.LogicalPlanBuilder;
import Operators.Operator;
import Operators.PhysicalPlanBuilder;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

public class Benchmark {

	public Benchmark() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		Benchmark bench = new Benchmark();
		String queries[] = {
				"",
				"",
				""
		};
		String methods[] = {"0", "1", "2", "2"};
		bench.setup();
		
		
		for (int i=0; i<methods.length; i++){
			for (int q=0; q<queries.length; q++){
				//time it
				if (i<3)
					bench.join(queries[q], methods[i], "1");
				else
					bench.join(queries[q], methods[i], "5");
			}
		}

	}
	
	
	public void setup() {
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
		
		TableInfo st = new TableInfo("src/SortTest", "SortTest");
		st.getColumns().add("A");
		st.getColumns().add("B");
		st.getColumns().add("C");
		
		TableInfo dup = new TableInfo("src/Duplicates","Duplicated");
		dup.getColumns().add("X");
		dup.getColumns().add("Y");
		dup.getColumns().add("Z");
		
		DbCatalog catalog = DbCatalog.getInstance();
		catalog.addTable("Boats", boats);
		catalog.addTable("Reserves", res);
		catalog.addTable("Sailors", sail);
		catalog.addTable("SortTest", st);
		catalog.addTable("Duplicates", dup);
	}
	
	public void TNLJ(String query) {
		
		String joinPlan[] = {"0"}; // TNLJ
		
		String sortPlan[] = {"0"}; // In memory sort 
		
		
		String tmpDir = "./";
		
		
		DbCatalog dbC = DbCatalog.getInstance();
		
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		
		LogicalPlanBuilder d = new LogicalPlanBuilder(body);
		LogicalOperator po = d.getRoot();
		
		PhysicalPlanBuilder ppb = new PhysicalPlanBuilder(po, joinPlan, sortPlan, tmpDir);
		Operator o = ppb.getResult();
		
		Set<Tuple> ts = new HashSet<Tuple>();
		Tuple t = o.getNextTuple();
		ts.add(t);
		while ((t = o.getNextTuple()) != null) {
			ts.add(t);
		}
		
	}
	
	public void BNLJ(String query, String nPage) {
		
	}
	
	public void SMJ(String query) {
		
	}

	public void join(String query, String method, String nPage) {
		
		String joinPlan[] = null;
		joinPlan[0] = method;
		joinPlan[1] = nPage;
		
		String sortPlan[] = {"1"}; // External Sort 
		
		
		String tmpDir = "./";
		
		
		DbCatalog dbC = DbCatalog.getInstance();
		
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		
		LogicalPlanBuilder d = new LogicalPlanBuilder(body);
		LogicalOperator po = d.getRoot();
		
		PhysicalPlanBuilder ppb = new PhysicalPlanBuilder(po, joinPlan, sortPlan, tmpDir);
		Operator o = ppb.getResult();
		
		o.dump(0);
		
	}
	
}
