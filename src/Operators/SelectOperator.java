package Operators;

import java.util.HashMap;

import Project.EvalExpressionVisitor;
import Project.Tuple;
import net.sf.jsqlparser.expression.Expression;

public class SelectOperator extends Operator {

	private Operator child; // child operator
	private Expression exp; // selection expression
	private HashMap<String, Integer> schema; // schema
	
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
	
	/**
	 * Checks if a tuple t passes the condition in expression
	 * @param t tuple we are checking
	 * @return true if pass, false otherwise
	 */
	private boolean passesCondition(Tuple t) {
		EvalExpressionVisitor e = new EvalExpressionVisitor(this.exp, this.schema, t);
		return e.getResult();
	}

}
