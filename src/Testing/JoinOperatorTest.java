package Testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import Operators.JoinOperator;
import Operators.ScanOperator;
import Operators.SelectOperator;
import Project.TableInfo;
import Project.Tuple;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

public class JoinOperatorTest {

	@Test
	public void BoatReserves1() {
		TableInfo tableInfo1 = new TableInfo("src/Boats","Boats");
		tableInfo1.getColumns().add("D");
		tableInfo1.getColumns().add("E");
		tableInfo1.getColumns().add("F");
		
		TableInfo tableInfo2 = new TableInfo("src/Reserves","Reserves");
		tableInfo2.getColumns().add("G");
		tableInfo2.getColumns().add("H");	
		
		// scan operators
		ScanOperator scn1 = new ScanOperator(tableInfo1);
		ScanOperator scn2 = new ScanOperator(tableInfo2);
		
		// setup		
		EqualsTo exp = new EqualsTo();
		
		Table boats = new Table();
		boats.setAlias("Boats");
		boats.setName("Boats");
		Column col1 = new Column(boats, "D");
		
		Table reserves = new Table();
		reserves.setAlias("Reserves");
		reserves.setName("Reserves");
		Column col2 = new Column(reserves, "H");
		
		exp.setLeftExpression(col1);
		exp.setRightExpression(col2);
		
		// join operator
		JoinOperator join = new JoinOperator(scn1, scn2, exp);
		Tuple t;
		t = join.getNextTuple();
		assertEquals(t.toString(), "101,2,3,1,101");
		t = join.getNextTuple();
		assertEquals(t.toString(), "101,2,3,2,101");
		t = join.getNextTuple();
		assertEquals(t.toString(), "102,3,4,1,102");
		t = join.getNextTuple();
		assertEquals(t.toString(), "102,3,4,3,102");
		t = join.getNextTuple();
		assertEquals(t.toString(), "104,104,2,4,104");
		t = join.getNextTuple();
		assertEquals(t.toString(), "103,1,1,1,103");
		t = join.getNextTuple();
		assertNull(t);
		join.close();
	}
	
	@Test
	public void BoatReserves2() {
		TableInfo tableInfo1 = new TableInfo("src/Boats","Boats");
		tableInfo1.getColumns().add("D");
		tableInfo1.getColumns().add("E");
		tableInfo1.getColumns().add("F");
		
		TableInfo tableInfo2 = new TableInfo("src/Reserves","Reserves");
		tableInfo2.getColumns().add("G");
		tableInfo2.getColumns().add("H");	
		
		// scan operators
		ScanOperator scn1 = new ScanOperator(tableInfo1);
		ScanOperator scn2 = new ScanOperator(tableInfo2);
		
		// join operator
		JoinOperator join = new JoinOperator(scn1, scn2);
		Tuple t;
		t = join.getNextTuple();
		assertEquals(t.toString(), "101,2,3,1,101");
		t = join.getNextTuple();
		assertEquals(t.toString(), "101,2,3,1,102");
		t = join.getNextTuple();
		assertEquals(t.toString(), "101,2,3,1,103");
		t = join.getNextTuple();
		assertEquals(t.toString(), "101,2,3,2,101");
		t = join.getNextTuple();
		assertEquals(t.toString(), "101,2,3,3,102");
		t = join.getNextTuple();
		assertEquals(t.toString(), "101,2,3,4,104");
		
		t = join.getNextTuple();
		assertEquals(t.toString(), "102,3,4,1,101");
		t = join.getNextTuple();
		assertEquals(t.toString(), "102,3,4,1,102");
		t = join.getNextTuple();
		assertEquals(t.toString(), "102,3,4,1,103");
		t = join.getNextTuple();
		assertEquals(t.toString(), "102,3,4,2,101");
		t = join.getNextTuple();
		assertEquals(t.toString(), "102,3,4,3,102");
		t = join.getNextTuple();
		assertEquals(t.toString(), "102,3,4,4,104");
		
		t = join.getNextTuple();
		assertEquals(t.toString(), "104,104,2,1,101");
		t = join.getNextTuple();
		assertEquals(t.toString(), "104,104,2,1,102");
		t = join.getNextTuple();
		assertEquals(t.toString(), "104,104,2,1,103");
		t = join.getNextTuple();
		assertEquals(t.toString(), "104,104,2,2,101");
		t = join.getNextTuple();
		assertEquals(t.toString(), "104,104,2,3,102");
		t = join.getNextTuple();
		assertEquals(t.toString(), "104,104,2,4,104");
		
		t = join.getNextTuple();
		assertEquals(t.toString(), "103,1,1,1,101");
		t = join.getNextTuple();
		assertEquals(t.toString(), "103,1,1,1,102");
		t = join.getNextTuple();
		assertEquals(t.toString(), "103,1,1,1,103");
		t = join.getNextTuple();
		assertEquals(t.toString(), "103,1,1,2,101");
		t = join.getNextTuple();
		assertEquals(t.toString(), "103,1,1,3,102");
		t = join.getNextTuple();
		assertEquals(t.toString(), "103,1,1,4,104");
		
		t = join.getNextTuple();
		assertEquals(t.toString(), "107,2,8,1,101");
		t = join.getNextTuple();
		assertEquals(t.toString(), "107,2,8,1,102");
		t = join.getNextTuple();
		assertEquals(t.toString(), "107,2,8,1,103");
		t = join.getNextTuple();
		assertEquals(t.toString(), "107,2,8,2,101");
		t = join.getNextTuple();
		assertEquals(t.toString(), "107,2,8,3,102");
		t = join.getNextTuple();
		assertEquals(t.toString(), "107,2,8,4,104");
		
		t = join.getNextTuple();
		assertNull(t);
		join.close();
	}
	
	@Test
	public void BoatReservesSailors() {
		TableInfo tableInfo1 = new TableInfo("src/Boats","Boats");
		tableInfo1.getColumns().add("D");
		tableInfo1.getColumns().add("E");
		tableInfo1.getColumns().add("F");
		
		TableInfo tableInfo2 = new TableInfo("src/Reserves","Reserves");
		tableInfo2.getColumns().add("G");
		tableInfo2.getColumns().add("H");
		
		TableInfo tableInfo3 = new TableInfo("src/Sailors","Sailors");
		tableInfo3.getColumns().add("A");
		tableInfo3.getColumns().add("B");
		tableInfo3.getColumns().add("C");
		
		
		// scan operators
		ScanOperator scn1 = new ScanOperator(tableInfo1);
		ScanOperator scn2 = new ScanOperator(tableInfo2);
		ScanOperator scn3 = new ScanOperator(tableInfo3);
		
		// setup		
		EqualsTo exp1 = new EqualsTo();
		EqualsTo exp2 = new EqualsTo();
		
		Table boats = new Table();
		boats.setAlias("Boats");
		boats.setName("Boats");
		Column col1 = new Column(boats, "D");
		
		Table reserves = new Table();
		reserves.setAlias("Reserves");
		reserves.setName("Reserves");
		Column col2 = new Column(reserves, "H");
		Column col3 = new Column(reserves, "G");
		
		Table sailors = new Table();
		sailors.setAlias("Sailors");
		sailors.setName("Sailors");
		Column col4 = new Column(sailors, "A");
		
		exp1.setLeftExpression(col1);
		exp1.setRightExpression(col2);
		exp2.setLeftExpression(col3);
		exp2.setRightExpression(col4);
		
		// join operator
		JoinOperator join1 = new JoinOperator(scn1, scn2, exp1);
		JoinOperator join2 = new JoinOperator(join1, scn3, exp2);
		Tuple t;
		t = join2.getNextTuple();
		assertEquals(t.toString(), "101,2,3,1,101,1,200,50");
		t = join2.getNextTuple();
		assertEquals(t.toString(), "101,2,3,2,101,2,200,200");
		t = join2.getNextTuple();
		assertEquals(t.toString(), "102,3,4,1,102,1,200,50");
		t = join2.getNextTuple();
		assertEquals(t.toString(), "102,3,4,3,102,3,100,105");
		t = join2.getNextTuple();
		assertEquals(t.toString(), "104,104,2,4,104,4,100,50");
		t = join2.getNextTuple();
		assertEquals(t.toString(), "103,1,1,1,103,1,200,50");
		t = join2.getNextTuple();
		assertNull(t);
		join2.close();
	}
	
	@Test
	public void reset1() {
		TableInfo tableInfo1 = new TableInfo("src/Boats","Boats");
		tableInfo1.getColumns().add("D");
		tableInfo1.getColumns().add("E");
		tableInfo1.getColumns().add("F");
		
		TableInfo tableInfo2 = new TableInfo("src/Reserves","Reserves");
		tableInfo2.getColumns().add("G");
		tableInfo2.getColumns().add("H");	
		
		// scan operators
		ScanOperator scn1 = new ScanOperator(tableInfo1);
		ScanOperator scn2 = new ScanOperator(tableInfo2);
		
		// setup		
		EqualsTo exp = new EqualsTo();
		
		Table boats = new Table();
		boats.setAlias("Boats");
		boats.setName("Boats");
		Column col1 = new Column(boats, "D");
		
		Table reserves = new Table();
		reserves.setAlias("Reserves");
		reserves.setName("Reserves");
		Column col2 = new Column(reserves, "H");
		
		exp.setLeftExpression(col1);
		exp.setRightExpression(col2);
		
		// join operator
		JoinOperator join = new JoinOperator(scn1, scn2, exp);
		Tuple t;
		t = join.getNextTuple();
		assertEquals(t.toString(), "101,2,3,1,101");
		t = join.getNextTuple();
		assertEquals(t.toString(), "101,2,3,2,101");
		join.reset();
		t = join.getNextTuple();
		assertEquals(t.toString(), "101,2,3,1,101");
		t = join.getNextTuple();
		assertEquals(t.toString(), "101,2,3,2,101");
		
		join.close();
	}
	
	@Test
	public void reset2() {
		TableInfo tableInfo1 = new TableInfo("src/Boats","Boats");
		tableInfo1.getColumns().add("D");
		tableInfo1.getColumns().add("E");
		tableInfo1.getColumns().add("F");
		
		TableInfo tableInfo2 = new TableInfo("src/Reserves","Reserves");
		tableInfo2.getColumns().add("G");
		tableInfo2.getColumns().add("H");	
		
		// scan operators
		ScanOperator scn1 = new ScanOperator(tableInfo1);
		ScanOperator scn2 = new ScanOperator(tableInfo2);
		
		// setup		
		EqualsTo exp = new EqualsTo();
		
		Table boats = new Table();
		boats.setAlias("Boats");
		boats.setName("Boats");
		Column col1 = new Column(boats, "D");
		
		Table reserves = new Table();
		reserves.setAlias("Reserves");
		reserves.setName("Reserves");
		Column col2 = new Column(reserves, "H");
		
		exp.setLeftExpression(col1);
		exp.setRightExpression(col2);
		
		// join operator
		JoinOperator join = new JoinOperator(scn1, scn2, exp);
		Tuple t;
		for (int i = 0; i < 3; i++) {
			t = join.getNextTuple();
			assertEquals(t.toString(), "101,2,3,1,101");
			t = join.getNextTuple();
			assertEquals(t.toString(), "101,2,3,2,101");
			t = join.getNextTuple();
			assertEquals(t.toString(), "102,3,4,1,102");
			t = join.getNextTuple();
			assertEquals(t.toString(), "102,3,4,3,102");
			t = join.getNextTuple();
			assertEquals(t.toString(), "104,104,2,4,104");
			t = join.getNextTuple();
			assertEquals(t.toString(), "103,1,1,1,103");
			t = join.getNextTuple();
			assertNull(t);
			join.reset();
		}
		join.close();
	}
	
	@Test
	public void testGetSchema() {
		TableInfo tableInfo1 = new TableInfo("src/Boats","Boats");
		tableInfo1.getColumns().add("D");
		tableInfo1.getColumns().add("E");
		tableInfo1.getColumns().add("F");
		
		TableInfo tableInfo2 = new TableInfo("src/Reserves","Reserves");
		tableInfo2.getColumns().add("G");
		tableInfo2.getColumns().add("H");	
		
		// scan operators
		ScanOperator scn1 = new ScanOperator(tableInfo1);
		ScanOperator scn2 = new ScanOperator(tableInfo2);
		
		// join operator
		JoinOperator join1 = new JoinOperator(scn1, scn2);
		assertEquals((int) join1.getSchema().get("Boats.D"), 0);
		assertEquals((int) join1.getSchema().get("Boats.E"), 1);
		assertEquals((int) join1.getSchema().get("Boats.F"), 2);
		assertEquals((int) join1.getSchema().get("Reserves.G"), 3);
		assertEquals((int) join1.getSchema().get("Reserves.H"), 4);
		assertNull(join1.getSchema().get("Boats.Z"));
		assertNull(join1.getSchema().get("Reserves.Z"));	
		
		// scan operators
		ScanOperator scn3 = new ScanOperator(tableInfo1, "B");
		ScanOperator scn4 = new ScanOperator(tableInfo2, "R");
		
		// join operator
		JoinOperator join2 = new JoinOperator(scn3, scn4);
		assertEquals((int) join2.getSchema().get("B.D"), 0);
		assertEquals((int) join2.getSchema().get("B.E"), 1);
		assertEquals((int) join2.getSchema().get("B.F"), 2);
		assertEquals((int) join2.getSchema().get("R.G"), 3);
		assertEquals((int) join2.getSchema().get("R.H"), 4);
		assertNull(join2.getSchema().get("Boats.D"));
		assertNull(join2.getSchema().get("Reserves.G"));
		assertNull(join2.getSchema().get("B.Z"));
		assertNull(join2.getSchema().get("R.Z"));
	}

}
