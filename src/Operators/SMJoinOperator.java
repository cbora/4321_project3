package Operators;

import Project.Tuple;
import Project.TupleComparator;
import Project.TupleDiffComparator;
import net.sf.jsqlparser.expression.Expression;

public class SMJoinOperator extends JoinOperator {

	/* ================================== 
	 * Fields
	 * ================================== */
	private Tuple left;
	private Tuple right;
	private Tuple rightPartition;
	int index;
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Constructor
	 * @param leftChild
	 * @param rightChild
	 * @param exp - expression for the condition we are joining on
	 */
	public SMJoinOperator(Operator leftChild, Operator rightChild, Expression exp) {
		super(leftChild, rightChild, exp);
		this.left = leftChild.getNextTuple();
		this.right = rightChild.getNextTuple();
		this.rightPartition = right;
		this.index = 0;
	}
	
	/**
	 * Cartesian product constructor - set join condition to null
	 * @param leftChild
	 * @param rightChild
	 */
	public SMJoinOperator(Operator leftChild, Operator rightChild) {
		this(leftChild, rightChild, null);
	}

	/* ================================== 
	 * Methods
	 * ================================== */
	@Override
	public Tuple getNextTuple() {
		if ((this.left == null || this.rightPartition == null)) {
			return null;
		}
		
		TupleDiffComparator tc = new TupleDiffComparator(((SortOperator) leftChild).sort_order, ((SortOperator) rightChild).sort_order) ;
		TupleComparator tc2 = new TupleComparator(((SortOperator) rightChild).sort_order);
		
		while (tc.compare(left,  rightPartition) != 0) {
			while (tc.compare(left, rightPartition) == -1) {
				left = leftChild.getNextTuple();
				if (left == null) {
					return null;
				}
			}
			
			while(tc.compare(left, rightPartition) == 1) {
				rightPartition = rightChild.getNextTuple();
				right = rightPartition;
				index++;
				if (rightPartition == null)  {
					return null;
				}
			}
		}
				
		Tuple result = Tuple.concat(left, right);
		right = rightChild.getNextTuple();
		if (right == null || tc2.compare(rightPartition, right) != 0) {
			left = leftChild.getNextTuple();
			((SortOperator) rightChild).reset(index);
			right = rightChild.getNextTuple();
		}
		return result;
	}

	@Override
	public void reset() {
		this.leftChild.reset();
		this.left = leftChild.getNextTuple();
		
		this.rightChild.reset();
		this.right = rightChild.getNextTuple();
		
		this.rightPartition = right;
		this.index = 0;
	}

}
