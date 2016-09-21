package Operators;

import Project.EvalExpressionVisitor;
import Project.Tuple;
import net.sf.jsqlparser.expression.Expression;

public class SelectOperator extends Operator {

	private Operator child;
	private Expression exp;
	
	public SelectOperator(Operator child, Expression exp) {
		this.child = child;
		this.exp = exp;
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
	
	public boolean passesCondition(Tuple t) {
		EvalExpressionVisitor e = new EvalExpressionVisitor(exp);
		return e.getResult();
	}

	@Override
	public void reset() {
		child.reset();
	}
	
	public Operator getChild() {
		return child;
	}

	public void setChild(Operator child) {
		this.child = child;
	}
}
