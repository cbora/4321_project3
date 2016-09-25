package Testing;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;

import org.junit.Test;

import Project.EvalExpressionVisitor;
import Project.Tuple;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

public class EvalExpressionVisitorTest {

	@Test
	public void oneTermTwoLongs() {
		EqualsTo eq = new EqualsTo();
		GreaterThan gt = new GreaterThan();
		GreaterThanEquals gte = new GreaterThanEquals();
		MinorThan lt = new MinorThan();
		MinorThanEquals lte = new MinorThanEquals();
		NotEqualsTo neq = new NotEqualsTo();
		
		LongValue l1 = new LongValue(5);
		LongValue l2 = new LongValue(5);
		LongValue l3 = new LongValue(4);
		LongValue l4 = new LongValue(6);
		
		eq.setLeftExpression(l1);
		eq.setRightExpression(l2);
		EvalExpressionVisitor v1 = new EvalExpressionVisitor(eq, null, null);
		assertTrue(v1.getResult());
		
		eq.setLeftExpression(l1);
		eq.setRightExpression(l3);
		EvalExpressionVisitor v2 = new EvalExpressionVisitor(eq, null, null);
		assertFalse(v2.getResult());
		
		gt.setLeftExpression(l1);
		gt.setRightExpression(l3);
		EvalExpressionVisitor v3 = new EvalExpressionVisitor(gt, null, null);
		assertTrue(v3.getResult());
		
		gt.setLeftExpression(l1);
		gt.setRightExpression(l2);
		EvalExpressionVisitor v4 = new EvalExpressionVisitor(gt, null, null);
		assertFalse(v4.getResult());
		
		gte.setLeftExpression(l1);
		gte.setRightExpression(l2);
		EvalExpressionVisitor v5 = new EvalExpressionVisitor(gte, null, null);
		assertTrue(v5.getResult());
		
		gte.setLeftExpression(l3);
		gte.setRightExpression(l4);
		EvalExpressionVisitor v6 = new EvalExpressionVisitor(gte, null, null);
		assertFalse(v6.getResult());
		
		lt.setLeftExpression(l3);
		lt.setRightExpression(l4);
		EvalExpressionVisitor v7 = new EvalExpressionVisitor(lt, null, null);
		assertTrue(v7.getResult());
		
		lt.setLeftExpression(l1);
		lt.setRightExpression(l2);
		EvalExpressionVisitor v8 = new EvalExpressionVisitor(lt, null, null);
		assertFalse(v8.getResult());
		
		lte.setLeftExpression(l1);
		lte.setRightExpression(l2);
		EvalExpressionVisitor v9 = new EvalExpressionVisitor(lte, null, null);
		assertTrue(v9.getResult());
		
		lte.setLeftExpression(l4);
		lte.setRightExpression(l3);
		EvalExpressionVisitor v10 = new EvalExpressionVisitor(lte, null, null);
		assertFalse(v10.getResult());
		
		neq.setLeftExpression(l3);
		neq.setRightExpression(l4);
		EvalExpressionVisitor v11 = new EvalExpressionVisitor(neq, null, null);
		assertTrue(v11.getResult());
		
		neq.setLeftExpression(l1);
		neq.setRightExpression(l2);
		EvalExpressionVisitor v12 = new EvalExpressionVisitor(neq, null, null);		
		assertFalse(v12.getResult());
	}
	
	@Test
	public void andExpression() {
		AndExpression rt = new AndExpression();
		
		EqualsTo eq = new EqualsTo();
		GreaterThan gt = new GreaterThan();
		rt.setLeftExpression(eq);
		rt.setRightExpression(gt);
		
		LongValue l1 = new LongValue(5);
		LongValue l2 = new LongValue(5);
		LongValue l3 = new LongValue(4);
		LongValue l4 = new LongValue(6);
		
		eq.setLeftExpression(l1);
		eq.setRightExpression(l2);
		gt.setLeftExpression(l4);
		gt.setRightExpression(l3);
		EvalExpressionVisitor v1 = new EvalExpressionVisitor(rt, null, null);
		assertTrue(v1.getResult());
		
		eq.setLeftExpression(l1);
		eq.setRightExpression(l3);
		gt.setLeftExpression(l4);
		gt.setRightExpression(l2);
		EvalExpressionVisitor v2 = new EvalExpressionVisitor(rt, null, null);
		assertFalse(v2.getResult());
		
		eq.setLeftExpression(l1);
		eq.setRightExpression(l2);
		gt.setLeftExpression(l3);
		gt.setRightExpression(l4);
		EvalExpressionVisitor v3 = new EvalExpressionVisitor(rt, null, null);
		assertFalse(v3.getResult());
		
		eq.setLeftExpression(l1);
		eq.setRightExpression(l3);
		gt.setLeftExpression(l2);
		gt.setRightExpression(l4);
		EvalExpressionVisitor v4 = new EvalExpressionVisitor(rt, null, null);
		assertFalse(v4.getResult());
	}
	
	@Test
	public void oneCol() {
		HashMap<String, Integer> schema = new HashMap<String,Integer>();
		Tuple t = new Tuple(2);
		schema.put("R.id", 0);
		schema.put("R.age", 1);
		t.add(1, 0);
		t.add(20,1);
		
		Table tbl = new Table();
		tbl.setName("Reserves");
		tbl.setAlias("R");
		Column col1 = new Column();
		col1.setTable(tbl);
		col1.setColumnName("id");
		Column col2 = new Column();
		col2.setTable(tbl);
		col2.setColumnName("age");
		
		EqualsTo eq = new EqualsTo();
		LongValue l1 = new LongValue(1);
		LongValue l2 = new LongValue(20);
		LongValue l3 = new LongValue(10);
		
		eq.setLeftExpression(col1);
		eq.setRightExpression(l1);
		EvalExpressionVisitor v1 = new EvalExpressionVisitor(eq, schema, t);
		assertTrue(v1.getResult());
		
		eq.setLeftExpression(col2);
		eq.setRightExpression(l2);
		EvalExpressionVisitor v2 = new EvalExpressionVisitor(eq, schema, t);
		assertTrue(v2.getResult());
		
		eq.setLeftExpression(col1);
		eq.setRightExpression(l3);
		EvalExpressionVisitor v3 = new EvalExpressionVisitor(eq, schema, t);
		assertFalse(v3.getResult());
	}
	
	@Test
	public void twoCol() {
		HashMap<String, Integer> schema = new HashMap<String,Integer>();
		Tuple t = new Tuple(3);
		schema.put("R.id1", 0);
		schema.put("R.id2", 1);
		schema.put("R.age", 2);
		t.add(1, 0);
		t.add(1, 1);
		t.add(20,2);
		
		Table tbl = new Table();
		tbl.setName("Reserves");
		tbl.setAlias("R");
		Column col1 = new Column();
		col1.setTable(tbl);
		col1.setColumnName("id1");
		Column col2 = new Column();
		col2.setTable(tbl);
		col2.setColumnName("id2");
		Column col3 = new Column();
		col3.setTable(tbl);
		col3.setColumnName("age");
		
		EqualsTo eq = new EqualsTo();
		
		eq.setLeftExpression(col1);
		eq.setRightExpression(col2);
		EvalExpressionVisitor v1 = new EvalExpressionVisitor(eq, schema, t);
		assertTrue(v1.getResult());
		
		eq.setLeftExpression(col2);
		eq.setRightExpression(col3);
		EvalExpressionVisitor v2 = new EvalExpressionVisitor(eq, schema, t);
		assertFalse(v2.getResult());
	}

}
