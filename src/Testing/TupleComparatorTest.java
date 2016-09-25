package Testing;

import org.junit.Test;
import static org.junit.Assert.*;

import Project.Tuple;
import Project.TupleComparator;

public class TupleComparatorTest {

	@Test
	public void testEqual() {
		Tuple t1 = new Tuple(3);
		t1.add(1, 0);
		t1.add(4,1);
		t1.add(2, 2);
		
		Tuple t2 = new Tuple(3);
		t2.add(1, 0);
		t2.add(4,1);
		t2.add(2, 2);
		
		int[] order = {1,2,0};
		TupleComparator tc1 = new TupleComparator(order);
		assertEquals(tc1.compare(t1, t2), 0);
		assertEquals(tc1.compare(t1,t2), 0);
	}
	
	@Test
	public void testLess() {
		Tuple t1 = new Tuple(3);
		t1.add(3, 0);
		t1.add(4,1);
		t1.add(2, 2);
		
		Tuple t2 = new Tuple(3);
		t2.add(5, 0);
		t2.add(4,1);
		t2.add(2, 2);
		
		int[] order = {1,2,0};
		TupleComparator tc1 = new TupleComparator(order);
		assertEquals(tc1.compare(t1, t2), -1);
	}
	
	@Test
	public void testGreater() {
		Tuple t1 = new Tuple(3);
		t1.add(5, 0);
		t1.add(4,1);
		t1.add(2, 2);
		
		Tuple t2 = new Tuple(3);
		t2.add(3, 0);
		t2.add(4,1);
		t2.add(2, 2);
		
		int[] order = {1,2,0};
		TupleComparator tc1 = new TupleComparator(order);
		assertEquals(tc1.compare(t1, t2), 1);
	}

}
