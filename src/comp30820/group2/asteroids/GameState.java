package comp30820.group2.asteroids;

/** 
 *
 * @author B. Agar Cox, E. Brard, T. Kelly, W. Song
 *
 */
/*MODIFICATIONS:
 * 22/03/nn ??; 
 * 
 */
public class GameState {
	
    private static GameState INSTANCE;

    // Define GameState attributes with sensible defaults...
    private String playername = "Player1";
    private int level = 1;
    private int lives = 3;
    private int score = 0;
	
    // #########################################################################
    // Order Matters!  Keep the Constructor and getInstance methods together so
    // there's no doubt this is a singleton...
    
    // Singleton - so it has a private constructor that no-one except this class
    // itself can all.
    private GameState() {
    }
    
	// Singleton - There can be ONLY One!!
    public static GameState getInstance() {
    	if(INSTANCE == null) {
            INSTANCE = new GameState();
        }
        return INSTANCE;
    }
    // #########################################################################

	// Getters and Setters

	public String getPlayername() {
		if (playername == null) {
			playername = "Player1";
			return playername;
		}
		else {
			return playername;
		}
	}
	public void setPlayername(String playername) {
		this.playername = playername;
	}
	//----------
	
	public int getScore() {
		return this.score;
	}
	public String getDisplayScore() {
    	String str1 = Integer.toString(score);
		return str1;
	}
	public void setScore(int score) {
		this.score = score;
	}
	/** Increment the players score.  Positive numbers make score bigger, negative
	 * numbers make it smaller!
	 * 
	 * @param points
	 */
	public void incrementScore(int points) {
		score = score + points;
	}
	//----------
	
	public String getLivesForDisplay() {
		String livesstring = Integer.toString(lives);
		return livesstring;
	}
	public int getLives() {
		return lives;
	}
	public void setLives(int lives) {
		this.lives = lives;
	}
	/** Lose one life.
	 * 
	 */
	public void loseALife() {
		lives -= 1;
	}

	//----------
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}

}
