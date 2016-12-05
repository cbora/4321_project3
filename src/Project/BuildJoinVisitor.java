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

public class BuildJoinVisitor implements ExpressionVisitor {
	
	private HashMap<String, Integer> table_mapping;
	private Expression exp;
	//private UnionFind union;
	private ArrayList<Expression> join;

	//public BuildJoinVisitor(HashMap<String, Integer> table_mapping, Expression exp, UnionFind union) {
	public BuildJoinVisitor(HashMap<String, Integer> table_mapping, Expression exp) {
		this.table_mapping = table_mapping;
		this.exp = exp;
		//this.union = union;
		this.join = new ArrayList<Expression>();
		
		for (int i = 0 ; i < table_mapping.size(); i++)
			this.join.add(null);
		
		//handleUnion();
		handleExp();
	}
	
	public ArrayList<Expression> getJoin() {
		return this.join;
	}
	

//	private void handleUnion() {
//		for (String attr : this.union.getAttributes()) {
//			String tbl = attr.substring(0, attr.indexOf('.'));
//			int idx = this.table_mapping.get(tbl);
//			
//			if (this.join.get(idx) == null) {
//				this.join.set(idx, buildExpression(attr))
//			}
//			else {
//				this.join.set(idx, new AndExpression(this.join.get(idx), buildExpression(attr)));
//			}
//		}
//	}
	
	private void handleExp() {
		if (this.exp == null)
			return;
		exp.accept(this);
	}
	
	private void addJoin(BinaryExpression node) {
		int index1 = table_mapping.get(((Column) node.getLeftExpression()).getTable().getName());
		int index2 = table_mapping.get(((Column) node.getRightExpression()).getTable().getName());
		
		int index = Math.max(index1, index2) - 1; // left deep according to table_mapping
		if (join.get(index) == null){ //if no join is present yet
			join.set(index, node);
		}else { //otherwise and with existing
			join.set(index, new AndExpression(join.get(index), node));
		}
	}
	
//	private Expression buildExpression(String attr) {
//		UnionFindElement elem = this.union.find(attr);
//	}
	
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
		addJoin(node);
	}
	
	/**
	 * Adds expression to appropriate bucket depending on left and right children
	 * @param node - node to be visited
	 */
	@Override
	public void visit(GreaterThan node) {
		addJoin(node);
	}

	/**
	 * Adds expression to appropriate bucket depending on left and right children
	 * @param node - node to be visited
	 */
	@Override
	public void visit(GreaterThanEquals node) {
		addJoin(node);
	}

	/**
	 * Adds expression to appropriate bucket depending on left and right children
	 * @param node - node to be visited
	 */
	@Override
	public void visit(MinorThan node) {
		addJoin(node);
	}

	/**
	 * Adds expression to appropriate bucket depending on left and right children
	 * @param node - node to be visited
	 */
	@Override
	public void visit(MinorThanEquals node) {
		addJoin(node);
	}

	/**
	 * Adds expression to appropriate bucket depending on left and right children
	 * @param node - node to be visited
	 */
	@Override
	public void visit(NotEqualsTo node) {
		addJoin(node);
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
