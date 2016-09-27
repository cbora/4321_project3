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

	private PlainSelect plain_select;
	private HashMap<String, Integer> table_mapping; //maps table to path	
	private LinkedList<Operator> linked_operator;
	private Operator root;
	private ArrayList<Expression> select_exp;
	private ArrayList<Expression> join_exp;
	private Expression exp_root;
	
	public Driver(PlainSelect plain_select) {
		this.plain_select = plain_select;
		this.table_mapping = new HashMap<String, Integer>();
		this.linked_operator = new LinkedList<Operator>();

		scanOperatorBuilder();
		BuildSelectVisitor bsv = new BuildSelectVisitor(this.table_mapping, this.plain_select.getWhere());
		this.select_exp = bsv.getSelect();
		this.join_exp = bsv.getJoin();
		this.exp_root = bsv.getExp();		 // root expression		
		selectBuilder();

		joinBuilder();
		
		root = linked_operator.removeLast();
		projectBuilder();
				
		sortBuilder();
		duplicateBuilder();
		if ( exp_root != null ) {
			root = new SelectOperator(root, exp_root);
		}
	}
	
	public Operator getRoot() {
		return root;		
	}
	
	public void scanOperatorBuilder() {	
		DbCatalog db = DbCatalog.getInstance();	
			
		ArrayList<Table> tables = new ArrayList<Table>();
		tables.add((Table) plain_select.getFromItem());
		for (int i=0; i < plain_select.getJoins().size(); i++){
			Table t = (Table) ((Join) plain_select.getJoins().get(i)).getRightItem();
			tables.add(t);
		}
		
		for (int i=0; i < tables.size(); i++){
			
			TableInfo tableName = db.get(tables.get(i).getName());
			ScanOperator op;
			if (tables.get(i).getAlias() != null){ // Alias of table
				op = new ScanOperator(tableName, tables.get(i).getAlias());
			}
			else { // No alias
				op = new ScanOperator(tableName);
			}
			table_mapping.put(tables.get(i).getAlias() == null ? tables.get(i).getName() : tables.get(i).getAlias() , i);
			linked_operator.add(op);
		}
	}
	
	public void selectBuilder() {
		ListIterator<Operator> iter = linked_operator.listIterator();
		int index = 0;
		while(iter.hasNext()){	// 			
			if (select_exp.size() > 0 && select_exp.get(index) != null) {
				SelectOperator so = new SelectOperator(iter.next(), select_exp.get(index));			
				iter.set(so);
			}
			else {
				iter.next();
			}
			index++;
		}				
	}
	
	public void projectBuilder() {		
		if (plain_select.getSelectItems() != null && !(plain_select.getSelectItems().get(0) instanceof AllColumns)){ 		
			System.out.println("Projecting");
			ArrayList<SelectItem> items = (ArrayList<SelectItem>) plain_select.getSelectItems();
			root =  new ProjectOperator(root, items);
		}
	}
	
	public void joinBuilder() {
		int size = linked_operator.size();
		for (int i=0; i<size-1; i++){
			Operator op1 = linked_operator.removeFirst();
			Operator op2 = linked_operator.removeFirst();
			JoinOperator jo1 = new JoinOperator(op1, op2, this.join_exp.get(i));
			linked_operator.add(0, jo1);
		}
	}
	
	public void sortBuilder() {
		if (plain_select.getOrderByElements() != null){
			ArrayList<OrderByElement> order = (ArrayList<OrderByElement>) plain_select.getOrderByElements();	
			root = new SortOperator(root, order);
		}
	}
	
	public void duplicateBuilder() {		
		if (plain_select.getDistinct() != null){			
			if (root instanceof SortOperator)
				root = new SortedDupElimOperator(root);
			else 
				root = new HashDupElimOperator(root);
		}
	}
	
	
}
