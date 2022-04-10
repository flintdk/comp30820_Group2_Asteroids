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

	private String playername = "Player1";  // Sensible default
	
    // Singleton - so it has a private constructor that no-one except this class
    // itself can all.
    private GameState() {
    }
    
    
    private int score = 0;
    private int lives = 3;
    
    
    
    public String getScore() {
    	String str1 = Integer.toString(score);
		return str1;
	}

	public void setScore(int score) {
		this.score = score;
	}
	public void adjustScore(int pointsduc) {
		score = score - pointsduc;
	}

	// Singleton - There can be ONLY One!!
    public static GameState getInstance() {
    	if(INSTANCE == null) {
            INSTANCE = new GameState();
        }
        return INSTANCE;
    }

	/** Save the User-specific Asteroids application configuration
	 * 
	 */
	public static void savePlayerHighScoresToConfig() {

	}

	// Getters and Setters

	public String getPlayername() {
		if (playername == null) {
			playername = "Player1";
			return playername;
		}
		else {
		return playername;
		}	}
	public void setPlayername(String playername) {
		this.playername = playername;
	}
	
	
	
	
	
	public String getLives() {
		// TODO Auto-generated method stub
		String livesstring = Integer.toString(lives);
		return livesstring;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}
	public void adjustLives() {
		lives = lives - 1;
	}

}
