package CostPlan;

import java.util.HashMap;

/**
 * Maps relations to v-values
 * 
 * @author Richard Henwood (rbh228)
 * @author Chris Bora (cdb239)
 * @author Han Wen Chen (hc844)
 *
 */
public class VStore {

	/* ================================== 
	 * Fields
	 * ================================== */
	private static HashMap<vWrapper, Long> vVals = null; // maps relations to v-vals
	
	/* ================================== 
	 * Constructor
	 * ================================== */
	private VStore() {}
	
	/* ================================== 
	 * Methods
	 * ================================== */
	/**
	 * 
	 * @return instance of the current (relation -> v-value) map
	 */
	public static HashMap<vWrapper, Long> getInstance() {
		if (vVals == null)
			vVals = new HashMap<vWrapper, Long>();
		
		return vVals;
	}
	
	/**
	 * destroys current v-value map
	 */
	public static void destroy() {
		vVals = null;
	}
}
