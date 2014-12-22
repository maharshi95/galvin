package com.sanskimars.consortium;

/**
 * Singleton class with static methods to provide utility methods to other classes.
 * @author Maharshi
 *
 */
public class Utility {
	
	private static Utility utility;
	private Utility(){}
	
	public static Utility getInstance() {
		if(utility == null)
			utility = new Utility();
		return utility;
	}
	
	public static int getLevelBufferSize(int level) {
		//to be implemented
		return 0;
	}
	
	public static int getLevelTimeout(int level) {
		//to be implemented
		return 0;
	}
	
	public static int getLevelSize(int level) {
		//to be implemeted
		return 0;
	}
}
