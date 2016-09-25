package Project;

import java.util.HashSet;

import Operators.HashDupElimOperator;
import Operators.ScanOperator;

public class Main {

	public static void main(String[] args) {
		System.out.println("hello world");
		
		TableInfo tableInfo = new TableInfo("src/Duplicates","Duplicated");
		tableInfo.getColumns().add("X");
		tableInfo.getColumns().add("Y");
		tableInfo.getColumns().add("Z");
		
		ScanOperator scn = new ScanOperator(tableInfo);
		HashDupElimOperator de = new HashDupElimOperator(scn);
		
		Tuple t;
		while ((t = de.getNextTuple()) != null) {
			System.out.println(t);
		}
		de.close();
	}

}
