package Project;

import java.io.BufferedReader;
import java.io.FileReader;

import IO.BinaryTupleWriter;
import LogicalOperator.LogicalOperator;
import LogicalOperator.LogicalPlanBuilder;
import Operators.Operator;
import Operators.PhysicalPlanBuilder;
import Operators.SMJoinOperator;
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
		DbCatalog dbC = DbCatalog.getInstance(); 
		final String inputDir = args[0];
		final String outputDir = args[1];
		final String tmpDir = args[2];
		final String queriesFile = "queries.sql";
		
		// Read from schema to construct DB catalog
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputDir + "/db/schema.txt"));

			String read = br.readLine();
		   
		    while (read  != null) {
		    	String[] line  = read.split(" "); 
		    	TableInfo t = new TableInfo(inputDir + "/db/data/" + line[0], line[0]);
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
		
		// Read from plan_builder_config.txt to develop query plan
		String joinPlan[] = null;
		String sortPlan[] = null;
		try {
			BufferedReader br = new BufferedReader(new FileReader(inputDir + "/plan_builder_config.txt"));
			joinPlan = br.readLine().split(" ");
			sortPlan = br.readLine().split(" ");
			br.close();
		} catch (Exception e) {
			System.err.println("Exception occurred during plan reading");
			e.printStackTrace();    			
		}
		
		// Parse and evaluate queries
		try {
			CCJSqlParser parser = new CCJSqlParser(new FileReader(inputDir + "/" + queriesFile));

			Statement statement;
			int queryNum = 1;
			
			try{
				while ((statement = parser.Statement()) != null) {
	
					Select select = (Select) statement;
					
					PlainSelect body = (PlainSelect) select.getSelectBody();	
					
					LogicalPlanBuilder d = new LogicalPlanBuilder(body);
					LogicalOperator po = d.getRoot();
					
					
					PhysicalPlanBuilder ppb = new PhysicalPlanBuilder(po, joinPlan, sortPlan, tmpDir);
					Operator o = ppb.getResult();
//					if (queryNum != 7)
//						System.out.println(o.getNextTuple());
//					
					BinaryTupleWriter writ = new BinaryTupleWriter(outputDir + "/query" + queryNum );
					//HumanTupleWriter writ = new HumanTupleWriter(outputDir + "/query" + queryNum );
					
					long start = System.currentTimeMillis();
					System.out.println("Query: " + queryNum);
					o.dump(writ, queryNum);
					long end = System.currentTimeMillis();
					System.out.println(end - start);
					writ.close();
					o.close();
					queryNum++;
				}
				
			} catch(Exception e){
				System.err.println("Exception occurred during processing query " + queryNum);
				e.printStackTrace();
				queryNum++;
			}
		} catch (Exception e) {
			System.err.println("Exception occurred during parsing");
			e.printStackTrace();
		}	
	}
}
