package IO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Class that represents info in config file
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class Configuration {

	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	private String configfile; // path to config file
	private FileReader fileReader; 
	private BufferedReader bufferedReader;
	private String inputDir; // directory to be used
	private String outputDir;  // output directory to be used
	private String tmpDir; // temp directory to be used for scratch work
	private boolean index; //
	private boolean evaluate;
	
	/*
	 * ================================== 
	 * Constructor
	 * ==================================
	 */
	public Configuration(String configfile) {
		this.configfile = configfile;
		try {
			this.fileReader = new FileReader(this.configfile);
			this.bufferedReader = new BufferedReader(this.fileReader);			
		} catch(FileNotFoundException ex) {
			System.out.println("Unable to open file " + this.configfile);
		}
		parse();
		close();
	}
	
	public Configuration(String input, String output, String tmp, boolean index, boolean evaluate){
		this.inputDir = input;
		this.outputDir = output;
		this.tmpDir = tmp;
		this.index = index;
		this.evaluate = evaluate;
	}

	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */
	public void printout() {
		System.out.println("Input dir     " + this.inputDir);
		System.out.println("Output dir    " + this.outputDir);
		System.out.println("temp dir      " + this.tmpDir);
		System.out.println("Indexing      " + this.index);
		System.out.println("Evaluate      " + this.evaluate);
		System.out.println("Option        " + runOption());
	}

	public void parse() {
		try {
			this.inputDir = this.bufferedReader.readLine();
			this.outputDir = this.bufferedReader.readLine();
			this.tmpDir = this.bufferedReader.readLine();
			String indexStr = this.bufferedReader.readLine();
			this.index = indexStr.equals("0") ? false : true;
			String evaluateStr = this.bufferedReader.readLine();
			this.evaluate = evaluateStr.equals("0") ? false : true;
			
		}catch (IOException e) {
			System.out.println("Unable to parse configuration on : " + e);
		}		
	}
	
	public void close() {
		try {
			this.bufferedReader.close();
		} catch (IOException ex) {
			System.out.println("Error closing file");
		}
		
	}

	public String getInputDir() {
		return inputDir;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public String getTmpDir() {
		return tmpDir;
	}

	public boolean isIndex() {
		return index;
	}

	public boolean isEvaluate() {
		return evaluate;
	}
	
	public int runOption() {
		
		if (this.index && !this.evaluate)
			return 1;
		else if (this.index && this.evaluate)
			return 2;
		else if(!this.index && this.evaluate)
			return 3;
		else 
			return -1;
	}
	
}
