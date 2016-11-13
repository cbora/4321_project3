package Project;

import Indexing.BPlusTree;
import Operators.ScanOperator;

public class Test {

	public static void main(String[] args) {
		TableInfo info = new TableInfo("src/SailorsBin", "Sailors");
		info.getColumns().add("A");
		info.getColumns().add("B");
		info.getColumns().add("C");
		
		DbCatalog db = DbCatalog.getInstance();
		db.addTable("Sailors", info);
		
		ScanOperator scan = new ScanOperator(info);
		BPlusTree btree = new BPlusTree(15, scan, 0, "/home/rhenwood39/Documents/CS4320-4321/p4/samples/output/Sailors_A");
	}
}
