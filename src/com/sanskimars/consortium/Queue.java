package com.sanskimars.consortium;

import java.util.LinkedList;

public class Queue {
	private LinkedList<Integer> list;
	
	public Queue() {
		list = new LinkedList<Integer>();
	}
	
	public boolean isEmpty() {
		return list.isEmpty();
	}
	
	public void pushBack(int num) {
		list.add(num);
	}
	
	public int pop() {
		return list.remove();
	}
}
