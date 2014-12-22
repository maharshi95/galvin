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
		
	}
	
	private int timeout() {
		return timeout;
	}
	
	private int bufferSize() {
		return bufferSize;
	}
	
	private int nsteps() {
		return nsteps;
	}
	
	private void addToScore(int stepScore) {
		score += stepScore;
	}
	
	private static int getTimeout(int level) {
		return 0;
	}
	
	private static int getBufferSize(int level) {
		return(level - 1)/LEVELS_PER_BUFFER + 1;
	}
	
	private static int getNSteps(int level) {
		return 0;
	}
}
