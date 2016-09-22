package Operators;

import java.util.HashMap;

import Project.EvalExpressionVisitor;
import Project.Tuple;
import net.sf.jsqlparser.expression.Expression;

public class SelectOperator extends Operator {

	private Operator child;
	private Expression exp;
	private HashMap<String, Integer> schema;
	
	public SelectOperator(Operator child, Expression exp) {
		this.child = child;
		this.exp = exp;
		this.schema = child.getSchema();
	}
	
	@Override
	public HashMap<String, Integer> getSchema() {
		return this.schema;
	}

	@Override
	public Tuple getNextTuple() {
		Tuple t = child.getNextTuple();
		if (t == null) 
			return null;
		else if (passesCondition(t))
			return t;
		else 
			return getNextTuple();
	}
	
	@Override
	public void reset() {
		child.reset();
	}
	
	@Override
	public void close() {
		child.close();
	}
	
	private boolean passesCondition(Tuple t) {
		EvalExpressionVisitor e = new EvalExpressionVisitor(exp, schema, t);
		return e.getResult();
	}
	
	public Operator getChild() {
		return child;
	}

	public void setChild(Operator child) {
		this.child = child;
	}
}
