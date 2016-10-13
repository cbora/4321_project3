package IO;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import Project.Tuple;

public class HumanTupleWriter extends TupleWriter {
	
	private String filename;
	private OutputStreamWriter fileWriter;
	private BufferedWriter bufferedWriter;
	
	public HumanTupleWriter(String filename){
		this.filename = filename;
		try {
			this.fileWriter = new OutputStreamWriter(new FileOutputStream(this.filename));
			this.bufferedWriter = new BufferedWriter(this.fileWriter);
		} catch (IOException ex) {
			System.err.println("Error: "+ ex);
			ex.printStackTrace();
		}
		
	}
	
	public void write(String line) {
		try {
			this.bufferedWriter.write(line);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void reset() {
		try {
			this.fileWriter = new OutputStreamWriter(new FileOutputStream(this.filename));
			this.bufferedWriter = new BufferedWriter(this.fileWriter);
		} catch (IOException ex) {
			System.err.println("Error: "+ ex);
			ex.printStackTrace();
		}
	}
	
	public void close() {
		try { 
			bufferedWriter.close();			
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	@Override
	public void write(Tuple t) {
		// TODO Auto-generated method stub
		
	}
}

