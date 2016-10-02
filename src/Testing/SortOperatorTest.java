package Testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Test;

import Operators.ScanOperator;
import Operators.SortOperator;
import Project.TableInfo;
import Project.Tuple;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

public class SortOperatorTest {
	
	@Test
	public void testSortInOrderOfColumns() {
		// sort by first, then second and so on..
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("A");
		ti.getColumns().add("B");
		ti.getColumns().add("C");
		ScanOperator so = new ScanOperator(ti);

		String query = "SELECT Boats.A, Boats.B, Boats.C FROM Boats ORDER BY Boats.A, Boats.B, Boats.C";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}				
		PlainSelect body = (PlainSelect) s.getSelectBody();
		ArrayList<OrderByElement> order = (ArrayList<OrderByElement>) body.getOrderByElements();
		SortOperator srt = new SortOperator(so, order);
		Tuple t = srt.getNextTuple();
		assertEquals(t.toString(), "101,2,3");
	}
	
	@Test
	public void testWithFirstAndSecondColumns() {
		// First column only given in the sort order by
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("A");
		ti.getColumns().add("B");
		ti.getColumns().add("C");
		ScanOperator so = new ScanOperator(ti);

		String query = "SELECT Boats.A, Boats.B, Boats.C FROM Boats ORDER BY Boats.A, Boats.B";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}				
		PlainSelect body = (PlainSelect) s.getSelectBody();
		ArrayList<OrderByElement> order = (ArrayList<OrderByElement>) body.getOrderByElements();
		SortOperator srt = new SortOperator(so, order);
		Tuple t = srt.getNextTuple();
		assertEquals(t.toString(), "101,2,3");
	}
	
		
	@Test
	public void testAllTuplesByColumnOrder() {
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("A");
		ti.getColumns().add("B");
		ti.getColumns().add("C");
		ScanOperator so = new ScanOperator(ti);

		String query = "SELECT Boats.A, Boats.B, Boats.C FROM Boats ORDER BY Boats.A, Boats.B, Boats.C";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}				
		PlainSelect body = (PlainSelect) s.getSelectBody();
		ArrayList<OrderByElement> order = (ArrayList<OrderByElement>) body.getOrderByElements();
		SortOperator srt = new SortOperator(so, order);
		
		Tuple t1 = srt.getNextTuple();
		assertEquals(t1.toString(), "101,2,3");
		
		Tuple t2 = srt.getNextTuple();
		assertEquals(t2.toString(), "102,3,4");
		
		Tuple t3 = srt.getNextTuple();
		assertEquals(t3.toString(), "103,1,1");
		
		Tuple t4 = srt.getNextTuple();
		assertEquals(t4.toString(), "104,104,2");
		
		Tuple t5 = srt.getNextTuple();
		assertEquals(t5.toString(), "107,2,8");
	}
		
	@Test
	public void testAllTuplesWithFirstColumn() {
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("A");
		ti.getColumns().add("B");
		ti.getColumns().add("C");
		ScanOperator so = new ScanOperator(ti);

		String query = "SELECT Boats.A, Boats.B, Boats.C FROM Boats ORDER BY Boats.A";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}				
		PlainSelect body = (PlainSelect) s.getSelectBody();
		ArrayList<OrderByElement> order = (ArrayList<OrderByElement>) body.getOrderByElements();
		SortOperator srt = new SortOperator(so, order);
		
		Tuple t1 = srt.getNextTuple();
		assertEquals(t1.toString(), "101,2,3");
		
		Tuple t2 = srt.getNextTuple();
		assertEquals(t2.toString(), "102,3,4");
		
		Tuple t3 = srt.getNextTuple();
		assertEquals(t3.toString(), "103,1,1");
		
		Tuple t4 = srt.getNextTuple();
		assertEquals(t4.toString(), "104,104,2");
		
		Tuple t5 = srt.getNextTuple();
		assertEquals(t5.toString(), "107,2,8");		
	}
		

	@Test
	public void testAllTuplesWithSecondColumn() {
		// First column only given in the sort order by
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("A");
		ti.getColumns().add("B");
		ti.getColumns().add("C");
		ScanOperator so = new ScanOperator(ti);

		String query = "SELECT Boats.A, Boats.B, Boats.C FROM Boats ORDER BY Boats.B";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}				
		PlainSelect body = (PlainSelect) s.getSelectBody();
		ArrayList<OrderByElement> order = (ArrayList<OrderByElement>) body.getOrderByElements();
		SortOperator srt = new SortOperator(so, order);
		
		Tuple t1 = srt.getNextTuple();
		assertEquals(t1.toString(), "103,1,1");
		
		Tuple t2 = srt.getNextTuple();
		assertEquals(t2.toString(), "101,2,3");
		
		Tuple t3 = srt.getNextTuple();
		assertEquals(t3.toString(), "107,2,8");
		
		Tuple t4 = srt.getNextTuple();
		assertEquals(t4.toString(), "102,3,4");
		
		Tuple t5 = srt.getNextTuple();
		assertEquals(t5.toString(), "104,104,2");
	}
		
	@Test
	public void testAllTuplesWithThirdColumn() {
		// First column only given in the sort order by
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("A");
		ti.getColumns().add("B");
		ti.getColumns().add("C");
		ScanOperator so = new ScanOperator(ti);

		String query = "SELECT Boats.A, Boats.B, Boats.C FROM Boats ORDER BY Boats.C";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}				
		PlainSelect body = (PlainSelect) s.getSelectBody();
		ArrayList<OrderByElement> order = (ArrayList<OrderByElement>) body.getOrderByElements();
		SortOperator srt = new SortOperator(so, order);
		
		Tuple t1 = srt.getNextTuple();
		assertEquals(t1.toString(), "103,1,1");
		
		Tuple t2 = srt.getNextTuple();
		assertEquals(t2.toString(), "104,104,2");
				
		Tuple t3 = srt.getNextTuple();
		assertEquals(t3.toString(), "101,2,3");
		
		Tuple t4 = srt.getNextTuple();		
		assertEquals(t4.toString(), "102,3,4");	
		
		Tuple t5 = srt.getNextTuple();
		assertEquals(t5.toString(), "107,2,8");
	}
	
	@Test
	public void testSortWithOnlyFirstColumn() {
		// First column only given in the sort order by
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("A");
		ti.getColumns().add("B");
		ti.getColumns().add("C");
		ScanOperator so = new ScanOperator(ti);

		String query = "SELECT Boats.A, Boats.B, Boats.C FROM Boats ORDER BY Boats.A";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}				
		PlainSelect body = (PlainSelect) s.getSelectBody();
		ArrayList<OrderByElement> order = (ArrayList<OrderByElement>) body.getOrderByElements();
		SortOperator srt = new SortOperator(so, order);
		Tuple t = srt.getNextTuple();
		assertEquals(t.toString(), "101,2,3");		
	}
	
	@Test
	public void testSortWithOnlySecondColumn() {
		// Second Column only
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("A");
		ti.getColumns().add("B");
		ti.getColumns().add("C");
		ScanOperator so = new ScanOperator(ti);

		String query = "SELECT Boats.A, Boats.B, Boats.C FROM Boats ORDER BY Boats.B";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}				
		PlainSelect body = (PlainSelect) s.getSelectBody();
		ArrayList<OrderByElement> order = (ArrayList<OrderByElement>) body.getOrderByElements();
		SortOperator srt = new SortOperator(so, order);
		Tuple t = srt.getNextTuple();
		assertEquals(t.toString(), "103,1,1");
	}
	
	@Test
	public void testWithOnlyThirdColumn() {
		TableInfo ti = new TableInfo("src/Boats", "Boats");
		ti.getColumns().add("A");
		ti.getColumns().add("B");
		ti.getColumns().add("C");
		ScanOperator so = new ScanOperator(ti);

		String query = "SELECT Boats.A, Boats.B, Boats.C FROM Boats ORDER BY Boats.C";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}				
		PlainSelect body = (PlainSelect) s.getSelectBody();
		ArrayList<OrderByElement> order = (ArrayList<OrderByElement>) body.getOrderByElements();
		SortOperator srt = new SortOperator(so, order);
		Tuple t = srt.getNextTuple();
		assertEquals(t.toString(), "103,1,1");
	}
	
	@Test
	public void sortTestColOne() {
		TableInfo ti = new TableInfo("src/SortTest", "SortTest");
		ti.getColumns().add("A");
		ti.getColumns().add("B");
		ti.getColumns().add("C");
		ScanOperator so = new ScanOperator(ti);

		String query = "SELECT * FROM SortTest ORDER BY SortTest.A";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}				
		PlainSelect body = (PlainSelect) s.getSelectBody();
		ArrayList<OrderByElement> order = (ArrayList<OrderByElement>) body.getOrderByElements();
		SortOperator srt = new SortOperator(so, order);
		Tuple t;
		t = srt.getNextTuple();
		assertEquals(t.toString(), "1,1,1");
		t = srt.getNextTuple();
		assertEquals(t.toString(), "1,2,3");
		t = srt.getNextTuple();
		assertEquals(t.toString(), "1,3,3");
	}
	
	@Test
	public void sortTestColTwo() {
		TableInfo ti = new TableInfo("src/SortTest", "SortTest");
		ti.getColumns().add("A");
		ti.getColumns().add("B");
		ti.getColumns().add("C");
		ScanOperator so = new ScanOperator(ti);

		String query = "SELECT * FROM SortTest ORDER BY SortTest.B";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}				
		PlainSelect body = (PlainSelect) s.getSelectBody();
		ArrayList<OrderByElement> order = (ArrayList<OrderByElement>) body.getOrderByElements();
		SortOperator srt = new SortOperator(so, order);
		Tuple t;
		t = srt.getNextTuple();
		assertEquals(t.toString(), "1,1,1");
		t = srt.getNextTuple();
		assertEquals(t.toString(), "2,1,1");
		t = srt.getNextTuple();
		assertEquals(t.toString(), "3,1,3");
	}
	
	@Test
	public void sortTestColThree() {
		TableInfo ti = new TableInfo("src/SortTest", "SortTest");
		ti.getColumns().add("A");
		ti.getColumns().add("B");
		ti.getColumns().add("C");
		ScanOperator so = new ScanOperator(ti);

		String query = "SELECT * FROM SortTest ORDER BY SortTest.C";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}				
		PlainSelect body = (PlainSelect) s.getSelectBody();
		ArrayList<OrderByElement> order = (ArrayList<OrderByElement>) body.getOrderByElements();
		SortOperator srt = new SortOperator(so, order);
		Tuple t;
		t = srt.getNextTuple();
		assertEquals(t.toString(), "1,1,1");
		t = srt.getNextTuple();
		assertEquals(t.toString(), "2,1,1");
		t = srt.getNextTuple();
		assertEquals(t.toString(), "3,2,1");
	}
	
	@Test
	public void sortTestColThreeTwo() {
		TableInfo ti = new TableInfo("src/SortTest", "SortTest");
		ti.getColumns().add("A");
		ti.getColumns().add("B");
		ti.getColumns().add("C");
		ScanOperator so = new ScanOperator(ti);

		String query = "SELECT * FROM SortTest ORDER BY SortTest.C, SortTest.B";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}				
		PlainSelect body = (PlainSelect) s.getSelectBody();
		ArrayList<OrderByElement> order = (ArrayList<OrderByElement>) body.getOrderByElements();
		SortOperator srt = new SortOperator(so, order);
		Tuple t;
		t = srt.getNextTuple();
		assertEquals(t.toString(), "1,1,1");
		t = srt.getNextTuple();
		assertEquals(t.toString(), "2,1,1");
		t = srt.getNextTuple();
		assertEquals(t.toString(), "3,2,1");
		t = srt.getNextTuple();
		assertEquals(t.toString(), "2,3,2");
		t = srt.getNextTuple();
		assertEquals(t.toString(), "3,1,3");
		t = srt.getNextTuple();
		assertEquals(t.toString(), "1,2,3");
	}
	
	@Test
	public void testReset() {
		TableInfo ti = new TableInfo("src/SortTest", "SortTest");
		ti.getColumns().add("A");
		ti.getColumns().add("B");
		ti.getColumns().add("C");
		ScanOperator so = new ScanOperator(ti);

		String query = "SELECT * FROM SortTest ORDER BY SortTest.C, SortTest.B";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}				
		PlainSelect body = (PlainSelect) s.getSelectBody();
		ArrayList<OrderByElement> order = (ArrayList<OrderByElement>) body.getOrderByElements();
		SortOperator srt = new SortOperator(so, order);
		Tuple t;
		for (int i = 0; i < 3; i++) {
			t = srt.getNextTuple();
			assertEquals(t.toString(), "1,1,1");
			t = srt.getNextTuple();
			assertEquals(t.toString(), "2,1,1");
			t = srt.getNextTuple();
			assertEquals(t.toString(), "3,2,1");
			t = srt.getNextTuple();
			assertEquals(t.toString(), "2,3,2");
			t = srt.getNextTuple();
			assertEquals(t.toString(), "3,1,3");
			t = srt.getNextTuple();
			assertEquals(t.toString(), "1,2,3");
			srt.reset();
		}
	}
	
	@Test
	public void testGetSchema() {
		TableInfo ti = new TableInfo("src/SortTest", "SortTest");
		ti.getColumns().add("A");
		ti.getColumns().add("B");
		ti.getColumns().add("C");
		ScanOperator so = new ScanOperator(ti);

		String query = "SELECT * FROM SortTest ORDER BY SortTest.C, SortTest.B";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}				
		PlainSelect body = (PlainSelect) s.getSelectBody();
		ArrayList<OrderByElement> order = (ArrayList<OrderByElement>) body.getOrderByElements();
		SortOperator srt = new SortOperator(so, order);
		HashMap<String, Integer> schema = srt.getSchema();
		assertEquals((Integer) schema.get("SortTest.A"), (Integer) 0);
		assertEquals((Integer) schema.get("SortTest.B"), (Integer) 1);
		assertEquals((Integer) schema.get("SortTest.C"), (Integer) 2);
		assertFalse(schema.containsKey("SortTest.Z"));
	}
}
