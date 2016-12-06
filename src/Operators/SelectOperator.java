package Operators;

import java.util.HashMap;

import Project.EvalExpressionVisitor;
import Project.Pair;
import Project.TableInfo;
import Project.Tuple;
import net.sf.jsqlparser.expression.Expression;

/**
 * Operator for applying selection conditions
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class SelectOperator extends OneTableOperator {

	/* ================================== 
	 * Fields
	 * ================================== */
	private OneTableOperator child; // child operator
	private Expression exp; // selection expression
	private HashMap<String, Integer> schema; // schema of tuples returned by this operator
	private HashMap<String, Pair> selectRange;
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param child - child in operator tree
	 * @param exp - selection condition
	 */
	public SelectOperator(OneTableOperator child, Expression exp, HashMap<String, Pair> selectRange) {
		this.child = child;
		this.exp = exp;
		this.schema = child.getSchema();
		this.selectRange = selectRange;
		//this.union = union;
		//calculateSelectCost();
	}
	
	/* ================================== 
	 * pretty print of method
	 * ================================== */
	public String prettyPrint(int depth){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<depth; i++)
			sb.append("-");
		sb.append("Select");
		sb.append("[");
		sb.append(this.exp);
		sb.append("]\n");
		sb.append(this.child.prettyPrint(depth+1));
		return sb.toString();
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
		do {
			Tuple t = child.getNextTuple();
			if (t == null)
				return null;
			else if (passesCondition(t))
				return t;
		} while (true);
	}
	
	@Override
	public void reset() {
		child.reset();
	}
	
	@Override
	public void reset(int index){}
	
	@Override
	public void close() {
		child.close();
	}
	
	@Override
	public TableInfo getTableInfo() {
		if (child == null)
			return null;
		return child.getTableInfo();
	}
	
	@Override
	public String getTableID() {
		if (child == null)
			return null;
		return child.getTableID();
	}
	
	public HashMap<String, Pair> getSelectRange() {
		return this.selectRange;
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
	
	

//	public int getRelationSize() {
//		return this.cost;
//	}
//	
//	private void calculateSelectCost() {
//		// visitor to get reduction factor
//		double reduction = visitor.getResult();
//		this.cost = Math.max((int) (this.child.getRelationSize() * reduction), 1);
//	}
}
