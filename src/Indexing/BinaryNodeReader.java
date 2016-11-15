package Indexing;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Reads nodes from index file
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class BinaryNodeReader {

	/*
	 * ================================== 
	 * Fields
	 * ==================================
	 */
	private static final int PAGE_SIZE = 4096; // bytes in our buffer
	private static final int BYTES_IN_INT = 4; // bytes in an int

	private String filename; // name of the file we are reading
	private ByteBuffer buffer; // buffer for holding data we read in
	private FileInputStream input; // Input Stream
	private FileChannel channel; // Channel
	
	private int rootPage; // page root is on
	private int nLeafPages; // number leaves
	private int D; // order of tree

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
	public BinaryNodeReader(String filename) {
		this.filename = filename;
		try {
			this.input = new FileInputStream(this.filename);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		this.channel = this.input.getChannel();
		this.buffer = ByteBuffer.allocate(PAGE_SIZE);
		
		readPage();
		
		this.rootPage = this.buffer.getInt(0);
		this.nLeafPages = this.buffer.getInt(BYTES_IN_INT * 1);
		this.D = this.buffer.getInt(BYTES_IN_INT * 2);
	}

	/*
	 * ================================== 
	 * Methods
	 * ==================================
	 */

	/**
	 * reads the node on page #pageIndex
	 * @param pageIndex - page we want to read from
	 * @return node at page #pageIndex
	 */
	public Node read(int pageIndex) {
		long byteIndex = pageIndex * PAGE_SIZE;
		
		try {
			this.channel.position(byteIndex);
			readPage();
		} catch (IOException e) {
			e.printStackTrace();
		}

		boolean isLeaf = buffer.getInt(0) == 0 ? true : false;
		int buffer_index = BYTES_IN_INT;
		if (isLeaf) {
			return readLeaf(buffer_index, pageIndex);
		}
		else {
			return readIndex(buffer_index, pageIndex);
		}
	}
	
	/**
	 * closes open I/O
	 */
	public void close() {
		try {
			this.input.close();
			this.channel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return page index of root
	 */
	public int getRootPage() {
		return this.rootPage;
	}
	
	/**
	 * 
	 * @return number of leaves in tree
	 */
	public int getNumLeaves() {
		return this.nLeafPages;
	}
	
	/**
	 * 
	 * @return order of the tree
	 */
	public int getOrder() {
		return this.D;
	}
	
	/**
	 * Reads a leaf node
	 * @param buffer_index - current position in buffer 
	 * @param pageIndex - page we are reading from
	 * @return LeafNode at pageIndex
	 */
	private LeafNode readLeaf(int buffer_index, int pageIndex) {
		LeafNode leaf = new LeafNode(pageIndex);
		
		int nDataEntries = buffer.getInt(buffer_index);
		buffer_index += BYTES_IN_INT;
		
		for (int i = 0; i < nDataEntries; i++) {
			int key = buffer.getInt(buffer_index);
			buffer_index += BYTES_IN_INT;
			
			int nRids = buffer.getInt(buffer_index);
			buffer_index += BYTES_IN_INT;
			
			ArrayList<RecordID> list = new ArrayList<RecordID>();
			for (int j = 0; j < nRids; j++) {
				int pid = buffer.getInt(buffer_index);
				buffer_index += BYTES_IN_INT;
				
				int tid = buffer.getInt(buffer_index);
				buffer_index += BYTES_IN_INT;
				
				list.add(new RecordID(pid, tid));
			}
			
			leaf.insert(key, list);
		}
		
		return leaf;
	}
	
	/**
	 * Reads IndexNode
	 * @param buffer_index - current position in buffer
	 * @param pageIndex - page we are reading from
	 * @return IndexNode at pageIndex
	 */
	private IndexNode readIndex(int buffer_index, int pageIndex) {
		IndexNode index = new IndexNode(pageIndex);
		
		int nKeys = buffer.getInt(buffer_index);
		buffer_index += BYTES_IN_INT;
		
		for (int i = 0; i < nKeys; i++, buffer_index += BYTES_IN_INT) {
			index.insertKey(buffer.getInt(buffer_index));
		}
		
		for (int i = 0; i <= nKeys; i++, buffer_index += BYTES_IN_INT) {
			index.insertChild(buffer.getInt(buffer_index));
		}
		
		return index;
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

		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
}
