package LogicalOperator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;

import Project.BuildSelectConditionsVisitor;
import Project.DbCatalog;
import Project.Pair;
import Project.TableInfo;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.AllColumns;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectItem;

/**
 * Builds operator tree associated with SQL query
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class LogicalPlanBuilder {

	/* ================================== 
	 * Fields
	 * ================================== */
	private PlainSelect plain_select; // query we are building operator tree for
	private LogicalOperator root; // root of operator tree
	private HashMap<String, Integer> table_mapping; // maps table to position in join list
	private LinkedList<LogicalOperator> linked_operator; // list of operators so far -- starts by constructing scan operators in order of join list
	private BuildSelectConditionsVisitor bsv; // retrieves selection/join information regarding our expression
		// builds selection expression list in order that corresponds with ordering in table_mapping
		// builds join expression list in left_deep order that corresponds with ordering in table_mapping
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param plain_select - SQL SELECT query
	 */
	public LogicalPlanBuilder(PlainSelect plain_select) {
		this.plain_select = plain_select;
		this.table_mapping = new HashMap<String, Integer>();
		this.linked_operator = new LinkedList<LogicalOperator>();

		// builds scan operators for each table
		scanBuilder();
		
		// build selection/join operators 
		this.bsv = new BuildSelectConditionsVisitor(this.table_mapping, this.plain_select.getWhere());
		selectBuilder();
		joinBuilder();
		
		// only one element in linked_operators now - pop and make it the root
		// this.root = this.linked_operator.removeLast();
		
		// add any selection conditions that don't involve tables
		Expression extra_exp = this.bsv.getExp(); 		 	
		if ( extra_exp != null ) {
			this.root = new SelectLogicalOperator(this.root, extra_exp, null);
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
	public LogicalOperator getRoot() {
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
			TableLogicalOperator op = new TableLogicalOperator(tableInfo, tables.get(i));
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
		ArrayList<HashMap<String,Pair>> select_range = this.bsv.getSelectRange();
		ListIterator<LogicalOperator> iter = linked_operator.listIterator();
		int index = 0;
		while(iter.hasNext()){	// 			
			if (select_exp.get(index) != null) {
				SelectLogicalOperator so = new SelectLogicalOperator(iter.next(), select_exp.get(index), select_range.get(index));			
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
	 * replacing them with appropriate join operator (taken from join_exp)
	 * until linked_operator has just one element remaining
	 */
	private void joinBuilder() {
		Expression join_exp = this.bsv.getJoin();
		ArrayList<LogicalOperator> children = new ArrayList<LogicalOperator>(linked_operator);
		JoinLogicalOperator jol = new JoinLogicalOperator(children, join_exp, this.bsv.getUnion());
		root = jol;
	}
	
	/**
	 * Adds projection operator to root if necessary
	 */
	private void projectBuilder() {		
		//if (plain_select.getSelectItems() != null && !(plain_select.getSelectItems().get(0) instanceof AllColumns)){ 		
			ArrayList<SelectItem> items = (ArrayList<SelectItem>) plain_select.getSelectItems();
			
			ArrayList<Table> tables = new ArrayList<Table>();
			tables.add((Table) plain_select.getFromItem());
			if (plain_select.getJoins() != null) {
				for (int i=0; i < plain_select.getJoins().size(); i++){
					Table t = (Table) ((Join) plain_select.getJoins().get(i)).getRightItem();
					tables.add(t);
				}
			}
			
			root =  new ProjectLogicalOperator(root, items, tables);
		//}
	}
	
	/** 
	 * Adds sort operator to root if necessary
	 */
	private void sortBuilder() {
		if (plain_select.getOrderByElements() != null){
			ArrayList<OrderByElement> order = (ArrayList<OrderByElement>) plain_select.getOrderByElements();	
			root = new SortLogicalOperator(root, order);
		}
	}
	
	/**
	 * Adds appropriate duplicate elimination operatot to root if necessary
	 */
	private void duplicateBuilder() {		
		if (plain_select.getDistinct() != null){						
			root = new DuplicateLogicalOperator(root);			
		}
	}
	
}
