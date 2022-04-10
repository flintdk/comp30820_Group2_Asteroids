package comp30820.group2.asteroids;

import javafx.scene.canvas.GraphicsContext;
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
public interface GameObjectBehaviour {

	/** Helper method to set a random initial screen position and velocity for
	 * game objects.  
	 * 
	 */
	public void randomPosRotVelInit();
	
	/** Access the hitModel for this 'game object'.  The expectation is that
	 * this is the only method that will be used to access the hitModel of this object.
	 * @return
	 */
	public Shape getHitModel();
	
	/** We want to know if two 'game objects' are touching/hitting (overlapping
	 * by any degree) on screen.  To do this we simply delegate the work to the
	 * Shape.intersect methods by checking for an intersection of the Shape on
	 * this object with the shape on the other object!
	 * 
	 * @param other The other 'game object'.
	 * @return
	 */
	public boolean isHitting(GameObjectBehaviour other);
	
	/** Check if the object has gone completely off the screen.
	 * how about it returns a boolean, if boolean is true than the object is at the end oh the screen ? 
	 */
	public void wrap (double screenWidth, double screenHeight);
	
	/** Method to update our 'game object'.  We want to track how much
	 * time has passed. Usually, if our game is running at 60 frames per second,
	 * our deltaTime will simply be 1/60th of a second.
	 * returns a boolean to know if the element should be removed from the array of moving object
	 * @param deltaTime
	 * @return
	 */
	public void updatePosition(double deltaTime);
	
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
	public void render(GraphicsContext context);
	
	/** The process for drawing an image on a context is different to that used
	 * to draw primitive shapes (Polygons for example).  So we extract that
	 * block of logic.  It is implemented in the more specific subclasses.
	 * 
	 * @param context
	 */
	public void drawObject(GraphicsContext context);
	
	/** Each game object has a hitmodel (a polygon).  We move the polygon around
	 * the screen - this involves rotation, transalation etc..  But we also draw
	 * the polygon on a canvas.  It's vital that the hitmodel and the canvas are
	 * in perfect synchronisation so collisions detected by the game match exactly
	 * what's on screen.
	 * To do this we need to know the pivot point that JavaFX uses.  So we work
	 * it out and store it.
	 */
	public void setRotationPivotOffsets();

}
