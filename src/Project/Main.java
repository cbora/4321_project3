package Project;

import Operators.*;

public class Main {

	public static void main(String[] args) {
		TableInfo tableInfo = new TableInfo("src/Boats","Boats");
		
		ScanOperator s = new ScanOperator(tableInfo);
		Tuple t;
		while ((t = s.getNextTuple()) != null) {
			System.out.println(t);
		}
	}

}
