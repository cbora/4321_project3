package IO;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import Project.Tuple;

/**
 * Writes tuples to a binary file
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */

public class BinaryTupleWriter extends TupleWriter {

	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	private static final int PAGE_SIZE = 4096; // bytes in a page
	private static final int BYTES_IN_INT = 4; // bytes in an int
	private static final int NUM_META_DATA = 2; // number of ints of meta data atop each page

	private String filename; // name of file we are writing to
	private ByteBuffer buffer; // buffer for holding data to be written
	private FileOutputStream output; // output stream
	private FileChannel channel; // channel
	private int col_number; // columns in tuple
	private int buffer_index; // our place in the buffer
	private int written_tuples; // how many tuples on the page we have written so far

	/*
	 * ================================== 
	 * Constructors
	 * ==================================
	 */
	/**
	 * Constructor
	 * @param filename - name of file we are writing to
	 */
	public BinaryTupleWriter(String filename) {
		this.filename = filename;
		try {
			this.output = new FileOutputStream(this.filename);
			this.channel = output.getChannel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.buffer = ByteBuffer.allocate(PAGE_SIZE);
		this.buffer_index = NUM_META_DATA * BYTES_IN_INT;
		this.col_number = -1;
		this.written_tuples = 0;
	}

	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */
	/**
	 * writes tuple to file
	 * @param t - tuple to be written
	 */
	@Override
	public void write(Tuple t) {
		int tuples_per_page = (PAGE_SIZE / BYTES_IN_INT - NUM_META_DATA) / t.length();
		if (written_tuples == tuples_per_page) {
			writePage();
		}

		if (this.col_number == -1) {
			this.col_number = t.length();
		}

		for (int i = 0; i < t.length(); i++) {

			this.buffer.putInt(this.buffer_index, t.getVal(i));
			this.buffer_index += BYTES_IN_INT;
		}
		this.written_tuples++;

	}
	
	/**
	 * writes array of tuples to file
	 * @param t - array of tuples to write
	 */
	public void write(Tuple[] t){
		for (int i=0; i< t.length&&t[i] != null; i++){
			write(t[i]);
		}
	}

	/**
	 * method for flushing any data remaining in buffer to file
	 */
	@Override
	public void finalize() {
		if (this.buffer_index != BYTES_IN_INT * NUM_META_DATA) {
			writePage();
		}
	}

	/**
	 * closes any I/O services
	 */
	@Override
	public void close() {
		try {
			this.output.close();
			this.channel.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * fills end of buffer with 0's
	 */
	private void fill_page() {
		for (int i = this.buffer_index; i < PAGE_SIZE; i += BYTES_IN_INT) {
			this.buffer.putInt(i, 0);
		}
	}
	
	/**
	 * writes buffer to output file
	 */
	private void writePage() {
		this.buffer.putInt(0, this.col_number);
		this.buffer.putInt(BYTES_IN_INT, this.written_tuples);
		fill_page();
		try {
			this.channel.write(this.buffer);
			this.buffer.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.buffer_index = BYTES_IN_INT * NUM_META_DATA;
		this.col_number = -1;
		this.written_tuples = 0;
	}

}
