package Testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import Operators.ScanOperator;
import Project.TableInfo;
import Project.Tuple;

public class ScanOperatorTest {

	@Test
	public void testGetTuplesBoats() {
		TableInfo tableInfo = new TableInfo("src/Boats","Boats");
		tableInfo.getColumns().add("D");
		tableInfo.getColumns().add("E");
		tableInfo.getColumns().add("F");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		Tuple t = scn.getNextTuple();
		assertEquals(t.toString(), "101,2,3");
		t = scn.getNextTuple();
		assertEquals(t.toString(), "102,3,4");
		t = scn.getNextTuple();
		assertEquals(t.toString(), "104,104,2");
		t = scn.getNextTuple();
		assertEquals(t.toString(), "103,1,1");
		t = scn.getNextTuple();
		assertEquals(t.toString(), "107,2,8");
		t = scn.getNextTuple();
		assertNull(t);
		scn.close();
	}
	
	@Test
	public void testGetTuplesReserves() {
		TableInfo tableInfo = new TableInfo("src/Reserves","Reserves");
		tableInfo.getColumns().add("G");
		tableInfo.getColumns().add("H");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		Tuple t = scn.getNextTuple();
		assertEquals(t.toString(), "1,101");
		t = scn.getNextTuple();
		assertEquals(t.toString(), "1,102");
		t = scn.getNextTuple();
		assertEquals(t.toString(), "1,103");
		t = scn.getNextTuple();
		assertEquals(t.toString(), "2,101");
		t = scn.getNextTuple();
		assertEquals(t.toString(), "3,102");
		t = scn.getNextTuple();
		assertEquals(t.toString(), "4,104");
		t = scn.getNextTuple();
		assertNull(t);
		scn.close();
	}
	
	@Test
	public void reset1() {
		TableInfo tableInfo = new TableInfo("src/Reserves","Reserves");
		tableInfo.getColumns().add("G");
		tableInfo.getColumns().add("H");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		Tuple t = scn.getNextTuple();
		assertEquals(t.toString(), "1,101");
		scn.reset();
		t = scn.getNextTuple();
		assertEquals(t.toString(), "1,101");
		scn.close();		
	}
	
	@Test
	public void reset2() {
		TableInfo tableInfo = new TableInfo("src/Reserves","Reserves");
		tableInfo.getColumns().add("G");
		tableInfo.getColumns().add("H");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		Tuple t;
		for (int i = 0; i < 3; i++) {
			t = scn.getNextTuple();
			assertEquals(t.toString(), "1,101");
			t = scn.getNextTuple();
			assertEquals(t.toString(), "1,102");
			t = scn.getNextTuple();
			assertEquals(t.toString(), "1,103");
			t = scn.getNextTuple();
			assertEquals(t.toString(), "2,101");
			t = scn.getNextTuple();
			assertEquals(t.toString(), "3,102");
			t = scn.getNextTuple();
			assertEquals(t.toString(), "4,104");
			t = scn.getNextTuple();
			assertNull(t);
			scn.reset();
		}
		scn.close();		
	}
	
	@Test
	public void testGetSchema() {
		TableInfo tableInfo = new TableInfo("src/Reserves","Reserves");
		tableInfo.getColumns().add("G");
		tableInfo.getColumns().add("H");
		
		ScanOperator scn1 = new ScanOperator(tableInfo);
		assertEquals((int) scn1.getSchema().get("Reserves.G"), 0);
		assertEquals((int) scn1.getSchema().get("Reserves.H"), 1);
		assertNull(scn1.getSchema().get("Reserves.Z"));
		
		ScanOperator scn2 = new ScanOperator(tableInfo, "R");
		assertEquals((int) scn2.getSchema().get("R.G"), 0);
		assertEquals((int) scn2.getSchema().get("R.H"), 1);
		assertNull(scn2.getSchema().get("Reserves.G"));
		assertNull(scn2.getSchema().get("R.Z"));
	}
}
