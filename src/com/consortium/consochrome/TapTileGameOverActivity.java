package com.consortium.consochrome;

import static com.consortium.consochrome.Data.DEVICE_REGISTRATION_SUCCESSFUL;
import static com.consortium.consochrome.Data.KEY_TT_BEST_SCORE;
import static com.consortium.consochrome.Data.KEY_TT_DAILY_BEST_SCORE;
import static com.consortium.consochrome.Data.MAC_ID_NOT_FOUND;
import static com.consortium.consochrome.Data.SCORE_UPDATED_SUCCESSFUL;
import static com.consortium.consochrome.Data.UNEXPECTED_ERROR;
import static com.consortium.consochrome.Data.USERNAME_ALREADY_EXISTS;
import static com.consortium.consochrome.Data.localData;
import static com.consortium.consochrome.Data.tapTileActivity;
import static com.consortium.consochrome.Data.tapTileOverActivity;
import static com.consortium.consochrome.Data.url_device_register;
import static com.consortium.consochrome.Data.url_submit_score;

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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import com.consortium.consochrome.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class TapTileGameOverActivity extends Activity {

	private int bestScore;
	private int dailyBestScore;
	private String macAddress;
	private ProgressDialog pDialog;
	private ImageView updateButton;
	private boolean confirmSubmit;

	private SubmitScoreTask submitTask;
	private RegisterDeviceTask regTask;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tap_tile_game_over);
		tapTileOverActivity = this;
		updateButton = (ImageView) findViewById(R.id.submit_button);
		TextView topTextView = (TextView) findViewById(R.id.highlight_new_update);
		topTextView.setTypeface(Data.appTypeface,Typeface.BOLD);
		TextView view = (TextView) findViewById(R.id.textview_gameover);
		view.setTypeface(Data.appTypeface,Typeface.BOLD);
		bestScore = localData.getInt(KEY_TT_BEST_SCORE, 0);
		dailyBestScore = localData.getInt(KEY_TT_DAILY_BEST_SCORE, 0);
		topTextView.setText("All time: " + bestScore + "\nToday's: "
				+ dailyBestScore);

	}

	public void backAction(View view) {
		onBackPressed();
	}

	public void replayAction(View view) {
		finish();
		tapTileActivity.startGame();
	}

	public void submitAction(View view) {
		macAddress = getDeviceMacAddress();
		boolean deviceHasInternetConnection = haveNetworkConnection();
		confirmSubmit = true;
		if (deviceHasInternetConnection) {
			new AlertDialog.Builder(tapTileOverActivity)
					.setIcon(android.R.drawable.ic_dialog_alert)
					.setTitle("Confirm Submit")
					.setMessage(
							"Today's best score: " + dailyBestScore
									+ "\nSubmit score?")
					.setNegativeButton("Cancel",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							})
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									new SubmitScoreTask(tapTileOverActivity,
											macAddress, dailyBestScore)
											.execute();
								}
							}).show();

		} else {
			showConnectionProblemDialog();
		}

	}

	public void onBackPressed() {
		super.onBackPressed();
		finish();
		// tapTileActivity.finish();
	}

	private boolean haveNetworkConnection() {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo[] netInfo = cm.getAllNetworkInfo();
		for (NetworkInfo ni : netInfo) {
			if (ni.getTypeName().equalsIgnoreCase("WIFI"))
				if (ni.isConnected())
					haveConnectedWifi = true;
			if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
				if (ni.isConnected())
					haveConnectedMobile = true;
		}
		return haveConnectedWifi || haveConnectedMobile;
	}

	private String getDeviceMacAddress() {
		BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
		boolean state = adapter.isEnabled();
		String addr = adapter.getAddress();
		if(addr.equals("") && state == false) {
			adapter.enable();
			while(addr.equals("")) {
				addr = adapter.getAddress();
			}
			adapter.disable();
		}
		return addr;
	}

	private void showSuccessDialog(String message) {
		new AlertDialog.Builder(this).setTitle("Success").setMessage(message)
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).setIcon(android.R.drawable.ic_dialog_info).show();
	}

	private void showConnectionProblemDialog() {
		new AlertDialog.Builder(this)
				.setTitle("Connection Error")
				.setMessage(
						"No Internet connection available right now\nTurn on Mobile Data or connect to a WiFi network and try again")
				.setPositiveButton("Try Again",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								submitAction(updateButton);
							}
						})
				.setCancelable(false)
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}

	private void showsubmissionErrorDialog() {
		new AlertDialog.Builder(this).setTitle("Connection Interrupted")
				.setMessage("Connect to a good internet connection and try again later")
				.setPositiveButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
					}
				}).setIcon(android.R.drawable.ic_dialog_alert).show();
	}

	private void showDeviceRegDialog() {
		LinearLayout view = new LinearLayout(this);
		view.setOrientation(LinearLayout.VERTICAL);

		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		params.setMargins(20, 10, 20, 10);

		final EditText usernameIp = new EditText(this);
		usernameIp.setHint("Username");
		usernameIp.setLayoutParams(params);

		final EditText phoneNoIp = new EditText(this);
		phoneNoIp.setInputType(InputType.TYPE_CLASS_PHONE);
		phoneNoIp.setHint("phone number");
		phoneNoIp.setLayoutParams(params);

		view.addView(usernameIp);
		view.addView(phoneNoIp);

		/**
		 * Displaying registration form
		 */
		final AlertDialog.Builder builder = new AlertDialog.Builder(this)
				.setIcon(android.R.drawable.ic_dialog_alert)
				.setTitle("Unregistered Device")
				.setCancelable(false)
				.setMessage(
						"Your device is not registered to submit the score.\nPlease register to submit")
				.setNegativeButton("Cancel",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

							}
						})
				.setPositiveButton("Register",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {

								final String phoneNo = phoneNoIp.getText()
										.toString();
								final String username = usernameIp.getText()
										.toString();
								boolean validNo = isValidPhoneNo(phoneNo);

								if (!validNo
										|| username.equals("")
										|| username
												.equals(Data.DEFAULT_USERNAME)) {
									String msg = "Username field cannot be empty";
									if (username
											.endsWith(Data.DEFAULT_USERNAME))
										msg = "Username cannot be "
												+ Data.DEFAULT_USERNAME
												+ ", its is default username for unregistered devices";
									else if (!validNo)
										msg = "Invalid phone number format\nPlease enter a 10 digit phone number";
									// if user gives in valid input
									// poping confirm dialog

									new AlertDialog.Builder(tapTileOverActivity)
											.setTitle("Invalid Input")
											.setMessage(msg)
											.setIcon(
													android.R.drawable.ic_dialog_alert)
											.setPositiveButton(
													"OK",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int which) {
															showDeviceRegDialog();
														}
													}).show();
								}

								else {

									// if input is valid then poping dialog for
									// confirmation

									new AlertDialog.Builder(tapTileOverActivity)
											.setIcon(
													android.R.drawable.ic_dialog_alert)
											.setTitle("Confirm Registeration")
											.setMessage(
													"Once a device is registered with a username, it cannot be changed\n"
															+ "Confirm?")
											.setNegativeButton(
													"Cancel",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int which) {

														}
													})
											.setPositiveButton(
													"Yes",
													new DialogInterface.OnClickListener() {
														public void onClick(
																DialogInterface dialog,
																int which) {
															new RegisterDeviceTask(
																	tapTileOverActivity,
																	macAddress,
																	username,
																	phoneNo,
																	dailyBestScore)
																	.execute();

														}
													}).show();
								}
							}

						});

		builder.setView(view);
		// dialog.setContentView(R.layout.dialog_device_reg);
		builder.show();
	}

	private boolean isValidPhoneNo(String str) {
		if (str.length() != 10)
			return false;
		for (int i = 0; i < 10; i++) {
			if (!Character.isDigit(str.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	class SubmitScoreTask extends AsyncTask<String, String, Integer> {

		private ProgressDialog pDialog;

		private static final String TAG_MAC_ID = "mac1";
		private static final String TAG_SCORE_ID = "score1";
		private static final String TAG_SUCCESS = "success";
		private static final String TAG_USERNAME = "username";

		private Context context;
		private String macID;
		private int score;
		private boolean timeout;

		public SubmitScoreTask(Context context, String macID, int score) {
			this.context = context;
			this.macID = macID;
			this.score = score;
		}

		@Override
		protected Integer doInBackground(String... params) {
			int success = MAC_ID_NOT_FOUND;
			String username = Data.DEFAULT_USERNAME;
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();
			paramList.add(new BasicNameValuePair(TAG_MAC_ID, macID));
			paramList.add(new BasicNameValuePair(TAG_SCORE_ID, "" + score));

			try {
				timeout = false;

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

				HttpPost httppost = new HttpPost(url_submit_score);
				httppost.setEntity(new UrlEncodedFormEntity(paramList));
				HttpResponse response = httpclient.execute(httppost);
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
				if (success == SCORE_UPDATED_SUCCESSFUL) {
					username = json.getString(TAG_USERNAME);
				}

			} catch (ConnectTimeoutException | SocketTimeoutException se) {
				timeout = true;
			} catch (Exception e) {
				success = UNEXPECTED_ERROR;
			}
			LocalMap.getInstance().put("submit_score_status", success);
			LocalMap.getInstance().put(TAG_USERNAME, username);
			return success;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(context);
			pDialog.setMessage("Submitting score... please wait");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected void onPostExecute(Integer result) {
			pDialog.dismiss();
			if (timeout == true) {
				Util.displayConnectionTimeoutAlert2(TapTileGameOverActivity.this);
			} else {
				int success_code = (Integer) LocalMap.getInstance().get(
						"submit_score_status");
				String username = (String) LocalMap.getInstance().get(
						TAG_USERNAME);
				if (success_code == SCORE_UPDATED_SUCCESSFUL) {
					SharedPreferences.Editor editor = localData.edit();
					editor.putString(Data.KEY_USERNAME, username);
					editor.commit();
					showSuccessDialog("username: " + username
							+ "\ntoday's best score: " + dailyBestScore);
				} else if (success_code == MAC_ID_NOT_FOUND) {
					showDeviceRegDialog();
				} else {
					showsubmissionErrorDialog();
				}
			}
		}
	}

	class RegisterDeviceTask extends AsyncTask<String, String, Integer> {

		private static final String TAG_USERNAME_ID = "username1";
		private static final String TAG_MAC_ID = "mac1";
		private static final String TAG_PHONE_NO_ID = "phoneno1";
		private static final String TAG_SCORE_ID = "score1";
		private static final String TAG_SUCCESS = "success";

		private Context context;
		private boolean timeout;
		private String macID;
		private String username;
		private String phoneNumber;
		private ProgressDialog pDialog;
		private int score;

		public RegisterDeviceTask(Context context, String mID, String uname,
				String pNo, int scr) {
			this.context = context;
			macID = mID;
			username = uname;
			phoneNumber = pNo;
			score = scr;
		}

		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(context);
			pDialog.setMessage("Registering Device with username " + username
					+ "... please wait");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected Integer doInBackground(String... params) {
			int success = USERNAME_ALREADY_EXISTS;
			List<NameValuePair> paramList = new ArrayList<NameValuePair>();

			paramList
					.add(new BasicNameValuePair(TAG_USERNAME_ID, "" + username));
			paramList.add(new BasicNameValuePair(TAG_MAC_ID, macID));
			paramList.add(new BasicNameValuePair(TAG_PHONE_NO_ID, ""
					+ phoneNumber));
			paramList.add(new BasicNameValuePair(TAG_SCORE_ID, "" + score));

			try {
				timeout = false;
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

				HttpPost httppost = new HttpPost(url_device_register);
				httppost.setEntity(new UrlEncodedFormEntity(paramList));
				HttpResponse response = httpclient.execute(httppost);
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
			} catch (ConnectTimeoutException | SocketTimeoutException se) {
				timeout = true;
			} catch (Exception e) {
				success = UNEXPECTED_ERROR;
			}
			LocalMap.getInstance().put("reg_dev_status", success);
			return success;
		}

		protected void onPostExecute(Integer result) {
			pDialog.dismiss();
			if (timeout == true) {
				Util.displayConnectionTimeoutAlert2(TapTileGameOverActivity.this);
			} else {
				int successCode = (Integer) LocalMap.getInstance().get(
						"reg_dev_status");
				if (successCode == DEVICE_REGISTRATION_SUCCESSFUL) {
					SharedPreferences.Editor editor = localData.edit();
					editor.putString(Data.KEY_USERNAME, username);
					editor.commit();
					showSuccessDialog("Device Registration successful\nusername: "
							+ username);
				} else if (successCode == USERNAME_ALREADY_EXISTS) {

					new AlertDialog.Builder(tapTileOverActivity)
							.setTitle("Username Exists")
							.setMessage(
									"An another device is registered with "
											+ username
											+ ", please select a different username")
							.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int which) {
											showDeviceRegDialog();
										}
									})
							.setIcon(android.R.drawable.ic_dialog_info).show();

				} else {
					showsubmissionErrorDialog();
				}
			}
		}

	}
}
