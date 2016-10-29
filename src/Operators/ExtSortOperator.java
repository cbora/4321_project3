package Operators;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.UUID;

import IO.BinaryTupleReader;
import IO.BinaryTupleWriter;
import IO.HumanTupleWriter;
import IO.TupleReader;
import IO.TupleWriter;
import Project.Tuple;
import Project.TupleComparator;
import net.sf.jsqlparser.statement.select.OrderByElement;


public class ExtSortOperator extends SortOperator {
	
	private String given_tmp_dir;
	private String tmp_dir;
	private String output_file; // filename of the last results
	private TupleReader output_reader; // reader for the sorted tuples 
	private int read_flag; // flag for human or binary
	private int bSize; // No buffer pages
	private static final int PAGE_SIZE = 4096;
	private static final int INT_SIZE = Integer.SIZE / Byte.SIZE;
	private Tuple[] buffer;
	private int prev_runs; 
	private int pass;
	private int prev_run_index;
	private int curr_run;
	//private int num
	
	
	public ExtSortOperator(Operator child, ArrayList<OrderByElement> order_by, String tmp_dir, int bSize) {
		//public ExtSortOperator(Operator child, ArrayList<OrderByElement> order_by) {
		super(child, order_by);
		this.bSize = (bSize > 0) ? bSize : 1;
		this.buffer = new Tuple[(bSize * PAGE_SIZE) / (INT_SIZE * schema.size())];
		this.given_tmp_dir = tmp_dir;
		this.prev_runs = 0;
		this.prev_run_index = 0;
		this.pass = 0;
		this.curr_run = 0;
		makeTemp(); // make subdirectory		
		extsort();		
	}
	
	
	public void makeTemp() {
		UUID uuid = UUID.randomUUID();
		this.tmp_dir = this.given_tmp_dir + "/" + String.valueOf(uuid);
		File newDir = new File(this.tmp_dir);
		if (!newDir.exists()){
			if(!newDir.mkdir()){
				this.tmp_dir = this.given_tmp_dir;
			}
		}else {
			this.tmp_dir = this.given_tmp_dir;
		}
	}
		/**
	 * Cleans up temporary directory used in the external sort
	 */
	public void cleanup() {

		if(this.tmp_dir.compareTo(this.given_tmp_dir) != 0){
			//delete tmp_dir
			File f = new File(this.tmp_dir);
			if (f.exists()) {
				String[] entries = f.list();
				for (String s: entries) {					
					File c = new File(f.getPath(), s);
					c.delete();
				}
			}
			f.delete();
		}
		else {
			//just delete files in the tmp directory
		}	
	}
	
	/**
	 * Fills buffer with next round of tuples from left child
	 */
	private void fillBuffer() {
		Tuple t;
		int i = 0;
		
		for (; i < buffer.length; i++) {
			if ((t = child.getNextTuple()) == null)
				break;
			buffer[i] = t;
		}
		for (; i < buffer.length; i++) {
			buffer[i] = null;
		}
	}
	
	
	/**
	 * Actual sort
	 */
	
	public void extsort() {
		
		pass0();

		while(this.prev_runs > 1) {
			passN();
		}

		this.output_reader = new BinaryTupleReader(this.tmp_dir + "/" + this.pass + "_" + "0");
		
	}
	
	public void pass0() {
		fillBuffer();

		//change buffer into a list
		while(buffer[buffer.length-1] != null) {
			Arrays.sort(buffer, new TupleComparator(this.sort_order));
			TupleWriter write = new BinaryTupleWriter(this.tmp_dir + "/" + "0_" + this.prev_runs );
			write.write(buffer);
			write.finalize();
			write.close();
			fillBuffer();
			this.prev_runs++;
						
		}
		if (buffer[0] != null) {
			int i = 0;
			for(; i<buffer.length; i++){
				if (buffer[i] == null)
					break;
			}
			Arrays.sort(buffer, 0, i, new TupleComparator(this.sort_order));
			TupleWriter write = new BinaryTupleWriter(this.tmp_dir + "/" + "0_" + this.prev_runs );
			write.write(buffer);
			write.finalize();
			write.close();
		}				
	}
	
	public void passN() {
		while(this.prev_run_index <= this.prev_runs){
			mergeRuns();
		}
		this.pass++;
		
		this.prev_runs = this.curr_run;
		this.prev_runs--;
		this.curr_run = 0;
		this.prev_run_index = 0;
			
	}

	public void mergeRuns() {	
		TupleReader[] readers = new TupleReader[this.bSize-1];
		Tuple[]  t = new Tuple[this.bSize-1];
		for (int i=0; i<this.bSize-1; i++) {
		
				if (this.prev_run_index > this.prev_runs){
				//this.prev_run_index				
				break;
				}			

			readers[i] = new BinaryTupleReader(this.tmp_dir + "/" + this.pass + "_" + this.prev_run_index);
			this.prev_run_index++; 
			t[i] = readers[i].read();
		}	
		TupleComparator comparator = new TupleComparator(this.sort_order);
		TupleWriter writer = new BinaryTupleWriter(this.tmp_dir + "/" + (this.pass + 1) + "_" + this.curr_run);
		
		while(!isNull(t)){
			Tuple min = null;
			int min_index = -1;
			for (int i=0; i<t.length; i++){
					
				if (t[i] == null){
					continue;
				}
				else if (min == null ){
					min = t[i];
					min_index = i;	
				}
				
				else if (comparator.compare(t[i], min) == -1){
						min = t[i];
						min_index = i;
				}				
			}
					
			t[min_index] = readers[min_index].read();								
			writer.write(min);
		}
		writer.finalize();
		writer.close();
	
		this.curr_run++;
	}
	
	public boolean isNull(Tuple [] t) {
		for(int i=0; i<t.length; i++){
			if(t[i] != null)
				return false;
		}
		return true;
	}
	
	@Override
	public Tuple getNextTuple() {
		Tuple t = this.output_reader.read();
		return t;
	}

	@Override
	public void reset() {
		this.output_reader.reset();
	}

	@Override
	public void close() {
		cleanup();
		this.output_reader.close();
	}

}
