package Testing;

import org.junit.Test;
import static org.junit.Assert.*;

import Project.Tuple;

public class TupleTest {

	@Test
	public void testEquals() {
		Tuple t1 = new Tuple(2);
		t1.add(1, 0);
		t1.add(4, 1);
		
		Tuple t2 = new Tuple(2);
		t2.add(1, 0);
		t2.add(4, 1);
		
		Tuple t3 = new Tuple(2);
		t3.add(1, 0);
		t3.add(2, 1);
		
		Tuple t4 = new Tuple(3);
		t4.add(1, 0);
		t4.add(4, 1);
		t4.add(1, 2);
		
		Tuple t5 = new Tuple(0);
		
		Tuple t6 = new Tuple(0);
		
		assertEquals(t1,t2);
		assertEquals(t5,t6);
		
		assertNotEquals(t1,t3);
		assertNotEquals(t1,t4);	
		assertNotEquals(t1,t5);
	}
	
	@Test
	public void testToString() {
		Tuple t1 = new Tuple(2);
		t1.add(1, 0);
		t1.add(4, 1);
		
		Tuple t2 = new Tuple(0);
		
		assertEquals(t1.toString(),"1,4");
		assertEquals(t2.toString(),"");
	}
	
	@Test
	public void testCat1() {
		Tuple t1 = new Tuple(2);
		t1.add(1, 0);
		t1.add(4, 1);
		
		Tuple t2 = new Tuple(4);
		t2.add(2, 0);
		t2.add(3, 1);
		t2.add(8, 2);
		t2.add(1, 3);
		
		Tuple t3 = new Tuple(6);
		t3.add(1, 0);
		t3.add(4, 1);
		t3.add(2, 2);
		t3.add(3, 3);
		t3.add(8, 4);
		t3.add(1, 5);
		
		assertEquals(Tuple.concat(t1, t2),t3);
	}
	
	@Test
	public void testCat2() {
		Tuple t1 = new Tuple(2);
		t1.add(1, 0);
		t1.add(4, 1);
		
		Tuple t2 = new Tuple(0);
		
		Tuple t3 = new Tuple(2);
		t3.add(1, 0);
		t3.add(4, 1);
		
		assertEquals(Tuple.concat(t1, t2),t3);
		assertEquals(Tuple.concat(t2, t1),t3);
	}
	
	@Test
	public void testCat3() {
		Tuple t1 = new Tuple(0);
		
		Tuple t2 = new Tuple(0);
		
		Tuple t3 = new Tuple(0);
		
		assertEquals(Tuple.concat(t1, t2),t3);
	}

}
