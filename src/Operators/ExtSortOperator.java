package Operators;

import java.util.ArrayList;

import Project.Tuple;
import net.sf.jsqlparser.statement.select.OrderByElement;

public class ExtSortOperator extends SortOperator {

	public ExtSortOperator(Operator child, ArrayList<OrderByElement> order_by) {
		super(child, order_by);
	}

	@Override
	public Tuple getNextTuple() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void reset() {
		// TODO Auto-generated method stub

	}

}
