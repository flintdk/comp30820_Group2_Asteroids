package comp30820.group2.asteroids;

/** A comparable object representing "Player Score" so we can easily manage our
 * High Scores table using a Java PriorityQueue
 *
 * @author B. Agar Cox, E. Brard, T. Kelly, W. Song
 *
 */
public class PlayerScore implements Comparable<PlayerScore> {

	private String playerName;  // the players name
	private int score;  // the numeric value of the score

	public PlayerScore(String playerName, int score){
		this.playerName = playerName;
		this.score = score;         // assuming negative scores are allowed
	}
	public void updateScore(double score){
		this.score += score;
	}

	/** Compare PlayerScore objects based on numerical score value
	 *
	 */
	@Override
	public int compareTo(PlayerScore otherScore) {
		// Note you have to BEAT the other score to get in front of it - if you
		// just match the score... oh well... better luck next time!!
		if(this.score > otherScore.score)  
			return 1;  
		else  
			return -1;  
	}
	
	/** Return a PlayerScore formatted for inclusion in the Hall of Fame
	 * 
	 * @return
	 */
	public String getHallOfFameScore() {
		return getPlayerName() + " - " + Integer.toString(getScore());
	}

	/* GETTERS AND SETTERS */

	public String getPlayerName() {
		return playerName;
	}
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getScore() {
		return score;
	}
	public void setScore(int score) {
		this.score = score;
	}

}