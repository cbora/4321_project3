package project2;


import java.io.FileReader;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.expression.*;

/**
 * Example class for getting started with JSQLParser. Reads SQL statements from
 * a file and prints them to screen; then extracts SelectBody from each query
 * and also prints it to screen.
 * 
 * @author Lucja Kot
 */
public class ParserExample {
	
	private static final String queriesFile = "test/project2/queries.sql";

	public static void main(String[] args) {
		
		try {
			CCJSqlParser parser = new CCJSqlParser(new FileReader(queriesFile));
			Statement statement;
			while ((statement = parser.Statement()) != null) {
				System.out.println("Read statement: " + statement);
				Select select = (Select) statement;
				System.out.println("Select body is: " + select.getSelectBody());
				PlainSelect body = (PlainSelect) select.getSelectBody();
				System.out.println("\t Get from item: "+ body.getFromItem());
				System.out.println("\t other tables to join: " + body.getJoins());
				System.out.println("\t Where: "+ body.getWhere());
				System.out.println("\t Select items: " + body.getSelectItems());
				System.out.println("\t Distinct: " + body.getDistinct());
				System.out.println("\t Order by: " + body.getOrderByElements());
				System.out.println("\n\n");
			}
		} catch (Exception e) {
			System.err.println("Exception occurred during parsing");
			e.printStackTrace();
		}
		
	}
	public void whereClause() {
		System.out.println("Where function");
	}
}