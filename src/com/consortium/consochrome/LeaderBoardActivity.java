package com.consortium.consochrome;

import static com.consortium.consochrome.Data.*;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import com.consortium.consochrome.R;
import com.consortium.consochrome.R.color;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableLayout.LayoutParams;
import android.widget.Button;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class LeaderBoardActivity extends Activity {

	private static final String TAG_SCORE_ID = "score1";
	private static final String TAG_SUCCESS = "success";
	private static final String TAG_USERNAME = "username1";
	private static final String TAG_USERS = "users";
	
	private int icon_all_time = R.drawable.ic_action_refresh_all_time;
	private int icon_daily = R.drawable.ic_action_refresh_daily;
	private Button allTimeButton;
	private Button dailyButton;
	private ImageView refreshButton;
	private TextView header;
	private int padding = 20;
	private int color_id;
	private JSONArray JSONList = null;
	private String url = url_leaderboard_daily;
	private ArrayList<String> usersList;
	private ArrayList<Integer> scores;
	private String key_size;
	private String[] keys_user;
	private String[] keys_score;
	private TableLayout table;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_leader_board);
		table = (TableLayout) findViewById(R.id.leaderboard_table);

		allTimeButton = (Button) findViewById(R.id.button1);
		allTimeButton.setTypeface(appTypeface, Typeface.BOLD);

		dailyButton = (Button) findViewById(R.id.button2);
		dailyButton.setTypeface(appTypeface, Typeface.BOLD);

		refreshButton = (ImageView) findViewById(R.id.refresh_list);

		header = (TextView) findViewById(R.id.leader_head);
		header.setTypeface(appTypeface, Typeface.BOLD);

		dailyLeaderBoard(dailyButton);
	}

	public void dailyLeaderBoard(View view) {
		url = url_leaderboard_daily;
		color_id = getResources().getColor(R.color.color_daily);
		key_size = KEY_DAILY_SIZE;
		keys_user = KEY_USERS_DAILY;
		keys_score = KEY_SCORES_DAILY;
		dailyButton.setEnabled(false);
		allTimeButton.setEnabled(true);
		header.setText("Daily LeaderBoard");
		header.setTextColor(color_id);
		refreshButton.setImageResource(icon_daily);
		refresh(view);
	}

	public void allTimeLeaderBoard(View view) {
		url = url_leaderboard_all_time;
		color_id = getResources().getColor(R.color.color_all_time);
		key_size = KEY_ALL_TIME_SIZE;
		keys_user = KEY_USERS_ALL_TIME;
		keys_score = KEY_SCORES_ALL_TIME;
		allTimeButton.setEnabled(false);
		dailyButton.setEnabled(true);
		header.setText("All time LeaderBoard");
		header.setTextColor(color_id);
		refreshButton.setImageResource(icon_all_time);
		refresh(view);
	}

	public void refresh(View view) {
		new PopulateLeaderBoard().execute();
	}

	class PopulateLeaderBoard extends AsyncTask<String, String, Integer> {

		private ProgressDialog pDialog;
		private boolean timeout;

		@Override
		protected Integer doInBackground(String... params) {
			int success = 2;
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();

			try {
				timeout = false;
				if (!Util.haveNetworkConnection(LeaderBoardActivity.this)) {
					throw new Exception();
				}
				HttpClient httpclient = new DefaultHttpClient();

				HttpParams httpParams = httpclient.getParams();
				httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION,
						HttpVersion.HTTP_1_1);
				int timeoutConnection = 4000;
				int socketConnection = 6000;
				HttpConnectionParams.setConnectionTimeout(httpParams,
						timeoutConnection);
				HttpConnectionParams.setSoTimeout(httpParams, socketConnection);
				HttpConnectionParams.setTcpNoDelay(httpParams, true);

				AndroidHttpClient client = AndroidHttpClient
						.newInstance("myClient");
				String paramString = URLEncodedUtils.format(paramList, "utf-8");
				url += "?" + paramString;
				HttpGet httpGet = new HttpGet(url);
				HttpResponse response = httpclient.execute(httpGet);
				HttpEntity httpEntity = response.getEntity();
				InputStream is = httpEntity.getContent();

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is, "iso-8859-1"), 8);
				StringBuilder sb = new StringBuilder();
				String line = null;
				while ((line = reader.readLine()) != null) {
					sb.append(line + "\n");
				}
				is.close();
				JSONObject json = new JSONObject(sb.toString());

				LocalMap.getInstance().put("json", json.toString());
				success = json.getInt(TAG_SUCCESS);
				usersList = new ArrayList<String>();
				scores = new ArrayList<Integer>();
				if (success == 1) {
					JSONList = json.getJSONArray(TAG_USERS);
					for (int i = 0; i < JSONList.length(); i++) {
						JSONObject obj = JSONList.getJSONObject(i);
						String username = obj.getString(TAG_USERNAME);
						int score = obj.getInt(TAG_SCORE_ID);
						usersList.add(username);
						scores.add(score);
					}
				} else {

				}
			} catch (ConnectTimeoutException e) {
				timeout = true;
			} catch (SocketTimeoutException e) {
				timeout = true;
			} catch (Exception e) {
				success = 2;
			}
			LocalMap.getInstance().put("l_s", success);
			return success;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(LeaderBoardActivity.this);
			pDialog.setMessage("fetching results... please wait");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected void onPostExecute(Integer result) {
			pDialog.dismiss();
			int success = (Integer) LocalMap.getInstance().get("l_s");
			if (timeout == true || success == 2) {
				if (timeout == true)
					showSlowInternetDialog();
				else {
					showNoInternetConnectionDialog();
				}
				usersList = new ArrayList<String>();
				scores = new ArrayList<Integer>();
				int n = localData.getInt(key_size, 0);
				for (int i = 0; i < n; i++) {
					usersList.add(localData.getString(keys_user[i], ""));
					scores.add(localData.getInt(keys_score[i], 0));
				}
			}
			SharedPreferences.Editor editor = localData.edit();
			editor.putInt(key_size, usersList.size());
			table.removeAllViews();
			TableRow header = new TableRow(LeaderBoardActivity.this);
			header.addView(getCell("Rank"));
			header.addView(getCell("Username"));
			header.addView(getCell("Score"));
			table.addView(header);
			for (int i = 0; i < usersList.size(); i++) {
				TableRow row = new TableRow(LeaderBoardActivity.this);

				TextView rankView = getCell("#" + (i + 1));
				row.addView(rankView);

				TextView userNameView = getCell(usersList.get(i));
				row.addView(userNameView);
				editor.putString(keys_user[i], usersList.get(i));

				TextView userScoreView = getCell("" + scores.get(i));
				row.addView(userScoreView);
				editor.putInt(keys_score[i], scores.get(i));

				table.addView(row);
			}
			editor.commit();
		}
	}

	private TextView getCell(String str) {
		TextView cell = new TextView(LeaderBoardActivity.this);
		cell.setTextSize(20);
		cell.setTypeface(appTypeface, Typeface.BOLD);
		cell.setPadding(padding, padding, padding, padding);
		cell.setText(str);
		cell.setTextColor(color_id);
		return cell;
	}

	private void showSlowInternetDialog() {
		new AlertDialog.Builder(LeaderBoardActivity.this)
				.setTitle("Connection Timeout")
				.setMessage(
						"You seem to have a slow internet connection.\nDisplaying last refreshed leaderboard. Try again to see the recent updates in leaderboard")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).setCancelable(false)
				.setIcon(android.R.drawable.ic_dialog_alert).show();
	}

	private void showNoInternetConnectionDialog() {
		final Dialog dialog = 
		new AlertDialog.Builder(LeaderBoardActivity.this)
				.setTitle("Connection Error")
				.setMessage(
						"No Internet connection available right now\nTurn on Mobile Data or connect to a WiFi network to to see the recent updates in leaderboard")
				.setPositiveButton("Try Again",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								refresh(refreshButton);
							}
						})
				.setCancelable(false)
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).setIcon(android.R.drawable.ic_dialog_alert).create();
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.show();
	}
	
	public void onBackPressed() {
        super.onBackPressed();
        this.finish();
}

}
