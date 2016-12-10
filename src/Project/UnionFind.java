package Project;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Union find class
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class UnionFind {

	/* ================================== 
	 * Fields
	 * ================================== */
	private HashMap<String, UnionFindElement> map; // attribute name and UnionFindElement mapper

	/* ================================== 
	 * Constructor
	 * ================================== */
	/**
	 * Constructor
	 */
	public UnionFind() {
		this.map = new HashMap<String, UnionFindElement>();
	}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	
	/**
	 * Add attribute to the hashset
	 */
	public void add(String attribute) {
		HashSet<String> set = new HashSet<String>();
		set.add(attribute);
		
		map.put(attribute, new UnionFindElement(set));
	}
	
	/**
	 *  find the element
	 * @param attribute
	 * @return UnionFindElement
	 */
	public UnionFindElement find(String attribute) {
		return map.get(attribute);
	}
	
	/**
	 * Union two attributes
	 * @param attr1
	 * @param attr2
	 */
	public void union(String attr1, String attr2) {
		UnionFindElement elem1 = map.get(attr1);
		UnionFindElement elem2 = map.get(attr2);
		
		HashSet<String> set = new HashSet<String>(elem1.getAttributes());
		set.addAll(elem2.getAttributes());
		
		UnionFindElement newElem = new UnionFindElement(set);
		if (elem1.equals != null) {
			newElem.equals = elem1.equals;
			newElem.ceiling = elem1.equals;
			newElem.floor = elem1.equals;
		}
		else if (elem2.equals != null) {
			newElem.equals = elem2.equals;
			newElem.ceiling = elem2.equals;
			newElem.floor = elem2.equals;
		}
		else {
			if (elem1.ceiling != null && elem2.ceiling != null)
				newElem.ceiling = Math.min(elem1.ceiling, elem2.ceiling);
			else
				newElem.ceiling = elem1.ceiling != null ? elem1.ceiling : elem2.ceiling;
			
			if (elem1.floor != null && elem2.floor != null)
				newElem.floor = Math.max(elem1.floor, elem2.floor);
			else
				newElem.floor = elem1.floor != null ? elem1.floor : elem2.floor;
		}

		//map.put(attr1, newElem);
		//map.put(attr2, newElem);
		//System.out.println(elem1.getAttributes().size());
		for (String attr : elem1.getAttributes())
			map.put(attr, newElem);
		//System.out.println(elem2.getAttributes().size());
		for (String attr : elem2.getAttributes())
			map.put(attr, newElem);
		
	}
	
	/**
	 * Get the set of attributes
	 * @return set of attributes
	 */
	public Set<String> getAttributes() {
		return map.keySet();
	}
	
	/**
	 * Union find elements to string
	 */
	public String toString() {
		StringBuffer sb = new StringBuffer();
		HashSet<UnionFindElement> set = new HashSet<UnionFindElement>(this.map.values());

		for (UnionFindElement elem : set) {
			sb.append(elem.toString());
			sb.append("\n");
		}
		return sb.toString();
	}
	
}
