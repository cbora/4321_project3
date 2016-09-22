package Operators;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import Project.Pair;
import Project.TableInfo;
import Project.Tuple;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;

public class ScanOperator extends Operator {

	private TableInfo tableInfo;   // info about the table the operator corresponds to
	private String alias;		   // alias of the table. if there is no alias, tableName is used
	private FileReader fileReader; // FileReader for reading tuples
	private BufferedReader bufferedReader;   // BufferedReader for reading tuples
	private HashMap<String, Integer> schema; // Schema to return from getSchema()

	/* =====================================
	 * Constructors
	 * ===================================== */
	public ScanOperator(TableInfo tableInfo, String alias) {
		super();

		this.tableInfo = tableInfo;
		this.alias = alias;

		try {
			this.fileReader = new FileReader(this.tableInfo.getFileName());
			this.bufferedReader = new BufferedReader(this.fileReader);
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + this.tableInfo.getFileName() + "'");
		}
		
		ArrayList<String> columns = tableInfo.getColumns();
		this.schema = new HashMap<String, Integer>();
		
		// Read from columns in tableInfo
		// Add (<alias> + "." + <name of column i>, i) to hash map
		for (int i = 0; i < columns.size(); i++) {
			this.schema.put(this.alias + "." + columns.get(i), i);
		}
	}

	/**
	 * Sets alias equal to tableInfo.tableName
	 */
	public ScanOperator(TableInfo tableInfo) {
		this(tableInfo, tableInfo.getTableName());
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
			this.fileReader = new FileReader(this.tableInfo.getFileName());
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
