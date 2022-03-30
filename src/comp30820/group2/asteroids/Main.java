
package comp30820.group2.asteroids;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
		// "asteroids.fxml" specifies the layout manager for the scene based on
    	// this root.  At the time of writing it was a BorderPane - but having
    	// it specified in the .fxml means it can be tweaked and changed at will!
    	
    	// A scene graph is a tree data structure, most commonly found in graphical
    	// applications and libraries such as vector editing tools, 3D libraries and
    	// video games. The JavaFX scene graph is a retained mode API, meaning that
    	// it maintains an internal model of all graphical objects in your application.
    	// A 'Parent' is the root node of the JavaFX Scene Graph
    	
    	// Code for access to everything relating to JavaFX.  This might not be
    	// necessary - but for an abundance of caution...
    	FXMLLoader loader = new FXMLLoader(getClass().getResource("asteroidsBorderPane.fxml"));
    	Parent root = (Parent) loader.load();
    	//AsteroidsFXMLController controller = loader.<AsteroidsFXMLController>getController();

    	// Then we create a scene based on the FXML root...
    	Scene mainScene = new Scene(root);
    	mainScene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());

		// The 'stage' is essentially the 'window' in which your application
		// will run.  JFX doesn't use terms like 'window' as it's designed to
		// support desktop, mobile and web applications.
		mainStage.setTitle("Asteroids");
		
		// Each stage has a scene (or scenes) and each scene requires a layout
		// manager.  The JavaFX SDK provides several layout panes for the easy
		// setup and management of classic layouts such as rows, columns, stacks,
		// tiles, and others.  Read more about the various options here:
		//   -> https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm

		// Next we attach this scene to the stage (ours is 'mainStage'...)
		mainStage.setScene(mainScene);

		KeyStrokeManager.getInstance().manageThisScene(mainScene);
		
		mainStage.show();
	}

	/* GETTERS AND SETTERS */

}