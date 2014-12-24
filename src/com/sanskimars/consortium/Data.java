package com.sanskimars.consortium;

import java.util.ArrayList;

import android.widget.ImageButton;
import android.widget.ImageView;

public class Data {
	public static final int LEVELS_PER_BUFFER = 8;
	public static final int MAX_BUFFER = 5;
	public static final int TOTOL_LEVELS = LEVELS_PER_BUFFER*MAX_BUFFER;
	public static final int TIMEOUT_INCREMENT = 1000;
	public static final int TIMEOUT_MIN = 1000;
	public static final int TIMEOUT_MAX = 3000;

	public static final String COLOR_RED = "RED";
	public static final String COLOR_YELLOW = "YELLOW";
	public static final String COLOR_BLUE = "BLUE";
	public static final String COLOR_GREEN = "GREEN";
	public static final String COLOR_BROWN = "BROWN";
	public static final String COLOR_MAGENTA = "MAGENTA";
	public static final String COLOR_INDIGO = "INDIGO";
	public static final String COLOR_GRAY = "GRAY";

	public static final String[] COLORS = new String[] { 
		COLOR_RED, COLOR_YELLOW, COLOR_BLUE, COLOR_GREEN, 
		COLOR_MAGENTA, COLOR_INDIGO, COLOR_BROWN, COLOR_GRAY };
	
	public static final int N_COLORS = 8;
	public static ImageView[] consoLogos = new ImageView[N_COLORS];
	public static int[] colorImages = new int[N_COLORS];//to be initialized

}
