package Testing;

import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import LogicalOperator.LogicalOperator;
import LogicalOperator.LogicalPlanBuilder;
import Operators.Operator;
import Operators.PhysicalPlanBuilder;
import Project.DbCatalog;
import Project.TableInfo;
import Project.Tuple;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

public class CompareSortTest {

	
	@Before
	public void setUp () throws Exception {
	
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
	
	@Test
	public void TNLJvsBNLJ() {
		
		String joinPlan[] = {"0"}; // TNLJ
		
		String sortPlan[] = {"0"}; // In memory sort 
		
		String joinPlan2[] = {"1", "5"}; // BNLJ
		
		String tmpDir = "./";
		
		
		DbCatalog dbC = DbCatalog.getInstance();
		
		String query = "SELECT * FROM Reserves, Boats WHERE Reserves.R2 = Boats.B1";
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
		
		LogicalPlanBuilder d2 = new LogicalPlanBuilder(body);
		LogicalOperator po2 = d.getRoot();
		
		PhysicalPlanBuilder ppb2 = new PhysicalPlanBuilder(po2, joinPlan2, sortPlan, tmpDir);
		Operator o2 = ppb2.getResult();
		Set<Tuple> ts2 = new HashSet<Tuple>();
		
		Tuple t2 = o2.getNextTuple();
		ts2.add(t2);
		while ((t2 = o2.getNextTuple()) != null ) {
			ts2.add(t2);
			
		}
		
		assertEquals(ts2, ts);
		
	}
	
		@Test
	public void TNLJvsBNLJOnePage() {
		
		String[] joinPlan = {"0"}; // TNLJ
		
		
		String sortPlan[] = {"0"}; // In memory 
		
		String joinPlan2[] = {"1", "1"}; // BNLJ
		
		String tmpDir = "./";
		
		
		DbCatalog dbC = DbCatalog.getInstance();
		
		String query = "SELECT * FROM Reserves, Boats WHERE Reserves.R2 = Boats.B1";
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
		
		LogicalPlanBuilder d2 = new LogicalPlanBuilder(body);
		LogicalOperator po2 = d.getRoot();
		
		PhysicalPlanBuilder ppb2 = new PhysicalPlanBuilder(po2, joinPlan2, sortPlan, tmpDir);
		Operator o2 = ppb2.getResult();
		Set<Tuple> ts2 = new HashSet<Tuple>();
		
		Tuple t2 = o2.getNextTuple();
		ts2.add(t2);
		while ((t2 = o2.getNextTuple()) != null ) {
			ts2.add(t2);
			
		}
		
		assertEquals(ts2, ts);
		
	}
	
	@Test
	public void SMJvsTNLJ() {
		String joinPlan[] = {"0"}; // TNLJ 
				
		String joinPlan2[] = {"2"}; // for SMJ		
		
		String sortPlan[] = {"0"}; // In memory
					
		String tmpDir = "./";
		
		
		DbCatalog dbC = DbCatalog.getInstance();
		
		String query = "SELECT * FROM Reserves, Boats WHERE Reserves.R2 = Boats.B1";
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
		
		LogicalPlanBuilder d2 = new LogicalPlanBuilder(body);
		LogicalOperator po2 = d.getRoot();
		
		PhysicalPlanBuilder ppb2 = new PhysicalPlanBuilder(po2, joinPlan2, sortPlan, tmpDir);
		Operator o2 = ppb2.getResult();
		Set<Tuple> ts2 = new HashSet<Tuple>();
		
		Tuple t2 = o2.getNextTuple();
		ts2.add(t2);
		while ((t2 = o2.getNextTuple()) != null ) {
			ts2.add(t2);
			
		}		
		assertEquals(ts2, ts);
	}
	
	
	@Test 
	public void ExtSortvsInMemSort() {
		
		String joinPlan[] = {"0"}; // TNLJ		
		
		String sortPlan[] = {"0"}; // In memory
				
		String sortPlan2[] = {"1", "5"}; // External sort
				
		String tmpDir = "./";		
		
		DbCatalog dbC = DbCatalog.getInstance();

		
		String query = "SELECT * FROM Boats ORDER BY Boats.B2";
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
		
		LogicalPlanBuilder d2 = new LogicalPlanBuilder(body);
		LogicalOperator po2 = d.getRoot();
		
		PhysicalPlanBuilder ppb2 = new PhysicalPlanBuilder(po2, joinPlan, sortPlan2, tmpDir);
		Operator o2 = ppb2.getResult();
		Set<Tuple> ts2 = new HashSet<Tuple>();
		
		Tuple t2 = o2.getNextTuple();
		ts2.add(t2);
		while ((t2 = o2.getNextTuple()) != null ) {
			ts2.add(t2);
			
		}
		
		assertEquals(ts2, ts);
		
	}
	
	
	@Test 
	public void ExtSortvsInMemSortThreePage() {
		
		String joinPlan[] = {"0"};
		
		String sortPlan[] = {"0"}; // In memory sort
		
		String sortPlan2[] = {"1", "3"}; // External sort
		
		String tmpDir = "./";
		
		
		DbCatalog dbC = DbCatalog.getInstance();
		
		String query = "SELECT * FROM Boats ORDER BY Boats.B2";
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
		
		LogicalPlanBuilder d2 = new LogicalPlanBuilder(body);
		LogicalOperator po2 = d.getRoot();
		
		PhysicalPlanBuilder ppb2 = new PhysicalPlanBuilder(po2, joinPlan, sortPlan2, tmpDir);
		Operator o2 = ppb2.getResult();
		Set<Tuple> ts2 = new HashSet<Tuple>();
		
		Tuple t2 = o2.getNextTuple();
		ts2.add(t2);
		while ((t2 = o2.getNextTuple()) != null ) {
			ts2.add(t2);
			
		}
		
		assertEquals(ts2, ts);		
	}
	

}
