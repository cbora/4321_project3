/**
 * 
 */
package IO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import Project.TableInfo;
import Project.Tuple;

/**
 * Class not implemented yet
 */
public class HumanTupleReader extends TupleReader {
	
	private String filename;
	private FileReader fileReader;
	private BufferedReader bufferedReader;
	
	public HumanTupleReader(String filename) {
		super();
		this.filename = filename;
		try {
			this.fileReader = new FileReader(this.filename);
			this.bufferedReader = new BufferedReader(this.fileReader);
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + this.filename + "'");
		}
	}
	
	public HumanTupleReader(TableInfo tableinfo) {
		//this(tableInfo.get)
	}
	
	public Tuple read(){
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
			this.fileReader = new FileReader(this.filename);
			this.bufferedReader = new BufferedReader(this.fileReader);
		} catch (IOException ex) {
			System.out.println("Error :(");
		}
	}	
	
	@Override
	public void reset(int index) {
		reset();
		for (int i = 0; i < index; i++)
			read();
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
