package Indexing;

import Project.TableInfo;
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

/**
 * Visitor that determines which select conditions we can use index on and which we cannot
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class IndexExpressionVisitor implements ExpressionVisitor {

	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	private int lowkey; // lowest key we can use index for
	private int highkey; // highest key we can use index for
	private TableInfo tableInfo; // tableInfo on table we want to use index of
	private Expression otherSlctExps; // select conditions we cannot use index for
	private boolean canUseIndex; // can we use the index?
	
	/*
	 * ================================== 
	 * Constructor
	 * ==================================
	 */
	/**
	 * Constructor
	 * @param e - select expression
	 * @param tableInfo - tableInfo for table we are querying
	 */
	public IndexExpressionVisitor(Expression e, TableInfo tableInfo) {
		this.lowkey = Integer.MIN_VALUE;
		this.highkey = Integer.MAX_VALUE;
		this.tableInfo = tableInfo;
		this.otherSlctExps = null;
		this.canUseIndex = false;
		if(e != null)
			e.accept(this);
	}
	
	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */
	/**
	 * 
	 * @return true if we can use the index, false otherwise
	 */
	public boolean canUseIndex() {
		return this.canUseIndex;
	}
	
	/**
	 * getter for lowkey
	 * @return lowkey
	 */
	public int getLowkey() {
		return this.lowkey;
	}
	
	/**
	 * getter for highkey
	 * @return highkey
	 */
	public int getHighkey() {
		return this.highkey;
	}
	
	/**
	 * getter for select expressions we can't use index on
	 * @return otherSlctExps
	 */
	public Expression getOtherSlctExps() {
		return this.otherSlctExps;
	}
	
	/**
	 * visitor for and
	 * @param node - node to be visited
	 */
	@Override
	public void visit(AndExpression node) {
		node.getLeftExpression().accept(this);
		node.getRightExpression().accept(this);
	}
	
	/**
	 * visitor for not equals
	 * @param node - node to be visited
	 */
	@Override
	public void visit(NotEqualsTo node) {
		addToOtherSlctExps(node);
	}
	
	/**
	 * visitor for equals
	 * @param node - node to be visited
	 */
	@Override
	public void visit(EqualsTo node) {		
		Expression left = node.getLeftExpression();
		Expression right = node.getRightExpression();
		
		if (leftIndexRightVal(left, right)) {
			LongValue rightVal = (LongValue) right;
			updateKeys((int) rightVal.getValue(), (int) rightVal.getValue());
		}
		else if (rightIndexLeftVal(left, right)) {
			LongValue leftVal = (LongValue) left;
			updateKeys((int) leftVal.getValue(), (int) leftVal.getValue());
		}
		else {
			addToOtherSlctExps(node);
		}
	}

	/**
	 * visitor for greater than
	 * @param node - node to be visited
	 */
	@Override
	public void visit(GreaterThan node) { // set low key
		Expression left = node.getLeftExpression();
		Expression right = node.getRightExpression();
		
		if (leftIndexRightVal(left, right)) {
			LongValue rightVal = (LongValue) right;
			updateKeys((int) rightVal.getValue() + 1, Integer.MAX_VALUE);
		}
		else if (rightIndexLeftVal(left, right)) {
			LongValue leftVal = (LongValue) left;
			updateKeys(Integer.MIN_VALUE, (int) leftVal.getValue() - 1);
		}
		else {
			addToOtherSlctExps(node);
		}
	}

	/**
	 * visitor for greater than or equals
	 * @param node - node to be visited
	 */
	@Override
	public void visit(GreaterThanEquals node) { 
		Expression left = node.getLeftExpression();
		Expression right = node.getRightExpression();
		
		if (leftIndexRightVal(left, right)) {
			LongValue rightVal = (LongValue) right;
			updateKeys((int) rightVal.getValue(), Integer.MAX_VALUE);
		}
		else if (rightIndexLeftVal(left, right)) {
			LongValue leftVal = (LongValue) left;
			updateKeys(Integer.MIN_VALUE, (int) leftVal.getValue());
		}
		else {
			addToOtherSlctExps(node);
		}
	}
	
	/**
	 * visitor for less than
	 * @param node - node to be visited
	 */
	@Override
	public void visit(MinorThan node) { 
		Expression left = node.getLeftExpression();
		Expression right = node.getRightExpression();
		
		if (leftIndexRightVal(left, right)) {
			LongValue rightVal = (LongValue) right;
			updateKeys(Integer.MIN_VALUE, (int) rightVal.getValue() - 1);
		}
		else if (rightIndexLeftVal(left, right)) {
			LongValue leftVal = (LongValue) left;
			updateKeys((int) leftVal.getValue() + 1, Integer.MAX_VALUE);
		}
		else {
			addToOtherSlctExps(node);
		}
	}

	/**
	 * visitor for less than or equals
	 * @param node - node to be visited
	 */
	@Override
	public void visit(MinorThanEquals node) { //low
		Expression left = node.getLeftExpression();
		Expression right = node.getRightExpression();
		
		if (leftIndexRightVal(left, right)) {
			LongValue rightVal = (LongValue) right;
			updateKeys(Integer.MIN_VALUE, (int) rightVal.getValue());
			return;
		}
		else if (rightIndexLeftVal(left, right)) {
			LongValue leftVal = (LongValue) left;
			updateKeys((int) leftVal.getValue(), Integer.MAX_VALUE);
			return;
		}
		else {
			addToOtherSlctExps(node);
		}
	}
	
	/**
	 * adds condition to otherSlctExps
	 * @param exp - condition to add
	 */
	private void addToOtherSlctExps(Expression exp) {
		if (this.otherSlctExps == null)
			this.otherSlctExps = exp;
		else
			this.otherSlctExps = new AndExpression(this.otherSlctExps, exp);
	}
	
	/**
	 * checks whether left term is index and right is a long value
	 * @param left - left term
	 * @param right - right term
	 * @return true if left is index and right is long value. false otherwise
	 */
	private boolean leftIndexRightVal(Expression left, Expression right) {
		if (left instanceof Column && right instanceof LongValue) {
			Column leftCol = (Column) left;
			if (isIndex(leftCol)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * checks whether right term is index and left is a long value
	 * @param left - left term
	 * @param right - right term
	 * @return true if right is index and left is long value. false otherwise
	 */
	private boolean rightIndexLeftVal(Expression left, Expression right) {
		if (left instanceof LongValue && right instanceof Column) {
			Column rightCol = (Column) right;
			if (isIndex(rightCol)) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * updates lowkey and highkey if the new values shrink the current window
	 * @param lowkey - proposed new lowked
	 * @param highkey - proposed new highkey
	 */
	private void updateKeys(int lowkey, int highkey) {
		this.canUseIndex = true;
		this.lowkey = lowkey > this.lowkey ? lowkey : this.lowkey;
		this.highkey = highkey < this.highkey ? highkey : this.highkey;
	}
	
	/**
	 * checks if we have an index on col
	 * @param col - column to check
	 * @return true if we have an index on col
	 */
	private boolean isIndex(Column col) {
		if (this.tableInfo.getIndexInfo() == null)
			return false;
		return col.getColumnName().equals(tableInfo.getIndexAttribute());
	}
	
	/*******************
	 * NOT NEEDED
	 */

	@Override
	public void visit(Column node) { 
		
	}
	
	@Override
	public void visit(LongValue node) {
		
	}
	
	@Override
	public void visit(NullValue node) {

	}

	@Override
	public void visit(Function node) {

	}

	@Override
	public void visit(InverseExpression node) {

	}

	@Override
	public void visit(JdbcParameter node) {

	}

	@Override
	public void visit(DoubleValue node) {

	}

	@Override
	public void visit(DateValue node) {

	}

	@Override
	public void visit(TimeValue node) {

	}

	@Override
	public void visit(TimestampValue node) {

	}

	@Override
	public void visit(Parenthesis node) {

	}

	@Override
	public void visit(StringValue node) {

	}

	@Override
	public void visit(Addition node) {

	}

	@Override
	public void visit(Division node) {

	}

	@Override
	public void visit(Multiplication node) {

	}

	@Override
	public void visit(Subtraction node) {

	}

	@Override
	public void visit(OrExpression node) {

	}

	@Override
	public void visit(Between node) {

	}

	@Override
	public void visit(InExpression node) {

	}

	@Override
	public void visit(IsNullExpression node) {

	}

	@Override
	public void visit(LikeExpression node) {

	}



	@Override
	public void visit(SubSelect node) {

	}

	@Override
	public void visit(CaseExpression node) {

	}

	@Override
	public void visit(WhenClause node) {

	}

	@Override
	public void visit(ExistsExpression node) {

	}

	@Override
	public void visit(AllComparisonExpression node) {

	}

	@Override
	public void visit(AnyComparisonExpression node) {

	}

	@Override
	public void visit(Concat node) {

	}

	@Override
	public void visit(Matches node) {

	}

	@Override
	public void visit(BitwiseAnd node) {

	}

	@Override
	public void visit(BitwiseOr node) {

	}

	@Override
	public void visit(BitwiseXor node) {

	}

}
