package Testing;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import Project.Tuple;
import Project.TupleDiffComparator;

public class TupleDiffComparatorTest {

	@Test
	public void testEqual() {
		Tuple t1 = new Tuple(3);
		t1.add(1, 0);
		t1.add(4,1);
		t1.add(2, 2);
		
		Tuple t2 = new Tuple(5);
		t2.add(6, 0);
		t2.add(1, 1);
		t2.add(-1, 2);
		t2.add(4,3);
		t2.add(2, 4);
		
		int[] order1 = {1,2,0};
		int[] order2 = {3, 4, 1};
		TupleDiffComparator tc1 = new TupleDiffComparator(order1, order2);
		TupleDiffComparator tc2 = new TupleDiffComparator(order2, order1);
		assertEquals(tc1.compare(t1, t2), 0);
		assertEquals(tc2.compare(t2, t1), 0);
	}
	
	@Test
	public void testEqual2() {
		Tuple t1 = new Tuple(3);
		t1.add(1, 0);
		t1.add(4,1);
		t1.add(2, 2);
		
		Tuple t2 = new Tuple(5);
		t2.add(6, 0);
		t2.add(1, 1);
		t2.add(-1, 2);
		t2.add(4,3);
		t2.add(2, 4);
		
		int[] order1 = {1,2,0};
		int[] order2 = {3, 4, 1};
		TupleDiffComparator tc1 = new TupleDiffComparator(order1, order2);
		TupleDiffComparator tc2 = new TupleDiffComparator(order2, order1);
		assertEquals(tc1.compare(t1, t2), 0);
		assertEquals(tc2.compare(t2, t1), 0);
	}
	
	@Test
	public void testLess() {
		Tuple t1 = new Tuple(3);
		t1.add(3, 0);
		t1.add(4,1);
		t1.add(2, 2);
		
		Tuple t2 = new Tuple(5);
		t2.add(6, 0);
		t2.add(1, 1);
		t2.add(-1, 2);
		t2.add(4,3);
		t2.add(5, 4);
		
		int[] order1 = {1,2,0};
		int[] order2 = {3, 4, 1};
		TupleDiffComparator tc1 = new TupleDiffComparator(order1, order2);
		assertEquals(tc1.compare(t1, t2), -1);
	}
	
	@Test
	public void testGreater() {
		Tuple t1 = new Tuple(3);
		t1.add(5, 0);
		t1.add(4,1);
		t1.add(2, 2);
		
		Tuple t2 = new Tuple(5);
		t2.add(6, 0);
		t2.add(1, 1);
		t2.add(-1, 2);
		t2.add(4,3);
		t2.add(2, 4);
		
		int[] order1 = {1,2,0};
		int[] order2 = {3, 4, 1};
		TupleDiffComparator tc1 = new TupleDiffComparator(order1, order2);
		assertEquals(tc1.compare(t1, t2), 1);
	}

}
