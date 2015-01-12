package com.consortium.consochrome;

import com.consortium.consochrome.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.Toast;

public class SplashScreen extends Activity {

	private Thread splashThread;
	private Thread animator;
	private int[] splashImages = new int[8];

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		foo();
	}

	private void foo() {
		final ImageView splashImageView = (ImageView) findViewById(R.id.splash_image_view);

		final SplashScreen splashScreen = this;

		splashImages[0] = R.drawable.splash00;
		splashImages[1] = R.drawable.splash01;
		splashImages[2] = R.drawable.splash02;
		splashImages[3] = R.drawable.splash03;
		splashImages[4] = R.drawable.splash04;
		splashImages[5] = R.drawable.splash05;
		splashImages[6] = R.drawable.splash06;
		splashImages[7] = R.drawable.splash07;

		animator = new Thread() {
			public void run() {
				int i = 0;
				while (true) {
					final int index = i;
					runOnUiThread(new Runnable() {
						public void run() {
							splashImageView
									.setImageResource(splashImages[index]);
						}
					});
					try {
						synchronized (this) {
							wait(200);
						}
					} catch (InterruptedException e) {
					}
					i = (i + 1) % 8;
				}
			};
		};

		splashThread = new Thread() {
			public void run() {
				try {
					synchronized (this) {
						// Wait given period of time or exit on touch
						wait(2000);
					}
				} catch (InterruptedException ex) {
				}
				animator.interrupt();
				finish();
				try {
					synchronized (this) {
						wait(100);
					}
				} catch (InterruptedException e) {
				}
				// Run next activity
				Intent intent = new Intent();
				intent.setClass(splashScreen, MainActivity.class);
				startActivity(intent);
			}
		};
		animator.start();
		splashThread.start();
	}

	@Override
	public boolean onTouchEvent(MotionEvent evt) {
		if (evt.getAction() == MotionEvent.ACTION_DOWN) {
			synchronized (splashThread) {
				splashThread.notifyAll();
			}
		}
		return true;
	}

	public void onBackPressed() {
	}
}
