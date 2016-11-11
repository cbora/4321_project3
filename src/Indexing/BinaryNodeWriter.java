package Indexing;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;

public class BinaryNodeWriter {
	
	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	private static final int PAGE_SIZE = 4096; // bytes in a page
	private static final int BYTES_IN_INT = 4; // bytes in an int

	private String filename; // name of file we are writing to
	private ByteBuffer buffer; // buffer for holding data to be written
	private FileOutputStream output; // output stream
	private FileChannel channel; // channel
	private ArrayList<Node> nodes;
	private int nLeaves;
	private int order;

	/*
	 * ================================== 
	 * Constructors
	 * ==================================
	 */
	/**
	 * Constructor
	 * @param filename - name of file we are writing to
	 */
	public BinaryNodeWriter(String filename, ArrayList<Node> nodes, int nLeaves, int order) {
		this.filename = filename;
		try {
			this.output = new FileOutputStream(this.filename);
			this.channel = output.getChannel();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.buffer = ByteBuffer.allocate(PAGE_SIZE);
	}

	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */
	
	public void write() {
		writeHeader(nodes.size(), nLeaves, order);
		for (Node n : nodes) 
			write(n);
	}
	
	private void writeHeader(int rtIndex, int nLeaves, int order) {
		buffer.putInt(0, rtIndex);
		buffer.putInt(BYTES_IN_INT * 1, nLeaves);
		buffer.putInt(BYTES_IN_INT * 2, order);
		fill_page(BYTES_IN_INT * 3);
		writePage();
	}
	
	private void write(Node n) {
		if (n.isLeafNode)
			writeLeaf((LeafNode) n);
		else 
			writeIndex((IndexNode<Node>) n);
	}
	
	private void writeLeaf(LeafNode n) {
		buffer.putInt(0, 0);
		buffer.putInt(BYTES_IN_INT * 1, n.getKeys().size());
		
		int buffer_index = BYTES_IN_INT * 2;
		
		ArrayList<Integer> keys = n.getKeys();
		ArrayList<LinkedList<RecordID>> values = n.getValues();
		
		for (int i = 0; i < keys.size(); i++) {
			buffer.putInt(buffer_index, keys.get(i));
			buffer_index += BYTES_IN_INT;
			
			buffer.putInt(buffer_index, values.get(i).size());
			buffer_index += BYTES_IN_INT;
			
			for (RecordID rid : values.get(i)) {
				buffer.putInt(buffer_index, rid.pageid);
				buffer_index += BYTES_IN_INT;
				
				buffer.putInt(buffer_index, rid.tupleid);
				buffer_index += BYTES_IN_INT;
			}
		}
		
		fill_page(buffer_index);
		writePage();
	}
	
	private void writeIndex(IndexNode<Node> n) {
		buffer.putInt(0, 1);
		buffer.putInt(BYTES_IN_INT * 1, n.getKeys().size());
		
		int buffer_index = BYTES_IN_INT * 2;
		
		ArrayList<Integer> keys = n.getKeys();
		ArrayList<Node> children = n.getChildren();

		for (Integer key : keys) {
			buffer.putInt(buffer_index, key);
			buffer_index += BYTES_IN_INT;
		}
		
		for (Node child : children) {
			buffer.putInt(buffer_index, child.getPos());
			buffer_index += BYTES_IN_INT;
		}
		
		fill_page(buffer_index);
		writePage();
	}

	/**
	 * closes any I/O services
	 */
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
	private void fill_page(int buffer_index) {
		for (int i = buffer_index; i < PAGE_SIZE; i += BYTES_IN_INT) {
			this.buffer.putInt(i, 0);
		}
	}
	
	/**
	 * writes buffer to output file
	 */
	private void writePage() {
		try {
			this.channel.write(this.buffer);
			this.buffer.clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
