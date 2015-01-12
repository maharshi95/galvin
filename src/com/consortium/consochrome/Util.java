package com.consortium.consochrome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Util {

	private Util() {
	}
	
	public static void displayConnectionTimeoutAlert(Context context) {
		new AlertDialog.Builder(context)
		.setTitle("Connection Timeout Error")
		.setMessage("You seem to have a slow internet connection, refresh to try again")
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
					}
				})
		.setCancelable(false)
		.setIcon(android.R.drawable.ic_dialog_alert).show();
	}
	
	public static void displayConnectionTimeoutAlert2(Context context) {
		new AlertDialog.Builder(context)
		.setTitle("Connection Timeout Error")
		.setMessage("You seem to have a slow internet connection, try again later")
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
					}
				})
		.setCancelable(false)
		.setIcon(android.R.drawable.ic_dialog_alert).show();
	}
	
	public static void displayUnexpectedErrorAlert(Context context) {
		new AlertDialog.Builder(context)
		.setTitle("Error")
		.setMessage("Something went wrong, try again later")
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
					}
				})
		.setCancelable(false)
		.setIcon(android.R.drawable.ic_dialog_alert).show();
	}
	
	public static void displayOKAlert(Context context,String title,String msg) {
		new AlertDialog.Builder(context)
		.setTitle(title)
		.setMessage(msg)
		.setPositiveButton("OK",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog,
							int which) {
					}
				})
		.setCancelable(false)
		.setIcon(android.R.drawable.ic_dialog_alert).show();
	}
	
	public static boolean haveNetworkConnection(Context context) {
		boolean haveConnectedWifi = false;
		boolean haveConnectedMobile = false;

		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
}
