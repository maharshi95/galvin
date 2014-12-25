package com.sanskimars.consortium;

import android.os.CountDownTimer;
import android.widget.Toast;

public abstract class Timer {

	private CountDownTimer timer;
	private String tag;
	
	public Timer(long time, long interval,String name) {
		tag = name;
		timer = new CountDownTimer(time,interval) {
			public void onTick(long millisUntilFinished) {
				Timer.this.onTick(millisUntilFinished);
			}
			public void onFinish() {
				Timer.this.onFinish();
			}
		};
	}
	
	public void reset(long time, long interval,String name) {
		tag = name; 
		timer = new CountDownTimer(time,interval) {
			public void onTick(long millisUntilFinished) {
				Timer.this.onTick(millisUntilFinished);
			}
			public void onFinish() {
				Timer.this.onFinish();
			}
		};
	}
	
	synchronized public void start() {
		
		timer.start();
	}
	
	synchronized public void cancel() {
		timer.cancel();
	}
	
	public String tag() {
		return tag;
	}
	
	abstract public void onTick(long millisUntilFinished);

	abstract public void onFinish();
	
}
