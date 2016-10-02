package Testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import Operators.ProjectOperator;
import Operators.ScanOperator;
import Project.TableInfo;
import Project.Tuple;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;

public class ProjectOperatorTest {

	@Test
	public void testGetAllColumns() {		
		TableInfo tableInfo = new TableInfo("src/Boats", "Boats");
		tableInfo.getColumns().add("A");
		tableInfo.getColumns().add("B");
		tableInfo.getColumns().add("C");	
		ScanOperator scn = new ScanOperator(tableInfo);
		
		String  query = "SELECT Boats.A, Boats.B, Boats.C FROM Boats";
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
		ProjectOperator po = new ProjectOperator(scn, items);
		Tuple t= po.getNextTuple();				
		assertEquals(t.toString(), "101,2,3");
	}
	
	@Test 
	public void testGetAllColumnsWithStar() {
		TableInfo tableInfo = new TableInfo("src/Boats", "Boats");
		tableInfo.getColumns().add("A");
		tableInfo.getColumns().add("B");
		tableInfo.getColumns().add("C");	
		ScanOperator scn = new ScanOperator(tableInfo);
		
		String  query = "SELECT * FROM Boats";
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
		ProjectOperator po = new ProjectOperator(scn, items);
		Tuple t= po.getNextTuple();				
		assertEquals(t.toString(), "101,2,3");		
	}

	@Test
	public void testGetFirstColumn() {
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("First");
		ti.getColumns().add("Second");
		ti.getColumns().add("Third");
				
		ScanOperator scn = new ScanOperator(ti);
		
		String query = "SELECT Boats.First FROM Boats";
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
		ProjectOperator po = new ProjectOperator(scn, items);
		Tuple t= po.getNextTuple();				
		assertEquals(t.toString(), "101");		
	}
	
	@Test
	public void testGetSecondColumn() {
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("First");
		ti.getColumns().add("Second");
		ti.getColumns().add("Third");
				
		ScanOperator scn = new ScanOperator(ti);
		
		String query = "SELECT Boats.Second FROM Boats";
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
		ProjectOperator po = new ProjectOperator(scn, items);
		Tuple t= po.getNextTuple();				
		assertEquals(t.toString(), "2");		
	}
	
	@Test
	public void testGetThirdColumn() {
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("First");
		ti.getColumns().add("Second");
		ti.getColumns().add("Third");
				
		ScanOperator scn = new ScanOperator(ti);
		
		String query = "SELECT Boats.Third FROM Boats";
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
		ProjectOperator po = new ProjectOperator(scn, items);
		Tuple t= po.getNextTuple();				
		assertEquals(t.toString(), "3");
	}
	
	@Test
	public void testGetFirstAndSecond() {
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("First");
		ti.getColumns().add("Second");
		ti.getColumns().add("Third");
				
		ScanOperator scn = new ScanOperator(ti);
		
		String query = "SELECT Boats.First, Boats.Second FROM Boats";
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
		ProjectOperator po = new ProjectOperator(scn, items);
		Tuple t= po.getNextTuple();				
		assertEquals(t.toString(), "101,2");
	}
	
	@Test
	public void testGetFirstAndThird() {
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("First");
		ti.getColumns().add("Second");
		ti.getColumns().add("Third");
				
		ScanOperator scn = new ScanOperator(ti);
		
		String query = "SELECT Boats.First, Boats.Third FROM Boats";
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
		ProjectOperator po = new ProjectOperator(scn, items);
		Tuple t= po.getNextTuple();				
		assertEquals(t.toString(), "101,3");
	}
	
	@Test
	public void testGetSecondAndThird() {
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("First");
		ti.getColumns().add("Second");
		ti.getColumns().add("Third");
				
		ScanOperator scn = new ScanOperator(ti);
		
		String query = "SELECT Boats.Second, Boats.Third FROM Boats";
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
		ProjectOperator po = new ProjectOperator(scn, items);
		Tuple t= po.getNextTuple();				
		assertEquals(t.toString(), "2,3");		
	}
	
	@Test 
	public void testGetAllReverseOrder() {
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("First");
		ti.getColumns().add("Second");
		ti.getColumns().add("Third");
				
		ScanOperator scn = new ScanOperator(ti);
		
		String query = "SELECT Boats.Third, Boats.Second, Boats.First FROM Boats";
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
		ProjectOperator po = new ProjectOperator(scn, items);
		Tuple t= po.getNextTuple();				
		assertEquals(t.toString(), "3,2,101");		
	}
	
	@Test
	public void testGetFirstAndSecondReverseOrder() {
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("First");
		ti.getColumns().add("Second");
		ti.getColumns().add("Third");
				
		ScanOperator scn = new ScanOperator(ti);
		
		String query = "SELECT Boats.Second, Boats.First FROM Boats";
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
		ProjectOperator po = new ProjectOperator(scn, items);
		Tuple t= po.getNextTuple();			
		assertEquals(t.toString(), "2,101");
	}
	
	@Test
	public void testGetFirstAndThirdReverseOrder() {
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("First");
		ti.getColumns().add("Second");
		ti.getColumns().add("Third");
				
		ScanOperator scn = new ScanOperator(ti);
		
		String query = "SELECT Boats.Third, Boats.First FROM Boats";
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
		ProjectOperator po = new ProjectOperator(scn, items);
		Tuple t= po.getNextTuple();				
		assertEquals(t.toString(), "3,101");
	}
	
	@Test
	public void testGetSecondAndThirdReverseOrder() {
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("First");
		ti.getColumns().add("Second");
		ti.getColumns().add("Third");
				
		ScanOperator scn = new ScanOperator(ti);
		
		String query = "SELECT Boats.Third, Boats.Second FROM Boats";
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
		ProjectOperator po = new ProjectOperator(scn, items);
		Tuple t= po.getNextTuple();				
		assertEquals(t.toString(), "3,2");
	}
	
	@Test
	public void testReset() {
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("First");
		ti.getColumns().add("Second");
		ti.getColumns().add("Third");
				
		ScanOperator scn = new ScanOperator(ti);
		
		String query = "SELECT Boats.Third, Boats.Second FROM Boats";
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
		ProjectOperator po = new ProjectOperator(scn, items);
		Tuple t;
		for (int i = 0; i < 3; i++) {
			t= po.getNextTuple();				
			assertEquals(t.toString(), "3,2");
			t= po.getNextTuple();				
			assertEquals(t.toString(), "4,3");
			t= po.getNextTuple();				
			assertEquals(t.toString(), "2,104");
			po.reset();
		}
	}
	
	@Test
	public void testSchema() {
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("First");
		ti.getColumns().add("Second");
		ti.getColumns().add("Third");
				
		ScanOperator scn = new ScanOperator(ti);
		
		String query = "SELECT Boats.Third, Boats.Second FROM Boats";
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
		ProjectOperator po = new ProjectOperator(scn, items);
		HashMap<String,Integer> schema = po.getSchema();
		assertEquals((Integer) schema.get("Boats.Third"), (Integer) 0);
		assertEquals((Integer) schema.get("Boats.Second"), (Integer) 1);
		assertFalse(schema.containsKey("Boats.First"));
	}
}