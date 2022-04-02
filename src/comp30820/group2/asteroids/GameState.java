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
		return playername;
	}
	public void setPlayername(String playername) {
		this.playername = playername;
	}

}
