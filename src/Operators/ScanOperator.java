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

	private TableInfo tableInfo;
	private String alias;
	private FileReader fileReader;
	private BufferedReader bufferedReader;

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
	}

	public ScanOperator(TableInfo tableInfo) {
		super();

		this.tableInfo = tableInfo;
		this.alias = tableInfo.getTableName();

		try {
			this.fileReader = new FileReader(this.tableInfo.getFileName());
			this.bufferedReader = new BufferedReader(this.fileReader);
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + this.tableInfo.getFileName() + "'");
		}
	}
	
	@Override
	public HashMap<String, Integer> getSchema() {
		ArrayList<String> columns = tableInfo.getColumns();
		HashMap<String, Integer> result = new HashMap<String, Integer>();
		for (int i = 0; i < columns.size(); i++) {
			result.put(alias + "." + columns.get(i), i);
		}
		return result;
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
		
//		Table tbl = new Table();
//		tbl.setName(tableInfo.getTableName());
//		tbl.setAlias(this.alias);
		
		for (int i = 0; i < t.length(); i++) {
			//Column col = new Column(tbl, tableInfo.getColumns().get(i));
			t.add(Integer.parseInt(values[i]), i);
		}
		return t;
	}

	@Override
	public void reset() {
		try {
			bufferedReader.close();
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
