package Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
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
import net.sf.jsqlparser.statement.select.SubSelect;

/**
 * Parses the selection conditions and join conditions of WHERE clause
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class BuildSelectConditionsVisitor implements ExpressionVisitor {

	/* ================================== 
	 * Fields
	 * ================================== */
	private UnionFind union;
	private ArrayList<Expression> select; // list of selection expressions
	private ArrayList<HashMap<String,Pair>> select_ranges;
	//private ArrayList <Expression> join; // list of join expressions
	private Expression join;
	private Expression extra_exp; // any expressions that don't involve tables
	private Stack<Column> stack; // stack to keep track of cols seen in expressions
	private HashMap<String, Integer> table_mapping; // mapping between tables and integers
		
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param table_mapping - mapping that indicates where expressions involving table t should appear in select list and join list
	 * 	selections involving table t go to index table_mapping.get(t) in this.select
	 *	joins involving tables t1 and t2 go to index MAX(table_mapping.get(t1), table_mapping.get(t2)) - 1 in this.join
	 * @param e - WHERE expression
	 */
	public BuildSelectConditionsVisitor(HashMap<String, Integer> table_mapping, Expression e) {
		union = new UnionFind();
		
		select = new ArrayList<Expression>();
		select_ranges = new ArrayList<HashMap<String,Pair>>();
		for (int i=0; i<table_mapping.size(); i++) {
			select.add(null);
			select_ranges.add(new HashMap<String,Pair>());
		}
		//join = new ArrayList<Expression>();
		join = null;
//		for (int i=0; i<table_mapping.size()-1; i++)
//			join.add(null);
		
		extra_exp = null;
		
		stack = new Stack<Column>();
		
		this.table_mapping = table_mapping;
		
		if (e != null)
			e.accept(this);
		
		Set<String> attributes = union.getAttributes();
		for (String attribute : attributes) {
			UnionFindElement elem = union.find(attribute);
			if (elem.equals != null) {
				// construct EqualsTo & call addSelect
				Table tbl = new Table();
				tbl.setName(attribute.substring(0, attribute.indexOf('.')));
				Column col = new Column(tbl, attribute.substring(attribute.indexOf('.') + 1));
				
				LongValue val = new LongValue(elem.equals);
						
				EqualsTo eq = new EqualsTo(col, val);
				addSelect(tbl, eq);	
				addSelectRange(tbl, attribute, elem.equals, elem.equals);
			}
			else {
				if (elem.ceiling != null) {
					// construct MinorThanEquals & call addSelect
					Table tbl = new Table();
					tbl.setName(attribute.substring(0, attribute.indexOf('.')));
					Column col = new Column(tbl, attribute.substring(attribute.indexOf('.') + 1));
					
					LongValue val = new LongValue(elem.ceiling);
							
					MinorThanEquals lte = new MinorThanEquals(col, val);
					addSelect(tbl, lte);	
					addSelectRange(tbl, attribute, Integer.MIN_VALUE, elem.ceiling);
				}
				
				if (elem.floor != null) {
					// construct GreaterThanEquals and call addSelect
					Table tbl = new Table();
					tbl.setName(attribute.substring(0, attribute.indexOf('.')));
					Column col = new Column(tbl, attribute.substring(attribute.indexOf('.') + 1));
					
					LongValue val = new LongValue(elem.floor);
							
					GreaterThanEquals gte = new GreaterThanEquals(col, val);
					addSelect(tbl, gte);	
					addSelectRange(tbl, attribute, elem.floor, Integer.MAX_VALUE);
				}
			}
		}
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
	 * @return ArrayList of join expressions in left-deep corresponding to ordering provided in table_mapping
	 */
	public Expression getJoin() {
		return join;
	}

	/**
	 * @return any selection expressions that do not involve any tables
	 */
	public Expression getExp() {
		return extra_exp;
	}
	
	public UnionFind getUnion() {
		return union;
	}
	
	public ArrayList<HashMap<String, Pair>> getSelectRange() {
		return this.select_ranges;
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
			String attribute = c.getTable().getName() + "." + c.getColumnName();
			
			if (!(node instanceof NotEqualsTo)) {
				// find & update equals, ceil, floor -- second bullet
				if (union.find(attribute) == null)
					union.add(attribute);
				UnionFindElement elem = union.find(attribute);
				
				setBounds(elem, node);
			}
			else {
				addSelect(c.getTable(), node);	
			}
		}
		else if (stack.size() == 2){ // expression involves 2 attributes
			Column c = stack.pop();
			String attr1 = c.getTable().getName() + "." + c.getColumnName();
			
			Column c2 = stack.pop();
			String attr2 = c2.getTable().getName() + "." + c2.getColumnName();
			
			if (node instanceof EqualsTo) {
				// union together -- first bullet
				if (union.find(attr1) == null)
					union.add(attr1);
				if (union.find(attr2) == null)
					union.add(attr2);
				union.union(attr1, attr2);
				
				if (!c.getTable().toString().equals(c2.getTable().toString()) ){ // expression involved 2 cols from same table				
					addJoin(c.getTable(), c2.getTable(), node);
				}
			}
			else {
				if (c.getTable().toString().equals(c2.getTable().toString()) ){ // expression involved 2 cols from same table				
					addSelect(c.getTable(), node);
				}				
				else {	// expression involved 2 different tables			
					addJoin(c.getTable(), c2.getTable(), node);
				}
			}
		}
	}
	
	private void setBounds(UnionFindElement elem, BinaryExpression node) {
		LongValue val;
		if (node.getRightExpression() instanceof LongValue) {
			val = (LongValue) node.getRightExpression();
		}
		else if (node.getLeftExpression() instanceof LongValue) {
			val = (LongValue) node.getLeftExpression();
			
			if (node instanceof EqualsTo) 
				node = new EqualsTo(node.getRightExpression(), node.getLeftExpression());
			else if (node instanceof GreaterThan) 
				node = new MinorThan(node.getRightExpression(), node.getLeftExpression());
			else if (node instanceof GreaterThanEquals) 
				node = new MinorThanEquals(node.getRightExpression(), node.getLeftExpression());
			else if (node instanceof MinorThan) 
				node = new GreaterThan(node.getRightExpression(), node.getLeftExpression());
			else if (node instanceof MinorThanEquals) 
				node = new GreaterThanEquals(node.getRightExpression(), node.getLeftExpression());
			else
				return;
		}
		else {
			return;
		}
		
		if (node instanceof EqualsTo) {
			elem.equals = (int) val.toLong();
			elem.ceiling = (int) val.toLong();
			elem.floor = (int) val.toLong();
		}
		else if (node instanceof GreaterThan) {
			elem.floor = (int) val.toLong() + 1;
		}
		else if (node instanceof GreaterThanEquals) {
			elem.floor = (int) val.toLong();
		}
		else if (node instanceof MinorThan) {
			elem.ceiling = (int) val.toLong() - 1;
		}
		else if (node instanceof MinorThanEquals) {
			elem.ceiling = (int) val.toLong();
		}
	}

	/**
	 * Adds join expression to entry # MAX(table_mapping.get(t1), table_mapping.get(t2)) - 1 in this.join
	 * @param t1 - first table involved in join
	 * @param t2 - second table involved in join
	 * @param node - join expression
	 */
	private void addJoin(Table t1, Table t2 , BinaryExpression node){
//		int index1 = table_mapping.get(t1.getName());
//		int index2 = table_mapping.get(t2.getName());
//		
//		int index = Math.max(index1, index2) - 1; // left deep according to table_mapping
//		if (join.get(index) == null){ //if no join is present yet
//			join.set(index, node);
//		}else { //otherwise and with existing
//			join.set(index, new AndExpression(join.get(index), node));
//		}
		
		if (join == null) {
			join = node;
		}
		else {
			join = new AndExpression(join, node);
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
	
	private void addSelectRange(Table t, String attr, int low, int high) {
		int index = table_mapping.get(t.getName());
		if (select_ranges.get(index).containsKey(attr)) {
			int old_low = select_ranges.get(index).get(attr).low;
			int old_high = select_ranges.get(index).get(attr).high;
			select_ranges.get(index).get(attr).low = Math.max(low, old_low);
			select_ranges.get(index).get(attr).high = Math.min(high, old_high);
		}
		else {
			select_ranges.get(index).put(attr, new Pair(low, high));
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
		check(node);
	}
	
	/**
	 * Adds expression to appropriate bucket depending on left and right children
	 * @param node - node to be visited
	 */
	@Override
	public void visit(GreaterThan node) {
		check(node);	
	}

	/**
	 * Adds expression to appropriate bucket depending on left and right children
	 * @param node - node to be visited
	 */
	@Override
	public void visit(GreaterThanEquals node) {
		check(node);
	}

	/**
	 * Adds expression to appropriate bucket depending on left and right children
	 * @param node - node to be visited
	 */
	@Override
	public void visit(MinorThan node) {
		check(node);		
	}

	/**
	 * Adds expression to appropriate bucket depending on left and right children
	 * @param node - node to be visited
	 */
	@Override
	public void visit(MinorThanEquals node) {
		check(node);
	}

	/**
	 * Adds expression to appropriate bucket depending on left and right children
	 * @param node - node to be visited
	 */
	@Override
	public void visit(NotEqualsTo node) {
		check(node);
	}

	/**
	 * Push column to stack to inform parent that one of its children was a column
	 * @param node - node to be visited
	 */
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
