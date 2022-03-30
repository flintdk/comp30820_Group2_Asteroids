package comp30820.group2.asteroids;

import java.net.URISyntaxException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import comp30820.group2.asteroids.Configuration.SoundEffects;
import comp30820.group2.asteroids.Sprite.Graphics;
import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.shape.Shape;

public class AsteroidsFXMLController implements Initializable {
    
    // Our canvas is magically provided by JavaFX/FXML
	@FXML protected Canvas asteroidsGameCanvas;
    
    KeyStrokeManager keys = KeyStrokeManager.getInstance();
    
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
		GameObject initialAsteroid1;
		GameObject initialAsteroid2;
		GameObject initialAsteroid3;

		// Initialise the startup objects...
		if (Configuration.GRAPHICS_MODE == Configuration.GraphicsMode.ARCADE) {
			spaceship = new Sprite(Graphics.SPACESHIP.path);
			initialAsteroid1  = new Sprite(Graphics.ASTEROID.path,
							Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2),
							Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2) );    // ############### WHY DO PICTURES APPEAR SMALLER THAN POLYGONS?!? NO IDEA??
			initialAsteroid2  = new Sprite(Graphics.ASTEROID.path,
					Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2),
					Double.valueOf(Configuration.ASTEROID_LRG_SIZE*2) );    // ############### WHY DO PICTURES APPEAR SMALLER THAN POLYGONS?!? NO IDEA??
			initialAsteroid3  = new Sprite(Graphics.ASTEROID.path,
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
			initialAsteroid1  = new AsteroidsShape(AsteroidsShape.InGameShape.ASTEROID_LARGE);
			initialAsteroid2  = new AsteroidsShape(AsteroidsShape.InGameShape.ASTEROID_MEDIUM);
			initialAsteroid3  = new AsteroidsShape(AsteroidsShape.InGameShape.ASTEROID_SMALL);
		}
		spaceship.position = new GameVector( (Configuration.SCENE_WIDTH / 2),(Configuration.SCENE_HEIGHT / 2) );
		initialAsteroid1.randomInit();
		initialAsteroid2.randomInit();
		initialAsteroid3.randomInit();
		//spaceship.velocity.set(50,0);
		//spaceship.render(context);
		
		movingObjectsOnScreen.add(spaceship);
		movingObjectsOnScreen.add(initialAsteroid1);
		movingObjectsOnScreen.add(initialAsteroid2);
		movingObjectsOnScreen.add(initialAsteroid3);
		
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
					bulletArr.get(i).render(context);
				}

				//##############################################################
				//##############################################################

				//for(int i = 1;i<movingObjectsOnScreen.size();i++) {
				if(Shape.intersect(spaceship.hitModel(),initialAsteroid1.hitModel()).getBoundsInLocal().getWidth() !=-1) {
					// TODO Print out the coordinates of everything to see what's up!!
					System.out.println("Collision detected!!!" + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
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
		asteroidsGameCanvas.setWidth(Configuration.SCENE_WIDTH);
		asteroidsGameCanvas.setHeight(Configuration.SCENE_HEIGHT);
    }
}