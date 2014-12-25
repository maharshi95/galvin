package com.sanskimars.consortium;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Process;
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

	/**
	 * UI componenets
	 */
	private Button startButton;
	private RelativeLayout centerView;
	private ImageView consoLogo;
	private ImageView[] colorTiles;
	private TextView console;
	private TextView dummyTile;
	
	/**
	 * Game control components
	 */
	//Countdown timer used for each step of every level
	private Timer timer;
	private CountDownTimer cdtimer;
	
	//listener for the color tiles
	private OnClickListener listener;
	
	//Thread which runs the game control of popping the questions 
	//of every level and starting and stopping the timer
	private Thread gameThread;
	
	private Thread dummyThread;
	
	
	/**
	 * Game State data
	 */
	//This variable goes true when player runs out of time to answer a question
	private boolean gameOver;
	
	//This variable goes true when the timer starts and goes false when timer finishes 
	//or user gives answer to the question
	private boolean waiting;
	
	//The cumulative time taken by the user in millisec to solve the questions answered
	private long score;
	
	// variables used to keep track of time taken by user to solve each answer
	private long startTime;
	private long timeTakenPerStep;
	
	//timer ticking interval
	private long interval;
	
	//number depecting the current level 
	private int currentLevel;
	private int step;
	private HashMap<Integer, Integer> tileIndexMap;
	private HashMap<Integer, Integer> colorIndexMap;
	private HashMap<Integer, Integer> logoIndexMap;
	
	private LinkedList<Integer> indexList;

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
		
		tileIndexMap = new HashMap<Integer, Integer>();
		tileIndexMap.put(R.id.tile_0, 0);
		tileIndexMap.put(R.id.tile_1, 1);
		tileIndexMap.put(R.id.tile_2, 2);
		tileIndexMap.put(R.id.tile_3, 3);
		tileIndexMap.put(R.id.tile_4, 4);
		tileIndexMap.put(R.id.tile_5, 5);
		tileIndexMap.put(R.id.tile_6, 6);
		tileIndexMap.put(R.id.tile_7, 7);
		
		listener = new OnClickListener() {
			public void onClick(View v) {
				handleUserTap((ImageView)v);
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
		interval = 300;

		timer = new Timer(2000, interval,"") {

			public void onTick(long millisUntilFinished) {
				println("timer: " + timer.tag()+ " " + millisUntilFinished);
			}

			public void onFinish() {
				gameOver = true;
				waiting = false;
				println("timer: " + timer.tag()+ " finished");
			}
		};
		
		initGameThread();
		//timer.start();
		
	}
	
	private void handleUserTap(ImageView tile) {
		waiting = false;
		cdtimer.cancel();
		println("timer cancelled");
		int id = tileIndexMap.get(tile.getId());
		int displayNum = indexList.remove();
		if (id == displayNum) {
			timeTakenPerStep = System.currentTimeMillis() - startTime;
			setText("");
			println("yeah, right choice");
		} else {
			gameOver = true;
			println("oops! wrong choice");
		}
	}
	
	private void initGameThread() {
		
		dummyThread = new Thread(new Runnable() {
			public void run() {
				runOnUiThread(new Runnable() {
					public void run() {
						timer.reset(5000, 100, "hello:");
						timer.start();
					}
				});
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {}
				runOnUiThread(new Runnable() {
					public void run() {
						timer.cancel();
						timer.reset(2000, 200, "hello2");
						timer.start();
					}
				});
			}
		});
		
		//implementation of the gameThread to pop questions 
		//and start timer for each step of every level
		gameThread = new Thread() {
			public void run() {
				//initializing score,level and disabling the start button
				runOnUiThread(new Runnable() {
					public void run() {
						startButton.setEnabled(false);
						currentLevel = 1;
						score = 0;
					}
				});
				//looping while the game is not over or player completes all the leves
				while(!gameOver && currentLevel <= TOTOL_LEVELS) {
					final Level level = new Level(currentLevel);
					final int buffer = level.bufferSize();
					runOnUiThread(new Runnable() {
						public void run() {
							println("starting Lv "+currentLevel + " Buffer size: " + buffer);
						}
					});
					
					//list to maintain the buffer colors
					indexList = new LinkedList<Integer>();
					
					//displaying the initial colors (of buffer size)  at the start of the level with inteval of 1.5 sec
					
					for (int i = 0; i < buffer; i++) {
						final int colorIndex = level.getRandomIndex();
						indexList.add(colorIndex);
						runOnUiThread(new Runnable() {
							public void run() {
								dummyTile.setText("" + colorIndex);
							}
						});
						try {
							sleep(1500);
						} catch (InterruptedException e) {
							return;
						}
					}//for
					
					runOnUiThread(new Runnable() {
						public void run() {
							println("begin...");
						}
					});
					step = 1;
					//looping through each step of the level till the game is not over or level is not complete
					while (step < level.nsteps() && !gameOver) {
						runOnUiThread(new Runnable() {
							public void run() {
								final int colorIndex = level.getRandomIndex();
								indexList.add(colorIndex);
								dummyTile.setText("" + colorIndex);
								cdtimer = new CountDownTimer(level.timeout(),interval) {
									public void onTick(long millisUntilFinished) {
										onTickAction(millisUntilFinished);
									}
									
									public void onFinish() {
										onFinishAction();
									}
								};
								cdtimer.start();
							}
						});
						//colorIndex added to the end of the buffer and displayed on the center view
						waiting = true;
						startTime = System.currentTimeMillis();
						while (waiting); //waiting until the user clicks or timer interupts
						runOnUiThread(new Runnable() {
							public void run() {
								println("waiting over");
							}
						});
						if (!gameOver) {
							level.addToScore(timeTakenPerStep);
							step++;
						}else {
							runOnUiThread(new Runnable() {
								@Override
								public void run() {
									println("inner loop: game is over!!");
								}
							});
						}
					}
					runOnUiThread(new Runnable() {
						public void run() {
							println("Lv "+currentLevel + " complete...");
						}
					});
					if (!gameOver) {
						score += level.score();
						currentLevel++;
					}else {
						runOnUiThread(new Runnable() {
							@Override
							public void run() {
								println("outer loop: game is over!!");
							}
						});
					}
				}
				String message;
				if(gameOver) {
					message = "LOSE!!: Lev: " + (currentLevel - 1) + " time: "+score;
				}
				else{
					message = "WIN!!: Lev: " + (currentLevel - 1) + " time: "+score;
				}
				final String msg = message;
				runOnUiThread(new Runnable() {
					public void run() {
						println(msg);
					}
				});
			}
		};
	}

	synchronized public void onFinishAction() {
		gameOver = true;
		waiting = false;
		println("timer: " + timer.tag()+ " finished");
	}
	
	synchronized public void onTickAction(long timeLeft) {
		println("timer: L:" + currentLevel + " S:" +step + " T: "+ timeLeft + "\n" + indexList);
		if(gameOver) {
			println("onTick: gameOver");
		}
		else {
			println("onTick: game not over");
		}
	}

	public void startGame(View view) {
		gameThread.start();
		//dummyThread.start();
	}

	public static void log(String str) {
		Toast.makeText(null, str, Toast.LENGTH_SHORT).show();
	}
	
	public void println(String str) {
		String text = console.getText().toString();
		console.setText(text + str + "\n");
	}
	
	public void setText(String str) {
		console.setText(str);
	}
}
