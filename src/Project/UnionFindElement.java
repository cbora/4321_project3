package Project;

import java.util.Set;

public class UnionFindElement {
	private Set<String> attributes;
	public Integer equals;
	public Integer ceiling;
	public Integer floor;
	
	public UnionFindElement(Set<String> attributes) {
		this.equals = null;
		this.ceiling = null;
		this.floor = null;
		this.attributes = attributes;		
	}
	
	public Set<String> getAttributes() {
		return this.attributes;
	}

}
