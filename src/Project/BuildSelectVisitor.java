package Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

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
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItemVisitor;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Union;

public class BuildSelectVisitor implements SelectVisitor, FromItemVisitor, ExpressionVisitor {

	private ArrayList <Expression> select; // list of selection expressions
	private ArrayList <Expression> join; // list of join expressions
	private Expression extra_exp; // any expressions that don't involve tables
	private Stack<Column> stack; // stack to keep track of cols seen in expressions
	private HashMap<String, Integer> table_mapping; // mapping between tables and integers
		// selections involving table t map to index table_mapping.get(t) in this.select
		// joins involving tables t1 and t2 map to index 
		// MAX(table_mapping.get(t1), table_mapping.get(t2)) - 1 in this.join
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	public BuildSelectVisitor(HashMap<String, Integer> table_mapping, Expression e) {
		select = new ArrayList<Expression>();
		for (int i=0; i<table_mapping.size(); i++)
			select.add(null);
		
		join = new ArrayList<Expression>();
		for (int i=0; i<table_mapping.size()-1; i++)
			join.add(null);
		
		extra_exp = null;
		
		stack = new Stack<Column>();
		
		this.table_mapping = table_mapping;
		
		if (e != null)
			e.accept(this);
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	
	/**
	 * @return ArrayList of selection expression that corresponds to order provided in table_mapping
	 */
	public ArrayList<Expression> getSelect() {
		return select;
	}

	/**
	 * @return ArrayList of join expressions in left-deep order in accordance with ordering provided in table_mapping
	 */
	public ArrayList<Expression> getJoin() {
		return join;
	}

	/**
	 * @return any selection expressions that do not involve any tables
	 */
	public Expression getExp() {
		return extra_exp;
	}
	
	/**
	 * Adds expression to the appropriate container (this.select, this.join, this.extra_exp)
	 * @param node - relational expression (EqualsTo, GreaterThan, etc.)
	 */
	private void check(BinaryExpression node){
		node.getLeftExpression().accept(this);
		node.getRightExpression().accept(this);
		if (stack.isEmpty()) { // expression did not involve any tables
			if (extra_exp == null)
				extra_exp = node;
			else
				extra_exp = new AndExpression(extra_exp, node);
		}
		else if (stack.size() == 1){ // expression involved 1 table, 1 constant	
			Column c = stack.pop();
			addSelect(c.getTable(), node);		
		}
		else if (stack.size() == 2){
			Column c = stack.pop();
			Column c2 = stack.pop();
			if (c.getTable().toString().equals(c2.getTable().toString()) ){ // expression 2 cols from same table				
				addSelect(c.getTable(), node);
			}				
			else {	// expression involved 2 different tables			
				addJoin(c.getTable(), c2.getTable(), node);
			}			
		}
	}

	/**
	 * Adds join expression to entry # MAX(table_mapping.get(t1), table_mapping.get(t2)) - 1 in this.join
	 * @param t1 - first table involved in join
	 * @param t2 - second table involved in join
	 * @param node - join expression
	 */
	private void addJoin(Table t1, Table t2 , BinaryExpression node){
		int index1 = table_mapping.get(t1.getName());
		int index2 = table_mapping.get(t2.getName());
		
		int index = Math.max(index1, index2) - 1; // left deep according to table_mapping
		if (join.get(index) == null){ //if no join is present yet
			join.set(index, node);
		}else { //otherwise and with existing
			join.set(index, new AndExpression(join.get(index), node));
		}
	}
	
	/**
	 * Adds selection expression to entry # table_mapping.get(t) in this.select
	 * @param t - table the selection involves
	 * @param node - selection expression
	 */
	private void addSelect(Table t, Expression node) {		
		int index = table_mapping.get(t.getName());		
		if (select.get(index) == null) { // if no expression for the table is present
			select.set(index, node);
		}
		else{ //otherwise and with existing
			select.set(index, new AndExpression(select.get(index), node));
		}		
		
	}
	
	@Override
	public void visit(LongValue arg0) {
		//nothing 
	}

	@Override
	public void visit(AndExpression node) {
		node.getLeftExpression().accept(this);
		node.getRightExpression().accept(this);
	}

	@Override
	public void visit(EqualsTo node) {
		check(node);
	}
	
	@Override
	public void visit(GreaterThan node) {
		check(node);	
	}

	@Override
	public void visit(GreaterThanEquals node) {
		check(node);
	}

	@Override
	public void visit(MinorThan node) {
		check(node);		
	}

	@Override
	public void visit(MinorThanEquals node) {
		check(node);
	}

	@Override
	public void visit(NotEqualsTo node) {
		check(node);
	}

	@Override
	public void visit(Column node) {
		stack.push(node);
	}
	// ------------------------------------------------------------------- //
	// ------------------------------------------------------------------- //
	// ---- Methods below here are unimplemented and included only to ---- //
	// -------------- comply with requirements of interfaces ------------- //
	// ------------------------------------------------------------------- //
	// ------------------------------------------------------------------- //
	
	@Override
	public void visit(Table arg0) {
		// TODO Auto-generated method stub	
	}

	@Override
	public void visit(PlainSelect arg0) {
		// TODO Auto-generated method stub
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
	@Override
	public void visit(SubJoin arg0) {
		// unimplemented
	}
	@Override
	public void visit(Union arg0) {
		// unimplemented
	}
}
