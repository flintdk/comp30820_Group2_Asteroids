package comp30820.group2.asteroids;

public class Timer {
	//Timer is used to decide when the object should appear
    public enum TIMER_CLASS {
    	LOSE_A_LIFE("Player Loses a Life"),
    	LOSE_THE_GAME("Player Loses the Game"),
    	INVINCIBLE("Player Invincible for a Short Time"),
    	INVINCIBLE_FLASH_VISIBLE("Player Ship Flashing, Visible "),
    	INVINCIBLE_FLASH_HIDDEN("Player Ship Flashing, Visible "),
        ALIEN_TIMER("Alien Timer"),
        ALIEN_BULLET_TIMER("Alien bullet timer"),
    	HYPERSPACE("flash");
        
    	public final String description;

        private TIMER_CLASS(String description) {
            this.description = description;
        }
    }
    
    private int time;
    
    public Timer(int time) {
    	this.time = time;
    }
    
    public void increment() {
    	this.time +=1;
    }
    public void decrement() {
    	this.time -=1;
    }
    
    public void set_time(int time) {
    	this.time = time;
    }
    public int get_time() {
    	return this.time;
    }
}
