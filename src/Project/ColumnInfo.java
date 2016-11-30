package Project;

public class ColumnInfo {
	
	public String column; // col name
	public int min; // min value
	public int max; // max value
	
	public ColumnInfo(String column) {
		this.column = column;
		this.min = Integer.MIN_VALUE;
		this.max = Integer.MAX_VALUE;
	}

}
