package Operators;

import java.util.HashMap;
import java.util.Set;

import Project.EvalExpressionVisitor;
import Project.Tuple;
import net.sf.jsqlparser.expression.Expression;

/**
 * Operator for joining tuples from two child operators
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class JoinOperator extends Operator {
	
	/* ================================== 
	 * Fields
	 * ================================== */
	private Operator leftChild; // left child operator in operator tree
	private Operator rightChild; // right child operator in operator tree
	private HashMap<String, Integer> schema; // schema of tuples returned by this operator
	private Expression exp; // join condition
	private Tuple lastLeft; // left tuple we have seen from leftChild
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param leftChild
	 * @param rightChild
	 * @param exp - expression for the condition we are joining on
	 */
	public JoinOperator(Operator leftChild, Operator rightChild, Expression exp) {
		this.leftChild = leftChild;
		this.rightChild = rightChild;
		this.schema = new HashMap<String, Integer>();
		this.exp = exp;
		this.lastLeft = this.leftChild.getNextTuple();
		
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
	
	/**
	 * Cartesian product constructor - set join condition to null
	 * @param leftChild
	 * @param rightChild
	 */
	public JoinOperator(Operator leftChild, Operator rightChild) {
		this(leftChild, rightChild, null);
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
		Tuple right = this.rightChild.getNextTuple();
		// if rightChild returns null, we should reset it and increment left
		if (right == null) { 
			this.rightChild.reset();
			lastLeft = this.leftChild.getNextTuple();
			right = this.rightChild.getNextTuple();
			// if left null, we've checked all tuples. if right still null, right is empty relation
			if (lastLeft == null || right == null)
				return null;
		}
		Tuple result = Tuple.concat(lastLeft, right);
		if (passesCondition(result))
			return result;
		else
			return getNextTuple();
	}

	@Override
	public void reset() {
		this.leftChild.reset();
		this.lastLeft = leftChild.getNextTuple();
		this.rightChild.reset();
	}
	
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
	private boolean passesCondition(Tuple t) {
		if (this.exp == null)
			return true;
		EvalExpressionVisitor e = new EvalExpressionVisitor(this.exp, this.schema, t);
		return e.getResult();
	}

}
