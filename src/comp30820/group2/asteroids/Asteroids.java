package comp30820.group2.asteroids;

import java.net.URISyntaxException;
import java.util.ArrayList;

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
			
		
		// The BorderPane allows us to set containers/to set objects and nodes in
		// different regions of the screen. We want ours to be right in the center.
//		context.setFill(Color.BLACK);
//		context.fillRect(0,0,Configuration.SCENE_WIDTH,Configuration.SCENE_HEIGHT);
		// Load the image file (path is relative to class loader!)
		// The background is a special case, we make sure it's scaled to the size
		// of our background (as per the config file!)
		GameObject background
			= new Sprite(Graphics.SPACE.path,
					Double.valueOf(Configuration.SCENE_WIDTH),
					Double.valueOf(Configuration.SCENE_HEIGHT) );
		background.position = new GameVector( (Configuration.SCENE_WIDTH / 2),(Configuration.SCENE_HEIGHT / 2) );

		GameObject spaceship;
		if (Configuration.GRAPHICS_MODE == Configuration.GraphicsMode.ARCADE) {
			spaceship = new Sprite(Graphics.SPACESHIP.path);
		}
		else if (Configuration.GRAPHICS_MODE == Configuration.GraphicsMode.EASTER_EGG) {
			spaceship = new Sprite(Graphics.SPACESHIP.path);
		}
		else {
			// Configuration.GraphicsMode.CLASSIC
			// Default to 'Classic' mode where the game objects are Polygons..
			spaceship = new AsteroidsShape(AsteroidsShape.InGameShape.SPACEHIP);
		}
		spaceship.position = new GameVector( (Configuration.SCENE_WIDTH / 2),(Configuration.SCENE_HEIGHT / 2) );
		//spaceship.velocity.set(50,0);
		//spaceship.render(context);
		
		// The AnimationTimer is a way you can create a set of code that will run
		// 60 times per second in JavaFX
		// Anonymous inner class????????
		AnimationTimer gameloop = new AnimationTimer() {
			public void handle(long nanotime) {
				// Code we want to run goes here...
				
				// Process user input
				if (keyPressedList.contains("LEFT")) {
					spaceship.rotation -= 3;
				}
				if (keyPressedList.contains("RIGHT")) {
					spaceship.rotation += 3;
				}
				// For UP we want to make sure we move in the direction the
				// spaceship is facing!!
				if (keyPressedList.contains("UP")) {
					try {
						// Fire thrusters!!
						// We use Asteroids as our resource-anchor class...
						Media sound = new Media(Asteroids.class.getResource(SoundEffects.THRUST.path).toURI().toString());
						MediaPlayer mediaPlayer = new MediaPlayer(sound);
						mediaPlayer.play();
					}
					catch (URISyntaxException USE) {
						// ####################################################### LOGGING??
						System.out.println(USE.getStackTrace());
					}
					// Think of the length of the velocity vector as 'speed'
					spaceship.velocity = spaceship.velocity.lengthSetTo(100);
					spaceship.velocity = spaceship.velocity.angleSetTo(spaceship.rotation);
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
				spaceship.update(1/60.0);

				background.render(context);
				spaceship.render(context);
			}
		};
		gameloop.start();

		mainStage.show();
	}
}
