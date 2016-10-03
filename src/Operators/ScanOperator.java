package Operators;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import Project.TableInfo;
import Project.Tuple;
import net.sf.jsqlparser.schema.Table;

/**
 * Operator for scanning a datafile
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class ScanOperator extends Operator {

	/* =====================================
	 * Fields
	 * ===================================== */
	private TableInfo tableInfo;   // info about the table the operator corresponds to
	private String tableID;		   // alias of the table. if there is no alias, tableName is used
	private FileReader fileReader; // FileReader for reading tuples
	private BufferedReader bufferedReader;   // BufferedReader for reading tuples
	private HashMap<String, Integer> schema; // Schema to return from getSchema()

	/* =====================================
	 * Constructors
	 * ===================================== */
	/**
	 * Constructor
	 * @param tableInfo - info about the table we are scanning
	 * @param tableID - identifier for table to be used in schema
	 */
	public ScanOperator(TableInfo tableInfo, String tableID) {
		super();

		this.tableInfo = tableInfo;
		this.tableID = tableID;

		try {
			this.fileReader = new FileReader(this.tableInfo.getFilePath());
			this.bufferedReader = new BufferedReader(this.fileReader);
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + this.tableInfo.getFilePath() + "'");
		}
		
		ArrayList<String> columns = tableInfo.getColumns();
		this.schema = new HashMap<String, Integer>();
		
		// Read from columns in tableInfo
		// Add (<alias> + "." + <name of column i>, i) to hash map
		for (int i = 0; i < columns.size(); i++) {
			this.schema.put(this.tableID + "." + columns.get(i), i);
		}
	}

	/**
	 * Constructor that sets tableID equal to tableInfo.getTableName()
	 * @param tableInfo - info about table we are scanning
	 */
	public ScanOperator(TableInfo tableInfo) {
		this(tableInfo, tableInfo.getTableName());
	}
	
	/**
	 * Constructor that sets tableID to tbl.getAlias() == null ? tbl.getName() : tbl.getAlias()
	 * @param tableInfo - info about the table we are scanning
	 * @param tbl - table we are scanning
	 */
	public ScanOperator(TableInfo tableInfo, Table tbl) {
		this(tableInfo, tbl.getAlias() == null ? tbl.getName() : tbl.getAlias());
	}
	
	/* ===============================================
	 * Methods
	 * =============================================== */
	@Override
	public HashMap<String, Integer> getSchema() {
		return schema;
	}

	@Override
	public Tuple getNextTuple() {
		String line = "";
		try {
			line = bufferedReader.readLine();
			if (line == null)
				return null;
		}  catch (IOException ex) {
			System.out.println("Error :(");
		}
		
		String values[] = line.split(",");
		Tuple t = new Tuple(values.length);
		
		for (int i = 0; i < t.length(); i++) {
			t.add(Integer.parseInt(values[i]), i);
		}
		return t;
	}

	@Override
	public void reset() {
		try {
			bufferedReader.close();
			this.fileReader = new FileReader(this.tableInfo.getFilePath());
			this.bufferedReader = new BufferedReader(this.fileReader);
		} catch (IOException ex) {
			System.out.println("Error :(");
		}
	}

	@Override
	public void close() {
		try {
			bufferedReader.close();
		} catch (IOException ex) {
			System.out.println("Error closing");
		}
	}
}
