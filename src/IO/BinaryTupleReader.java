package IO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

import Project.Tuple;

public class BinaryTupleReader extends TupleReader {

	private String filename;
	private ByteBuffer buffer;
	private FileInputStream input;
	private FileChannel channel;
	private static final int page_size = 4096;
	private int col_number;
	private int row_number;
	private int buffer_index;
	
	public BinaryTupleReader(String filename){
		this.filename = filename;
		try {
			this.input = new FileInputStream(this.filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.channel = this.input.getChannel();		
		this.buffer = ByteBuffer.allocate(this.page_size);
		readPage();
	}
	
	public boolean readPage() {
		this.col_number = 0;
		this.row_number = 0;
		this.buffer_index = 0;
		
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    return true;
	}
	
	public Tuple read() {
		if (this.buffer_index/4 == (this.col_number*this.row_number+2)) {			
			if(!readPage())
				return null;			
		}
		
		Tuple t = new Tuple(this.col_number);
		for(int i=0; i<this.col_number; i++){
			t.add(this.buffer.getInt(this.buffer_index), i) ;
			this.buffer_index += 4;
		}
		return t;
	}
	
	public void reset(){
		try {
			this.input.close();
			this.input = new FileInputStream(this.filename);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.channel = this.input.getChannel();		
		this.buffer = ByteBuffer.allocate(this.page_size);
		readPage();
	}
	
	public void close(){
		try {
			this.input.close();
			this.channel.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
}
