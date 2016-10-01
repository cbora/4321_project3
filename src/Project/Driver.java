package Project;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import Operators.HashDupElimOperator;
import Operators.JoinOperator;
import Operators.Operator;
import Operators.ProjectOperator;
import Operators.ScanOperator;
import Operators.SelectOperator;
import Operators.SortOperator;
import Operators.SortedDupElimOperator;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;

public class Driver {

	private PlainSelect plain_select; // query we are building operator tree for
	private Operator root; // root of operator tree
	private HashMap<String, Integer> table_mapping; // maps table to position in join list
	private LinkedList<Operator> linked_operator; // list of operators so far -- starts by constructing scan operators in order of join list
	private BuildSelectVisitor bsv; // retrieves selection/join information regarding our expression
		// builds selection expression list in order that corresponds with ordering in table_mapping
		// builds join expression list in left_deep order that corresponds with ordering in table_mapping
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	public Driver(PlainSelect plain_select) {
		this.plain_select = plain_select;
		this.table_mapping = new HashMap<String, Integer>();
		this.linked_operator = new LinkedList<Operator>();

		// builds scan operators for each table /
		scanBuilder();
		
		// build selection/join operators 
		this.bsv = new BuildSelectVisitor(this.table_mapping, this.plain_select.getWhere());
		selectBuilder();
		joinBuilder();
		this.root = this.linked_operator.removeLast();
		Expression extra_exp = this.bsv.getExp(); // any selection condition that don't involve a table		 	
		if ( extra_exp != null ) {
			this.root = new SelectOperator(this.root, extra_exp);
		}
		
		// add projection operator if needed
		projectBuilder();
		
		// add sort operator if needed
		sortBuilder();
		
		// add duplicate operator if needed 
		duplicateBuilder();
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	
	/**
	 * @return root of the operator tree
	 */
	public Operator getRoot() {
		return root;		
	}
	
	/**
	 * Adds scan operators to linked_operator and adds tables to table_mapping
	 * according the order they appear in join list
	 */
	private void scanBuilder() {	
		DbCatalog db = DbCatalog.getInstance();	
		
		// get a list of tables in query
		ArrayList<Table> tables = new ArrayList<Table>();
		tables.add((Table) plain_select.getFromItem());
		if (plain_select.getJoins() != null) {
			for (int i=0; i < plain_select.getJoins().size(); i++){
				Table t = (Table) ((Join) plain_select.getJoins().get(i)).getRightItem();
				tables.add(t);
			}
		}
		
		// for each table, create a scan operator
		for (int i=0; i < tables.size(); i++){
			TableInfo tableInfo = db.get(tables.get(i).getName());
			ScanOperator op = new ScanOperator(tableInfo, tables.get(i));
			table_mapping.put(tables.get(i).getAlias() == null ? tables.get(i).getName() : tables.get(i).getAlias() , i);
			linked_operator.add(op);
		}
	}
	
	/**
	 * replaces operators in linked_operator with select operator
	 * if old operator corresponds to table that has a selection condition
	 */
	private void selectBuilder() {
		ArrayList<Expression> select_exp = this.bsv.getSelect();
		ListIterator<Operator> iter = linked_operator.listIterator();
		int index = 0;
		while(iter.hasNext()){	// 			
			if (select_exp.get(index) != null) {
				SelectOperator so = new SelectOperator(iter.next(), select_exp.get(index));			
				iter.set(so);
			}
			else {
				iter.next();
			}
			index++;
		}				
	}
	
	/**
	 * repeatedly pops first two elements off linked_operator
	 * replacing them with appropriate join operator until linked_operator has just one
	 * element remaining
	 */
	private void joinBuilder() {
		ArrayList<Expression> join_exp = this.bsv.getJoin();
		int size = linked_operator.size();
		for (int i=0; i<size-1; i++){
			Operator op1 = linked_operator.removeFirst();
			Operator op2 = linked_operator.removeFirst();
			JoinOperator jo1 = new JoinOperator(op1, op2, join_exp.get(i));
			linked_operator.add(0, jo1);
		}
	}
	
	/**
	 * Adds projection operator to root if necessary
	 */
	private void projectBuilder() {		
		if (plain_select.getSelectItems() != null && !(plain_select.getSelectItems().get(0) instanceof AllColumns)){ 		
			ArrayList<SelectItem> items = (ArrayList<SelectItem>) plain_select.getSelectItems();
			root =  new ProjectOperator(root, items);
		}
	}
	
	/** 
	 * Adds sort operator to root if necessary
	 */
	private void sortBuilder() {
		if (plain_select.getOrderByElements() != null){
			ArrayList<OrderByElement> order = (ArrayList<OrderByElement>) plain_select.getOrderByElements();	
			root = new SortOperator(root, order);
		}
	}
	
	/**
	 * Adds appropriate duplicate elimination operatot to root if necessary
	 */
	private void duplicateBuilder() {		
		if (plain_select.getDistinct() != null){			
			if (root instanceof SortOperator)
				root = new SortedDupElimOperator(root);
			else 
				root = new HashDupElimOperator(root);
		}
	}
	
}
