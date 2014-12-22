package com.sanskimars.consortium;
import static com.sanskimars.consortium.Data.*;
/**
 * Class to simulate and keep the data regarding each level of the game, which includes
 * timeout, buffer size, number of steps, score.
 * @author Maharshi
 *
 */
public class Level {
	private int timeout;
	private int bufferSize;
	private int nsteps;
	private int score;
	
	public Level(int level) {
		timeout = getTimeout(level);
		nsteps = getNSteps(level);
		bufferSize = getBufferSize(level);
		score = 0;
	}
	
	public int timeout() {
		return timeout;
	}
	
	public int bufferSize() {
		return bufferSize;
	}
	
	public int nsteps() {
		return nsteps;
	}
	
	public void addToScore(int stepScore) {
		score += stepScore;
	}
	
	/**
	 * 
	 * @param level
	 * @return timeout milliseconds for each step in the given level
	 */
	private static int getTimeout(int level) {
		int min = getTimeoutMinLimit(col(level));
		int max = getTimeoutMaxLimit(col(level));
		int r  = row(level);
		int L = LEVELS_PER_BUFFER;
		return (max*(L-r) + min*(r-1))/(L-1);
	}
	
	private static int getTimeoutMinLimit(int col) {
		return TIMEOUT_MIN + (col - 1)*TIMEOUT_INCREMENT;
	}
	
	private static int getTimeoutMaxLimit(int col) {
		return TIMEOUT_MAX  + (col-1)*TIMEOUT_INCREMENT;
	}
	
	private static int col(int level) {
		return (level-1)/LEVELS_PER_BUFFER + 1;
	}
	
	private static int row(int level) {
		return (level-1)%LEVELS_PER_BUFFER + 1;
	}
	private static int getBufferSize(int level) {
		return col(level);
	}
	
	private static int getNSteps(int level) {
		return 10;
	}
	
}
