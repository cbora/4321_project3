package Testing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Test;

import Operators.ScanOperator;
import Operators.SelectOperator;
import Project.TableInfo;
import Project.Tuple;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

public class SelectOperatorTest {

	@Test
	public void testGetTuplesBoats1() {
		TableInfo tableInfo = new TableInfo("src/Boats","Boats");
		tableInfo.getColumns().add("D");
		tableInfo.getColumns().add("E");
		tableInfo.getColumns().add("F");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		
		EqualsTo eq = new EqualsTo();
		
		Table tbl = new Table();
		tbl.setName("Boats");
		tbl.setAlias("Boats");
		Column col = new Column();
		col.setTable(tbl);
		col.setColumnName("E");
		
		LongValue lv = new LongValue(2);
		
		eq.setLeftExpression(col);
		eq.setRightExpression(lv);
		
		SelectOperator slct = new SelectOperator(scn, eq);
		Tuple t = slct.getNextTuple();
		assertEquals(t.toString(), "101,2,3");
		t = slct.getNextTuple();
		assertEquals(t.toString(), "107,2,8");
		t = slct.getNextTuple();
		assertNull(t);
		slct.close();
	}
	
	@Test
	public void testGetTuplesBoats2() {
		TableInfo tableInfo = new TableInfo("src/Boats","Boats");
		tableInfo.getColumns().add("D");
		tableInfo.getColumns().add("E");
		tableInfo.getColumns().add("F");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		
		EqualsTo eq = new EqualsTo();
		
		Table tbl = new Table();
		tbl.setName("Boats");
		tbl.setAlias("Boats");
		Column col1 = new Column();
		col1.setTable(tbl);
		col1.setColumnName("D");
		Column col2 = new Column();
		col2.setTable(tbl);
		col2.setColumnName("E");
		
		eq.setLeftExpression(col1);
		eq.setRightExpression(col2);
		
		SelectOperator slct = new SelectOperator(scn, eq);
		Tuple t = slct.getNextTuple();
		assertEquals(t.toString(), "104,104,2");
		t = slct.getNextTuple();
		assertNull(t);
		slct.close();
	}
	
	@Test
	public void testGetTuplesBoats3() {
		TableInfo tableInfo = new TableInfo("src/Boats","Boats");
		tableInfo.getColumns().add("D");
		tableInfo.getColumns().add("E");
		tableInfo.getColumns().add("F");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		
		EqualsTo eq1 = new EqualsTo();
		EqualsTo eq2 = new EqualsTo();
		
		Table tbl = new Table();
		tbl.setName("Boats");
		tbl.setAlias("Boats");
		Column col1 = new Column();
		col1.setTable(tbl);
		col1.setColumnName("D");
		Column col2 = new Column();
		col2.setTable(tbl);
		col2.setColumnName("F");
		
		LongValue l1 = new LongValue(104);
		LongValue l2 = new LongValue(2);
		
		eq1.setLeftExpression(col1);
		eq1.setRightExpression(l1);
		eq2.setLeftExpression(col2);
		eq2.setRightExpression(l2);
		
		AndExpression rt = new AndExpression();
		rt.setLeftExpression(eq1);
		rt.setRightExpression(eq2);
		
		SelectOperator slct = new SelectOperator(scn, rt);
		Tuple t = slct.getNextTuple();
		assertEquals(t.toString(), "104,104,2");
		t = slct.getNextTuple();
		assertNull(t);
		slct.close();
	}
	
	@Test
	public void testGetTuplesBoats4() {
		TableInfo tableInfo = new TableInfo("src/Boats","Boats");
		tableInfo.getColumns().add("D");
		tableInfo.getColumns().add("E");
		tableInfo.getColumns().add("F");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		
		EqualsTo eq = new EqualsTo();
		
		Table tbl = new Table();
		tbl.setName("Boats");
		tbl.setAlias("Boats");
		Column col = new Column();
		col.setTable(tbl);
		col.setColumnName("E");
		
		LongValue lv = new LongValue(500);
		
		eq.setLeftExpression(col);
		eq.setRightExpression(lv);
		
		SelectOperator slct = new SelectOperator(scn, eq);
		Tuple t = slct.getNextTuple();
		assertNull(t);
		slct.close();
	}
	
	@Test
	public void testReset1() {
		TableInfo tableInfo = new TableInfo("src/Boats","Boats");
		tableInfo.getColumns().add("D");
		tableInfo.getColumns().add("E");
		tableInfo.getColumns().add("F");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		
		EqualsTo eq = new EqualsTo();
		
		Table tbl = new Table();
		tbl.setName("Boats");
		tbl.setAlias("Boats");
		Column col = new Column();
		col.setTable(tbl);
		col.setColumnName("E");
		
		LongValue lv = new LongValue(2);
		
		eq.setLeftExpression(col);
		eq.setRightExpression(lv);
		
		SelectOperator slct = new SelectOperator(scn, eq);
		Tuple t;
		t = slct.getNextTuple();
		assertEquals(t.toString(), "101,2,3");
		slct.reset();
		t = slct.getNextTuple();
		assertEquals(t.toString(), "101,2,3");
		slct.close();
	}
	
	@Test
	public void testReset2() {
		TableInfo tableInfo = new TableInfo("src/Boats","Boats");
		tableInfo.getColumns().add("D");
		tableInfo.getColumns().add("E");
		tableInfo.getColumns().add("F");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		
		EqualsTo eq = new EqualsTo();
		
		Table tbl = new Table();
		tbl.setName("Boats");
		tbl.setAlias("Boats");
		Column col = new Column();
		col.setTable(tbl);
		col.setColumnName("E");
		
		LongValue lv = new LongValue(2);
		
		eq.setLeftExpression(col);
		eq.setRightExpression(lv);
		
		SelectOperator slct = new SelectOperator(scn, eq);
		Tuple t;
		for (int i = 0; i < 3; i++) {
			t = slct.getNextTuple();
			assertEquals(t.toString(), "101,2,3");
			t = slct.getNextTuple();
			assertEquals(t.toString(), "107,2,8");
			t = slct.getNextTuple();
			assertNull(t);
			slct.reset();
		}
		slct.close();
	}
	
	@Test
	public void testGetSchema() {
		TableInfo tableInfo = new TableInfo("src/Reserves","Reserves");
		tableInfo.getColumns().add("G");
		tableInfo.getColumns().add("H");
		
		ScanOperator scn1 = new ScanOperator(tableInfo);
		SelectOperator slct1 = new SelectOperator(scn1, null);
		assertEquals((int) slct1.getSchema().get("Reserves.G"), 0);
		assertEquals((int) slct1.getSchema().get("Reserves.H"), 1);
		assertNull(slct1.getSchema().get("Reserves.Z"));
		
		ScanOperator scn2 = new ScanOperator(tableInfo, "R");
		SelectOperator slct2 = new SelectOperator(scn2, null);
		assertEquals((int) slct2.getSchema().get("R.G"), 0);
		assertEquals((int) slct2.getSchema().get("R.H"), 1);
		assertNull(slct2.getSchema().get("Reserves.G"));
		assertNull(slct2.getSchema().get("R.Z"));
	}

}
