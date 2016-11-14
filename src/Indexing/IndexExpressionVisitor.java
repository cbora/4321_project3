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

public class IndexExpressionVisitor implements ExpressionVisitor {

	private int lowkey;
	private int highkey;
	private TableInfo tableInfo; 
	private Expression otherSlctExps;
	private boolean canUseIndex;
	
	
	public IndexExpressionVisitor(Expression e, TableInfo tableInfo) {
		this.lowkey = Integer.MIN_VALUE;
		this.highkey = Integer.MAX_VALUE;
		this.tableInfo = tableInfo;
		this.otherSlctExps = null;
		this.canUseIndex = false;
		if(e != null)
			e.accept(this);
	}
	
	public boolean canUseIndex() {
		return this.canUseIndex;
	}
	
	public int getLowkey() {
		return this.lowkey;
	}
	
	public int getHighkey() {
		return this.highkey;
	}
	
	public Expression getOtherSlctExps() {
		return this.otherSlctExps;
	}
	
	@Override
	public void visit(AndExpression arg0) {
		arg0.getLeftExpression().accept(this);
		arg0.getRightExpression().accept(this);
	}
	
	@Override
	public void visit(NotEqualsTo arg0) {
		addToOtherSlctExps(arg0);
	}
	
	@Override
	public void visit(EqualsTo arg0) {		
		Expression left = arg0.getLeftExpression();
		Expression right = arg0.getRightExpression();
		
		if (leftIndexRightVal(left, right)) {
			LongValue rightVal = (LongValue) right;
			updateKeys((int) rightVal.getValue(), (int) rightVal.getValue());
		}
		else if (rightIndexLeftVal(left, right)) {
			LongValue leftVal = (LongValue) left;
			updateKeys((int) leftVal.getValue(), (int) leftVal.getValue());
		}
		else {
			addToOtherSlctExps(arg0);
		}
	}

	@Override
	public void visit(GreaterThan arg0) { // set low key
		Expression left = arg0.getLeftExpression();
		Expression right = arg0.getRightExpression();
		
		if (leftIndexRightVal(left, right)) {
			LongValue rightVal = (LongValue) right;
			updateKeys((int) rightVal.getValue() + 1, Integer.MAX_VALUE);
		}
		else if (rightIndexLeftVal(left, right)) {
			LongValue leftVal = (LongValue) left;
			updateKeys(Integer.MIN_VALUE, (int) leftVal.getValue() - 1);
		}
		else {
			addToOtherSlctExps(arg0);
		}
	}

	@Override
	public void visit(GreaterThanEquals arg0) { 
		Expression left = arg0.getLeftExpression();
		Expression right = arg0.getRightExpression();
		
		if (leftIndexRightVal(left, right)) {
			LongValue rightVal = (LongValue) right;
			updateKeys((int) rightVal.getValue(), Integer.MAX_VALUE);
		}
		else if (rightIndexLeftVal(left, right)) {
			LongValue leftVal = (LongValue) left;
			updateKeys(Integer.MIN_VALUE, (int) leftVal.getValue());
		}
		else {
			addToOtherSlctExps(arg0);
		}
	}
	
	@Override
	public void visit(MinorThan arg0) { 
		Expression left = arg0.getLeftExpression();
		Expression right = arg0.getRightExpression();
		
		if (leftIndexRightVal(left, right)) {
			LongValue rightVal = (LongValue) right;
			updateKeys(Integer.MIN_VALUE, (int) rightVal.getValue() - 1);
		}
		else if (rightIndexLeftVal(left, right)) {
			LongValue leftVal = (LongValue) left;
			updateKeys((int) leftVal.getValue() + 1, Integer.MAX_VALUE);
		}
		else {
			addToOtherSlctExps(arg0);
		}
	}

	@Override
	public void visit(MinorThanEquals arg0) { //low
		Expression left = arg0.getLeftExpression();
		Expression right = arg0.getRightExpression();
		
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
			addToOtherSlctExps(arg0);
		}
	}
	
	private void addToOtherSlctExps(Expression exp) {
		if (this.otherSlctExps == null)
			this.otherSlctExps = exp;
		else
			this.otherSlctExps = new AndExpression(this.otherSlctExps, exp);
	}
	
	private boolean leftIndexRightVal(Expression left, Expression right) {
		if (left instanceof Column && right instanceof LongValue) {
			Column leftCol = (Column) left;
			if (isIndex(leftCol)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean rightIndexLeftVal(Expression left, Expression right) {
		if (left instanceof LongValue && right instanceof Column) {
			Column rightCol = (Column) right;
			if (isIndex(rightCol)) {
				return true;
			}
		}
		return false;
	}
	
	private void updateKeys(int lowkey, int highkey) {
		this.canUseIndex = true;
		this.lowkey = lowkey > this.lowkey ? lowkey : this.lowkey;
		this.highkey = highkey < this.highkey ? highkey : this.highkey;
	}
	
	private boolean isIndex(Column col) {
		if (this.tableInfo.getIndexInfo() == null)
			return false;
		return col.getColumnName().equals(tableInfo.getIndexAttribute());
	}
	
	/*******************
	 * NOT NEEDED
	 */

	@Override
	public void visit(Column arg0) { 
		
	}
	
	@Override
	public void visit(LongValue arg0) {
		
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
