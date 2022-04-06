package comp30820.group2.asteroids;

public class Timer {
	//Timer is used to decide when the object should appear
    public enum TIMER_CLASS {
        ALIEN_TIMER("Alien Timer"),
        ALIEN_BULLET_TIMER("Alien bullet timer");
        
    	public final String description;

        private TIMER_CLASS(String description) {
            this.description = description;
        }
    }
    
    private int time;
    
    public Timer(int time) {
    	this.time = time;
    }
    
    public void increment_timer() {
    	this.time +=1;
    }
    
    public void set_time(int time) {
    	this.time = time;
    }
    
    public int get_time() {
    	return this.time;
    }
}
