package com.consortium.consochrome;

import com.consortium.consochrome.R;

import android.R.color;
import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

public class InfoActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.anim.appear, R.anim.disappear);
		setContentView(R.layout.activity_info);
		Animation translatebu= AnimationUtils.loadAnimation(this, R.anim.appear);
		
		TextView view;
		view = (TextView) findViewById(R.id.info_header);
		view.setTypeface(Data.appTypeface,Typeface.BOLD);
		view.startAnimation(translatebu);
		
		view = (TextView) findViewById(R.id.info_sub_title);
		view.setTypeface(Data.appTypeface,Typeface.BOLD);
		view.startAnimation(translatebu);
		
		view = (TextView) findViewById(R.id.info_text1);
		view.setTypeface(Data.appTypeface,Typeface.NORMAL);
		view.startAnimation(translatebu);
		
		view = (TextView) findViewById(R.id.info_text2);
		view.setTypeface(Data.appTypeface,Typeface.NORMAL);
		view.startAnimation(translatebu);
		
		view = (TextView) findViewById(R.id.info_text3);
		view.setTypeface(Data.appTypeface,Typeface.NORMAL);
		view.startAnimation(translatebu);
		
		view = (TextView) findViewById(R.id.info_text4);
		view.setTypeface(Data.appTypeface,Typeface.NORMAL);
		view.startAnimation(translatebu);
		
		view = (TextView) findViewById(R.id.info_text5);
		view.setTypeface(Data.appTypeface,Typeface.NORMAL);
		view.startAnimation(translatebu);
		
		view = (TextView) findViewById(R.id.info_text6);
		view.setTypeface(Data.appTypeface,Typeface.NORMAL);
		view.startAnimation(translatebu);
		
		view = (TextView) findViewById(R.id.info_text7);
		view.setTypeface(Data.appTypeface,Typeface.BOLD);
		view.startAnimation(translatebu);
		
		view = (TextView) findViewById(R.id.info_text8);
		view.setTypeface(Data.appTypeface,Typeface.NORMAL);
		view.startAnimation(translatebu);
		
		view = (TextView) findViewById(R.id.info_text9);
		view.setTypeface(Data.appTypeface,Typeface.BOLD);
		view.startAnimation(translatebu);
		
		view = (TextView) findViewById(R.id.info_text10);
		view.setTypeface(Data.appTypeface, Typeface.BOLD);
		view.setText(Html
				.fromHtml("<a href=\"http://www.consortiumvnit.com\">www.consortiumvnit.com</a><br> "));
		view.setLinkTextColor(getResources().getColor(color.holo_blue_dark));
		view.setMovementMethod(LinkMovementMethod.getInstance());
		view.startAnimation(translatebu);
		
		view = (TextView) findViewById(R.id.info_text11);
		view.setTypeface(Data.appTypeface,Typeface.BOLD);
		view.startAnimation(translatebu);
		
		view = (TextView) findViewById(R.id.info_text12);
		view.setTypeface(Data.appTypeface,Typeface.NORMAL);
		view.startAnimation(translatebu);
		
		view = (TextView) findViewById(R.id.info_text13);
		view.setTypeface(Data.appTypeface, Typeface.BOLD);
		view.setText(Html
				.fromHtml("<a href=\"https://github.com/maharshi95/galvin/tree/revert-1-master\">fork me on github :)</a> "));
		view.setLinkTextColor(getResources().getColor(color.holo_blue_dark));
		view.setMovementMethod(LinkMovementMethod.getInstance());
		view.startAnimation(translatebu);
	}
}
