package comp30820.group2.asteroids;

import java.util.ArrayList;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
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
public class Asteroids extends Application {
	
	public static void main(String[] args)
	{
		// Sanity check: Print out the working directory of the application.
		System.out.println("Asteroids: Working Directory = " + System.getProperty("user.dir"));

		try {
			launch(args);
		}
		catch (Exception error) {
			error.printStackTrace();
		}
		finally {
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
		Canvas canvas = new Canvas(800,600);
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
//		context.fillRect(0,0,800,600);
		Sprite background = new Sprite("file:img/space.png");
		background.position.set(400,300);
		//background.render(context);
		
		Sprite spaceship = new Sprite("file:img/spaceship.png");
		spaceship.position.set(100,300);
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
					// Think of the length of the velocity vector as 'speed'
					spaceship.velocity.setLength(100);
					spaceship.velocity.setAngle(spaceship.rotation);
				}
				else {  // Not pressing up
					spaceship.velocity.setLength(0);
				}
//				if (keyPressedList.contains("DOWN")) {
//					spaceship.velocity.setLength(50);
//					spaceship.velocity.setAngle(spaceship.rotation);
//				}

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
