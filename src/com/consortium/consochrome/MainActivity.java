package com.consortium.consochrome;

import java.util.Calendar;

import java.util.concurrent.ExecutionException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.consortium.consochrome.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import static com.consortium.consochrome.Data.*;

public class MainActivity extends Activity {

	private CountDownTimer timer;
//	private MediaPlayer audioPlayer;

	private Button playTapTileButton;

	private ImageView centreImageView;
	private ImageView titleImageView;
	private TextView usernameDisplay;
	private RelativeLayout relativeLayout;

	private Button button_updates;
	private Button button_info;
	private Button button_leader;

	private Animation unreadAnimation;
	private boolean hasInternet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		appTypeface = Typeface.createFromAsset(getAssets(), "fonts/Averia-Regular.ttf");
		localData = getSharedPreferences(applicationPreference,
				Context.MODE_PRIVATE);
		calender = Calendar.getInstance();
		
		centreImageView = (ImageView) findViewById(R.id.centre_image_view);
		titleImageView = (ImageView) findViewById(R.id.title_image);
		relativeLayout = (RelativeLayout)findViewById(R.id.main_relative_layout);
		usernameDisplay = (TextView) findViewById(R.id.display_username);
		playTapTileButton = (Button) findViewById(R.id.button_play_tap_tiles);
		button_updates = (Button) findViewById(R.id.button_updates);
		button_info = (Button) findViewById(R.id.button_info);
		button_leader = (Button) findViewById(R.id.button_leaderboard);
		
		playTapTileButton.setTypeface(appTypeface,Typeface.BOLD);
		button_updates.setTypeface(appTypeface);
		button_leader.setTypeface(appTypeface);
		button_info.setTypeface(appTypeface);
		usernameDisplay.setTypeface(appTypeface,Typeface.BOLD);
		
		unreadAnimation = new AlphaAnimation(1, 0);
		unreadAnimation.setDuration(100);
		unreadAnimation.setInterpolator(new LinearInterpolator());
		unreadAnimation.setRepeatCount(Animation.INFINITE);
		unreadAnimation.setRepeatMode(Animation.REVERSE);

		//audioPlayer = MediaPlayer.create(getApplicationContext(),
			//	R.raw.bg_audio_main);
		//audioPlayer.start();
		init();
	}

	private void init() {
		int today = calender.get(Calendar.DAY_OF_MONTH);
		SharedPreferences.Editor editor = localData.edit();

		if (!localData.contains(KEY_NEW_INSTALL)) {
			editor.putBoolean(KEY_NEW_INSTALL, true);
			editor.putInt(KEY_TT_BEST_SCORE, 0);
			editor.putInt(KEY_TODAY,
					Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
			editor.putString(KEY_USERNAME, DEFAULT_USERNAME);
			editor.putString(KEY_UPDATES, "No Updates yet...");
			editor.putInt(KEY_UPDATES_READ, UPDATE_READ);
			editor.putInt(KEY_ALL_TIME_SIZE, 0);
			editor.putInt(KEY_DAILY_SIZE, 0);
			
			Util.displayOKAlert(this, "First time eh ??", "Simple Game-play: \n\nTap the tiles from bottom palette in the same order the empty tiles in the upper palette are filled... \n\nTap a Wrong tile or the upper palette runs out of empty tiles...BOOM! Game Over!\n\nRules can be view again in the info section :");
		}
		editor.commit();

		int lastDay = localData.getInt(KEY_TODAY, 0);

		if (!localData.contains(KEY_TT_DAILY_BEST_SCORE) || today > lastDay) {
			editor.putInt(KEY_TT_DAILY_BEST_SCORE, 0);
		}
		editor.commit();

		hasInternet = Util.haveNetworkConnection(this);

		if (hasInternet) {
			startBackgroundTasks();
		}
	}

	private void startBackgroundTasks() {
		new CheckDeviceUpdates().execute();
	}

	public void playtapTiles(View view) {
		Intent intent = new Intent(this, TapTilesActivity.class);
//		audioPlayer.pause();
		startActivity(intent);
	}

	public void hideItems() {
		relativeLayout.setVisibility(View.INVISIBLE);
	}

	public void showItems() {
		titleImageView.setVisibility(View.VISIBLE);
		relativeLayout.setVisibility(View.VISIBLE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		button_updates.clearAnimation();
		hideItems();
	}

	@Override
	protected void onResume() {
		super.onResume();
		//audioPlayer.start();
		int read = localData.getInt(KEY_UPDATES_READ, UPDATE_READ);
		if (read == UPDATE_UNREAD) {
			button_updates.startAnimation(unreadAnimation);
		}
		usernameDisplay.setText("Hello, "
				+ localData.getString(KEY_USERNAME, DEFAULT_USERNAME));
		
		showItems();
	}

	public void getLeaderBoard(View view) {
		Intent intent = new Intent(this, LeaderBoardActivity.class);
//		audioPlayer.pause();
		startActivity(intent);
	}

	public void getUpdates(View view) {
		button_updates.clearAnimation();
		SharedPreferences.Editor editor = localData.edit();
		editor.putInt(KEY_UPDATES_READ, UPDATE_READ);
		editor.commit();
		Intent intent = new Intent(this, UpdatesActivity.class);
//		audioPlayer.pause();
		startActivity(intent);
	}

	public void getInfo(View view) {
		titleImageView.setVisibility(View.INVISIBLE);
		Intent intent = new Intent(this,InfoActivity.class);
		startActivity(intent);
	}

	class CheckDeviceUpdates extends AsyncTask<String, String, Integer> {

		private boolean success;
		private String updates_new = "";

		protected Integer doInBackground(String... params) {
			try {
				String text = "";
				success = true;
				
				HttpClient httpclient = new DefaultHttpClient();
				
				HttpParams httpParams = httpclient.getParams();
				httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
				int timeoutConnection = 5000;
				int socketConnection = 10000;
				HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
				HttpConnectionParams.setSoTimeout(httpParams, socketConnection);
				HttpConnectionParams.setTcpNoDelay(httpParams, true);

				HttpPost httppost = new HttpPost(url_updates);
				HttpResponse response = httpclient.execute(httppost);
				text = EntityUtils.toString(response.getEntity());

				int index = text.indexOf("<!--");
				if (index > 0) {
					text = text.substring(0, index);
				}
				updates_new = text.trim();
			} catch (ConnectTimeoutException e) {
				success = false;
			} catch (Exception e) {
				success = false;
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			if (success) {
				String updates_old = localData.getString(KEY_UPDATES, "");
				if (!updates_old.equals(updates_new)) {
					SharedPreferences.Editor editor = localData.edit();
					editor.putString(KEY_UPDATES, updates_new);
					editor.putInt(KEY_UPDATES_READ, UPDATE_UNREAD);
					editor.commit();
					button_updates.startAnimation(unreadAnimation);
				}
			}
		}
	}
}
