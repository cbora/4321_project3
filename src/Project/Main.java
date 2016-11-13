package Project;

import java.io.BufferedReader;
import java.io.FileReader;

import IO.BinaryTupleWriter;
import IO.Configuration;
import Indexing.BPlusTree;
import Indexing.IndexConfig;
import Indexing.IndexInfo;
import LogicalOperator.LogicalOperator;
import LogicalOperator.LogicalPlanBuilder;
import Operators.InMemSortOperator;
import Operators.Operator;
import Operators.PhysicalPlanBuilder;
import Operators.ScanOperator;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

/**
 * Entry point to application
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class Main {

	public static void main(String[] args) {
		
		Configuration config = new Configuration(args[0]);
		
		// Build DbCatalog
		DbCatalog dbC = DbCatalog.getInstance();
		try {
			BufferedReader br = new BufferedReader(new FileReader(config.getInputDir() + "/db/schema.txt"));

			String read = br.readLine();
		   
		    while (read  != null) {
		    	String[] line  = read.split(" "); 
		    	TableInfo t = new TableInfo(config.getInputDir() + "/db/data/" + line[0], line[0]);
		    	for (int i=1; i < line.length; i++){
		    		t.getColumns().add(line[i]); 
		    	}
		    	dbC.addTable(t.getTableName(), t);
        		read = br.readLine();
    		}
		    
		    br.close();
		    
		 } catch (Exception e) {
    			System.err.println("Exception occurred during schema reading");
    			e.printStackTrace();    			
		 }
		
		Main m = new Main();
		if (config.runOption() == 1) {
			m.buildIndexes(config.getInputDir());
		}
		else if(config.runOption() == 2) {
			m.buildIndexes(config.getInputDir());
			m.runQueries(config.getInputDir(), config.getOutputDir(), config.getTmpDir());
		}
		else if(config.runOption() == 3) {
			m.runQueries(config.getInputDir(), config.getOutputDir(), config.getTmpDir());
		}
				
	}
	
	public void buildIndexes(String input) {
		DbCatalog dbC = DbCatalog.getInstance();
		
		String indexInfo = input + "/db/index_info.txt";
		IndexConfig config = new IndexConfig(indexInfo);
		for (int i=0; i< config.indices.size(); i++){
			ScanOperator scan = null;
			IndexInfo info = config.indices.get(i);
			int pos = scan.getSchema().get(info.attribute);
			int sort_order[] = {pos};
			if (info.clustered){
				scan = new ScanOperator(dbC.get(info.table));				
				InMemSortOperator sort = new InMemSortOperator(scan, sort_order );
				BinaryTupleWriter writer = new BinaryTupleWriter(dbC.get(info.table).getFilePath());
				sort.dump(writer, 0);
				writer.close();
				sort.close();
				scan = new ScanOperator(dbC.get(info.table));
			}				
			else {
				scan = new ScanOperator(dbC.get(info.table));
			}
			String indexFile = input + "/db/indexes/index_file_" + i +".txt";	
			BPlusTree bplus= new BPlusTree(info.D, scan, pos, indexFile);
			scan.close();
		}
		
	}
	
	public void runQueries(String input, String output, String tmp) {
		 
		final String inputDir = input;
		final String outputDir = output;
		final String tmpDir = tmp;
		final String queriesFile = "queries.sql";
		
		//get random tuples
//		String path = "/home/rhenwood39/Documents/CS4320-4321/p3/Samples/samples/input2";
//		RandomTupleGenerator.genTuples(path + "/db/data/TestTable1", 2000, 2);
//		RandomTupleGenerator.genTuples(path + "/db/data/TestTable2", 2500, 3);
		
		
		String planConfig = inputDir + "/plan_builder_config.txt";
		// Parse and evaluate queries
		try {
			CCJSqlParser parser = new CCJSqlParser(new FileReader(inputDir + "/" + queriesFile));

			Statement statement;
			int queryNum = 1;
			
			while ((statement = parser.Statement()) != null) {
				try {
					Select select = (Select) statement;
					
					PlainSelect body = (PlainSelect) select.getSelectBody();	
					
					LogicalPlanBuilder d = new LogicalPlanBuilder(body);
					LogicalOperator po = d.getRoot();
					
						
					PhysicalPlanBuilder ppb = new PhysicalPlanBuilder(po, planConfig, tmpDir);
					Operator o = ppb.getResult();
					
					BinaryTupleWriter writ = new BinaryTupleWriter(outputDir + "/query" + queryNum );
						
					long start = System.currentTimeMillis();
					o.dump(writ, queryNum);
					long end = System.currentTimeMillis();

					writ.close();
					o.close();
					queryNum++;
				} catch (Exception e) {
						System.err.println("Exception occurred during processing query " + queryNum);
						e.printStackTrace();
						queryNum++;
				}
				
			} 
		} catch (Exception e) {
			System.err.println("Exception occurred during parsing");
			e.printStackTrace();
		}	
		
		
	}
}
