package com.sanskimars.consortium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import static com.sanskimars.consortium.Data.*;

public class GameActivity extends Activity {

	private Button startButton;
	private RelativeLayout centerView;
	private ImageView consoLogo;
	private ImageView[] colorTiles;
	private TextView console;
	
	private TextView dummyTile;
	
	private Timer timer;
	private OnClickListener listener;
	private Thread gameThread;
	
	private boolean gameOver;
	private boolean waiting;
	private long score;
	private long startTime;
	private long timeTakenPerStep;
	private long interval;
	private int currentLevel;
	private HashMap<Integer, Integer> imageIndexMap;
	private LinkedList<Integer> list;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		initialize();
	}

	private void initialize() {
		startButton = (Button) findViewById(R.id.button_start);
		centerView = (RelativeLayout) findViewById(R.id.center_view);
		console = (TextView)findViewById(R.id.console);
		
		colorTiles = new ImageView[N_COLORS];
		colorTiles[0] = (ImageView)findViewById(R.id.tile_0);
		colorTiles[1] = (ImageView)findViewById(R.id.tile_1);
		colorTiles[2] = (ImageView)findViewById(R.id.tile_2);
		colorTiles[3] = (ImageView)findViewById(R.id.tile_3);
		colorTiles[4] = (ImageView)findViewById(R.id.tile_4);
		colorTiles[5] = (ImageView)findViewById(R.id.tile_5);
		colorTiles[6] = (ImageView)findViewById(R.id.tile_6);
		colorTiles[7] = (ImageView)findViewById(R.id.tile_7);
		
		imageIndexMap = new HashMap<Integer, Integer>();
		imageIndexMap.put(R.id.tile_0, 0);
		imageIndexMap.put(R.id.tile_1, 1);
		imageIndexMap.put(R.id.tile_2, 2);
		imageIndexMap.put(R.id.tile_3, 3);
		imageIndexMap.put(R.id.tile_4, 4);
		imageIndexMap.put(R.id.tile_5, 5);
		imageIndexMap.put(R.id.tile_6, 6);
		imageIndexMap.put(R.id.tile_7, 7);
		
		listener = new OnClickListener() {
			public void onClick(View v) {
				dummyTile.setText("image tapped: "+imageIndexMap.get(v.getId()));
			}
		};
		
		for(int i=0; i<N_COLORS; i++) {
			colorTiles[i].setImageResource(R.drawable.ic_launcher);
			colorTiles[i].setOnClickListener(listener);
		}
		
		dummyTile = new TextView(this);
		dummyTile.setTextSize(40);

		centerView.addView(dummyTile);

		gameOver = false;
		waiting = false;
		interval = 100;

		timer = new Timer(1000, interval) {

			public void onTick(long millisUntilFinished) {
				//show timer animation here
			}

			public void onFinish() {
				gameOver = true;
				waiting = false;
				log("finished");
			}
		};
		
		initGameThread();
	}
	
	private void initGameThread() {
		
		gameThread = new Thread() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						startButton.setEnabled(false);
						currentLevel = 1;
						score = 0;
						timer.start();
					}
				});
				while(!gameOver && currentLevel <= TOTOL_LEVELS) {
					final Level level = new Level(currentLevel);
					int buf = level.bufferSize();
					final int n = level.getRandomIndex();
					runOnUiThread(new Runnable() {
						public void run() {
							println(level.toString()+" " + n);
						}
					});
					list = new LinkedList<Integer>();
					try {
						sleep(1000);
					} catch (InterruptedException e) {
						return;
					}
					for (int i = 0; i < buf; i++) {
						final int num = level.getRandomIndex();
						list.add(num);
						runOnUiThread(new Runnable() {
							public void run() {
								dummyTile.setText("" + num);
							}
						});
//						try {
//							sleep(1000);
//						} catch (InterruptedException e) {
//							return;
//						}
					}
					currentLevel++;
				}
			}
		};
	}

	private void handleUserTap(ImageView tile) {
		waiting = false;
		timer.cancel();
		int id = imageIndexMap.get(tile.getId());
		int displayNum = Integer
				.parseInt(dummyTile.getText().toString().trim());
		if (id == displayNum) {
			timeTakenPerStep = System.currentTimeMillis() - startTime;
		} else {
			gameOver = true;
		}
	}

	public void startGame(View view) {
		if(!gameOver) {
			gameThread.run();
			return;
		}
		startButton.setEnabled(false);
		currentLevel = 1;
		score = 0;
		timer.start();
		while (!gameOver && currentLevel<= TOTOL_LEVELS) {
			Level level = new Level(currentLevel);
			int buf = level.bufferSize();
			int n = level.getRandomIndex();
			println(level.toString()+" " + n);
			list = new LinkedList<Integer>();
			for (int i = 0; i < buf; i++) {
				int num = level.getRandomIndex();
				list.add(num);
				dummyTile.setText("" + num);
				try {
					Thread.sleep(level.timeout());
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			int step = 1;
			while (step < level.nsteps() && gameOver) {
				timer.reset(level.timeout(), interval);
				int num = level.getRandomIndex();
				list.add(num);
				dummyTile.setText("" + num);
				waiting = true;
				timer.start();
				startTime = System.currentTimeMillis();
				while (waiting);
				if (!gameOver) {
					level.addToScore(timeTakenPerStep);
					step++;
				}
			}
			if (!gameOver) {
				score += level.score();
				currentLevel++;
			}
		}
		if(gameOver) {
			log("LOSE!!: Lev: " + (currentLevel - 1) + " time: ");
		}
		else{
			log("WIN!!: Lev: " + (currentLevel - 1) + " time: ");
		}
	}

	public void log(String str) {
		Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
	}
	
	public void println(String str) {
		String text = console.getText().toString();
		console.setText(text + str + "\n");
	}
	
	public void setText(String str) {
		console.setText(str);
	}
}
