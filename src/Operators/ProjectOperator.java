package Operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import Project.EvalSelectItemVisitor;
import Project.Tuple;
import net.sf.jsqlparser.statement.select.SelectItem;
public class ProjectOperator extends Operator {

	private Operator child;
	private ArrayList<SelectItem> items;
	private HashMap<String, Integer> schema; // schema

	public ProjectOperator(Operator child, ArrayList<SelectItem> items) {
		this.child = child;
		this.items = items;
		this.schema = child.getSchema();
	}
	@Override
	public HashMap<String, Integer> getSchema() {
		return this.schema;
	}

	@Override
	public Tuple getNextTuple() {
		Tuple t = child.getNextTuple();
		if (t == null)
			return null;
		return project(t);
	}
	
	private Tuple project(Tuple input) {
		EvalSelectItemVisitor visitor = new EvalSelectItemVisitor(this.items, this.schema);
		LinkedHashMap<String, Integer> projection = visitor.getResult();
		Tuple t = new Tuple(projection.size());
		int index = 0;
		for(Entry<String, Integer> entry : projection.entrySet()){
			int el = input.getVal(entry.getValue());
			t.add(el, index);
			index++;
		}
		return t;		
	}

	@Override
	public void reset() {
		child.reset();
	}
	
	@Override
	public void close() {
		child.close();
	}
	
	public Operator getChild() {
		return child;
	}

	public void setChild(Operator child) {
		this.child = child;
	}
}
