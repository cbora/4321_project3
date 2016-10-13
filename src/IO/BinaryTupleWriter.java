package IO;

import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

import Project.Tuple;

public class BinaryTupleWriter extends TupleWriter {
	
	private String filename;
	private ByteBuffer buffer;
	private FileOutputStream output;
	private FileChannel channel;
	private static final int page_size = 4096;
	private int col_number;	
	private int buffer_index;
	private int written_tuples;
	private int n_pages = 0;
	public BinaryTupleWriter(String filename){
		this.filename = filename;
		try {
			this.output = new FileOutputStream(this.filename);
			this.channel = output.getChannel();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.buffer = ByteBuffer.allocate(page_size);
		this.buffer_index = 8;
		this.col_number = -1;
		this.written_tuples = 0;	
	}
	
	public void write(Tuple t) {
		int tuples_per_page = (page_size/4 - 2)/t.length();
		if (written_tuples == tuples_per_page){
			writePage();
		}
		

		if (this.col_number == -1){
			this.col_number = t.length();
			
		}
		
		for (int i=0; i<t.length(); i++){

				this.buffer.putInt(this.buffer_index, t.getVal(i));
				this.buffer_index += 4;
		}
		this.written_tuples++;
		
	}

	public void writePage() {			
		this.buffer.putInt(0, this.col_number);
		this.buffer.putInt(4, this.written_tuples);
		fill_page();
		try{			
			int r = this.channel.write(this.buffer);
			this.buffer.clear();			
		} catch(Exception e){
			e.printStackTrace();
		}
		this.buffer_index = 8;
		this.col_number = -1;		
		this.written_tuples = 0;
	}
	
	public void finalize() {
		if (this.buffer_index != 8){
			writePage();
		}
	}
	
	public void fill_page() {
		for(int i=this.buffer_index; i< page_size; i+=4){
			this.buffer.putInt(i, 0);
		}
	}
	
	public void reset() {
		
	}
	
	public void close() {
		finalize();
		try {
			this.output.close();
			this.channel.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
}
