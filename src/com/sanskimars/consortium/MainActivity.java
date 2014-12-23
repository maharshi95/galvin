package com.sanskimars.consortium;

import android.app.Activity;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends Activity {

	private CountDownTimer timer;
	private Button playB;
	private Button intB;
	private EditText text;
	private long time = 1000;
	private long interval = 10;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		playB = (Button) findViewById(R.id.button_play);
		intB = (Button) findViewById(R.id.button_interupt);
		text = (EditText) findViewById(R.id.editText);
		
		intB.setEnabled(false);
		text.setEnabled(false);
	}

	public void playAction(View view) {
		
		playB.setEnabled(false);
		intB.setEnabled(true);
		timer = new CountDownTimer(time, interval) {
			public void onTick(long timeLeft) {
				text.setText("Time left: " + timeLeft);
			}

			public void onFinish() {
				text.setText("Timer finished!!");
				intB.setEnabled(false);
				playB.setEnabled(true);
			}
		};
		timer.start();
	}

	public void interuptAction(View view) {
		intB.setEnabled(false);
		text.setText("Timer interupted by user");
		timer.cancel();
		playB.setEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
