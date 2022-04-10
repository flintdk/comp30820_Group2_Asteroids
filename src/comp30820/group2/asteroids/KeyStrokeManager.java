package comp30820.group2.asteroids;

import java.util.HashMap;

import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;

/** One of the many great things about JavaFX is its powerful events system. It�s
 * powerful, but it�s based on events rather than polling. That means if we need
 * to catch it as it�s happening.
 * 
 * It seems more intuitive to use to give the developer the functionality to check
 * when they want whether a chosen key is �down�.  To create that functionality
 * we have wrapped the JavaFX event management in a management class. To give us
 * constant access to key state, we have set up a listener to run every time a key
 * is pressed and keep track of the keys.
 * 
 * We have no need to maintain more than one set of keys, so we�ll make our KeyStrokeManager class a Singleton. 
 *
 * @author B. Agar Cox, E. Brard, T. Kelly, W. Song
 *
 */
// The idea for using a Singleton to manage our input/output came from an online
// article about creating/managing GameLoops in Java, see here:
  //   -> https://edencoding.com/game-loop-javafx/
// Of course our implementation - essentially what we thought would make for a
// a game-friendly key system - ended up being quite different.
public class KeyStrokeManager {

	// The event listeners for javaFX are tied to a scene, so the KeyStrokeManager
	// has a scene.
    private static Scene scene;
    private static KeyStrokeManager INSTANCE;

	// Create an ArrayList to store the keys that are currently being pressed
    //private static ArrayList<String> keyPressedList = new ArrayList<String>();
	// Use for space enfocer juste une fois 
    
    //create an HashMap, if the value is false then no need to know for how long the key was pressed
    private static HashMap<String, Boolean> currentlyActiveKeys = new HashMap<String, Boolean>();
    
    // Singleton - so it has a private constructor that no-one except this class
    // itself can all.
    private KeyStrokeManager() {
    }
    // Singleton - There can be ONLY One!!
    public static KeyStrokeManager getInstance() {
    	if(INSTANCE == null) {
            INSTANCE = new KeyStrokeManager();
        }
        return INSTANCE;
    }
    
    /** <p>We want to manage keystrokes on several scenes.  When controlling the ship
     * in game, when entering your name against a high score, when quitting, etc. ...</p?
     * <p>When we want to manage a scene, we need to tell the KeyStrokeManager to
     * 'manage the keys for this scene'.  To do that we need to:</p>
     * <ul>
     * 	<li>remove any current/stale state information from our array</li>  
     * 	<li>remove the current event listeners (from an old scence??)</li>
     *	<li>create new event listeners for the key events on the new scene!</li>
     * </ul>
     * @param scene
     */
    public void manageThisScene(Scene scene) {
        clearKeys();
        removeCurrentKeyHandlers();
        setScene(scene);
    }

    /** Make sure our array is empty when starting to manage a new scene
     * 
     */
    private void clearKeys() {
    	//keyPressedList.clear();
    	currentlyActiveKeys.clear();
    }
    /** If we were managing an old scene, make sure to destroy those even listeners
     * 
     */
    private void removeCurrentKeyHandlers() {
        if (scene != null) {
            KeyStrokeManager.scene.setOnKeyPressed(null);
            KeyStrokeManager.scene.setOnKeyReleased(null);
        }
    }
    private void setScene(Scene scene) {
    	// Take a note of the scene...
        KeyStrokeManager.scene = scene;
        
		//######################################################################
		//                              KEYSTROKES
		//######################################################################

		// We want an EventListener so the user can control the spaceship. An event
		// listener is something that responds to user driven action like a key
		// press or a mouse press or something like that.  We will set attach our
		// EventListener to the mainScene using the method below.  That way JavaFX
		// knows 'when a key is press event occurs - call this event handler'

		// We're going to use lamda expressions rather than an anonymous inner class
		// lamda expressions were introduced in Java 8 as a "flagship feature".
		// More on lamda expressions here:
		//   -> https://www.w3schools.com/java/java_lambda.asp
		//   -> https://www.javatpoint.com/java-lambda-expressions
		// ... etc..
        KeyStrokeManager.scene.setOnKeyPressed(
				(KeyEvent event) ->
				{
					String keyName = event.getCode().toString();
					// Avoid adding duplicates to list
					if (keyName == "SPACE") { //first check if it's a space,  because we only want it to count 1 time
						if (!currentlyActiveKeys.containsKey(keyName)) {
							currentlyActiveKeys.put(keyName, true);
			            }
					}
					else if (!currentlyActiveKeys.containsKey(keyName)) {
						currentlyActiveKeys.put(keyName, false);
					}
					//System.out.println("list of key pressed : " + currentlyActiveKeys);

				}
				
		);
        KeyStrokeManager.scene.setOnKeyReleased(
				(KeyEvent event) ->
				{
					String keyName = event.getCode().toString();	
					if(currentlyActiveKeys.size() > 0 && currentlyActiveKeys.get(keyName) == false) {
						currentlyActiveKeys.remove(keyName);
					}
				}
		);
    }
    
	/** @Elise?
	 * method allowing us to distinct if a key has been pressed only once, no matter how long it has been pressed on
	 * @param codeString
	 * @return
	 */
    // @Elise Suggestion: rename this to "markKeyPressAsProcessed" or some such
    // really descriptive thing that explains what it does?
	protected boolean markKeyPressAsProcessed(String codeString) {
        Boolean isActive = currentlyActiveKeys.get(codeString);//returns the value from the 

        if (isActive != null && isActive) {//there is a space pressed and it's value is true 
        	currentlyActiveKeys.put(codeString, false);
            return true;
        } else {
            return false;
        }
    }

//    @Override
//    public String toString() {
//        StringBuilder keysDown = new StringBuilder("KeyStrokeManager on scene (").append(scene).append(")");
//        for (KeyCode code : keysCurrentlyDown) {
//            keysDown.append(code.getName()).append(" ");
//        }
//        return keysDown.toString();
//    }
    
//	public ArrayList<String> getKeyPressedList() {
//		return keyPressedList;
//	}
	public HashMap<String, Boolean> getCurrentlyActiveKeys() {
		return currentlyActiveKeys;
	}
    
}
