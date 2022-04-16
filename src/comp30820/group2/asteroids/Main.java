package comp30820.group2.asteroids;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import comp30820.group2.asteroids.Configuration.SoundEffects;
import comp30820.group2.asteroids.GameObject.GoClass;
import comp30820.group2.asteroids.Sprite.Graphics;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;

/** Asteroids - the Classic Arcade Game.
 * 
 * This implementation was initially inspired by an implementation by Prof. Lee
 * Stemkoski, Professor of Mathematics and Computer Science, Adelphi University.
 * See:
 *     https://www.youtube.com/user/ProfStemkoski/search?query=asteroids
 * It has since been heavily modified.
 * 
 * @author B. Agar Cox, E. Brard, T. Kelly, W. Song
 *
 */
/*MODIFICATIONS:
 * 22/03/nn ??; 
 * 
 */
public class Main extends Application {
	// The JavaFX runtime does the following, in order, whenever an application is launched:
	//
	//    Starts the JavaFX runtime, if not already started (see Platform.startup(Runnable) for more information)
	//    Constructs an instance of the specified Application class
	//    Calls the init() method
	//    Calls the start(javafx.stage.Stage) method
	//    Waits for the application to finish, which happens when either of the following occur:
	//        the application calls Platform.exit()
	//        the last window has been closed and the implicitExit attribute on Platform is true
	//    Calls the stop() method
	// Calling Platform.exit() is the preferred way to explicitly terminate a JavaFX Application.

	// Define a constant for our Application Name
	public static final String APP_NAME = "COMP30820_Group2_Asteroids";

	// Pattern for our FXML implementation inspired by "the manual" (lol):
	//   -> https://docs.oracle.com/javafx/2/fxml_get_started/jfxpub-fxml_get_started.htm

	// Initialise the KeyStrokeManager singleton...
	KeyStrokeManager keys = KeyStrokeManager.getInstance();

	private static Scene mainGameScene;
	// If you want to see the raw Hit Model drawn on the game pane (i.e.
	// not using the graphics context - all we have to do is rab the main
	// game pane from the .fxml.  Then below we can add children etc..
	@SuppressWarnings("unused")  // Following is quite useful for debugging...  
	private static Pane mainGamePane;
	private static boolean ctrlResetGameState;
	private static boolean ctrlAllowMovement;  // Allows us to enable/disable movement of all objects

	public static void main(String[] args)
	{
		// Sanity check: Print out the working directory of the application.
		System.out.println("Asteroids: Working Directory = " + System.getProperty("user.dir"));

		try {
			// Load our configuration file
			Configuration.loadConfig();

			launch(args);
		}
		catch (Exception error) {
			error.printStackTrace();
		}
		finally {
			// Before exit - save our configuration file
			Configuration.saveConfig();

			System.exit(0);
		}
	}

	// Because we're extending Application our Asteroids class must
	// implement the inherited abstract method Application.start(Stage)
	@Override
	public void start(Stage mainStage) throws Exception 
	{
		// The 'stage' is essentially the 'window' in which our application runs.
		// JFX doesn't use terms like 'window' as it's designed to support desktop,
		// mobile and web applications.

		mainStage.setTitle("Asteroids");

		// https://www.developer.com/design/multithreading-in-javafx/

		// Thing is - you're not tied to a particular stage - you can create as
		// many of them as you want.  Also the JavaFX docs are very, very explicit
		// when they say that a JavaFX Scene Graph Is Not Thread-safe - it is
		// modeled to execute on the single JavaFX Application Thread.
		//   -> The constructor and the initialization method init() is called in
		//      the JavaFX-Launcher thread.
		//   -> The start() and stop() methods are invoked in the JavaFX Application Thread.
		//   -> The events also are processed on the JavaFX Application Thread.
		// Therefore, any live manipulation on the scene MUST be done on the main
		// Application Thread alone.

		// We attempted to move some of the logic for the gameloop into the
		// FXML Controller and had very unpredictable results (some objects would
		// draw - some would simply not draw.

		// So - the pattern we have now: with our main gameloop running in this
		// thread (even in the background when other screens are displayed) may
		// not be the most elegant pattern.  But it works.  It was implemented
		// this way for a reason.  And we don't have the project time to explore
		// other patterns.

		//    	System.out.println("Thread Information: " + Thread.currentThread().getName() + "," + Thread.currentThread().getId());

		//######################################################################

		// A scene graph is a tree data structure, most commonly found in graphical
		// applications and libraries such as vector editing tools, 3D libraries and
		// video games. The JavaFX scene graph is a retained mode API, meaning that
		// it maintains an internal model of all graphical objects in your application.
		// A 'Parent' is the root node of the JavaFX Scene Graph

		// Here in this 'start' method we start out with a 'mainStage' which is
		// provided magically by JavaFX. The .fxml resource we load specifies the
		// layout manager for the scene based on it's root.  At the time of writing
		// it was a BorderPane - but having it specified in the .fxml means it can
		// be tweaked and changed at will!
		FXMLLoader welcomeLoader =
				new FXMLLoader(getClass().getResource(Configuration.GameWindows.WELCOME_MAIN_MENU.fxmlResource));
		Parent root = (Parent) welcomeLoader.load();
		Scene welcomeScene = new Scene(root);
		welcomeScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		// Attach the KeyStrokeManager singleton to this scene...
		//KeyStrokeManager.getInstance().manageThisScene(welcomeScene);
		// Create the GameState singleton for things like Player Name etc...
		GameState.getInstance();

		mainStage.setScene(welcomeScene);
		mainStage.show();

		//######################################################################
		//                         STARTUP GAME OBJECTS
		//######################################################################

		// OK! The Welcome screen has been displayed - now we perform the necessary
		// setup for our game screen...

		FXMLLoader mainGameLoader =
				new FXMLLoader(getClass().getResource(Configuration.GameWindows.MAIN_GAME.fxmlResource));
		// In the following line the 'mainGameRoot corresponds to the BorderLayout
		// in our Java FX scene (it is of class BorderLayout)
		Parent mainGameRoot = (Parent) mainGameLoader.load();
		// Create our mainGameScene.  We want to use this scene over and over
		// so it gets created once as the game starts up and then we draw to the
		// canvas it contains whenever it's presented on the stage.
		Main.mainGameScene = new Scene(mainGameRoot);
		Main.mainGameScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		AsteroidsFXMLController controller = mainGameLoader.<AsteroidsFXMLController>getController();
		Map<String, Object> mainGameNamespace = mainGameLoader.getNamespace();
		// Store a ref to the main game pane - useful for debugging...
		Main.mainGamePane = (Pane) mainGameNamespace.get("mainGamePane");

		//		GameObject initialAsteroid1;
		//		Pane pane = new Pane();
		//	    pane.setPrefSize(1024, 600);

		// We want to display some graphics, so we use the Canvas that is configured
		// in our FXML.  We can display our graphics on and set it's size. The Canvas
		// class basically creates an image *that can be drawn on* using a set of
		// graphics commands provided by a "GraphicsContext". Canvas has a specified
		// height and width and all the drawing operations are clipped to the bounds
		// of the canvas.

		// In order to perform draw operations on the canvas we need to create
		// a graphicsContext object (we get it *from* the canvas.
		GraphicsContext context = controller.getAsteroidsGameCanvas().getGraphicsContext2D();

		// The BorderPane allows us to set containers/to set objects and nodes in
		// different regions of the screen. We want ours to be right in the center.

		// Load the image file (path is relative to class loader!)
		// **** The background is a special case, we make sure it's scaled to the
		// size of our background (as per the config file!) and IT DOES NOT MOVE
		GameObject background
		= new Sprite(Graphics.BACKGROUND,
				Double.valueOf(Configuration.SCENE_WIDTH),
				Double.valueOf(Configuration.SCENE_HEIGHT));
		background.position = new GameVector(0,0);

		//######################################################################
		//                         THE ANIMATION / RUNNING GAME
		//######################################################################

		// The first time we enter the animation timer, we definitely want to 
		// set the game state to initial settings.
		Main.ctrlResetGameState = false;
		Main.ctrlAllowMovement = false;  // Motion disabled by default!
		// Occasionally handy to know the center of the screen...
		GameVector centerOfScreen
		= new GameVector( (Configuration.SCENE_WIDTH / 2),(Configuration.SCENE_HEIGHT / 2) );

		// Grab a handle on the gamestate singlton
		GameState gameState = GameState.getInstance();

		// The AnimationTimer is a way you can create a set of code that will run
		// 60 times per second in JavaFX

		//create a hashmap to store timers for the gameObjects
		HashMap<Enum,Timer> timers = new HashMap<Enum, Timer>();

		AnimationTimer gameloop = new AnimationTimer() {

			// The players spaceship is a special object as it's motion is controlled
			// reacting to input from the keyboard (from the user)
			GameObject spaceship = null;
			GameObject alienOnScreen;

			// We keep track of all the objects on screen.
			List<GameObject> movingObjectsOnScreen = new ArrayList<GameObject>();

			// NOT USED!?
			//			List<GameObject> alienBulletsOnScreen = null;
			//			List<GameObject> allBulletsOnScreen = null;

			public void handle(long nanotime) {

				//#############################################################3
				//         FOLLOWING BLOCK EXECUTES ONLY ON GAME RESET
				//   CODE WE ONLY WANT TO RUN ONCE (NOT 60-TIMES/s) GOES HERE
				//#############################################################3

				// The animation loop runs all the time.  If a process sets the
				// following ctrl boolean to true, we reset all the game objects
				// to their initial conditions.  This gives us a way to reset
				// for a new game if we want to.
				if (Main.ctrlResetGameState) {
					setGameToInitialState(timers);
				}

				//########################################################3
				/// UPDATE GAME SCORE, LIVES and LEVEL
				//########################################################3

				displayScoreLivesAndLevel();

				//########################################################3
				// TIMERS
				//########################################################3

				// Increment all Timers...
				timers.values().forEach( timer -> timer.increment() );

				// Then check them to control in-game behaviour.
				manageTimedEvents();

				//########################################################3
				// PROCESS KEY INPUT
				//########################################################3

				processKeyboardInput();

				//########################################################3
				// GAME ANIMATION AND FRAME PROCESSING
				//########################################################3

				// LAMBDA EXPRESSION
				if (ctrlAllowMovement) {
					movingObjectsOnScreen.forEach( (object) -> object.updatePosition(1/60.0));
				}

				background.render(context);

				//set the timer for aliens
				Timer alienTimer = timers.get(Timer.TIMER_CLASS.ALIEN_TIMER);
				if (alienTimer != null) {
					//alienTimer.increment();
					List<GameObject> alienOnScreenList = findGameObjectsInList(GameObject.GoClass.ALIEN);
					if(alienOnScreenList!=null) {
						alienOnScreen = alienOnScreenList.get(0);
					}
					//System.out.println(timers.get(Timer.TIMER_CLASS.ALIEN_TIMER).get_time());
					//when alien timer = 800, alien appear
					if(timers.get(Timer.TIMER_CLASS.ALIEN_TIMER).get_time()%800==0) {
						if(alienOnScreenList==null) {
							alienOnScreen = new AsteroidsShape(AsteroidsShape.InGameShape.ALIEN);
							alienOnScreen.randomInitAlien();
							movingObjectsOnScreen.add(alienOnScreen);					
						}
					}

					//if the alien object exist, set the path for alien
					if(alienOnScreen!=null){
						alienOnScreen.changePathAlien();
					}
					//if alien out of screen, remove it and reset timer again

					if(alienOnScreenList!=null && (alienOnScreen.position.getX()<0)) {
						movingObjectsOnScreen.remove(alienOnScreen);	
						timers.get(Timer.TIMER_CLASS.ALIEN_TIMER).set_time(0);
					}

					// get the alien bullet timer
					Timer alienBulletTimer = timers.get(Timer.TIMER_CLASS.ALIEN_BULLET_TIMER);
					if(alienOnScreenList != null && alienBulletTimer == null) {
						timers.put(Timer.TIMER_CLASS.ALIEN_BULLET_TIMER, new Timer(0));
					} else if(alienOnScreenList == null) {
						timers.remove(Timer.TIMER_CLASS.ALIEN_BULLET_TIMER);
					}


					if (alienBulletTimer != null) {
						int timerBullet = alienBulletTimer.get_time();
						if(alienOnScreenList != null && spaceship != null
								&& alienOnScreen.position.getX()>20 && alienOnScreen.position.getY()>10
								&& alienOnScreen.position.getX()<1004 && alienOnScreen.position.getY()<590)
						{
							//alien Bullet fire At regular intervals
							GameObject.alienBulletFire(alienOnScreen,timerBullet,spaceship, movingObjectsOnScreen);
						}
					}
				}

				//if the alien bullet out of fire range, it should be removed
				removeAlienBulletWhenHitsMaxRange();

				//check if some bullet are out the screen boundaries and remove them for the moving object, no need to update them anymore 
				removeBulletOutScreen();

				//check if any of the bullets on screen are hitting an asteroids, 
				//if yes creates, depending on the type of the asteroids, create two new smaller asteroids
				//method not finished : need to add points if collision ! 
				collisionBulletAsteroidAlien();

				// check if the ship is hitting an asteroids or is hit by an alien bullet
				collisionSpaceship();

				// LAMBDA EXPRESSION
				movingObjectsOnScreen.forEach( (object) -> object.render(context));

			}

			//##################################################################
			// KEYBOARD INPUT
			//##################################################################

			private void processKeyboardInput() {
				// Process user input
				// FOR CONSIDERATION: Should we extract our keystrokes and make
				//                    them part of the configuration file so users
				//                    can choose/save their own.
				if (keys.getCurrentlyActiveKeys().containsKey("LEFT")) {
					spaceship.rotation -= 3;
				}
				if (keys.getCurrentlyActiveKeys().containsKey("Q")) {
					try {
						controller.activateScene(mainStage, Configuration.GameWindows.END_OF_GAME);
					}
					catch (IOException IOe) {
						IOe.printStackTrace();
					}
				}
				if (keys.getCurrentlyActiveKeys().containsKey("RIGHT")) {
					spaceship.rotation += 3;
				}
				// For UP we want to make sure we move in the direction the
				// spaceship is facing!!
				if (keys.getCurrentlyActiveKeys().containsKey("UP")) {
					// Don't allow the player to use engines if the spaceship is
					// temporarily off the screen...
					if (spaceship.willRender) {
						// The animation timer runs 60 times a second.  If a player presses a button
						// that triggers a sound to play (like say the ships thrusters) then - if the
						// player holds the button down - you can get an audio distortion as the media
						// player attempts to play the clip over and over again (60 times per second)
						// HOW TO DEAL WITH THIS?
						fireEnginesAndChangeVelocity();
					}
				} 
				// For UP we want to make sure we move in the direction the
				// spaceship is facing!!
				if (keys.processKeypressAndMarkAsProcessed("SPACE")) {
					// Don't allow the player to fire if the spaceship is temporarily
					// off the screen...
					if (spaceship.willRender) {
						fireABulletFromSpaceshipAndPlaySound();
					}
				}

				//press keyboard H and Let the spaceship jump to a place where is no enemies
				if (keys.processKeypressAndMarkAsProcessed("H")) {
				//if (keys.getCurrentlyActiveKeys().containsKey("H")) {
					goToHyperspace();
				}

				// We know the frame rate of the AnimationTimer, so every time
				// the 'handle' gets called, we know that 1/60th of a second has
				// passed.
				// This is ordered from background to front 
				// Instead of running each update individually, we can use a Lambda Expression to
				// the method on each object as we iterate over the list.
			}

			//##################################################################
			// TIMED EVENTS
			//##################################################################

			/** Several events in the game rely on a timer.  For example the
			 * pause when a player goes into hyperspace, or the pause after a
			 * player loses a life.  This method groups all the timed events into
			 * a single place for more convenient management.
			 * 
			 */
			private void manageTimedEvents() {
				// Now deal with specific events when timers run out...
				Timer hyperspaceTimer = timers.get(Timer.TIMER_CLASS.HYPERSPACE);
				if (hyperspaceTimer != null && hyperspaceTimer.get_time() >= 120) {    // 120 = 2s
					// Our ship has done it's stint in hyperspace... bring us home!
					timers.remove(Timer.TIMER_CLASS.HYPERSPACE);
					returnFromHyperspace();
				}
				//----------------------------------------
				// When we lose a life the game pauses for 1.5 seconds to allow
				// the player to mourn...
				Timer endOfLifeTimer = timers.get(Timer.TIMER_CLASS.LOSE_A_LIFE);
				if (endOfLifeTimer != null && endOfLifeTimer.get_time() >= 90) {    // 90 = 1.5s
					resumePlayAfterLosingLife();
				}
				//----------------------------------------
				// When we recover after losing a life we are invincible for 
				// a little bit.
				Timer invincibilityTimer = timers.get(Timer.TIMER_CLASS.INVINCIBLE);
				if (invincibilityTimer != null && invincibilityTimer.get_time() >= 180) {    // 180 = 3s
					timers.remove(Timer.TIMER_CLASS.INVINCIBLE);
					// Also remove any 'flash' timers...
					timers.remove(Timer.TIMER_CLASS.INVINCIBLE_FLASH_VISIBLE);
					timers.remove(Timer.TIMER_CLASS.INVINCIBLE_FLASH_HIDDEN);
					// Bye-bye super powers!
					spaceship.canBeHit = true;
					spaceship.willRender = true;
				}
				//----------------------------------------
				// Manage the flashing ship while invincible...
				Timer flashVisible = timers.get(Timer.TIMER_CLASS.INVINCIBLE_FLASH_VISIBLE);
				if (flashVisible != null && flashVisible.get_time() >= 6) {    // 6 = 1/10th s
					timers.remove(Timer.TIMER_CLASS.INVINCIBLE_FLASH_VISIBLE);
					timers.put(Timer.TIMER_CLASS.INVINCIBLE_FLASH_HIDDEN, new Timer(0));
					spaceship.willRender = false;
				}
				Timer flashHidden = timers.get(Timer.TIMER_CLASS.INVINCIBLE_FLASH_HIDDEN);
				if (flashHidden != null && flashHidden.get_time() >= 6) {    // 6 = 1/10th s
					timers.remove(Timer.TIMER_CLASS.INVINCIBLE_FLASH_HIDDEN);
					timers.put(Timer.TIMER_CLASS.INVINCIBLE_FLASH_VISIBLE, new Timer(0));
					spaceship.willRender = true;
				}
				//----------------------------------------
				Timer endOfGameTimer = timers.get(Timer.TIMER_CLASS.LOSE_THE_GAME);
				if (endOfGameTimer != null && endOfGameTimer.get_time() >= 450) {    // 450 = 7.5s
					// Be very careful to remove this timer!  Remember the loop
					// is running the whole time (we don't pause it) so  it must
					// be removed or we'll just launch this scene over and over...
					timers.remove(Timer.TIMER_CLASS.LOSE_THE_GAME);

					// Set the game over label not to display - you don't want
					// that still on the screen if the player decides to play
					// again!
					Label gameOver= (Label) mainGameNamespace.get("mainGameGameOver");
					gameOver.setVisible(false);

					try {
						controller.activateScene(mainStage, Configuration.GameWindows.END_OF_GAME);
					}
					catch (IOException IOe) {
						IOe.printStackTrace();
					}
				}
			}

			//##################################################################
			// GENERAL HELPER FUNCTIONS
			//##################################################################

			/** Update the Main Game screen labels with the current values for
			 * score, lives and level.
			 * 
			 * @param mainGameNamespace
			 * @param gameState
			 */
			private void displayScoreLivesAndLevel() {
				Label score= (Label) mainGameNamespace.get("mainGameScore");
				score.setText(gameState.getDisplayScore());

				Label lives = (Label) mainGameNamespace.get("mainGameLives");
				lives.setText(gameState.getLivesForDisplay());

				Label level = (Label) mainGameNamespace.get("mainGameLevel");
				level.setText(Integer.toString(gameState.getLevel()));
			}

			/** <p>Perform the necessary functions when play resumes after losing
			 * a life.</p>
			 * <p>When the player loses a life there is an explosion and the player
			 * ship disappears from the screen.  But when play resumes we don't
			 * just pop back in straight away.  Rather for the first few seconds
			 * the player in invincible (and the ship flashes to indicate this).
			 * We use... some of our lovely timers to manage this using the
			 * animation loop as our clock.</p>
			 * 
			 * @param timers
			 */
			private void resumePlayAfterLosingLife() {
				// Our player has lost a life... but is coming back for more!
				timers.remove(Timer.TIMER_CLASS.LOSE_A_LIFE);

				// When we recover after losing a life we are invincible for 
				// a little bit.
				timers.put(Timer.TIMER_CLASS.INVINCIBLE, new Timer(0));
				timers.put(Timer.TIMER_CLASS.INVINCIBLE_FLASH_VISIBLE, new Timer(0));

				spaceship.canBeHit = false;
				spaceship.willRender = true;
			}

			/** Take the ship to hyperspace! Play a sound and start a timer
			 * (there should only ever really be one for 'being in hyperspace')
			 * @param timers
			 */
			private void goToHyperspace() {
				// Beep into hyperspace...
				playSoundEffect(SoundEffects.HYPERSPACE_ENTER);

				// We've gone into hyperspace - we want to stay there until the
				// timer runs out!
				timers.put(Timer.TIMER_CLASS.HYPERSPACE, new Timer(0));
				// Set the spaceship so it won't draw on screen and can't be hit
				// until the timer runs out...
				spaceship.willRender = false;
				spaceship.canBeHit = false;
			}

			/** Return from hyperspace.
			 *
			 * @param GameObject spaceship, List<GameObject> movingObjectsOnScreen
			 * @return void
			 */
			private void returnFromHyperspace()  {
				// Beep out of hyperspace...
				playSoundEffect(SoundEffects.HYPERSPACE_EXIT);

				Random r = new Random();
				spaceship.position = new GameVector(
						Configuration.SCENE_WIDTH * r.nextDouble(),
						Configuration.SCENE_HEIGHT * r.nextDouble());
				// Set the spaceship so it will draw on screen again and can be hit
				// by asteroids etc. ...
				spaceship.willRender = true;
				spaceship.canBeHit = true;
			}				

			/** Create a new bullet at the spaceships nose-position, giving it an
			 * appropriate initial velocity based on the ships direction and speed. Then
			 * play the sound effect for firing the ships engines.
			 *
			 * @param 
			 * @return void
			 */
			private void fireABulletFromSpaceshipAndPlaySound()
			{
				// Fire!!
				GameObject newBullet;
				if (Configuration.GRAPHICS_MODE == Configuration.GraphicsMode.ARCADE) {
					newBullet = new Sprite(Sprite.getGraphicsForGOClass(GameObject.GoClass.BULLET));
				}
				else {
					newBullet = new AsteroidsShape(AsteroidsShape.getInGameShapeForGOClass(GameObject.GoClass.BULLET));
				}

				// Get the initial position of the bullet based on the spaceship position 
				double bulletPosnX = spaceship.position.getX() + Math.cos(Math.toRadians(spaceship.rotation)) * 12;
				double bulletPosnY = spaceship.position.getY() + Math.sin(Math.toRadians(spaceship.rotation)) * 12;
				newBullet.position = new GameVector(bulletPosnX,bulletPosnY);

				// Bullet speed emerges from muzzle of gun in straight line.
				// But bullet already in motion as it is in the spaceship!
				// We add spaceship speed to bullet speed to calculate the actual
				// bullet speed.
				// We do one small tweak...  we set a minimum speed for that bullet
				// so it is not possible to leave one floating, static on the screen.
				// This wouldn't happen in reality... but... meh....
				double spaceshipSpeedX = spaceship.velocity.getX() ;
				double spaceshipSpeedY = spaceship.velocity.getY() ;

				// If the spaceship was standing still... what would the initial
				// speed of the bullet be?  Work it out...
				double bulletInitVelX = Math.cos(Math.toRadians(spaceship.rotation)) * Configuration.SPEED_BULLET;
				double bulletInitVelY = Math.sin(Math.toRadians(spaceship.rotation)) * Configuration.SPEED_BULLET;

				// Depending on the direction the bullet is going... the following
				// will make the bullet faster... or slower. It just depends!
				bulletInitVelX += spaceshipSpeedX;
				bulletInitVelY += spaceshipSpeedY;
				
				GameVector suggestedVelocity = newBullet.velocity.add(bulletInitVelX, bulletInitVelY);
				// Don't violate minimum speed limit
				if (suggestedVelocity.getLength() < Configuration.SPEED_BULLET_MIN) {
					suggestedVelocity.lengthSetTo(Configuration.SPEED_BULLET_MIN);
				}

				// Now we want to add those velocity increments to the current velocity!
				newBullet.velocity = suggestedVelocity;

				movingObjectsOnScreen.add(newBullet);

				playSoundEffect(SoundEffects.FIRE);
			}

			/** Calculate the ships new velocity based on it's current velocity
			 * including the increment that results from firing the engines in
			 * the direction the spaceship is facing.  Then play the sound effect
			 * for firing the ships engines.
			 * 
			 */
			private void fireEnginesAndChangeVelocity() {
				// Think of the length of the velocity vector as 'speed'

				//---------------------------------------------------------------------------------------
				// If the user is pointing the spaceship and firing thrusters
				// then we want to increment  our speed based on the direction
				// we're facing.

				// First we work out the change in velocity...
				double changeX
				= Math.cos(Math.toRadians(spaceship.rotation)) * Configuration.SPEED_INCREMENT;
				double changeY
				= Math.sin(Math.toRadians(spaceship.rotation)) * Configuration.SPEED_INCREMENT;

				// Don't violate maximum speed limit
				GameVector newVelocity = spaceship.velocity.add(changeX, changeY);
				if (newVelocity.getLength() > Configuration.SPEED_MAX) {
					newVelocity.lengthSetTo(Configuration.SPEED_MAX);
				}
				// Now we want to add those velocity increments to the current velocity!
				spaceship.velocity = newVelocity;
				// ... to here
				//---------------------------------------------------------------------------------------

				// Maths first... sound effects later...
				// Fire thrusters!!
				// We use Asteroids as our resource-anchor class...
				playSoundEffect(SoundEffects.THRUST);

			}

			/** Most things inside the animation block run sixty times per
			 * second.  But when we start a new game there are a couple of things
			 * we only need to do once (to put the game in a clean initial state
			 * - those things go HERE!
			 * 
			 * @param timers
			 */
			private void setGameToInitialState(HashMap<Enum, Timer> timers)
			{
				GameState gameState = GameState.getInstance();
				gameState.setScore(0);  // Hard coded - move to Configuration?
				gameState.setLevel(1);  // Hard coded - move to Configuration?
				gameState.setLives(3);  // Hard coded - move to Configuration?

				//  Set the PlayerName label in the main game layout:
				Label label = (Label) mainGameNamespace.get("mainGamePlayerName");
				label.setText(gameState.getPlayername());

				// First, create and initialise the list of moving game objects
				movingObjectsOnScreen = getStartOfLevelMovingObjects();

				// Get the reference for the spaceship so we can steer it.
				//spaceship = findSpaceshipInList(movingObjectsOnScreen);
				spaceship = findGameObjectsInList(GameObject.GoClass.SPACESHIP).get(0);

				// Start the timer for when alien should appear
				timers.put(Timer.TIMER_CLASS.ALIEN_TIMER, new Timer(0));
				
				// When we start the game we are invincible for a little bit....
				timers.put(Timer.TIMER_CLASS.INVINCIBLE, new Timer(0));
				timers.put(Timer.TIMER_CLASS.INVINCIBLE_FLASH_VISIBLE, new Timer(0));

				Main.ctrlResetGameState = false;
				Main.ctrlAllowMovement = true;
			}

			/** <p>For the supplied 'InGameShape - look for and return all game
			 * object references from the 'movingObjectsOnScreen' list of GameObjects</p> 
			 * <p>E.g. when we've just initialised the game we have a set of asteroids
			 * and our (still stationary) spaceship all set up in a list.  But we need
			 * to be able to reference the spaceship directly so we can manipulate it's
			 * rotation and velocity.  This helper method scans the list and plucks out
			 * the correct reference</p>
			 * 
			 * @param movingObjectsOnScreen
			 * @return the spaceship!! (null if none found)
			 */	
			private List<GameObject> findGameObjectsInList(GoClass gameObjectClass)	{
				List<GameObject> gameObjects = null ;

				// Check for null to avoid NPE on application startup...
				if (movingObjectsOnScreen != null) {
					// One of the objects we've just created is the spaceship.
					// We need to be able to reference this object directly so
					// we can control it's position.
					for (GameObject gameObject: movingObjectsOnScreen) {
						// The spaceship is in the list... and it's either a 
						// Sprite or an 'AsteroidsShape' (wish we had a better
						// name for these).  So search for it and assign it's
						// reference to the spaceship object.

						if (( gameObject.gameObjectClass == gameObjectClass))
						{
							if (gameObjects==null){// i'm not sure why but it needs that if when the list is empty otherwise error..
								gameObjects = new ArrayList<>();
								gameObjects.add(gameObject);
							}
							else{
								gameObjects.add(gameObject);
							}
						}
					}
				}

				return gameObjects;
			}

			/** Remove from the movingObjectsOnScreen list the bullets
			 * that are out of the screen boundaries 
			 * because bullets do not wrap around like the ship.
			 * 
			 * @return List<GameObject>
			 */
			private List<GameObject> removeBulletOutScreen()
			{
				List<GameObject> spaceshipBulletsOnScreen = null;
				spaceshipBulletsOnScreen = findGameObjectsInList(GameObject.GoClass.BULLET);
				if( spaceshipBulletsOnScreen != null) { //avoid error if no bullet on screen 
					for(int i = 0;i<spaceshipBulletsOnScreen.size();i++) { 
						//check if the bullet coordinates if outside the screen boundaries
						int indexBullet = movingObjectsOnScreen.indexOf(spaceshipBulletsOnScreen.get(i)) ;
						if(spaceshipBulletsOnScreen.get(i).position.getX() > Configuration.SCENE_WIDTH || 
								spaceshipBulletsOnScreen.get(i).position.getX() < 0 ||
								spaceshipBulletsOnScreen.get(i).position.getY() > Configuration.SCENE_HEIGHT ||
								spaceshipBulletsOnScreen.get(i).position.getY() < 0) {
							movingObjectsOnScreen.remove(indexBullet);
						}						
					}
				}
				return movingObjectsOnScreen;
			}

			/** Alien Bullets can't travel the full width of the screen like the
			 * bullets from the main ship can.  If the alien bullet out of range,
			 * it should be removed
			 * 
			 * @return void
			 */
			private void removeAlienBulletWhenHitsMaxRange() {
				// Check for null to avoid NPE on startup...
				if (movingObjectsOnScreen != null) {
					for(GameObject gameObject: movingObjectsOnScreen) {
						if (gameObject.gameObjectClass == GameObject.GoClass.ALIEN_BULLET)
						{
							double dx = gameObject.position.getX()-gameObject.initialX;
							double dy = gameObject.position.getY()-gameObject.initialY;
							double d = Math.sqrt(Math.pow(dx,2)+ Math.pow(dy,2));
							if(d>400){
								movingObjectsOnScreen.remove(gameObject);
								break;
							}
						}
					}
				}
			}

			/** check if one of the spaceship bullet has hit 
			 * an object on screen, either an asteroid or the alien ship
			 * 
			 * @return void
			 */
			private List<GameObject> collisionBulletAsteroidAlien()
			{
				//find all the bullets 
				List<GameObject> spaceshipBulletsOnScreen
				= findGameObjectsInList(GameObject.GoClass.BULLET);

				//find all the asteroids 
				List<GameObject> asteroidsAlienOnScreen = null;
				asteroidsAlienOnScreen = Stream.of(findGameObjectsInList(GameObject.GoClass.ASTEROID_SMALL),
						findGameObjectsInList(GameObject.GoClass.ASTEROID_MEDIUM),
						findGameObjectsInList(GameObject.GoClass.ASTEROID_LARGE),
						findGameObjectsInList(GameObject.GoClass.ALIEN))
						.flatMap(x -> x == null? null : x.stream()) //don't add is null, avoid errors 
						.collect(Collectors.toList());

				//collision all spaceship bullets against all asteroids 
				if( spaceshipBulletsOnScreen != null) { //avoid error if no bullet on screen 
					//System.out.println("num bullet  :  " + bulletList.size());
					for(int i = 0;i<spaceshipBulletsOnScreen.size();i++) {
						for(int j = 0;j<asteroidsAlienOnScreen.size();j++) {
							if( spaceshipBulletsOnScreen.get(i).isHitting(asteroidsAlienOnScreen.get(j)) ){
								//create a list with elements that needs to be removed after a collision  
								List<GameObject> removeElements = new ArrayList<GameObject>();
								removeElements.add(spaceshipBulletsOnScreen.get(i));
								removeElements.add(asteroidsAlienOnScreen.get(j));

								//System.out.println("x  destroyed :  " + asteroidsOnScreen.get(j).position.getX() + "y  destroyed :  " + asteroidsOnScreen.get(j).position.getY());

								//@Bryan
								// IDEA to alter the score if the spaceship hits the something...Need to call this adjust function within gamestate classs

								//GameState.adjustScore(5);

								//store the coordinated of the 'original'asteroids'
								double xOriginalAsteroid = asteroidsAlienOnScreen.get(j).position.getX();
								double yOriginalAsteroid = asteroidsAlienOnScreen.get(j).position.getY();
								// Not used?
								//								double rotationOriginalAsteroid = asteroidsAlienOnScreen.get(j).rotation ;

								// Remove the bullet and asteroid (just destroyed) from 
								// the on-screen collection.
								movingObjectsOnScreen.removeAll(removeElements); // remove simultaneously both elements

								// Not Used?
								//								double xOffset = Math.cos(Math.toRadians(rotationOriginalAsteroid));
								//								double yOffset = Math.sin(Math.toRadians(rotationOriginalAsteroid));
								//create two new asteroids
								for(int h = -1 ; h <2 ; h+=2) {
									//points ++ en fonction de la taille de l'astéroide ? 
									//									if (asteroidsOnScreen.get(j) instanceof AsteroidsShape
									//											&& ((AsteroidsShape) asteroidsOnScreen.get(j)).type == AsteroidsShape.InGameShape.ASTEROID_LARGE) {
									//										createMediumAsteroid(xOriginalAsteroid,yOriginalAsteroid,rotationOriginalAsteroid);
									//									}
									//									if (asteroidsOnScreen.get(j) instanceof AsteroidsShape
									//											&& ((AsteroidsShape) asteroidsOnScreen.get(j)).type == AsteroidsShape.InGameShape.ASTEROID_MEDIUM) {
									//										createSmallAsteroid(xOriginalAsteroid,yOriginalAsteroid,rotationOriginalAsteroid);
									//
									//									}
									// If large asteroid create two medium...
									if (asteroidsAlienOnScreen.get(j).gameObjectClass == GameObject.GoClass.ASTEROID_LARGE) {
										playSoundEffect(SoundEffects.BANG_LARGE);

										// You hit a big asteroid - get 100 points!
										gameState.incrementScore(100);

										double Offset = 0.45;

										createAsteroid(
												GameObject.GoClass.ASTEROID_MEDIUM,
												xOriginalAsteroid + (h * Offset) * Configuration.ASTEROID_MED_SIZE,
												yOriginalAsteroid  + (h * Offset) * Configuration.ASTEROID_MED_SIZE,
												Configuration.ASTEROID_MED_SPEED);
									}
									// If medium asteroid create two small...
									if (asteroidsAlienOnScreen.get(j).gameObjectClass == GameObject.GoClass.ASTEROID_MEDIUM) {
										// Medium Bang!
										playSoundEffect(SoundEffects.BANG_MEDIUM);

										// You hit a medium asteroid - get 150 points!
										gameState.incrementScore(150);

										double Offset = 0.1;
										createAsteroid(
												GameObject.GoClass.ASTEROID_SMALL,
												xOriginalAsteroid + ( h * Offset ) * Configuration.ASTEROID_SML_SPEED,
												yOriginalAsteroid  + ( h * Offset ) * Configuration.ASTEROID_SML_SPEED,
												Configuration.ASTEROID_SML_SPEED);
									}
									// If small asteroid no need to create anything...
									// just do the small bang and give the player some score!
									if (asteroidsAlienOnScreen.get(j).gameObjectClass == GameObject.GoClass.ASTEROID_SMALL) {
										// Small Bang!
										playSoundEffect(SoundEffects.BANG_SMALL);

										// You hit a medium asteroid - get 250 points!
										gameState.incrementScore(250);
									}

									if (asteroidsAlienOnScreen.get(j).gameObjectClass == GameObject.GoClass.ALIEN) {
										// Small Bang - puny alien!
										playSoundEffect(SoundEffects.BANG_SMALL);

										// You hit a alien - get 300 points!
										gameState.incrementScore(300);
									}
								}										
							}
						}

					}
				}

				if (asteroidsAlienOnScreen.isEmpty()) {
					// If we've destroyed all the asteroids on screen then the
					// level is over! Move to the next level
					gameState.setLevel(gameState.getLevel() + 1);
					getStartOfLevelMovingObjects();
				}
				return movingObjectsOnScreen;
			}

			/** We occasionally want to play sound effects. Play one. Now...
			 * at this point in time we don't really care if there is an error.
			 * So trap any reported errors here and log them - but the gameplay
			 * should never be affected on account of a bad sound effect.
			 * @throws URISyntaxException
			 */
			private void playSoundEffect(SoundEffects soundEffect) {
				try {
					// Big BanG!
					Media sound = new Media(Main.class.getResource(soundEffect.path).toURI().toString());
					MediaPlayer mediaPlayer = new MediaPlayer(sound);
					mediaPlayer.play();
				}
				catch (URISyntaxException USE) {
					// ####################################################### LOGGING??
					System.out.println(USE.getStackTrace());
				}
			}

			/**Create a singular new asteroid
			 * it's position is based on the original asteroid
			 * a direction is random
			 *
			 * @param AsteroidsShape.InGameShape asteroidType, double initialX, double initialY , int speed
			 * @return void
			 */
			private void createAsteroid(GameObject.GoClass asteroidClass, double initialX, double initialY , int speed )
			{
				GameObject myNewAsteroid;
				if (Configuration.GRAPHICS_MODE == Configuration.GraphicsMode.ARCADE) {
					myNewAsteroid = new Sprite(Sprite.getGraphicsForGOClass(asteroidClass));
				}
				else {
					myNewAsteroid = new AsteroidsShape(AsteroidsShape.getInGameShapeForGOClass(asteroidClass));
				}

				myNewAsteroid.position = new GameVector( initialX,initialY);

				Random r = new Random();
				double randomRotation = 0 + (360 - 0) * r.nextDouble();

				myNewAsteroid.rotation = randomRotation;

				double changeXAsteroid
					= Math.cos(Math.toRadians(myNewAsteroid.rotation)) * speed;
				double changeYAsteroid
					= Math.sin(Math.toRadians(myNewAsteroid.rotation)) * speed;

				// Don't violate maximum speed limit
				GameVector newVelocity = myNewAsteroid.velocity.add(changeXAsteroid, changeYAsteroid);

				// Now we want to add those velocity increments to the current velocity!
				myNewAsteroid.velocity = newVelocity;

				movingObjectsOnScreen.add(myNewAsteroid);
				return;
			}

			/** check if the spaceship has hit a asteroid or has been hit by an alien bullet
			 * then lose a life
			 * 
			 * @return void
			 */
			private void collisionSpaceship()
			{
				// find the spaceship
				List<GameObject> gameObjects = findGameObjectsInList(GameObject.GoClass.SPACESHIP);

				// We check for null/no spaceship to avoid NPE's on game startup...
				if (gameObjects != null && gameObjects.size() > 0)
				{
					spaceship = gameObjects.get(0);
					//find all the asteroids 
					List<GameObject> asteroidsOnScreen = null;
					asteroidsOnScreen = Stream.of(findGameObjectsInList(GameObject.GoClass.ASTEROID_SMALL),
							findGameObjectsInList(GameObject.GoClass.ASTEROID_MEDIUM),
							findGameObjectsInList(GameObject.GoClass.ASTEROID_LARGE))
							.flatMap(x -> x == null? null : x.stream()) //don't add is null, avoid errors 
							.collect(Collectors.toList());

					for(int i = 0;i<asteroidsOnScreen.size();i++) {
						if( spaceship.isHitting(asteroidsOnScreen.get(i)) ) {
							spaceshipHasBeenHit();
						}
					}

					List<GameObject> alienBulletsOnScreen = null;
					alienBulletsOnScreen = findGameObjectsInList(GameObject.GoClass.ALIEN_BULLET);
					if (alienBulletsOnScreen != null && alienBulletsOnScreen.size() > 0)
					{
						for(int j = 0;j<alienBulletsOnScreen.size();j++) {
							if( spaceship.isHitting(alienBulletsOnScreen.get(j)) ) {
								spaceshipHasBeenHit();
							}
						}
					}
				}

			}

			/** Player spaceship has been hit!  Is this the end?  Carry out all
			 * expected steps when the player spaceship is hit.  Play the
			 * explosion, reduce the lives by one, check if it's game over... etc..
			 * 
			 */
			private void spaceshipHasBeenHit() {
				// Awww... we blew up! BOOM!
				playSoundEffect(SoundEffects.BANG_LARGE);

				GameState gameState = GameState.getInstance();
				gameState.incrementScore(-50);
				gameState.loseALife();

				// After we die we're invincible for a bit.  But more importantly,
				// while the player death plays out on screen, we want to make
				// sure the game doesn't keep registering more deaths.
				spaceship.canBeHit = false;
				// At the moment there's just a boom and the spaceship disappears
				// TODO: Add an animation/explosion!
				spaceship.willRender = false;
				spaceship.position = centerOfScreen;
				spaceship.velocity = new GameVector();

				// You've just died!
				if (gameState.getLives() == 0) {
					// If this player was good enough... add them to the Hall of
					// Fame!
					Configuration.insertPlayerToLeaderTableIfHighEnough();

					// No lives left... pause the screen for a bit to give the
					// player a chance to digest the enormity of what's happened...
					timers.put(Timer.TIMER_CLASS.LOSE_THE_GAME, new Timer(0));

					// Stop stuff moving - allows the animation loop to keep going
					// but prevents any further collisions/impacts.
					Main.ctrlAllowMovement = false;

					Label gameOver= (Label) mainGameNamespace.get("mainGameGameOver");
					gameOver.setVisible(true);
				}
				else {
					// Losing a life hurts... but it's not like you've lost
					// the game.
					timers.put(Timer.TIMER_CLASS.LOSE_A_LIFE, new Timer(0));
				}
			}


			/** <p>Set the List 'movingObjectsOnScreen' to an initial state</p>
			 * <p>When starting a game of asteroids the player spaceship is static, on
			 * screen and surrounded by floating asteroids.  Set up this list.</p>
			 * @return
			 */
			public List<GameObject> getStartOfLevelMovingObjects() {

				// Grab the current gameState so we know how many asteroids to spawn...
				GameState gameState = GameState.getInstance();

				// We keep track of all the objects on screen.  The list is created
				// above, but when starting a level we make sure to start with a 
				// clean list...
				movingObjectsOnScreen.clear();

				// Create on-screen objects at the very start of the game:
				GameObject spaceship;

				// Initialise the startup objects...
				if (Configuration.GRAPHICS_MODE == Configuration.GraphicsMode.ARCADE) {
					spaceship = new Sprite(Graphics.SPACESHIP);

					// We create 1 asteroid on level 1, two asteroids on level 2, etc. etc.
					// so the game gets harder and harder as we move through the levels...
					for (int i = 0; i < gameState.getLevel() ; i++) {
						Sprite initialAsteroid  = new Sprite(Graphics.ASTEROID,
								Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2),
								Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2) );    // ############### WHY DO PICTURES APPEAR SMALLER THAN POLYGONS?!? INVESTIGATE!
						initialAsteroid.randomPosRotVelInit();
						movingObjectsOnScreen.add(initialAsteroid);
						// If you want to see the asteroid on the plain layout (no graphics) then
						// all you have to do is uncomment the following line... (useful for debugging)...
						// mainGamePane.getChildren().add(initialAsteroid1.hitModel);
					}
				}
				// Configure funny pictures in a non-published game state?? Lol...
				//		else if (Configuration.GRAPHICS_MODE == Configuration.GraphicsMode.EASTER_EGG) {
				//
				//		}
				else {
					// Configuration.GraphicsMode.CLASSIC
					// Default to 'Classic' mode where the game objects are Polygons..
					spaceship = new AsteroidsShape(AsteroidsShape.InGameShape.SPACESHIP);

					// We create 1 asteroid on level 1, two asteroids on level 2, etc. etc.
					// so the game gets harder and harder as we move through the levels...
					for (int i = 0; i < gameState.getLevel() ; i++) {
						AsteroidsShape initialAsteroid  = new AsteroidsShape(AsteroidsShape.InGameShape.ASTEROID_LARGE);
						initialAsteroid.randomPosRotVelInit();
						movingObjectsOnScreen.add(initialAsteroid);
						// If you want to see the asteroid on the plain layout (no graphics) then
						// all you have to do is uncomment the following line... (useful for debugging)...
						// mainGamePane.getChildren().add(initialAsteroid1.hitModel);
					}
				}

				spaceship.position = centerOfScreen;
				movingObjectsOnScreen.add(spaceship);
				// If you want to see the spaceship on the plain layout (no graphics) then
				// all you have to do is uncomment the following line... (useful for debugging)...
				// mainGamePane.getChildren().add(spaceship.hitModel);

				return movingObjectsOnScreen;
			}

		};
		gameloop.start();

	}

	//##########################################################################
	//          EVERYTHING THAT FOLLOWS IS OUTSIDE THE GAME LOOP
	//##########################################################################

	/* GETTERS AND SETTERS */
	/** Get the reference to the main game scene.
	 * @return
	 */
	public static Scene getMainGameScene() {
		return mainGameScene;
	}
	/** Set the ResetGameState ctrl flag
	 * @param ctrlResetGameState
	 */
	public static void setCtrlResetGameState(boolean ctrlResetGameState) {
		Main.ctrlResetGameState = ctrlResetGameState;
	}

}


