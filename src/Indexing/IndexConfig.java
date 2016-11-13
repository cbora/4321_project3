package Indexing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class IndexConfig {

	private String configfile;
	private FileReader fileReader;
	private BufferedReader bufferedReader;
	public ArrayList<IndexInfo> indices;
	
	public IndexConfig(String configfile) {
		// TODO Auto-generated constructor stub
		this.configfile = configfile;
		try {
			this.fileReader = new FileReader(this.configfile);
			this.bufferedReader = new BufferedReader(this.fileReader);
		}catch(FileNotFoundException ex) {
			System.out.println("Unable to open file " + this.configfile);
		}
		indices = new ArrayList<IndexInfo>();
	}

	public void parse() {
		String line;
		try { 
			while((line = this.bufferedReader.readLine()) != null) {
				String[] parts = line.split(" ");
				IndexInfo i = new IndexInfo(parts[0], parts[1], parts[2], parts[3]);
				indices.add(i);
			}
			
		}catch (IOException ex) {
			
		}
	}
	
}
