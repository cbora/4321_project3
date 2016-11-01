package IO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import Project.Tuple;

/**
 * Reads in a binary file of tuples
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class BinaryTupleReader extends TupleReader {

	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	private static final int PAGE_SIZE = 4096; // bytes in our buffer
	private static final int BYTES_IN_INT = 4; // bytes in an int
	private static final int NUM_META_DATA = 2; // number of ints of meta data atop each page

	private String filename; // name of the file we are reading
	private ByteBuffer buffer; // buffer for holding data we read in
	private FileInputStream input; // Input Stream
	private FileChannel channel; // Channel
	private int col_number; // number of columns in tuple
	private int row_number; // number of rows in buffer
	private int buffer_index; // where we are in the buffer
	private boolean isEmpty; // flags empty file

	/*
	 * ================================== 
	 * Constructors
	 * ==================================
	 */
	/**
	 * Constructors
	 * 
	 * @param filename
	 *            - name of the file we are reading data from
	 */
	public BinaryTupleReader(String filename) {
		this.filename = filename;
		try {
			this.input = new FileInputStream(this.filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.channel = this.input.getChannel();
		this.buffer = ByteBuffer.allocate(PAGE_SIZE);
		this.col_number = -1;
		this.isEmpty = !readPage();
	}

	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */

	/**
	 * reads the next tuple from the file
	 * @return the next tuple in the file if it exists, null otherwise
	 */
	@Override
	public Tuple read() {
		if (isEmpty)
			return null;
		if (this.buffer_index / BYTES_IN_INT == (this.col_number * this.row_number + NUM_META_DATA)) {
			if (!readPage())
				return null;
		}

		Tuple t = new Tuple(this.col_number);
		for (int i = 0; i < this.col_number; i++) {
			t.add(this.buffer.getInt(this.buffer_index), i);
			this.buffer_index += BYTES_IN_INT;
		}
		return t;
	}

	/**
	 * resets the reader back to the start of the file
	 */
	@Override
	public void reset() {
		try {
			this.input.close();
			this.input = new FileInputStream(this.filename);
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.channel = this.input.getChannel();
		this.buffer = ByteBuffer.allocate(PAGE_SIZE);
		readPage();
	}
	
	/**
	 * resets the reader back to specific tuple in file
	 */
	@Override
	public void reset(int index) {
		int tuplesPerPage = (PAGE_SIZE - NUM_META_DATA * BYTES_IN_INT) / (col_number * BYTES_IN_INT);
		int nPage = index / tuplesPerPage;
		int tupleToGo = index % tuplesPerPage;
		long idxIntoPage = nPage * PAGE_SIZE;
		
		try {
			this.channel.position(idxIntoPage);
			readPage();
			for (int i = 0; i < tupleToGo; i++)
				read();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * closes open I/O
	 */
	@Override
	public void close() {
		try {
			this.input.close();
			this.channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * reads the next page into the buffer
	 * @return true if there were pages remaining, false otherwise
	 */
	private boolean readPage() {
		int bytesRead;
		try {
			bytesRead = this.channel.read(this.buffer);
			if (bytesRead <= 0)
				return false;
			this.buffer.flip();
			this.col_number = (int) this.buffer.getInt(0);
			this.row_number = this.buffer.getInt(4);
			this.buffer_index = 8;

		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}

}
