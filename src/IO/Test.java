package IO;

import Project.Tuple;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("This is life");
	
		
	BinaryTupleReader bin = new BinaryTupleReader("./Reserves");
	BinaryTupleWriter writ = new BinaryTupleWriter("./R");
	Tuple t = null;
	int i =0;
	while ((t = bin.read()) != null){
		writ.write(t);
		//System.out.println(t);
		i++;
	}
	System.out.println("I1 " + i);
	
	//writ.finalize();
	//writ.writePage();
	writ.close();
	
	BinaryTupleReader bin2 = new BinaryTupleReader("./R");
	Tuple t2 = null;
	int i2 =0;
	while ((t2 = bin2.read()) != null){
		
		System.out.println(t2);
		i2++;
	}
	System.out.println("I " + i2);
	
	
	}
	


}
