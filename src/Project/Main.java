package Project;

import java.io.BufferedReader;
import java.io.FileReader;

import IO.BinaryTupleWriter;
import LogicalOperator.LogicalOperator;
import LogicalOperator.PhysicalPlanBuilder;
import Operators.Operator;
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
		final String queriesFile = "queries.sql";
		
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
    			System.err.println("Exception occurred during reading");
    			e.printStackTrace();    			
		 }
		
		try {
			CCJSqlParser parser = new CCJSqlParser(new FileReader(inputDir + "/" + queriesFile));

			Statement statement;
			int queryNum = 1;
			
			try{
				while ((statement = parser.Statement()) != null) {
	
					Select select = (Select) statement;
					
					PlainSelect body = (PlainSelect) select.getSelectBody();	
					Driver d = new Driver(body);
					LogicalOperator po = d.getRoot();
					PhysicalPlanBuilder ppb = new PhysicalPlanBuilder(po);
					Operator o = ppb.getResult();
					
					Tuple t = o.getNextTuple();

					BinaryTupleWriter writ = new BinaryTupleWriter(outputDir + "/query" + queryNum );
					while (t != null){
						writ.write(t);
			            t = o.getNextTuple();     					
					}
					queryNum++;
					writ.finalize();
					writ.close();
					o.close();
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
