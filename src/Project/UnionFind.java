package Project;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UnionFind {

	private HashMap<String, UnionFindElement> map;

	public UnionFind() {
		this.map = new HashMap<String, UnionFindElement>();
	}
	
	public void add(String attribute) {
		HashSet<String> set = new HashSet<String>();
		set.add(attribute);
		
		map.put(attribute, new UnionFindElement(set));
	}
	
	public UnionFindElement find(String attribute) {
		return map.get(attribute);
	}
	
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
		
		map.put(attr1, newElem);
		map.put(attr2, newElem);
	}
	
	public Set<String> getAttributes() {
		return map.keySet();
	}
	
}
