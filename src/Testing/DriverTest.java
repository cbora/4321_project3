package Testing;

public class DriverTest {
	/*
	@Before
	public void setUp() throws Exception {
		TableInfo boats = new TableInfo("src/Boats", "Boats");
		boats.getColumns().add("B1");
		boats.getColumns().add("B2");
		boats.getColumns().add("B3");
		
		TableInfo res = new TableInfo("src/Reserves", "Reserves");
		res.getColumns().add("R1");
		res.getColumns().add("R2");
		
		TableInfo sail = new TableInfo("src/Sailors", "Sailors");
		sail.getColumns().add("S1");
		sail.getColumns().add("S2");
		sail.getColumns().add("S3");
		
		TableInfo st = new TableInfo("src/SortTest", "SortTest");
		st.getColumns().add("A");
		st.getColumns().add("B");
		st.getColumns().add("C");
		
		TableInfo dup = new TableInfo("src/Duplicates","Duplicated");
		dup.getColumns().add("X");
		dup.getColumns().add("Y");
		dup.getColumns().add("Z");
		
		DbCatalog catalog = DbCatalog.getInstance();
		catalog.addTable("Boats", boats);
		catalog.addTable("Reserves", res);
		catalog.addTable("Sailors", sail);
		catalog.addTable("SortTest", st);
		catalog.addTable("Duplicates", dup);
	}

	@Test
	public void testSelectAll() {
		String query = "SELECT * FROM Reserves";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,101");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,102");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,103");
		t = o.getNextTuple();
		assertEquals(t.toString(), "2,101");
		t = o.getNextTuple();
		assertEquals(t.toString(), "3,102");
		t = o.getNextTuple();
		assertEquals(t.toString(), "4,104");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
	}
	
	@Test
	public void testSelection1() {
		String query = "SELECT * FROM Reserves WHERE Reserves.R2 = 101";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(),"1,101");
		t = o.getNextTuple();
		assertEquals(t.toString(),"2,101");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
	}
	
	@Test
	public void testSelection2() {
		String query = "SELECT * FROM Reserves WHERE Reserves.R1 = 2 AND Reserves.R2 = 101";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(), "2,101");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
	}
	
	@Test
	public void testProjec1() {
		String query = "SELECT Reserves.R2 FROM Reserves";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(),"101");
		t = o.getNextTuple();
		assertEquals(t.toString(),"102");
		t = o.getNextTuple();
		assertEquals(t.toString(),"103");
		t = o.getNextTuple();
		assertEquals(t.toString(),"101");
		t = o.getNextTuple();
		assertEquals(t.toString(),"102");
		t = o.getNextTuple();
		assertEquals(t.toString(),"104");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
		
	}
	
	@Test
	public void testProjec2() {
		String query = "SELECT Boats.B3, Boats.B2 FROM Boats";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(),"3,2");
		t = o.getNextTuple();
		assertEquals(t.toString(),"4,3");
		t = o.getNextTuple();
		assertEquals(t.toString(),"2,104");
		t = o.getNextTuple();
		assertEquals(t.toString(),"1,1");
		t = o.getNextTuple();
		assertEquals(t.toString(),"8,2");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
	}
	
	@Test
	public void testProject3() {
		String query = "SELECT Reserves.R2 FROM Reserves WHERE Reserves.R1 = 1";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(), "101");
		t = o.getNextTuple();
		assertEquals(t.toString(), "102");
		t = o.getNextTuple();
		assertEquals(t.toString(), "103");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
	}
	
	@Test
	public void testCrossProd() {
		String query = "SELECT * FROM Reserves, Sailors";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,101,1,200,50");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,101,2,200,200");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,101,3,100,105");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,101,4,100,50");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,101,5,100,500");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,101,6,300,400");
		
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,102,1,200,50");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,102,2,200,200");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,102,3,100,105");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,102,4,100,50");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,102,5,100,500");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,102,6,300,400");
		
		for (int i = 0; i < 18; i++) {
			o.getNextTuple();
		}
		
		t = o.getNextTuple();
		assertEquals(t.toString(), "4,104,1,200,50");
		t = o.getNextTuple();
		assertEquals(t.toString(), "4,104,2,200,200");
		t = o.getNextTuple();
		assertEquals(t.toString(), "4,104,3,100,105");
		t = o.getNextTuple();
		assertEquals(t.toString(), "4,104,4,100,50");
		t = o.getNextTuple();
		assertEquals(t.toString(), "4,104,5,100,500");
		t = o.getNextTuple();
		assertEquals(t.toString(), "4,104,6,300,400");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
	}

	@Test
	public void testJoin() {
		String query = "SELECT * FROM Reserves, Boats WHERE Reserves.R2 = Boats.B1";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,101,101,2,3");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,102,102,3,4");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,103,103,1,1");
		t = o.getNextTuple();
		assertEquals(t.toString(), "2,101,101,2,3");
		t = o.getNextTuple();
		assertEquals(t.toString(), "3,102,102,3,4");
		t = o.getNextTuple();
		assertEquals(t.toString(), "4,104,104,104,2");
		t = o.getNextTuple();
		assertNull(t);
	}
	
	@Test
	public void testJoin2() {
		String query = "SELECT * FROM Reserves, Sailors WHERE Reserves.R1 = Sailors.S1 AND Sailors.S2 = 200";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(),"1,101,1,200,50");
		t = o.getNextTuple();
		assertEquals(t.toString(),"1,102,1,200,50");
		t = o.getNextTuple();
		assertEquals(t.toString(),"1,103,1,200,50");
		t = o.getNextTuple();
		assertEquals(t.toString(),"2,101,2,200,200");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
		
	}
	
	@Test
	public void testJoin3() {
		String query = "SELECT * FROM Reserves, Sailors, Boats WHERE Reserves.R1 = Sailors.S1 AND Reserves.R2 = Boats.B1";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,101,1,200,50,101,2,3");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,102,1,200,50,102,3,4");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,103,1,200,50,103,1,1");
		t = o.getNextTuple();
		assertEquals(t.toString(), "2,101,2,200,200,101,2,3");
		t = o.getNextTuple();
		assertEquals(t.toString(), "3,102,3,100,105,102,3,4");
		t = o.getNextTuple();
		assertEquals(t.toString(), "4,104,4,100,50,104,104,2");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
	}

	@Test
	public void testJoin4() {
		String query = "SELECT Sailors.S1, Sailors.S2, Boats.B1, Boats.B2 FROM Reserves, Sailors, Boats WHERE Reserves.R1 = Sailors.S1 AND Reserves.R2 = Boats.B1";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,200,101,2");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,200,102,3");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,200,103,1");
		t = o.getNextTuple();
		assertEquals(t.toString(), "2,200,101,2");
		t = o.getNextTuple();
		assertEquals(t.toString(), "3,100,102,3");
		t = o.getNextTuple();
		assertEquals(t.toString(), "4,100,104,104");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
	}
	
	@Test
	public void testSelectAllAlias() {
		String query = "SELECT * FROM Reserves R";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,101");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,102");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,103");
		t = o.getNextTuple();
		assertEquals(t.toString(), "2,101");
		t = o.getNextTuple();
		assertEquals(t.toString(), "3,102");
		t = o.getNextTuple();
		assertEquals(t.toString(), "4,104");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
	}
	
	@Test
	public void testSelectionAlias() {
		String query = "SELECT * FROM Reserves R WHERE R.R2 = 101";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(),"1,101");
		t = o.getNextTuple();
		assertEquals(t.toString(),"2,101");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
	}
	
	@Test
	public void testProjectAlias() {
		String query = "SELECT R.R2 FROM Reserves R WHERE R.R1 = 1";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(), "101");
		t = o.getNextTuple();
		assertEquals(t.toString(), "102");
		t = o.getNextTuple();
		assertEquals(t.toString(), "103");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
	}
	
	@Test
	public void testJoinAlias() {
		String query = "SELECT S.S1, S.S2, B.B1, B.B2 FROM Reserves R, Sailors S, Boats B WHERE R.R1 = S.S1 AND R.R2 = B.B1";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,200,101,2");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,200,102,3");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,200,103,1");
		t = o.getNextTuple();
		assertEquals(t.toString(), "2,200,101,2");
		t = o.getNextTuple();
		assertEquals(t.toString(), "3,100,102,3");
		t = o.getNextTuple();
		assertEquals(t.toString(), "4,100,104,104");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
	}
	
	@Test
	public void testSort1() {

		String query = "SELECT * FROM SortTest ORDER BY SortTest.C";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,1,1");
		t = o.getNextTuple();
		assertEquals(t.toString(), "2,1,1");
		t = o.getNextTuple();
		assertEquals(t.toString(), "3,2,1");
	}
	
	@Test
	public void testSort2() {
		String query = "SELECT * FROM SortTest ORDER BY SortTest.C, SortTest.B";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,1,1");
		t = o.getNextTuple();
		assertEquals(t.toString(), "2,1,1");
		t = o.getNextTuple();
		assertEquals(t.toString(), "3,2,1");
		t = o.getNextTuple();
		assertEquals(t.toString(), "2,3,2");
		t = o.getNextTuple();
		assertEquals(t.toString(), "3,1,3");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,2,3");
	}
	
	@Test
	public void testSort3() {
		String query = "SELECT S.S1, S.S2, B.B1, B.B2 FROM Reserves R, Sailors S, Boats B WHERE R.R1 = S.S1 AND R.R2 = B.B1 ORDER BY B.B1, S.S2";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,200,101,2");
		t = o.getNextTuple();
		assertEquals(t.toString(), "2,200,101,2");
		t = o.getNextTuple();
		assertEquals(t.toString(), "3,100,102,3");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,200,102,3");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,200,103,1");
		t = o.getNextTuple();
		assertEquals(t.toString(), "4,100,104,104");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
	}
	
	@Test
	public void testSort4() {
		String query = "SELECT S.S1, S.S2, B.B1, B.B2 FROM Reserves R, Sailors S, Boats B WHERE R.R1 = S.S1 AND R.R2 = B.B1 ORDER BY B.B1";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,200,101,2");
		t = o.getNextTuple();
		assertEquals(t.toString(), "2,200,101,2");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,200,102,3");
		t = o.getNextTuple();
		assertEquals(t.toString(), "3,100,102,3");
		t = o.getNextTuple();
		assertEquals(t.toString(), "1,200,103,1");
		t = o.getNextTuple();
		assertEquals(t.toString(), "4,100,104,104");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
	}
	
	@Test
	public void testDuplicatesNoSort1() {
		String query = "SELECT DISTINCT * FROM Duplicates";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(), "100,2,3");
		t = o.getNextTuple();
		assertEquals(t.toString(), "101,5,4");
		t = o.getNextTuple();
		assertEquals(t.toString(), "105,1,9");
		t = o.getNextTuple();
		assertEquals(t.toString(), "100,6,2");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
	}
	
	@Test
	public void testDuplicatesNoSort2() {
		String query = "SELECT DISTINCT S.S2 FROM Reserves R, Sailors S, Boats B WHERE R.R1 = S.S1 AND R.R2 = B.B1";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(), "200");
		t = o.getNextTuple();
		assertEquals(t.toString(), "100");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
	}
	
	@Test
	public void testDuplicatesSort1() {
		String query = "SELECT DISTINCT * FROM Duplicates ORDER BY Duplicates.X";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(), "100,2,3");
		t = o.getNextTuple();
		assertEquals(t.toString(), "100,6,2");
		t = o.getNextTuple();
		assertEquals(t.toString(), "101,5,4");
		t = o.getNextTuple();
		assertEquals(t.toString(), "105,1,9");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
	}
	
	@Test
	public void testDuplicatesSort2() {
		String query = "SELECT DISTINCT S.S2 FROM Reserves R, Sailors S, Boats B WHERE R.R1 = S.S1 AND R.R2 = B.B1 ORDER BY S.S2";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();
		
		Tuple t;
		t = o.getNextTuple();
		assertEquals(t.toString(), "100");
		t = o.getNextTuple();
		assertEquals(t.toString(), "200");
		t = o.getNextTuple();
		assertNull(t);
		o.close();
	}
	
	@Test
	public void testNoResult() {
		String query = "SELECT S.S1, S.S2, B.B1, B.B2 FROM Reserves R, Sailors S, Boats B WHERE R.R1 = S.S1 AND R.R2 = B.B1 AND B.B2 = 100";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();		
		Tuple t;
		t = o.getNextTuple();
		assertNull(t);
	}
	
	@Test
	public void testEmptyFile() {
		TableInfo emp = new TableInfo("src/Empty", "Empty");
		emp.getColumns().add("EMP1");
		emp.getColumns().add("EMP2");
		
		DbCatalog catalog = DbCatalog.getInstance();		
		catalog.addTable("Empty", emp);
		
		String query = "SELECT Empty.EMP1, Empty.EMP2 FROM Empty";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();		
		Tuple t;
		t = o.getNextTuple();
		assertNull(t);		
	}
	
	@Test
	public void TestEmptyFileWithSelect() {
		TableInfo emp = new TableInfo("src/Empty", "Empty");
		emp.getColumns().add("EMP1");
		emp.getColumns().add("EMP2");
		
		DbCatalog catalog = DbCatalog.getInstance();		
		catalog.addTable("Empty", emp);
		
		String query = "SELECT Empty.EMP1, Empty.EMP2 FROM Empty WHERE Empty.EMP1 = 1";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();		
		Tuple t;
		t = o.getNextTuple();
		assertNull(t);
	}
	
	@Test
	public void TestEmptyFileWithProject() {
		TableInfo emp = new TableInfo("src/Empty", "Empty");
		emp.getColumns().add("EMP1");
		emp.getColumns().add("EMP2");
		
		DbCatalog catalog = DbCatalog.getInstance();		
		catalog.addTable("Empty", emp);
		
		String query = "SELECT Empty.EMP1 FROM Empty";
		CCJSqlParser parser = new CCJSqlParser(new StringReader(query));
		Select s = null;
		try {
			s = (Select) parser.Statement();
		}catch (Exception e) {
			System.err.println("Exception occured during parsing" + e);
			e.printStackTrace();
		}
				
		PlainSelect body = (PlainSelect) s.getSelectBody();	
		Driver d = new Driver(body);
		Operator o = d.getRoot();		
		Tuple t;
		t = o.getNextTuple();
		assertNull(t);
	}
	*/
	
}
