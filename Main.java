
package comp30820.group2.asteroids;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import comp30820.group2.asteroids.Configuration.SoundEffects;
import comp30820.group2.asteroids.Sprite.Graphics;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
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
	private static boolean ctrlResetGameState;
	
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
        KeyStrokeManager.getInstance().manageThisScene(welcomeScene);
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
    	Parent mainGameRoot = (Parent) mainGameLoader.load();
    	// Create our mainGameScene.  We want to use this scene over and over
    	// so it gets created once as the game starts up and then we draw to the
    	// canvas it contains whenever it's presented on the stage.
        Main.mainGameScene = new Scene(mainGameRoot);
        Main.mainGameScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
        AsteroidsFXMLController controller = mainGameLoader.<AsteroidsFXMLController>getController();

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
					Double.valueOf(Configuration.SCENE_HEIGHT) );
		background.position = new GameVector( (Configuration.SCENE_WIDTH / 2),(Configuration.SCENE_HEIGHT / 2) );

		//######################################################################
		//                         THE ANIMATION / RUNNING GAME
		//######################################################################
		
		// The first time we enter the animation timer, we definitely want to 
		// set the game state to initial settings.
		Main.ctrlResetGameState = true;

		// The AnimationTimer is a way you can create a set of code that will run
		// 60 times per second in JavaFX
		// Anonymous inner class????????
		AnimationTimer gameloop = new AnimationTimer() {

			// The players spaceship is a special object as it's motion is controlled
			// reacting to input from the keyboard (from the user)
			GameObject spaceship = null;
			// We keep track of all the objects on screen.
			List<GameObject> movingObjectsOnScreen = null;
			// Create an empty array of bullets 
			List<GameObject> bulletArr = null;

			public void handle(long nanotime) {
				// Code we want to run goes here...

				// The animation loop runs all the time.  If a process sets the
				// following ctrl boolean to true, we reset all the game objects
				// to their initial conditions.  This gives us a way to reset
				// for a new game if we want to.
				if (Main.ctrlResetGameState) {
					// First, create and initialise the list of moving game objects
					// #################### TODO MIGHT THIS CHANGE IF WE INTRODUCE LEVELS????
					movingObjectsOnScreen = setGameToInitialState();
					// Get the reference for the spaceship so we can steer it.
					spaceship = findSpaceshipInList(movingObjectsOnScreen);
					// Create an empty array of bullets 
					bulletArr = new ArrayList<GameObject>();
					
					Main.ctrlResetGameState = false;
				}

				// Process user input
				// FOR CONSIDERATION: Should we extract our keystrokes and make
				//                    them part of the configuration file so users
				//                    can choose/save their own.
				if (keys.getKeyPressedList().contains("LEFT")) {
					spaceship.rotation -= 3;
				}
				if (keys.getKeyPressedList().contains("Q")) {
					try {
						controller.activateScene(mainStage, Configuration.GameWindows.END_OF_GAME);
					}
					catch (IOException IOe) {
						IOe.printStackTrace();
					}
				}
				if (keys.getKeyPressedList().contains("RIGHT")) {
					spaceship.rotation += 3;
				}
				// For UP we want to make sure we move in the direction the
				// spaceship is facing!!
				if (keys.getKeyPressedList().contains("UP")) {
					// The animation timer runs 60 times a second.  If a player presses a button
					// that triggers a sound to play (like say the ships thrusters) then - if the
					// player holds the button down - you can get an audio distortion as the media
					// player attempts to play the clip over and over again (60 times per second)
					// HOW TO DEAL WITH THIS?

					try {
						// Fire thrusters!!
						// We use Asteroids as our resource-anchor class...
						Media sound = new Media(Main.class.getResource(SoundEffects.THRUST.path).toURI().toString());
						MediaPlayer mediaPlayer = new MediaPlayer(sound);
						//mediaPlayer.setCycleCount(MediaPlayer.INDEFINITE);
						
						
						
						mediaPlayer.play();
						//							mediaPlayer.setOnEndOfMedia(() -> {
						//								
						//							 });
					}
					catch (URISyntaxException USE) {
						// ####################################################### LOGGING??
						System.out.println(USE.getStackTrace());
					}
					// Think of the length of the velocity vector as 'speed'

					// @Wendy should the following be a method in GameObject perhaps???  spacheship.fireEngines??????????
					//---------------------------------------------------------------------------------------
					// from here....
					// First crude attempt:  Just start the spaceship moving
					// instantly in the direction it's facing, at a fixed speed:
					// spaceship.velocity = spaceship.velocity.lengthSetTo(100);
					// spaceship.velocity = spaceship.velocity.angleSetTo(spaceship.rotation);

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

				}
				// For UP we want to make sure we move in the direction the
				// spaceship is facing!!
				if (keys.removeActiveKey("SPACE")) {
					try {
						//System.out.println("Size of the array, number of bullet " + bulletArr.size() + " "+ spaceship.position.getX() + "  " + spaceship.rotation);

						// @Elise all the bullet stuff here now...
						// Fire!!
						//add a bullet to the array of bullets on the screen
						//get the initial position of the bullet based on the spaceship position 
						double bulletIniX = spaceship.position.getX() + Math.cos(Math.toRadians(spaceship.rotation)) * 12;
						double bulletIniY = spaceship.position.getY() + Math.sin(Math.toRadians(spaceship.rotation)) * 12;
						bulletArr.add(new AsteroidsShape(AsteroidsShape.InGameShape.BULLET));
						bulletArr.get(bulletArr.size()-1).position = new GameVector( bulletIniX,bulletIniY);
						bulletArr.get(bulletArr.size()-1).rotation = spaceship.rotation;


						double changeX
						= Math.cos(Math.toRadians(bulletArr.get(bulletArr.size()-1).rotation)) * Configuration.SPEED_BULLET;
						double changeY
						= Math.sin(Math.toRadians(bulletArr.get(bulletArr.size()-1).rotation)) * Configuration.SPEED_BULLET;

						// Don't violate maximum speed limit
						GameVector newVelocity = bulletArr.get(bulletArr.size()-1).velocity.add(changeX, changeY);
						if (newVelocity.getLength() > Configuration.SPEED_MAX) {
							newVelocity.lengthSetTo(Configuration.SPEED_MAX);
						}
						// Now we want to add those velocity increments to the current velocity!
						bulletArr.get(bulletArr.size()-1).velocity = newVelocity;

						//est ce que toutes les bullet on la meme vitesse ? et la vitesse est constante right ? 
						// creer la bullet a la fin du vaisceau ?  DONE
						//mm c'est pas encore ca hein ? 
						//est ce que la vitesse dela bullet depend de la vitess du vaisceau ? 
						//ajouter une fonction pour supprimer la bullet once it's out of the screen 
						// We use Asteroids as our resource-anchor class...
						Media sound = new Media(Main.class.getResource(SoundEffects.FIRE.path).toURI().toString());
						MediaPlayer mediaPlayer = new MediaPlayer(sound);
						mediaPlayer.play();
						
					}
					catch (URISyntaxException USE) {
						// ####################################################### LOGGING??
						System.out.println(USE.getStackTrace());
					}
				}

				// #################################################################
				// Deal with keyReleases? Set boolean flag on key press so that, for
				// example, we don't attempt to play a sound a second time until the
				// key releases?  Can we detect if "the sound has ended"?? Address

				// We know the frame rate of the AnimationTimer, so every time
				// the 'handle' gets called, we know that 1/60th of a second has
				// passed.
				// This is ordered from background to front 
				// Instead of running each update individually, we can use a Lambda Expression to
				// the method on each object as we iterate over the list.
				//spaceship.update(1/60.0);
				//asteroid.update(1/60.0);
				// LAMBDA EXPRESSION
				movingObjectsOnScreen.forEach( (object) -> object.updatePosition(1/60.0));

				// COLLISION DETECTION?????????????????????????????

				background.render(context);

				// LAMBDA EXPRESSION
				movingObjectsOnScreen.forEach( (object) -> object.render(context));

				for (int i = 0; i < bulletArr.size(); i++) {
//					GameObject bullet;
//					bullet  = new AsteroidsShape(AsteroidsShape.InGameShape.BULLET);
//					bullet.randomInit();
//					bullet.updatePosition(1/60.0);
//					bullet.render(context);
					
					bulletArr.get(i).updatePosition(1/60.0);
					//					System.out.println("Spaceship" + spaceship.position.toString());
					//					System.out.println("\tBullet" + bulletArr.get(i).position.toString());
					bulletArr.get(i).render(context);
				}

				//##############################################################
				//##############################################################

				//for(int i = 1;i<movingObjectsOnScreen.size();i++) {
//				if(Shape.intersect(spaceship.hitModel(),initialAsteroid1.hitModel()).getBoundsInLocal().getWidth() !=-1) {
//					// TODO Print out the coordinates of everything to see what's up!!
//					//System.out.println("Collision detected!!!" + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
//				}

				//System.out.println("square"+asteroid1.hitModel().getBoundsInLocal());
				//System.out.println("spaceship"+spaceship.hitModel().getBoundsInLocal());
				//System.out.println("inter"+Shape.intersect(spaceship.hitModel(),asteroid1.hitModel()).getBoundsInLocal());
				//##############################################################
				//##############################################################

			}

		};
		gameloop.start();
    	
	}

    /** <p>Set the List 'movingObjectsOnScreen' to an initial state</p>
     * <p>When starting a game of asteroids the player spaceship is static, on
     * screen and surrounded by floating asteroids.  Set up this list.</p>
     * @return
     */
    public List<GameObject> setGameToInitialState() {

		// We keep track of all the objects on screen.
		List<GameObject> movingObjectsOnScreen = new ArrayList<GameObject>();

		// We also keep track of all the possible pairs of objects on screen (so
		// that we can check for collisions).
		// ????????????? Can asteroids hit other asteroids? What happens? ???????????????????????????  ANSWER ME

		// Create on-screen objects at the very start of the game:
		GameObject spaceship;
		GameObject initialAsteroid1;
		GameObject initialAsteroid2;
		GameObject initialAsteroid3;

		// Initialise the startup objects...
		if (Configuration.GRAPHICS_MODE == Configuration.GraphicsMode.ARCADE) {
			spaceship = new Sprite(Graphics.SPACESHIP);
			initialAsteroid1  = new Sprite(Graphics.ASTEROID,
					Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2),
					Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2) );    // ############### WHY DO PICTURES APPEAR SMALLER THAN POLYGONS?!? NO IDEA??
			initialAsteroid2  = new Sprite(Graphics.ASTEROID,
					Double.valueOf(Configuration.ASTEROID_MED_SIZE*2),
					Double.valueOf(Configuration.ASTEROID_MED_SIZE*2) );    // ############### WHY DO PICTURES APPEAR SMALLER THAN POLYGONS?!? NO IDEA??
			initialAsteroid3  = new Sprite(Graphics.ASTEROID,
					Double.valueOf(Configuration.ASTEROID_SML_SIZE*2),
					Double.valueOf(Configuration.ASTEROID_SML_SIZE*2) );    // ############### WHY DO PICTURES APPEAR SMALLER THAN POLYGONS?!? NO IDEA??
		}
		// Configure funny pictures in a non-published game state?? Lol...
		//		else if (Configuration.GRAPHICS_MODE == Configuration.GraphicsMode.EASTER_EGG) {
		//			spaceship = new Sprite(Graphics.SPACESHIP);
		//			asteroid1  = new Sprite(Graphics.ASTEROID);
		//		}
		else {
			// Configuration.GraphicsMode.CLASSIC
			// Default to 'Classic' mode where the game objects are Polygons..
			spaceship = new AsteroidsShape(AsteroidsShape.InGameShape.SPACESHIP);
			initialAsteroid1  = new AsteroidsShape(AsteroidsShape.InGameShape.ASTEROID_LARGE);
			initialAsteroid2  = new AsteroidsShape(AsteroidsShape.InGameShape.ASTEROID_MEDIUM);
			initialAsteroid3  = new AsteroidsShape(AsteroidsShape.InGameShape.ASTEROID_SMALL);
		}
		spaceship.position = new GameVector( (Configuration.SCENE_WIDTH / 2),(Configuration.SCENE_HEIGHT / 2) );
		initialAsteroid1.randomInit();
		initialAsteroid2.randomInit();
		initialAsteroid3.randomInit();

		movingObjectsOnScreen.add(spaceship);
		movingObjectsOnScreen.add(initialAsteroid1);
		movingObjectsOnScreen.add(initialAsteroid2);
		movingObjectsOnScreen.add(initialAsteroid3);

		return movingObjectsOnScreen;
    }
    
	/** <p>Look for and return the spaceship (player ship) object reference from 
	 * a list of GameObjects</p> 
	 * <p>When we've just initialised the game we have a set of asteroids and our
	 * (still stationary) spaceship all set up in a list.  But we need to be able
	 * to reference the spaceship directly so we can manipulate it's rotation and
	 * velocity.  This helper method scans the list and plucks out the correct
	 * reference</p>
	 * 
	 * @param movingObjectsOnScreen
	 * @return the spaceship!! (null if none found)
	 */
	private GameObject findSpaceshipInList(List<GameObject> movingObjectsOnScreen)
	{
		GameObject spaceship = null;

		// One of the objects we've just created is the spaceship.
		// We need to be able to reference this object directly so
		// we can control it's position.
		for (GameObject newGameObject: movingObjectsOnScreen) {
			// The spaceship is in the list... and it's either a 
			// Sprite or an 'AsteroidsShape' (wish we had a better
			// name for these).  So search for it and assign it's
			// reference to the spaceship object.
			if ((newGameObject instanceof Sprite
					&& ((Sprite) newGameObject).type == Sprite.Graphics.SPACESHIP)
				||
				(newGameObject instanceof AsteroidsShape
						&& ((AsteroidsShape) newGameObject).type == AsteroidsShape.InGameShape.SPACESHIP))
			{
				spaceship = newGameObject;
			}
		}
		return spaceship;
	}

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