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

	private ArrayList <Expression> select;
	private ArrayList <Expression> join;
	private Expression exp;
	private Stack<Column> stack; // stack to keep track of cols
	private HashMap<String, Integer> table_mapping;
	
	public BuildSelectVisitor(HashMap<String, Integer> table_mapping, Expression e) {
		select = new ArrayList<Expression>();
		for (int i=0; i<table_mapping.size(); i++)
			select.add(null);		
		join = new ArrayList<Expression>();
		for (int i=0; i<table_mapping.size()-1; i++)
			join.add(null);
		exp = null;
		stack = new Stack<Column>();
		this.table_mapping = table_mapping;
		if (e != null)
			e.accept(this);
	}
	
	
	public ArrayList<Expression> getSelect() {
		return select;
	}

	public void setSelect(ArrayList<Expression> select) {
		this.select = select;
	}

	public ArrayList<Expression> getJoin() {
		return join;
	}

	public void setJoin(ArrayList<Expression> join) {
		this.join = join;
	}

	public Expression getExp() {
		return exp;
	}

	public void setExp(Expression exp) {
		this.exp = exp;
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
	
	public void check(BinaryExpression node){
		//node.accept(this);
		node.getLeftExpression().accept(this);
		node.getRightExpression().accept(this);
		if (stack.isEmpty()) {
			if (exp == null)
				exp = node;
			else
				exp = new AndExpression(exp, node);
		}
		else if (stack.size() == 1){		
			Column c = stack.pop();
			addSelect(c, node);		
		}
		else if (stack.size() == 2){
			Column c = stack.pop();
			Column c2 = stack.pop();
			if (c.getTable().toString().equals(c2.getTable().toString()) ){ // same column names				
				addSelect(c, node);
			}				
			else {				
				addJoin(c, c2, node);
			}			
		}
	}

	public void addJoin(Column c, Column c2, Expression node){
		System.out.println(c.getTable().getName());
		System.out.println(c.getTable().getWholeTableName());
		String name1 = c.getTable().getName();
		String name2 = c.getTable().toString();
		System.out.println(table_mapping);
		int index1 = table_mapping.get(name1);
		int index2 = table_mapping.get(name2);
		
		int index = Math.max(index1, index2) - 1;
		if (join.get(index) == null){
			join.set(index, node);
		}else {
			join.set(index, new AndExpression(join.get(index), node));
		}
	}
	
	public void addSelect(Column c, Expression node) {		
		String name = c.getTable().toString();
		int index = table_mapping.get(name);		
		if (select.get(index) == null) { // if no expression for the table is present
			select.set(index, node);
		}
		else{
			select.set(index, new AndExpression(select.get(index), node));
		}		
		
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
