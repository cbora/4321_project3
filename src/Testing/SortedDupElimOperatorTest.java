package Testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import Operators.ScanOperator;
import Operators.InMemSortOperator;
import Operators.SortedDupElimOperator;
import Project.TableInfo;
import Project.Tuple;

public class SortedDupElimOperatorTest {

	@Test
	public void duplicates() {
		TableInfo tableInfo = new TableInfo("src/SortedDuplicates","SortedDuplicated");
		tableInfo.getColumns().add("X");
		tableInfo.getColumns().add("Y");
		tableInfo.getColumns().add("Z");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		SortedDupElimOperator de = new SortedDupElimOperator(scn);
		
		Tuple t;
		t = de.getNextTuple();
		assertEquals(t.toString(), "100,2,3");
		t = de.getNextTuple();
		assertEquals(t.toString(), "100,6,2");
		t = de.getNextTuple();
		assertEquals(t.toString(), "101,5,4");
		t = de.getNextTuple();
		assertEquals(t.toString(), "105,1,9");
		t = de.getNextTuple();
		assertNull(t);
		
		de.close();
	}
	
	@Test
	public void noDuplicates() {
		TableInfo tableInfo = new TableInfo("src/SortedBoats","SortedBoats");
		tableInfo.getColumns().add("D");
		tableInfo.getColumns().add("E");
		tableInfo.getColumns().add("F");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		SortedDupElimOperator de = new SortedDupElimOperator(scn);
		
		Tuple t = de.getNextTuple();
		assertEquals(t.toString(), "101,2,3");
		t = de.getNextTuple();
		assertEquals(t.toString(), "102,3,4");
		t = de.getNextTuple();
		assertEquals(t.toString(), "103,1,1");
		t = de.getNextTuple();
		assertEquals(t.toString(), "104,104,2");
		t = de.getNextTuple();
		assertEquals(t.toString(), "107,2,8");
		t = de.getNextTuple();
		assertNull(t);
		de.close();
	}
	
	@Test
	public void reset1() {
		TableInfo tableInfo = new TableInfo("src/SortedDuplicates","SortedDuplicates");
		tableInfo.getColumns().add("X");
		tableInfo.getColumns().add("Y");
		tableInfo.getColumns().add("Z");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		SortedDupElimOperator de = new SortedDupElimOperator(scn);
		
		Tuple t;
		t = de.getNextTuple();
		assertEquals(t.toString(), "100,2,3");
		t = de.getNextTuple();
		assertEquals(t.toString(), "100,6,2");
		de.reset();
		t = de.getNextTuple();
		assertEquals(t.toString(), "100,2,3");
		t = de.getNextTuple();
		assertEquals(t.toString(), "100,6,2");
		
		de.close();
	}
	
	@Test
	public void duplicates2() {
		TableInfo tableInfo = new TableInfo("src/SortedDuplicates","SortedDuplicated");
		tableInfo.getColumns().add("X");
		tableInfo.getColumns().add("Y");
		tableInfo.getColumns().add("Z");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		SortedDupElimOperator de = new SortedDupElimOperator(scn);
		
		Tuple t;
		for (int i = 0; i < 3; i++) {
			t = de.getNextTuple();
			assertEquals(t.toString(), "100,2,3");
			t = de.getNextTuple();
			assertEquals(t.toString(), "100,6,2");
			t = de.getNextTuple();
			assertEquals(t.toString(), "101,5,4");
			t = de.getNextTuple();
			assertEquals(t.toString(), "105,1,9");
			t = de.getNextTuple();
			assertNull(t);
			de.reset();
		}
		
		de.close();
	}
	
	@Test
	public void testGetSchema() {
		TableInfo tableInfo = new TableInfo("src/SortedDuplicates","SortedDuplicates");
		tableInfo.getColumns().add("X");
		tableInfo.getColumns().add("Y");
		tableInfo.getColumns().add("Z");
		
		ScanOperator scn1 = new ScanOperator(tableInfo);
		SortedDupElimOperator de1 = new SortedDupElimOperator(scn1);
		assertEquals((int) de1.getSchema().get("SortedDuplicates.X"), 0);
		assertEquals((int) de1.getSchema().get("SortedDuplicates.Y"), 1);
		assertEquals((int) de1.getSchema().get("SortedDuplicates.Z"), 2);
		assertNull(de1.getSchema().get("SortedDuplicates.A"));
		
		ScanOperator scn2 = new ScanOperator(tableInfo, "SD");
		SortedDupElimOperator de2 = new SortedDupElimOperator(scn2);
		assertEquals((int) de2.getSchema().get("SD.X"), 0);
		assertEquals((int) de2.getSchema().get("SD.Y"), 1);
		assertEquals((int) de2.getSchema().get("SD.Z"), 2);
		assertNull(de2.getSchema().get("SortedDuplicates.X"));
		assertNull(de2.getSchema().get("SortedDuplicates.A"));
	}

}
