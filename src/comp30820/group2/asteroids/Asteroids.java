package comp30820.group2.asteroids;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import comp30820.group2.asteroids.Configuration.SoundEffects;
import comp30820.group2.asteroids.Sprite.Graphics;
import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;

/** Asteroids - the Classic Arcade Game.
 * 
 * This implementation leans heavily on an implementatin by Prof. Lee Stemkoski,
 * Professor of Mathematics and Computer Science, Adelphi University.  See:
 *     https://www.youtube.com/user/ProfStemkoski/search?query=asteroids
 * 
 * @author B. Agar Cox, E. Brard, T. Kelly, W. Song
 *
 */
/*MODIFICATIONS:
 * 22/03/nn ??; 
 * 
 */
public class Asteroids extends Application {
	
	// Define a constant for our Application Name
	public static final String APP_NAME = "COMP30820_Group2_Asteroids";
	
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
	public void start(Stage mainStage)
	{
		// The 'stage' is essentially the 'window' in which your application
		// will run.  JFX doesn't use terms like 'window' as it's designed to
		// support desktop, mobile and web applications.
		mainStage.setTitle("Asteroids");
		
		// Each stage has a scene (or scenes) and each scene requires a layout
		// manager.  The JavaFX SDK provides several layout panes for the easy
		// setup and management of classic layouts such as rows, columns, stacks,
		// tiles, and others.  Read more about the various options here:
		//   -> https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm

		// We're going to use a BorderPane object as our layout manager...
		BorderPane root = new BorderPane();
		// Then we create a scene based on the borderpane...
		Scene mainScene = new Scene(root);
		// Next we attach this scene to the stage (ours is 'mainStage'...)
		mainStage.setScene(mainScene);
		
		// We want to display some graphics, so we create a Canvas that we can 
		// display our graphics on and set it's size. The Canvas class basically
		// creates an image *that can be drawn on* using a set of graphics commands
		// provided by a "GraphicsContext". Canvas has a specified height and width
		// and all the drawing operations are clipped to the bounds of the canvas.
		Canvas canvas = new Canvas(Configuration.SCENE_WIDTH,Configuration.SCENE_HEIGHT);
		// In order to perform draw operations on the canvas we need to create
		// a graphicsContext object (we get it *from* the canvas.
		GraphicsContext context = canvas.getGraphicsContext2D();
		
		// Now we add our canvas to our layout pane (in the middle pane).
		root.setCenter(canvas);
		
		// Create an ArrayList to store the keys that are currently being pressed
		ArrayList<String> keyPressedList = new ArrayList<String>();
		
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
		mainScene.setOnKeyPressed(
				(KeyEvent event) ->
				{
					String keyName = event.getCode().toString();
					// Avoid adding duplicates to list
					if (!keyPressedList.contains(keyName)) {
						keyPressedList.add(keyName);
					}
				}
		);
		mainScene.setOnKeyReleased(
				(KeyEvent event) ->
				{
					String keyName = event.getCode().toString();
					// Avoid removing keys not list - should never happen!!!
					if (keyPressedList.contains(keyName)) {
						keyPressedList.remove(keyName);
					}
					
				}
		);
		
		//######################################################################
		//                         STARTUP GAME OBJECTS
		//######################################################################
		
		// The BorderPane allows us to set containers/to set objects and nodes in
		// different regions of the screen. We want ours to be right in the center.
//		context.setFill(Color.BLACK);
//		context.fillRect(0,0,Configuration.SCENE_WIDTH,Configuration.SCENE_HEIGHT);
		// Load the image file (path is relative to class loader!)
		// **** The background is a special case, we make sure it's scaled to the
		// size of our background (as per the config file!) and IT DOES NOT MOVE
		GameObject background
			= new Sprite(Graphics.SPACE.path,
					Double.valueOf(Configuration.SCENE_WIDTH),
					Double.valueOf(Configuration.SCENE_HEIGHT) );
		background.position = new GameVector( (Configuration.SCENE_WIDTH / 2),(Configuration.SCENE_HEIGHT / 2) );
		
		// We keep track of all the objects on screen.
		List<GameObject> movingObjectsOnScreen = new ArrayList<GameObject>();
		
		// We also keep track of all the possible pairs of objects on screen (so
		// that we can check for collisions).
		// ????????????? Can asteroids hit other asteroids? What happens? ???????????????????????????  ANSWER ME

		// Create on-screen objects at the very start of the game:
		GameObject spaceship;
		GameObject asteroid1;
		GameObject asteroid2;
		GameObject asteroid3;
		
		// Initialise the startup objects...
		if (Configuration.GRAPHICS_MODE == Configuration.GraphicsMode.ARCADE) {
			spaceship = new Sprite(Graphics.SPACESHIP.path);
			asteroid1  = new Sprite(Graphics.ASTEROID.path,
							Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2),
							Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2) );    // ############### WHY DO PICTURES APPEAR SMALLER THAN POLYGONS?!? NO IDEA??
			asteroid2  = new Sprite(Graphics.ASTEROID.path,
					Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2),
					Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2) );    // ############### WHY DO PICTURES APPEAR SMALLER THAN POLYGONS?!? NO IDEA??
			asteroid3  = new Sprite(Graphics.ASTEROID.path,
					Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2),
					Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2) );    // ############### WHY DO PICTURES APPEAR SMALLER THAN POLYGONS?!? NO IDEA??
		}
//		else if (Configuration.GRAPHICS_MODE == Configuration.GraphicsMode.EASTER_EGG) {
//			spaceship = new Sprite(Graphics.SPACESHIP.path);
//			asteroid1  = new Sprite(Graphics.ASTEROID.path);
//		}
		else {
			// Configuration.GraphicsMode.CLASSIC
			// Default to 'Classic' mode where the game objects are Polygons..
			spaceship = new AsteroidsShape(AsteroidsShape.InGameShape.SPACESHIP);
			asteroid1  = new AsteroidsShape(AsteroidsShape.InGameShape.ASTEROID_LARGE);
			asteroid2  = new AsteroidsShape(AsteroidsShape.InGameShape.ASTEROID_MEDIUM);
			asteroid3  = new AsteroidsShape(AsteroidsShape.InGameShape.ASTEROID_SMALL);
		}
		spaceship.position = new GameVector( (Configuration.SCENE_WIDTH / 2),(Configuration.SCENE_HEIGHT / 2) );
		asteroid1.randomInit();
		asteroid2.randomInit();
		asteroid3.randomInit();
		//spaceship.velocity.set(50,0);
		//spaceship.render(context);
		
		movingObjectsOnScreen.add(spaceship);
		movingObjectsOnScreen.add(asteroid1);
		//movingObjectsOnScreen.add(asteroid2);
		//movingObjectsOnScreen.add(asteroid3);
		
		//######################################################################
		//                         THE ANIMATION / RUNNING GAME
		//######################################################################
		
		// The AnimationTimer is a way you can create a set of code that will run
		// 60 times per second in JavaFX
		// Anonymous inner class????????
		AnimationTimer gameloop = new AnimationTimer() {
			public void handle(long nanotime) {
				// Code we want to run goes here...
				
				// Process user input
				// FOR CONSIDERATION: Should we extract our keystrokes and make
				//                    them part of the configuration file so users
				//                    can choose/save their own.
				if (keyPressedList.contains("LEFT")) {
					spaceship.rotation -= 3;
				}
				if (keyPressedList.contains("RIGHT")) {
					spaceship.rotation += 3;
				}
				// For UP we want to make sure we move in the direction the
				// spaceship is facing!!
				if (keyPressedList.contains("UP")) {
					// The animation timer runs 60 times a second.  If a player presses a button
					// that triggers a sound to play (like say the ships thrusters) then - if the
					// player holds the button down - you can get an audio distortion as the media
					// player attempts to play the clip over and over again (60 times per second)
					// HOW TO DEAL WITH THIS?

					try {
						// Fire thrusters!!
						// We use Asteroids as our resource-anchor class...
						Media sound = new Media(Asteroids.class.getResource(SoundEffects.THRUST.path).toURI().toString());
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
				    if (newVelocity.getLength() < Configuration.SPEED_MAX) {
						// Now we want to add those velocity increments to the current velocity!
					    spaceship.velocity = newVelocity;
				    }
				}
				// For UP we want to make sure we move in the direction the
				// spaceship is facing!!
				if (keyPressedList.contains("SPACE")) {
					try {
						// Fire lasers!!
						// We use Asteroids as our resource-anchor class...
						Media sound = new Media(Asteroids.class.getResource(SoundEffects.FIRE.path).toURI().toString());
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
// Instead of running each update individually, we can use a Lambda Expression to
// the method on each object as we iterate over the list.
//				spaceship.update(1/60.0);
//				asteroid.update(1/60.0);
				// LAMBDA EXPRESSION
				movingObjectsOnScreen.forEach( (object) -> object.updatePosition(1/60.0));
				
				// COLLISION DETECTION?????????????????????????????
				//for(int i = 1;i<movingObjectsOnScreen.size();i++) {
				if(Shape.intersect(spaceship.hitModel(),asteroid1.hitModel()).getBoundsInLocal().getWidth() !=-1) {
					stop();
					}
				

			//System.out.println("square"+asteroid1.hitModel().getBoundsInLocal());
			//System.out.println("spaceship"+spaceship.hitModel().getBoundsInLocal());
			System.out.println("inter"+Shape.intersect(spaceship.hitModel(),asteroid1.hitModel()).getBoundsInLocal());

				background.render(context);
				// LAMBDA EXPRESSION
				movingObjectsOnScreen.forEach( (object) -> object.render(context));
			}
		};
		gameloop.start();

		mainStage.show();
	}

	/* GETTERS AND SETTERS */

}
