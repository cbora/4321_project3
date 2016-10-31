package Project;

import java.util.ArrayList;

import Operators.Operator;
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
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * Converts a join expression into arrays of what cols are involved in the join
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class JoinExp2OrderByVisitor implements ExpressionVisitor {

	/* ================================== 
	 * Fields
	 * ================================== */
	private Operator left; // left operator
	private Operator right; // right operator
	private ArrayList<Integer> leftOrderBy; // left sort order
	private ArrayList<Integer> rightOrderBy; // right sort order
	
	/* ================================== 
	 * Constructor
	 * ================================== */
	/**
	 * Constructor
	 * @param left - left operator
	 * @param right - right operator
	 * @param exp - join expression
	 */
	public JoinExp2OrderByVisitor(Operator left, Operator right, Expression exp) {
		this.left = left;
		this.right = right;
		this.leftOrderBy = new ArrayList<Integer>();
		this.rightOrderBy = new ArrayList<Integer>();
		exp.accept(this);
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	/**
	 * sort order of left relation based on join condition
	 * @return left sort order
	 */
	public int[] getLeft() {
		int[] result = new int[leftOrderBy.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = leftOrderBy.get(i);
		return result;
	}
	
	/***
	 * sort order of right relation based on join condition
	 * @return
	 */
	public int[] getRight() {
		int[] result = new int[rightOrderBy.size()];
		for (int i = 0; i < result.length; i++)
			result[i] = rightOrderBy.get(i);
		return result;
	}

	/**
	 * visitor for ands
	 * @param node - node to visit
	 */
	@Override
	public void visit(AndExpression node) {
		node.getLeftExpression().accept(this);
		node.getRightExpression().accept(this);
	}

	/**
	 * visitor for equals
	 * @param node - node to visit
	 */
	@Override
	public void visit(EqualsTo node) {
		node.getLeftExpression().accept(this);
		node.getRightExpression().accept(this);
	}

	/**
	 * visitor for cols
	 * @param node - node to visit
	 */
	@Override
	public void visit(Column node) {
		String tbl = node.getTable().getAlias() == null ? node.getTable().getName() : node.getTable().getAlias();
		
		OrderByElement obe = new OrderByElement();
		obe.setExpression(node);
		if (left.getSchema().containsKey(tbl + "." + node.getColumnName())) {
			leftOrderBy.add(left.getSchema().get(tbl + "." + node.getColumnName()));
		}
		else {
			rightOrderBy.add(right.getSchema().get(tbl + "." + node.getColumnName()));
		}
	}

	////////////////////////////////////////////////////////////////////////////////////////
	////////// Unimplemented ///////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////////////
	
	@Override
	public void visit(LongValue node) {
	}
	
	@Override
	public void visit(GreaterThan node) {

	}

	@Override
	public void visit(GreaterThanEquals node) {

	}

	@Override
	public void visit(MinorThan node) {

	}
	
	@Override
	public void visit(MinorThanEquals node) {

	}

	@Override
	public void visit(NotEqualsTo node) {

	}
	
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
