package LogicalOperator;

import java.util.Stack;

import Operators.JoinOperator;
import Operators.Operator;
import Operators.ProjectOperator;
import Operators.ScanOperator;
import Operators.SelectOperator;
import Operators.SortOperator;
import Operators.SortedDupElimOperator;

public class PhysicalPlanBuilder {

	public Stack<Operator> proot;
	//public LogicalOperator lroot;
	
	public PhysicalPlanBuilder(LogicalOperator root) {
		this.proot = new Stack<Operator>();
		root.accept(this);
	}
	
	public Operator getResult() {
		return this.proot.peek();
	}
	
	public void visit(DuplicateLogicalOperator lo) {
		lo.getChild().accept(this);
		Operator o = proot.pop();
		if ( !(o instanceof SortOperator) ){
			SortOperator s = new SortOperator(o, null);
			o = s;
		}
		SortedDupElimOperator s = new SortedDupElimOperator(o);
		proot.push(s);
	}
	
	public void visit(JoinLogicalOperator lo) {
		lo.getChild1().accept(this);
		lo.getChild2().accept(this);
		Operator o = proot.pop();
		Operator o2 = proot.pop();
		JoinOperator j = new JoinOperator(o2, o, lo.getExp());
		proot.push(j);
	}
	
	public void visit(ProjectLogicalOperator lo) {
		lo.getChild().accept(this);
		Operator o = proot.pop();
		ProjectOperator p = new ProjectOperator(o, lo.getItems());
		proot.push(p);
	}
	
	public void visit(SelectLogicalOperator lo) {
		lo.getChild().accept(this);
		Operator o = proot.pop();
		SelectOperator s = new SelectOperator(o, lo.getEx());
		proot.push(s);
	}
	public void visit(SortLogicalOperator lo) {
		lo.getChild().accept(this);
		Operator o = proot.pop();
		SortOperator s = new SortOperator(o, lo.getOrder_by());
		proot.push(s); 
	}	
	
	public void visit(TableLogicalOperator lo) {
		ScanOperator s = new ScanOperator(lo.getInfo(), lo.getTableID());
		proot.push(s);
	}
}
