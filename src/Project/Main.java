package Project;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.FileReader;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;


import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import Operators.HashDupElimOperator;
import Operators.Operator;
import Operators.ScanOperator;
import Operators.SortOperator;
import Project.DbCatalog;
import Project.Driver;
import Project.TableInfo;
import Project.Tuple;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

public class Main {

	public static void main(String[] args) {
		DbCatalog dbC = DbCatalog.getInstance(); 
		final String queriesFile = "queries.sql";

		
		try {
			//BufferedReader br = new BufferedReader(new FileReader(args[0]+"db/schema.txt"));
			BufferedReader br = new BufferedReader(new FileReader("src/db/schema.txt"));

			
			String read = br.readLine();
		   
		    while (read  != null) {
		    	String[] line  = read.split(" "); 
		    	//TableInfo t = new TableInfo(args[0]+"db/data/" + line[0], line[0]);
		    	TableInfo t = new TableInfo("src/db/data/" + line[0], line[0]);
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
			//CCJSqlParser parser = new CCJSqlParser(new FileReader(args[0]+"/"+queriesFile));
			CCJSqlParser parser = new CCJSqlParser(new FileReader("src/"+queriesFile));

			Statement statement;
			int queryNum = 1;
			
			try{
				while ((statement = parser.Statement()) != null) {
	
					Select select = (Select) statement;
					
					PlainSelect body = (PlainSelect) select.getSelectBody();	
					Driver d = new Driver(body);
					Operator o = d.getRoot();
					Tuple t = o.getNextTuple();
					//PrintWriter writer = new PrintWriter(args[1]+"/query"+ queryNum, "UTF-8");
	
					PrintWriter writer = new PrintWriter("src/query"+ queryNum, "UTF-8");
	
					while (t != null){
						writer.println(t.toString());
			            t = o.getNextTuple();     					
					}
					writer.close();				
					queryNum++;
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
