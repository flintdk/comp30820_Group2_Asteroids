package comp30820.group2.asteroids;

import java.util.Random;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Shape;

/** Positioned on-screen objects.  These might be static objects (like the space
 * background).  Or they might be moving objects (like the spaceship or asteroids
 * etc.).
 * 
 * @author B. Agar Cox, E. Brard, T. Kelly, W. Song 
 *
 */
/*MODIFICATIONS:
 * 22/03/nn ??; 
 * 
 */
public abstract class GameObject {
	public GameVector position;
	public GameVector velocity;
	public double rotation;  // degrees
	public Shape hitModel;  // A polygon describing our game objects on screen
	
	/** Default constructor, creates an 'empty' (0-positioned, no-velocity,
	 * 0-angled, minimum-HitBox 'game object').
	 * 
	 */
	public GameObject() {
		this.position = new GameVector();
		this.velocity = new GameVector();
		this.rotation = 0;
		// The default hitModel is just a 1 x 1 square!
		this.hitModel = new Polygon(0, 0, 1, 0, 1, 1, 0, 1);
	}
	
	/** Helper method to set a random initial screen position and velocity for
	 * game objects.  
	 * 
	 */
	// ################################################ Can we use this for hyperspace as well as asteroids?
	public void randomInit() {
		Random r = new Random();
		this.position = new GameVector((Configuration.SCENE_WIDTH * r.nextDouble()),(Configuration.SCENE_HEIGHT * r.nextDouble()));
		this.rotation = r.nextDouble() * 360.0;
		double initialX
			= Math.cos(Math.toRadians(this.rotation)) * Configuration.ASTEROID_LRG_SPEED;
		double initialY
    		= Math.sin(Math.toRadians(this.rotation)) * Configuration.ASTEROID_LRG_SPEED;
		this.velocity = new GameVector(initialX, initialY);
		
	};
	
	/** Access the hitModel for this 'game object'.  The expectation is that
	 * this is the only method that will be used to access the hitModel of this object.
	 * @return
	 */
	public Shape hitModel() {
		// Before we return the hitModel, make sure it is up to date!
		this.hitModel.setTranslateX(this.position.getX());
		this.hitModel.setTranslateY(this.position.getY());
		this.hitModel.setRotate(this.rotation);
		
		return this.hitModel;
	}
	
	/** We want to know if two 'game objects' are touching/hitting (overlapping
	 * by any degree) on screen.  To do this we simply delegate the work to the
	 * Shape.intersect methods by checking for an intersection of the Shape on
	 * this object with the shape on the other object!
	 * 
	 * @param other The other 'game object'.
	 * @return
	 */
	public boolean isHitting(GameObject other ) {
		return this.hitModel.intersects(other.hitModel.getBoundsInLocal());
	}
	
	/** Check if the object has gone completely off the screen.
	 * 
	 */
	public void wrap (double screenWidth, double screenHeight) {
		double halfShipWidth  = this.hitModel.getLayoutBounds().getWidth() / 2;
		double halfShipHeight = this.hitModel.getLayoutBounds().getHeight() / 2;
		
		// If we go off screen to the left (i.e. if the right edge of our sprite
		// goes all the way past the left edge of our scene)...
		if (this.position.getX() + halfShipWidth < 0) {
			// it should appear on the right side of the screen!
			this.position = this.position.xMovedTo(screenWidth + halfShipWidth);
		}
		// If we go off screen to the right (i.e. if the left edge of our sprite
		// goes all the way past the right edge of our scene)...
		if (this.position.getX() - halfShipWidth > screenWidth) {
			// it should appear on the right side of the screen!
			this.position = this.position.xMovedTo(-halfShipWidth);
		}
		// If we go off screen to the top (i.e. if the bottom edge of our sprite
		// goes all the way past the top edge of our scene)...
		if (this.position.getY() + halfShipHeight < 0) {
			// it should appear on the right side of the screen!
			this.position = this.position.yMovedTo(screenHeight + halfShipHeight);
		}
		// If we go off screen to the bottom (i.e. if the top edge of our sprite
		// goes all the way past the bottom edge of our scene)...
		if (this.position.getY() - halfShipHeight > screenHeight) {
			// it should appear on the right side of the screen!
			this.position = this.position.yMovedTo(-halfShipHeight);
		}
		
	}
	
	/** Method to update our 'game object'.  We want to track how much
	 * time has passed. Usually, if our game is running at 60 frames per second,
	 * our deltaTime will simply be 1/60th of a second.
	 * @param deltaTime
	 * @return
	 */
	public void update(double deltaTime) {
		// Update the position according to velocity
		this.position
			= new GameVector(
				this.position.add(
						this.velocity.getX() * deltaTime, this.velocity.getY() * deltaTime
						)
				);
		// Wrap around screen..
		this.wrap(Configuration.SCENE_WIDTH,Configuration.SCENE_HEIGHT);
	}
	
	/** Draw our object on the screen.  In order to draw an object onto a canvas
	 * in JavaFX we use a graphics context object
	 * @param context
	 */
	/** Draw our image on the screen.  In order to draw an object onto a canvas
	 * in JavaFX we use a graphics context object.
	 * 
	 * For AsteroidsShapes we're actually both rendering on the graphics context
	 * AND tracking
	 * @param context
	 */
	public void render(GraphicsContext context) {
		
		// save and restore allow us to apply these transformations only to this
		// particular sprite... Explain!?
		context.save();
		
		// Following operations occur in REVERSE
		// -> we're putting the transformations onto a stack
		// -> they get processed (drawn) in the order they come off the stack (LIFO)

		// We drew our shapes on a graphics context as described here:
		//   -> https://docs.oracle.com/javafx/2/canvas/jfxpub-canvas.htm
		// ... rather than adding polygons to our layout/pane. (A Pane is a UI
		// element ("Node") that contains other UI elements ("child nodes") and
		// manages the layout of those nodes within the Pane)
		
		// ... and finally translate our image according to position!
		context.translate(this.position.getX(), this.position.getY());
		// ... then rotate the image (which is now centered on the origin...
		context.rotate(this.rotation);
		// ... then center the on the origin...
		// The default pivot point is '0,0' or the origin (top left corner of the
		// screen)! We want to rotate about the center of the sprite. So we slide
		// the image to the left and up (note the negative numbers)
		context.translate(
				-(this.hitModel.getLayoutBounds().getWidth() / 2),
				-(this.hitModel.getLayoutBounds().getHeight() / 2) );
		// Draw the image at the origin...
		drawObject(context);

		// Pop the state off of the stack, setting the following attributes to
		// their value at the time when that state was pushed onto the stack.
		context.restore();
	}
	
	/** The process for drawing an image on a context is different to that used
	 * to draw primitive shapes (Polygons for example).  So we extract that
	 * block of logic.  It is implemented in the more specific subclasses.
	 * 
	 * @param context
	 */
	public abstract void drawObject(GraphicsContext context);

}
