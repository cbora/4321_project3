package Project;

import java.util.HashMap;
import java.util.Stack;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.CaseExpression;
import net.sf.jsqlparser.expression.DateValue;
import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.InverseExpression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.expression.LongValue;
import net.sf.jsqlparser.expression.NullValue;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.TimeValue;
import net.sf.jsqlparser.expression.TimestampValue;
import net.sf.jsqlparser.expression.WhenClause;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseAnd;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseOr;
import net.sf.jsqlparser.expression.operators.arithmetic.BitwiseXor;
import net.sf.jsqlparser.expression.operators.arithmetic.Concat;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExistsExpression;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.expression.operators.relational.IsNullExpression;
import net.sf.jsqlparser.expression.operators.relational.LikeExpression;
import net.sf.jsqlparser.expression.operators.relational.Matches;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

public class EvalExpressionVisitor implements ExpressionVisitor {

	private boolean result; // whether expression is true or false
	private Tuple tuple;	// tuple we are evaluating expression on
	private HashMap<String, Integer> schema; 		// tuple schema
	private Stack<Long> nums = new Stack<Long>(); 	// holds longs for relational expressions
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	public EvalExpressionVisitor(Expression exp, HashMap<String, Integer> schema, Tuple tuple) {
		this.schema = schema;
		this.tuple = tuple;
		exp.accept(this);
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	
	/**
	 * @return true if expression evaluates to true, false otherwise
	 */
	public boolean getResult() {
		return result;
	}
	
	@Override
	public void visit(LongValue node) {
		nums.push(node.toLong());
	}

	@Override
	public void visit(AndExpression node) {
		node.getLeftExpression().accept(this);
		if (result)
			node.getRightExpression().accept(this);
	}

	@Override
	public void visit(EqualsTo node) {
		node.getLeftExpression().accept(this);
		node.getRightExpression().accept(this);
		
		long second = nums.pop();
		long first = nums.pop();
		result = (first == second);
	}

	@Override
	public void visit(GreaterThan node) {
		node.getLeftExpression().accept(this);
		node.getRightExpression().accept(this);
		
		long second = nums.pop();
		long first = nums.pop();
		result = (first > second);
	}

	@Override
	public void visit(GreaterThanEquals node) {
		node.getLeftExpression().accept(this);
		node.getRightExpression().accept(this);
		
		long second = nums.pop();
		long first = nums.pop();
		result = (first >= second);
	}


	@Override
	public void visit(MinorThan node) {
		node.getLeftExpression().accept(this);
		node.getRightExpression().accept(this);
		
		long second = nums.pop();
		long first = nums.pop();
		result = (first < second);
	}

	@Override
	public void visit(MinorThanEquals node) {
		node.getLeftExpression().accept(this);
		node.getRightExpression().accept(this);
		
		long second = nums.pop();
		long first = nums.pop();
		result = (first <= second);
	}

	@Override
	public void visit(NotEqualsTo node) {
		node.getLeftExpression().accept(this);
		node.getRightExpression().accept(this);
		
		long second = nums.pop();
		long first = nums.pop();
		result = (first != second);
	}

	@Override
	public void visit(Column node) {
		// use schema to find out where column is in tuple
		int index = schema.get(node.getWholeColumnName());
		// retrieve value from tuple, push to stack
		nums.push((long) tuple.getVal(index));
	}

	////////////////////////////////////////////////////////////////////////////////////////
	////////// Unimplemented ///////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void visit(NullValue arg0) {
	}

	@Override
	public void visit(Function arg0) {

	}

	@Override
	public void visit(InverseExpression arg0) {

	}

	@Override
	public void visit(JdbcParameter arg0) {

	}

	@Override
	public void visit(DoubleValue arg0) {

	}
	
	@Override
	public void visit(DateValue arg0) {

	}

	@Override
	public void visit(TimeValue arg0) {

	}

	@Override
	public void visit(TimestampValue arg0) {

	}

	@Override
	public void visit(Parenthesis arg0) {

	}

	@Override
	public void visit(StringValue arg0) {

	}

	@Override
	public void visit(Addition arg0) {

	}

	@Override
	public void visit(Division arg0) {

	}

	@Override
	public void visit(Multiplication arg0) {

	}

	@Override
	public void visit(Subtraction arg0) {

	}

	@Override
	public void visit(OrExpression arg0) {

	}

	@Override
	public void visit(Between arg0) {

	}
	
	@Override
	public void visit(InExpression arg0) {

	}

	@Override
	public void visit(IsNullExpression arg0) {

	}

	@Override
	public void visit(LikeExpression arg0) {

	}
	
	@Override
	public void visit(SubSelect arg0) {

	}

	@Override
	public void visit(CaseExpression arg0) {

	}

	@Override
	public void visit(WhenClause arg0) {

	}

	@Override
	public void visit(ExistsExpression arg0) {

	}

	@Override
	public void visit(AllComparisonExpression arg0) {

	}

	@Override
	public void visit(AnyComparisonExpression arg0) {

	}

	@Override
	public void visit(Concat arg0) {

	}

	@Override
	public void visit(Matches arg0) {

	}

	@Override
	public void visit(BitwiseAnd arg0) {

	}

	@Override
	public void visit(BitwiseOr arg0) {

	}

	@Override
	public void visit(BitwiseXor arg0) {
	}

}
