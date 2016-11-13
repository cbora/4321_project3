package Project;

import Indexing.BPlusTree;
import Operators.ScanOperator;

public class Test {

	public static void main(String[] args) {
		TableInfo info = new TableInfo("src/BoatsBin", "Boats");
		info.getColumns().add("D");
		info.getColumns().add("E");
		info.getColumns().add("F");
		
		DbCatalog db = DbCatalog.getInstance();
		db.addTable("Boats", info);
		
		ScanOperator scan = new ScanOperator(info);
		BPlusTree btree = new BPlusTree(10, scan, 1, "/home/rhenwood39/Documents/CS4320-4321/p4/samples/output/Boats_E");
	}
}
