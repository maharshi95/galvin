package com.consortium.consochrome;

import android.text.method.TimeKeyListener;
import static com.consortium.consochrome.Data.*;

public class Score implements Comparable<Score>{

	private int level;
	private int step;
	private long time;
	
	public Score() {
		level = 1;
		step = 1;
		time = 0;
	}
	
	public int level() {
		return level;
	}
	
	public int step() {
		return step;
	}
	
	public long time() {
		return time;
	}
	
	public void increment(long timetaken) {
		if(step < STEPS_PER_LEVEL) {
			step ++;
		}
		else {
			level++;
			step = 1;
		}
		time += timetaken;
	}
	
	@Override
	public int compareTo(Score another) {
		if(level > another.level())
			return 1;
		else if(level < another.level)
			return -1;
		
		if(step > another.step) 
			return 1;
		else if(step < another.step)
			return -1;
		
		if(time < another.time)
			return 1;
		else if(time > another.time)
			return -1;
		
		return 0;
	}
	
	@Override
	public String toString() {
		return "[" + level + "." + step + " " + time +"]";
	}

}
