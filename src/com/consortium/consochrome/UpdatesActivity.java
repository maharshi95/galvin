package com.consortium.consochrome;

import static com.consortium.consochrome.Data.KEY_UPDATES;
import static com.consortium.consochrome.Data.localData;
import static com.consortium.consochrome.Data.url_updates;

import java.net.SocketTimeoutException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;

import com.consortium.consochrome.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.TextView;

public class UpdatesActivity extends Activity {

	private TextView content;
	private TextView header;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_updates);
		content = (TextView) findViewById(R.id.updates_content);
		header = (TextView)findViewById(R.id.updates_head);
		header.setTypeface(Data.appTypeface,Typeface.BOLD);
		content.setTypeface(Data.appTypeface);
		String text = localData.getString(KEY_UPDATES, "No Updates yet...");
		content.setText(text);
	}
	
	public void refresh(View view) {
		if (haveNetworkConnection()) {
			new GetUpdatesTask().execute();
		} else {
			new AlertDialog.Builder(this)
					.setTitle("Connection Error")
					.setMessage(
							"No Internet connection available right now\nTurn on Mobile Data or connect to a WiFi network for getting latest updates")
					.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
								}
							})
					.setCancelable(false)
					.setIcon(android.R.drawable.ic_dialog_alert).show();
		}
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

	class GetUpdatesTask extends AsyncTask<String, String, Integer> {

		private ProgressDialog pDialog;
		private boolean timeout;
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(UpdatesActivity.this);
			pDialog.setMessage("fetching updates... please wait");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		public GetUpdatesTask() {
			// TODO Auto-generated constructor stub
		}
		@Override
		protected Integer doInBackground(String... params) {
			try {
				String text = "";
				timeout = false;
				
				HttpClient httpclient = new DefaultHttpClient();
				
				HttpParams httpParams = httpclient.getParams();
				httpParams.setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
				int timeoutConnection = 4000;
				int socketConnection = 6000;
				HttpConnectionParams.setConnectionTimeout(httpParams, timeoutConnection);
				HttpConnectionParams.setSoTimeout(httpParams, socketConnection);
				HttpConnectionParams.setTcpNoDelay(httpParams, true);
				
				HttpPost httppost = new HttpPost(url_updates);
				HttpResponse response = httpclient.execute(httppost);
				text = EntityUtils.toString(response.getEntity());
		
				int index = text.indexOf("<!--");
				if(index > 0) {
					text = text.substring(0, index);
				}
				SharedPreferences.Editor editor = localData.edit();
				editor.putString(KEY_UPDATES, text.trim());
				editor.commit();
				
			}	catch(ConnectTimeoutException e) {
				timeout = true;
			}	catch(SocketTimeoutException e) {
				timeout = true;
			}	catch (Exception e) {
				
			}
			return 0;
		}

		@Override
		protected void onPostExecute(Integer result) {
			pDialog.dismiss();
			if(timeout) {
				Util.displayConnectionTimeoutAlert(UpdatesActivity.this);
			}
			else {
				String text = localData.getString(KEY_UPDATES, "No Updates yet...");
				content.setText(text);
			}	
		}
	}
	
	public void onBackPressed() {
        super.onBackPressed();
        this.finish();
}
}
