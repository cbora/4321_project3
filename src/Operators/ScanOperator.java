package Operators;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Project.TableInfo;
import Project.Tuple;

public class ScanOperator extends Operator {

	private FileReader fileReader;
	private BufferedReader bufferedReader;

	public ScanOperator(TableInfo tableInfo) {
		super();

		String fileName = tableInfo.getFileName();

		try {
			this.fileReader = new FileReader(fileName);
			this.bufferedReader = new BufferedReader(this.fileReader);
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
		}
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
			this.bufferedReader = new BufferedReader(this.fileReader);
		} catch (IOException ex) {
			System.out.println("Error :(");
		}
	}

	public void close() {
		try {
			bufferedReader.close();
		} catch (IOException ex) {
			System.out.println("Error closing");
		}
	}
}
