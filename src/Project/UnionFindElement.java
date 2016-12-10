package Project;

import java.util.Set;

/**
 * Union find element data structure
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 */
public class UnionFindElement {
	
	/* ================================== 
	 * Fields
	 * ================================== */
	private Set<String> attributes; // set of table attributes
	public Integer equals; // equals
	public Integer ceiling; // upper bound
	public Integer floor; // lower  bound
	
	/* ================================== 
	 * Constructor
	 * ================================== */
	/**
	 * Constructor
	 * @param attributes
	 */
	public UnionFindElement(Set<String> attributes) {
		this.equals = null;
		this.ceiling = null;
		this.floor = null;
		this.attributes = attributes;		
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	
	/**
	 * 
	 * @return set of attributes
	 */
	public Set<String> getAttributes() {
		return this.attributes;
	}
	
	/**
	 * @return string representation of this element
	 */
	public String toString() {
		return "[" + attributes.toString() + ", equals " + equals + ", min " + floor + ", max " + ceiling + "]";
	}

}
