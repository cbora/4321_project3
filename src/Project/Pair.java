package Project;

import net.sf.jsqlparser.schema.Column;

public class Pair {
	
	public Column col;
	public int value;
	
	public Pair(Column col, int value) {
		this.col = col;
		this.value = value;
	}
	
	@Override
	public String toString() {
		return "(" + col +": " + value + ")";
	}
}
