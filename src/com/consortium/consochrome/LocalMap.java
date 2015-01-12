package com.consortium.consochrome;

import java.util.HashMap;

public class LocalMap extends HashMap<Object, Object>{

	private static LocalMap map = new LocalMap();  
	public static LocalMap getInstance() {
		return map;
	}
	
	private LocalMap() {
	}

}
