package Operators;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.UUID;

import IO.BinaryTupleReader;
import IO.BinaryTupleWriter;
import IO.TupleReader;
import IO.TupleWriter;
import Project.Tuple;
import Project.TupleComparator;
import net.sf.jsqlparser.statement.select.OrderByElement;

/**
 * Sort Operator that uses external merge sort
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class ExtSortOperator extends SortOperator {
	
	/* ================================== 
	 * Fields
	 * ================================== */
	private static final int PAGE_SIZE = 4096; // page size
	private static final int INT_SIZE = Integer.SIZE / Byte.SIZE; // bytes per int
	private int bSize; // No buffer pages
	private Tuple[] buffer; // tuple buffer
	private String given_tmp_dir; // tmp for sratch work
	private String tmp_dir; // subdirectory inside given_tmp_dir for this speceific operator
	private TupleReader output_reader; // reader for the sorted tuples 
	private int pass; // number of the pass we are on
	private int prev_runs; // how many runs made in last round
	private int prev_run_index; // how many runs from last round we have look at
	private int curr_run; // how many runs we have made in current round
	
	/* ================================== 
	 * Constructors
	 * ================================== */
	/**
	 * Order By Constructor
	 * @param child - child operator
	 * @param order_by - order by expression
	 * @param tmp_dir - directory for scratch work
	 * @param bSize - buffer size in pages
	 */
	public ExtSortOperator(Operator child, ArrayList<OrderByElement> order_by, String tmp_dir, int bSize) {
		super(child, order_by);
		
		this.bSize = (bSize > 0) ? bSize : 1;
		this.buffer = new Tuple[(bSize * PAGE_SIZE) / (INT_SIZE * schema.size())];
		
		this.given_tmp_dir = tmp_dir;
		
		this.prev_runs = 0;
		this.prev_run_index = 0;
		this.pass = 0;
		this.curr_run = 0;
		
		makeTemp(); // make subdirectory		
		extsort();	// perform sort
	}
	
	/*
	 * Sort Order Constructor
	 * @param child - child operator
	 * @param sort_order - priority ordering of cols
	 * @param tmp_dir - directory for scratch work
	 * @param bSize - buffer size in pages
	 */
	public ExtSortOperator(Operator child, int[] sort_order, String tmp_dir, int bSize) {
		super(child, sort_order);
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

	/**
	 * pretty print method
	 * @param depth
	 * @return this method's name
	 */
	public String prettyPrint(int depth){
		StringBuffer sb = new StringBuffer();
		for(int i=0; i<depth; i++)
			sb.append("-");
		sb.append("ExternalSort");
		
		if (this.order_by == null){
			ArrayList<String> a = new ArrayList<String>();	
			for (int i=0; i < this.sort_order.length; i++){
				for (String c: this.schema.keySet()){
					if(schema.get(c) == i){						
						a.add(c);
					}
				}
			}
			sb.append(a.toString());
		}else
			sb.append(this.order_by);
		sb.append("\n");
		sb.append(this.child.prettyPrint(depth + 1));
		return sb.toString();
	}
	
	
	/* ================================== 
	 * Methods
	 * ================================== */
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
	public void reset(int index) {
		output_reader.reset(index);
	}

	@Override
	public void close() {
		cleanup();
		this.output_reader.close();
		this.child.close();
	}
	
	/**
	 * Create subdirectory inside of given_tmp for scratch work
	 */
	private void makeTemp() {
		UUID uuid = UUID.randomUUID();
		this.tmp_dir = this.given_tmp_dir + "/" + String.valueOf(uuid);
		File newDir = new File(this.tmp_dir);
		if (!newDir.exists()){
			if(!newDir.mkdir()){
				this.tmp_dir = this.given_tmp_dir;
			}
		} else {
			//this.tmp_dir = this.given_tmp_dir;
			makeTemp();
		}
	}
	
	/**
	 * Cleans up temporary directory used in the external sort
	 */
	private void cleanup() {
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
	 * Actual sort
	 */
	private void extsort() {
		pass0();

		// keep performing passes until only one file remains
		while(this.prev_runs > 1) {
			passN();
		}

		// configure output_reader to read from final sorted file
		this.output_reader = new BinaryTupleReader(this.tmp_dir + "/" + this.pass + "_" + "0");	
	}
	
	
	/**
	 * Performs the "Pass 0" phase of external merge sort
	 */
	private void pass0() {
		fillBuffer();

		// handle all runs involving full buffer
		while(buffer[buffer.length-1] != null) {
			Arrays.sort(buffer, new TupleComparator(this.sort_order));
			TupleWriter write = new BinaryTupleWriter(this.tmp_dir + "/" + "0_" + this.prev_runs );
			write.write(buffer);
			write.finalize();
			write.close();
			fillBuffer();
			this.prev_runs++;
		}

		// handle final run
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
			this.prev_runs++;
		}				
	}
	
	/**
	 * Performs the "Pass N" (where N > 0) phase of external merge sort
	 */
	private void passN() {
		while(this.prev_run_index < this.prev_runs){
			mergeRuns();
		}
		this.pass++;
		
		this.prev_runs = this.curr_run;
		this.curr_run = 0;
		this.prev_run_index = 0;
	}
	
	/**
	 * Performs (bSize - 1) way merge
	 */
	private void mergeRuns() {
		TupleReader[] readers = new TupleReader[this.bSize-1];
		PriorityQueue<TupleWrapper> t = new PriorityQueue<TupleWrapper>(new TupleWrapperComparator(this.sort_order));
		
		for (int i=0; i<this.bSize-1; i++) {		
			if (this.prev_run_index >= this.prev_runs){
				break;
			}			
			readers[i] = new BinaryTupleReader(this.tmp_dir + "/" + this.pass + "_" + this.prev_run_index);
			this.prev_run_index++; 
			t.add(new TupleWrapper(readers[i].read(), i));
		}
		
		TupleWriter writer = new BinaryTupleWriter(this.tmp_dir + "/" + (this.pass + 1) + "_" + this.curr_run);
		while(!t.isEmpty()) {
			TupleWrapper min = t.poll();
			writer.write(min.tuple);
			Tuple next = readers[min.pos].read();
			
			if (next != null) 
				t.add(new TupleWrapper(next, min.pos));
		}
		writer.finalize();
		writer.close();
	
		this.curr_run++;
	}
	
	/**
	 * Fills buffer with next round of tuples from left child (pads with nulls if buffer not filled)
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
	
	/* ================================== 
	 * Inner Classes
	 * ================================== */
	/**
	 * Wrapper used to contain tuples as well as the index of the reader said tuple came from
	 */
	private class TupleWrapper {
		public Tuple tuple;
		public int pos;
		
		public TupleWrapper(Tuple tuple, int pos) {
			this.tuple = tuple;
			this.pos = pos;
		}
	}
	
	/**
	 * Comparator for TupleWrapper - ignores the TupleWrapper.pos and functions the same as TupleComparator
	 */
	private class TupleWrapperComparator implements Comparator<TupleWrapper> {
		private final int[] sortOrder; 
		
		public TupleWrapperComparator(int[] sortOrder) {
			this.sortOrder = sortOrder;
		}

		@Override
		public int compare(TupleWrapper t1, TupleWrapper t2) {
			int i = 0;
			int result = 0;
			while (result == 0 && i < this.sortOrder.length) {
				int val1 = t1.tuple.getVal(this.sortOrder[i]);
				int val2 = t2.tuple.getVal(this.sortOrder[i]);
				result = ((Integer) val1).compareTo(val2);
				i++;
			}
			return result;
		}
		
	}

}
