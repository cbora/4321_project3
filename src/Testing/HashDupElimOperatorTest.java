package Testing;

import static org.junit.Assert.*;

import org.junit.Test;

import Operators.HashDupElimOperator;
import Operators.ScanOperator;
import Project.TableInfo;
import Project.Tuple;

public class HashDupElimOperatorTest {

	@Test
	public void duplicates() {
		TableInfo tableInfo = new TableInfo("src/Duplicates","Duplicated");
		tableInfo.getColumns().add("X");
		tableInfo.getColumns().add("Y");
		tableInfo.getColumns().add("Z");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		HashDupElimOperator de = new HashDupElimOperator(scn);
		
		Tuple t;
		t = de.getNextTuple();
		assertEquals(t.toString(), "100,2,3");
		t = de.getNextTuple();
		assertEquals(t.toString(), "101,5,4");
		t = de.getNextTuple();
		assertEquals(t.toString(), "105,1,9");
		t = de.getNextTuple();
		assertEquals(t.toString(), "100,6,2");
		t = de.getNextTuple();
		assertNull(t);
		
		de.close();
	}
	
	@Test
	public void noDuplicates() {
		TableInfo tableInfo = new TableInfo("src/Boats","Boats");
		tableInfo.getColumns().add("D");
		tableInfo.getColumns().add("E");
		tableInfo.getColumns().add("F");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		HashDupElimOperator de = new HashDupElimOperator(scn);
		
		Tuple t = de.getNextTuple();
		assertEquals(t.toString(), "101,2,3");
		t = de.getNextTuple();
		assertEquals(t.toString(), "102,3,4");
		t = de.getNextTuple();
		assertEquals(t.toString(), "104,104,2");
		t = de.getNextTuple();
		assertEquals(t.toString(), "103,1,1");
		t = de.getNextTuple();
		assertEquals(t.toString(), "107,2,8");
		t = de.getNextTuple();
		assertNull(t);
		de.close();
	}
	
	@Test
	public void reset1() {
		TableInfo tableInfo = new TableInfo("src/Duplicates","Duplicated");
		tableInfo.getColumns().add("X");
		tableInfo.getColumns().add("Y");
		tableInfo.getColumns().add("Z");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		HashDupElimOperator de = new HashDupElimOperator(scn);
		
		Tuple t;
		t = de.getNextTuple();
		assertEquals(t.toString(), "100,2,3");
		t = de.getNextTuple();
		assertEquals(t.toString(), "101,5,4");
		de.reset();
		t = de.getNextTuple();
		assertEquals(t.toString(), "100,2,3");
		t = de.getNextTuple();
		assertEquals(t.toString(), "101,5,4");
		
		de.close();
	}
	
	@Test
	public void duplicates2() {
		TableInfo tableInfo = new TableInfo("src/Duplicates","Duplicated");
		tableInfo.getColumns().add("X");
		tableInfo.getColumns().add("Y");
		tableInfo.getColumns().add("Z");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		HashDupElimOperator de = new HashDupElimOperator(scn);
		
		Tuple t;
		for (int i = 0; i < 3; i++) {
			t = de.getNextTuple();
			assertEquals(t.toString(), "100,2,3");
			t = de.getNextTuple();
			assertEquals(t.toString(), "101,5,4");
			t = de.getNextTuple();
			assertEquals(t.toString(), "105,1,9");
			t = de.getNextTuple();
			assertEquals(t.toString(), "100,6,2");
			t = de.getNextTuple();
			assertNull(t);
			de.reset();
		}
		
		de.close();
	}
	
	@Test
	public void testGetSchema() {
		TableInfo tableInfo = new TableInfo("src/Reserves","Reserves");
		tableInfo.getColumns().add("G");
		tableInfo.getColumns().add("H");
		
		ScanOperator scn1 = new ScanOperator(tableInfo);
		HashDupElimOperator de1 = new HashDupElimOperator(scn1);
		assertEquals((int) de1.getSchema().get("Reserves.G"), 0);
		assertEquals((int) de1.getSchema().get("Reserves.H"), 1);
		assertNull(de1.getSchema().get("Reserves.Z"));
		
		ScanOperator scn2 = new ScanOperator(tableInfo, "R");
		HashDupElimOperator de2 = new HashDupElimOperator(scn2);
		assertEquals((int) de2.getSchema().get("R.G"), 0);
		assertEquals((int) de2.getSchema().get("R.H"), 1);
		assertNull(de2.getSchema().get("Reserves.G"));
		assertNull(de2.getSchema().get("R.Z"));
	}

}
