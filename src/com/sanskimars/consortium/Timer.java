package com.sanskimars.consortium;

import android.os.CountDownTimer;

public abstract class Timer {

	private CountDownTimer timer;
	
	public Timer(long time, long interval) {
		timer = new CountDownTimer(time,interval) {
			public void onTick(long millisUntilFinished) {
				Timer.this.onTick(millisUntilFinished);
			}
			public void onFinish() {
				Timer.this.onFinish();
			}
		};
	}
	
	public void reset(long time, long interval) {
		timer = new CountDownTimer(time,interval) {
			public void onTick(long millisUntilFinished) {
				Timer.this.onTick(millisUntilFinished);
			}
			public void onFinish() {
				Timer.this.onFinish();
			}
		};
	}
	
	public void start() {
		timer.start();
	}
	
	public void cancel() {
		timer.cancel();
	}
	
	abstract public void onTick(long millisUntilFinished);

	abstract public void onFinish();
	
	

}
