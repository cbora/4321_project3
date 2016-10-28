package Operators;

import java.util.HashMap;
import java.util.Set;

import Project.EvalExpressionVisitor;
import Project.Tuple;
import net.sf.jsqlparser.expression.Expression;

public abstract class JoinOperator extends Operator {

	/* ================================== 
	 * Fields
	 * ================================== */
	protected Operator leftChild; // left child operator in operator tree
	protected Operator rightChild; // right child operator in operator tree
	protected HashMap<String, Integer> schema; // schema of tuples returned by this operator
	protected Expression exp; // join condition
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param leftChild
	 * @param rightChild
	 * @param exp - join condition
	 */
	public JoinOperator(Operator leftChild, Operator rightChild, Expression exp) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		this.schema = new HashMap<String, Integer>();
		this.exp = exp;
		
		HashMap<String, Integer> s1 = this.leftChild.getSchema();
		this.schema.putAll(s1); // add all the left child's columns to schema
		
		HashMap<String, Integer> s2 = this.rightChild.getSchema();
		Set<String> keys = s2.keySet();
		// add all right child's columns to schema, but increment index
		// to reflect the fact that they now are located after left child's columns
		for (String key : keys) {
			this.schema.put(key, s2.get(key) + s1.size());
		}
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	@Override
	public HashMap<String, Integer> getSchema() {
		return this.schema;
	}

	@Override
	public abstract Tuple getNextTuple();

	@Override
	public abstract void reset();

	@Override
	public void close() {
		leftChild.close();
		rightChild.close();
	}
	
	/**
	 * Checks if a tuple t passes the condition in join condition
	 * @param t - tuple we are checking
	 * @return true if pass, false otherwise
	 */
	protected boolean passesCondition(Tuple t) {
		if (this.exp == null)
			return true;
		EvalExpressionVisitor e = new EvalExpressionVisitor(this.exp, this.schema, t);
		return e.getResult();
	}

}
