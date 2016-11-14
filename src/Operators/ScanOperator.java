package Operators;

import java.util.ArrayList;
import java.util.HashMap;

import IO.BinaryTupleReader;
import IO.TupleReader;
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
	private HashMap<String, Integer> schema; // Schema to return from getSchema()
	private TupleReader reader; // tuplereader for pages
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

		this.reader = new BinaryTupleReader(this.tableInfo.getFilePath());
		
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
		Tuple t = reader.read();
		
		return t;
	}
	
	public boolean pageStatus() {
		return reader.pageIsFinished();
	}
	@Override
	public void reset() {
		reader.reset();
	}
	
	@Override
	public void reset(int index){}

	@Override
	public void close() {
		reader.close();
	}
	
	public TableInfo getTableInfo() {
		return this.tableInfo;
	}
	
	public String getTableID() {
		return this.tableID;
	}
}
