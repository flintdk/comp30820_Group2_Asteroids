
package comp30820.group2.asteroids;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
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
    	
    	// Here in this 'start' method we start out with a 'mainStage' which is
    	// provided magically by JavaFX
    	FXMLLoader loader =
    			new FXMLLoader(getClass().getResource(Configuration.GameWindows.WELCOME_MAIN_MENU.fxmlResource));
    	loader.load();
    	AsteroidsFXMLController controller = loader.<AsteroidsFXMLController>getController();
    	controller.activateSceneFromStage(mainStage, Configuration.GameWindows.WELCOME_MAIN_MENU);
	}

	/* GETTERS AND SETTERS */

}