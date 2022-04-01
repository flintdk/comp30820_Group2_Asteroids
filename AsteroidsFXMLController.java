package comp30820.group2.asteroids;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;
import java.util.ResourceBundle;

import comp30820.group2.asteroids.Configuration.SoundEffects;
import comp30820.group2.asteroids.Sprite.Graphics;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Shape;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AsteroidsFXMLController implements Initializable {

	// Our canvas is magically provided by JavaFX/FXML
	@FXML protected Canvas asteroidsGameCanvas;

	KeyStrokeManager keys = KeyStrokeManager.getInstance();
	
	// We occasionally need access to the stage for changine scenes etc..
	//    Not sure if this is the best approach?? ???????
	Stage stage;
	
	/** The initialize() method is called once on an implementing controller when
	 * the contents of its associated document have been completely loaded.  This
	 * allows the implementing class to perform any necessary post-processing on
	 * the content.
	 * 
	 * ###################################### ARE WE SURE THIS IS A GOOD SPOT FOR THE GAME BODY???
	 *
	 */
	public void initialize(URL url, ResourceBundle rb) {
		//String javaVersion = System.getProperty("java.version");
		//String javafxVersion = System.getProperty("javafx.version");

		initialiseCanvas();

		// We want to display some graphics, so we use the Canvas that is configured
		// in our FXML.  We can display our graphics on and set it's size. The Canvas
		// class basically creates an image *that can be drawn on* using a set of
		// graphics commands provided by a "GraphicsContext". Canvas has a specified
		// height and width and all the drawing operations are clipped to the bounds
		// of the canvas.

		// In order to perform draw operations on the canvas we need to create
		// a graphicsContext object (we get it *from* the canvas.
		GraphicsContext context = asteroidsGameCanvas.getGraphicsContext2D();

		//######################################################################
		//                         STARTUP GAME OBJECTS
		//######################################################################

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
		GameObject bullet;

		// Initialise the startup objects...
		if (Configuration.GRAPHICS_MODE == Configuration.GraphicsMode.ARCADE) {
			spaceship = new Sprite(Graphics.SPACESHIP);
			initialAsteroid1  = new Sprite(Graphics.ASTEROID,
					Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2),
					Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2) );    // ############### WHY DO PICTURES APPEAR SMALLER THAN POLYGONS?!? NO IDEA??
			initialAsteroid2  = new Sprite(Graphics.ASTEROID,
					Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2),
					Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2) );    // ############### WHY DO PICTURES APPEAR SMALLER THAN POLYGONS?!? NO IDEA??
			initialAsteroid3  = new Sprite(Graphics.ASTEROID,
					Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2),
					Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2) );    // ############### WHY DO PICTURES APPEAR SMALLER THAN POLYGONS?!? NO IDEA??
			bullet  = new Sprite(Graphics.LASER);    // ############### WHY DO PICTURES APPEAR SMALLER THAN POLYGONS?!? NO IDEA??
		}
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
			bullet  = new AsteroidsShape(AsteroidsShape.InGameShape.BULLET);
		}
		spaceship.position = new GameVector( (Configuration.SCENE_WIDTH / 2),(Configuration.SCENE_HEIGHT / 2) );
		initialAsteroid1.randomInit();
		initialAsteroid2.randomInit();
		initialAsteroid3.randomInit();
		bullet.randomInit();
		//spaceship.velocity.set(50,0);
		//spaceship.render(context);

		movingObjectsOnScreen.add(spaceship);
		movingObjectsOnScreen.add(initialAsteroid1);
		movingObjectsOnScreen.add(initialAsteroid2);
		movingObjectsOnScreen.add(initialAsteroid3);
		movingObjectsOnScreen.add(bullet);

		// Create an empty array of bullets 
		//GameObject[] bulletArr = new AsteroidsShape(AsteroidsShape.InGameShape.BULLET)[];
		List<GameObject> bulletArr = new ArrayList<>();

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
				if (keys.getKeyPressedList().contains("LEFT")) {
					spaceship.rotation -= 3;
				}
				if (keys.getKeyPressedList().contains("Q")) {
					try {
						activateSceneFromStage(stage, Configuration.GameWindows.END_OF_GAME);
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

					// @Wendy should the following be a methoed in GameObject perhaps???  spacheship.fireEngines??????????
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
						System.out.println("Size of the array, number of bullet " + bulletArr.size() + " "+ spaceship.position.getX() + "  " + spaceship.rotation);

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
					bulletArr.get(i).updatePosition(1/60.0);

					//					System.out.println("Spaceship" + spaceship.position.toString());
					//					System.out.println("\tBullet" + bulletArr.get(i).position.toString());

					bulletArr.get(i).render(context);
				}

				//##############################################################
				//##############################################################

				//for(int i = 1;i<movingObjectsOnScreen.size();i++) {
				if(Shape.intersect(spaceship.hitModel(),initialAsteroid1.hitModel()).getBoundsInLocal().getWidth() !=-1) {
					// TODO Print out the coordinates of everything to see what's up!!
					//System.out.println("Collision detected!!!" + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
				}

				//System.out.println("square"+asteroid1.hitModel().getBoundsInLocal());
				//System.out.println("spaceship"+spaceship.hitModel().getBoundsInLocal());
				//System.out.println("inter"+Shape.intersect(spaceship.hitModel(),asteroid1.hitModel()).getBoundsInLocal());
				//##############################################################
				//##############################################################

			}

		};
		gameloop.start();

	}

	/** Set the initial properties on the Canvas
	 * 
	 * ###################################################### @Bryan - do we need to do anything else for the layout pane?
	 * 
	 */
	private void initialiseCanvas() {
		System.out.println("initialiseCanvas: In the canvas");
		asteroidsGameCanvas.setWidth(Configuration.SCENE_WIDTH);
		asteroidsGameCanvas.setHeight(Configuration.SCENE_HEIGHT);
	}

	//@Bryan

	// This is where the magic happens...between scenes. these methods are trigger from within the FXML objects and this class is set as the controller for the scenes
	// Any queries about what anything does, drop me a line, pretty straightforward code once it works

	// A series of .FXML anAction endpoints.  Essentially the following are a 
	// type of mapping, allowing us to connect multiple enpoints to a single
	// controller method, to avoid code duplication.
	public void welcome(ActionEvent event) throws IOException {
		activateSceneFromEvent(event, Configuration.GameWindows.WELCOME_MAIN_MENU);
	}
	@FXML
	Label myLabel;
	
	public void mainGame(ActionEvent event) throws IOException {
		activateSceneFromEvent(event, Configuration.GameWindows.MAIN_GAME);
		
		
		// @Bryan, Trying to display the name that was entered into the game on screen, keeps coming up Null..
		
		
		String username = nameTextField.getText();
		String x = username.toString();
		System.out.print(x);
//		myLabel.setText(x);
		
		
		// @Bryan, Trying to store users in a dict,Name and game score within the dict, 
		// just seeing the best way to handle new users, probably best off using person/player class...New.person etc
//		
//		Dictionary<Integer, String> dict
//        = new Hashtable<Integer, String>();
//		
////		// Inserting values into the Dictionary
////		dict.put(0,username);
//		
//		System.out.print("here");
		System.out.printf(username);	
//		System.out.println("Initial Dictionary is: " + dict);
		//@Bryan
		// Need to assign the name to some sort of global variable...
		//Probably wrong term but something to hold the user input and display it in the hall of fame etc
		
		
	}
	public void farewell(ActionEvent event) throws IOException {
		activateSceneFromEvent(event, Configuration.GameWindows.END_OF_GAME);
		
		// @Bryan, Presume we'll update the game score here once the game is over?
		
		
		
	}
	
	protected void activateSceneFromEvent(ActionEvent event, Configuration.GameWindows window) throws IOException {
		// We start with an ActionEvent (this method is handling an action event
		// generated by JavaFX, based on our .FXML configuration...
		stage = (Stage)((Node)event.getSource()).getScene().getWindow();

		// The 'stage' is essentially the 'window' in which your application
		// will run.  JFX doesn't use terms like 'window' as it's designed to
		// support desktop, mobile and web applications.
		stage.setTitle("Asteroids");

		// Now pass the stage to the more general scene activation method...
		activateSceneFromStage(stage, window);
	}
	
	
	@FXML
	TextField nameTextField;
	
	//testing method for input
//	public void down(ActionEvent e) {
//		  String username = nameTextField.getText();
//		  System.out.print(username);
//		  Label nameLabel = null;
//		  nameLabel.setText(username);
//	}
	protected void activateSceneFromStage(Stage stage, Configuration.GameWindows window) throws IOException {
		//root = FXMLLoader.load(getClass().getResource("Scene1.fxml"));
		//stage = (Stage)((Node)event.getSource()).getScene().getWindow();
		//scene = new Scene(root);
		//stage.setScene(scene);
		//stage.show();
		
		this.stage = stage;

		// Code for access to everything relating to JavaFX.  This might not be
		// necessary - but for an abundance of caution...
		// @Bryan
		// Uncomment below to see the 3 screens directly, Both home and end menu have buttons for switching but need to fix the game screen, issue with ship and adding clickable feature

		//FXMLLoader loader = new FXMLLoader(getClass().getResource("asteroidsBorderPane.fxml"));
		FXMLLoader loader = new FXMLLoader(getClass().getResource(window.fxmlResource));
		//FXMLLoader loader = new FXMLLoader(getClass().getResource("endmenu.fxml"));

		Parent root = (Parent) loader.load();

		// Following line included just in case we need access to the controller...
		//AsteroidsFXMLController controller = loader.<AsteroidsFXMLController>getController();

		// Then we create a scene based on the FXML root...
		Scene scene = new Scene(root);
		initialiseCanvas();
		KeyStrokeManager.getInstance().manageThisScene(scene);
		scene.getStylesheets().add(getClass().getResource("styles.css").toExternalForm());
		
		// Each stage has a scene (or scenes) and each scene requires a layout
		// manager.  The JavaFX SDK provides several layout panes for the easy
		// setup and management of classic layouts such as rows, columns, stacks,
		// tiles, and others.  Read more about the various options here:
		//   -> https://docs.oracle.com/javafx/2/layout/builtin_layouts.htm

		// Next we attach this scene to the stage (ours is 'stage'...)

		stage.setScene(scene);
		System.out.println("activateSceneFromStage: Just about to show...,");
		stage.show();

	}

}