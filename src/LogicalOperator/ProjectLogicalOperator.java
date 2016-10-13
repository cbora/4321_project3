package LogicalOperator;

import java.util.ArrayList;

import net.sf.jsqlparser.statement.select.SelectItem;

public class ProjectLogicalOperator extends LogicalOperator {

	private LogicalOperator child;
	private ArrayList<SelectItem> items;
	
	public ProjectLogicalOperator(LogicalOperator child, ArrayList<SelectItem> items) {
		this.child = child;
		this.items = items;
	}

	public LogicalOperator getChild() {
		return child;
	}

	public void setChild(LogicalOperator child) {
		this.child = child;
	}

	public ArrayList<SelectItem> getItems() {
		return items;
	}

	public void setItems(ArrayList<SelectItem> items) {
		this.items = items;
	}
	
	public void accept(PhysicalPlanBuilder ppb){
		ppb.visit(this);	
	}
}
