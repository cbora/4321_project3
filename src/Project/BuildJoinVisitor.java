package Project;

import java.util.ArrayList;
import java.util.HashMap;

import net.sf.jsqlparser.expression.AllComparisonExpression;
import net.sf.jsqlparser.expression.AnyComparisonExpression;
import net.sf.jsqlparser.expression.BinaryExpression;
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
 * Visitor to determine whether to SMJ or revert to BNLJ
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class BuildJoinVisitor implements ExpressionVisitor {
	
	/* ================================== 
	 * Fields
	 * ================================== */
	private HashMap<String, Integer> table_mapping; // table name and index mapper
	private Expression exp; // expression to evaluate
	private ArrayList<Expression> join; //  expression for the join
	private ArrayList<Integer> joinType; // join type to use
	
	/* ================================== 
	 * Constructor
	 * ================================== */
	/**
	 * Constructor
	 * @param table_mapping
	 * @param exp
	 */
	public BuildJoinVisitor(HashMap<String, Integer> table_mapping, Expression exp) {
		this.table_mapping = table_mapping;
		this.exp = exp;
		this.join = new ArrayList<Expression>();
		this.joinType = new ArrayList<Integer>();
		
		for (int i = 0 ; i < table_mapping.size() - 1; i++) {
			this.join.add(null);
			this.joinType.add(null);
		}
		
		handleExp();
		
		for (int i = 0; i < joinType.size(); i++) {
			if (joinType.get(i) == null) {
				joinType.set(i, 1);
			}
		}
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	
	/**
	 * 
	 * @return Expression for join
	 */
	public ArrayList<Expression> getJoin() {
		return this.join;
	}
	
	/**
	 * 
	 * @return Type of join to use
	 */
	public ArrayList<Integer> getJoinType() {
		return this.joinType;
	}
	
	/***
	 * Handles null expression for cross product joins
	 */
	private void handleExp() {
		if (this.exp == null)
			return;
		exp.accept(this);
	}
	
	private void addJoin(BinaryExpression node, boolean canUseSMJ) {
		int index1 = table_mapping.get(((Column) node.getLeftExpression()).getTable().getName());
		int index2 = table_mapping.get(((Column) node.getRightExpression()).getTable().getName());
		
		int index = Math.max(index1, index2) - 1; // left deep according to table_mapping
		if (join.get(index) == null){ //if no join is present yet
			join.set(index, node);
		}else { //otherwise and with existing
			join.set(index, new AndExpression(join.get(index), node));
		}
		
		if (!canUseSMJ) {
			joinType.set(index, 1);
		}
		else if (canUseSMJ && joinType.get(index) == null) {
			joinType.set(index, 2);
		}
	}

	/**
	 * Visits both children
	 * @param node - node to be visited
	 */
	@Override
	public void visit(AndExpression node) {
		node.getLeftExpression().accept(this);
		node.getRightExpression().accept(this);
	}

	/**
	 * Adds expression to appropriate bucket depending on left and right children
	 * @param node - node to be visited
	 */
	@Override
	public void visit(EqualsTo node) {
		addJoin(node, true);
	}
	
	/**
	 * Adds expression to appropriate bucket depending on left and right children
	 * @param node - node to be visited
	 */
	@Override
	public void visit(GreaterThan node) {
		addJoin(node, false);
	}

	/**
	 * Adds expression to appropriate bucket depending on left and right children
	 * @param node - node to be visited
	 */
	@Override
	public void visit(GreaterThanEquals node) {
		addJoin(node, false);
	}

	/**
	 * Adds expression to appropriate bucket depending on left and right children
	 * @param node - node to be visited
	 */
	@Override
	public void visit(MinorThan node) {
		addJoin(node, false);
	}

	/**
	 * Adds expression to appropriate bucket depending on left and right children
	 * @param node - node to be visited
	 */
	@Override
	public void visit(MinorThanEquals node) {
		addJoin(node, false);
	}

	/**
	 * Adds expression to appropriate bucket depending on left and right children
	 * @param node - node to be visited
	 */
	@Override
	public void visit(NotEqualsTo node) {
		addJoin(node, false);
	}
	
	// ------------------------------------------------------------------- //
	// ------------------------------------------------------------------- //
	// ---- Methods below here are unimplemented and included only to ---- //
	// -------------- comply with requirements of interfaces ------------- //
	// ------------------------------------------------------------------- //
	// ------------------------------------------------------------------- //
	
	@Override
	public void visit(Column node) {
	}
	
	@Override
	public void visit(LongValue arg0) {
		//unimplemented
	}
	
	@Override
	public void visit(NullValue arg0) {
		// unimplemented
	}
	@Override
	public void visit(Function arg0) {
		// unimplemented
	}
	@Override
	public void visit(InverseExpression arg0) {
		// unimplemented	
	}
	@Override
	public void visit(JdbcParameter arg0) {
		// unimplemented
	}
	@Override
	public void visit(DoubleValue arg0) {
		// unimplemented
	}
	@Override
	public void visit(DateValue arg0) {
		// unimplemented	
	}
	@Override
	public void visit(TimeValue arg0) {
		// unimplemented
	}
	@Override
	public void visit(TimestampValue arg0) {
		// unimplemented		
	}
	@Override
	public void visit(Parenthesis arg0) {
		// unimplemented		
	}
	@Override
	public void visit(StringValue arg0) {
		// unimplemented
	}
	@Override
	public void visit(Addition arg0) {
		// unimplemented		
	}
	@Override
	public void visit(Division arg0) {
		// unimplemented		
	}
	@Override
	public void visit(Multiplication arg0) {
		// unimplemented		
	}
	@Override
	public void visit(Subtraction arg0) {
		// unimplemented		
	}
	@Override
	public void visit(OrExpression arg0) {
		// unimplemented
	}
	@Override
	public void visit(Between arg0) {
		// unimplemented		
	}
	@Override
	public void visit(IsNullExpression arg0) {
		// unimplemented
	}
	@Override
	public void visit(LikeExpression arg0) {
		// unimplemented		
	}
	@Override
	public void visit(InExpression arg0) {
		// unimplemented
	}
	@Override
	public void visit(CaseExpression arg0) {
		// unimplemented
	}
	@Override
	public void visit(WhenClause arg0) {
		// unimplemented
	}
	@Override
	public void visit(ExistsExpression arg0) {
		// unimplemented
	}
	@Override
	public void visit(AllComparisonExpression arg0) {
		// unimplemented
	}
	@Override
	public void visit(AnyComparisonExpression arg0) {
		// unimplemented
	}
	@Override
	public void visit(Concat arg0) {
		// unimplemented
	}
	@Override
	public void visit(Matches arg0) {
		// unimplemented
	}
	@Override
	public void visit(BitwiseAnd arg0) {
		// unimplemented
	}
	@Override
	public void visit(BitwiseOr arg0) {
		// unimplemented
	}
	@Override
	public void visit(BitwiseXor arg0) {
		// unimplemented
	}
	@Override
	public void visit(SubSelect arg0) {
		// unimplemented
	}
}
