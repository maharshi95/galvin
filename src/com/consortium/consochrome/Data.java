package com.consortium.consochrome;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.widget.ImageButton;
import android.widget.ImageView;

/**
 * Class contains all the global data used for the application
 * @author Maharshi
 *
 */
public class Data {
	public static final int LEVELS_PER_BUFFER = 2;
	public static final int MAX_BUFFER = 3;
	public static final int STEPS_PER_LEVEL = 30;
	public static final int TOTOL_LEVELS = LEVELS_PER_BUFFER*MAX_BUFFER;
	public static final int TIMEOUT_INCREMENT = 500;
	public static final int TIMEOUT_MIN = 3000;
	public static final int TIMEOUT_MAX = 10000;

	public static final int LEADERBOARD_LENGTH = 10;
	
	public static final String COLOR_RED = "RED";
	public static final String COLOR_YELLOW = "YELLOW";
	public static final String COLOR_BLUE = "BLUE";
	public static final String COLOR_GREEN = "GREEN";
	public static final String COLOR_BROWN = "BROWN";
	public static final String COLOR_MAGENTA = "MAGENTA";
	public static final String COLOR_INDIGO = "INDIGO";
	public static final String COLOR_GRAY = "GRAY";

	public static final String[] COLORS = new String[] { 
		COLOR_RED, COLOR_BLUE, COLOR_GREEN, COLOR_YELLOW, 
		COLOR_MAGENTA, COLOR_INDIGO, COLOR_BROWN, COLOR_GRAY };
	
	public static final int N_COLORS = 8;
	public static ImageView[] consoLogos = new ImageView[N_COLORS];
	public static int[] colorImages = new int[N_COLORS];//to be initialized
	
	public static MainActivity mainActivity;
	public static TapTilesActivity tapTileActivity;
	public static TapTileGameOverActivity tapTileOverActivity;
	
	public static Calendar calender;
	public static SharedPreferences localData;
	public static final String applicationPreference = "appPref";
	
	public static final String KEY_NEW_INSTALL = "new_install";
	public static final String KEY_TT_BEST_SCORE = "bs1";
	public static final String KEY_TT_DAILY_BEST_SCORE = "dbs1";
	public static final String KEY_TODAY = "day";
	public static final String KEY_CONSO_TILE_BEST_SCORE = "bs2";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_UPDATES = "updates";
	public static final String KEY_UPDATES_READ = "update_read";
	public static final String KEY_LEADER_D = "daily_l";
	public static final String KEY_LEADER_A = "all_time_l";
	
	public static String KEY_DAILY_SIZE = "daily_size";
	public static String KEY_ALL_TIME_SIZE = "all_time_size";
	public static String[] KEY_USERS_ALL_TIME = new String[LEADERBOARD_LENGTH];
	public static String[] KEY_USERS_DAILY = new String[LEADERBOARD_LENGTH];
	public static String[] KEY_SCORES_ALL_TIME = new String[LEADERBOARD_LENGTH];
	public static String[] KEY_SCORES_DAILY = new String[LEADERBOARD_LENGTH];
	
	public static final String DEFAULT_USERNAME = "guest";
	
	public static final Integer UPDATE_READ = 1;
	public static final Integer UPDATE_UNREAD = 0;
	
	public static final int MAC_ID_NOT_FOUND = 0;
	public static final int SCORE_UPDATED_SUCCESSFUL = 1;
	public static final int UNEXPECTED_ERROR = 2;
	
	public static final int USERNAME_ALREADY_EXISTS = 0;
	public static final int DEVICE_REGISTRATION_SUCCESSFUL = 1;
	
	public static final String url_submit_score = "http://consortium123.site50.net/consotiles/verify_user_v2.php";
	public static final String url_device_register = "http://consortium123.site50.net/consotiles/submit_user_v2.php";
	public static final String url_leaderboard_daily = "http://consortium123.site50.net/consotiles/leaderboard_daily_v2.php";
	public static final String url_leaderboard_all_time = "http://consortium123.site50.net/consotiles/leaderboard_all_time_v2.php";
	public static final String url_updates = "http://consortium123.site50.net/consotiles/view_updates_v2.php";
	
	public static Typeface appTypeface;
	
	static {
		
		for(int i=0; i<LEADERBOARD_LENGTH; i++) {
			KEY_USERS_ALL_TIME[i] = "all_time_users_" + (i+1);
			KEY_SCORES_ALL_TIME[i] = "all_time_scores_"+ (i+1);
			KEY_USERS_DAILY[i] = "daily_users_"+ (i+1);
			KEY_SCORES_DAILY[i] = "daily_scores_"+ (i+1);
		}
	}
}
