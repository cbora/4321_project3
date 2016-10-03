package Operators;

import java.util.HashMap;

import Project.EvalExpressionVisitor;
import Project.Tuple;
import net.sf.jsqlparser.expression.Expression;

/**
 * Operator for applying selection conditions
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class SelectOperator extends Operator {

	/* ================================== 
	 * Fields
	 * ================================== */
	private Operator child; // child operator
	private Expression exp; // selection expression
	private HashMap<String, Integer> schema; // schema of tuples returned by this operator
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param child - child in operator tree
	 * @param exp - selection condition
	 */
	public SelectOperator(Operator child, Expression exp) {
		this.child = child;
		this.exp = exp;
		this.schema = child.getSchema();
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
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
	 * Checks if a tuple t passes the condition in selection condition
	 * @param t - tuple we are checking
	 * @return true if pass, false otherwise
	 */
	private boolean passesCondition(Tuple t) {
		EvalExpressionVisitor e = new EvalExpressionVisitor(this.exp, this.schema, t);
		return e.getResult();
	}

}
