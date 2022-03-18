package comp30820.group2.asteroids;

import java.net.URISyntaxException;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
//import javafx.scene.transform.Rotate;

/** On-screen entities that can move around in the game.
 * 
 * Several of the images used to draw the sprites on-screen were copied from
 * the source codes for books published by Apress (including Lee Stemkoski's
 * "Java Game Development with LibGDX". See:
 * https://github.com/Apress/java-game-dev-LibGDX/tree/master/Ch04%20Space%20Rocks/assets
 * 
 * @author B. Agar Cox, E. Brard, T. Kelly, W. Song 
 *
 */
/*MODIFICATIONS:
 * 22/03/nn ??; 
 * 
 */
public class Sprite {
	public Vector position;
	public Vector velocity;
	public double rotation;  // degrees
	public Rectangle boundary;
	public Image image;
	
	/** Default constructor, creates an 'empty' (0-positioned, no-velocity,
	 * 0-angled, minimum-Rectangle sprite).
	 * 
	 */
	public Sprite() {
		this.position = new Vector();
		this.velocity = new Vector();
		this.rotation = 0;
		this.boundary = new Rectangle();
	}
	
	/** Parameterised constructor.
	 * 
	 */
	public Sprite(String imageFileName) {
		// Call the no arguments constructor...
		this();
		this.setImage(imageFileName);
	}
	
	/** Set the image attribute for this Sprite
	 * 
	 * NOTE: The image file name is relative to the classloader!!
	 * @param imageFileName
	 */
	public void setImage(String imageFileName) {
		try {
			// First attempt loaded a file from the filesystem...
			//this.image = new Image(imageFileName);
			// We use Asteroids as our resource-anchor class...
			this.image = new Image(Asteroids.class.getResource(imageFileName).toURI().toString());
		}
		catch (URISyntaxException USE) {
			// ####################################################### LOGGING??
			System.out.println(USE.getStackTrace());
		}
		this.boundary.setSize(this.image.getWidth(), this.image.getHeight());
	}
	
	/** Access the Boundary for this sprite.  The expectation is that this is the
	 * only method that will be used to access the boundary of this sprite.
	 * @return
	 */
	public Rectangle getBoundary() {
		// Before we return the boundary, make sure it is up to date!
		this.boundary.setPosition(this.position.x, this.position.y);
		
		return this.boundary;
	}
	
	/** We want to know if two sprites are overlapping on screen.  To do this
	 * we simply delegate the work to the Rectangle.overlaps methods by comparing
	 * the boundary on this sprite, to the boundary on the other sprite!
	 * 
	 * @param other The other sprite.
	 * @return
	 */
	public boolean overlaps(Sprite other ) {
		return (this.getBoundary().overlaps(other.getBoundary()));
	}
	
	
	/** Check if the object has gone completely off the screen.
	 * 
	 */
	public void wrap (double screenWidth, double screenHeight) {
		double halfShipWidth  = this.image.getWidth() / 2;
		double halfShipHeight = this.image.getHeight() / 2;
		
		// If we go off screen to the left (i.e. if the right edge of our sprite
		// goes all the way past the left edge of our scene)...
		if (this.position.x + halfShipWidth < 0) {
			// it should appear on the right side of the screen!
			this.position.x = screenWidth + halfShipWidth;
		}
		// If we go off screen to the right (i.e. if the left edge of our sprite
		// goes all the way past the right edge of our scene)...
		if (this.position.x - halfShipWidth > screenWidth) {
			// it should appear on the right side of the screen!
			this.position.x = -halfShipWidth;
		}
		// If we go off screen to the top (i.e. if the bottom edge of our sprite
		// goes all the way past the top edge of our scene)...
		if (this.position.y + halfShipHeight < 0) {
			// it should appear on the right side of the screen!
			this.position.y = screenHeight + halfShipHeight;
		}
		// If we go off screen to the bottom (i.e. if the top edge of our sprite
		// goes all the way past the bottom edge of our scene)...
		if (this.position.y - halfShipHeight > screenHeight) {
			// it should appear on the right side of the screen!
			this.position.y = -halfShipHeight;
		}
		
	}
	
	/** Method to update our Sprite.  We want to track how much time has passed.
	 * Usually, if our game is running at 50 frames per second, our deltaTime
	 * will simply be 1/50th of a second.
	 * @param deltaTime
	 * @return
	 */
	public void update(double deltaTime) {
		// Update the position according to velocity
		this.position.add(this.velocity.x * deltaTime, this.velocity.y * deltaTime);
		// Wrap around screen..
		this.wrap(Configuration.SCENE_WIDTH,Configuration.SCENE_HEIGHT);
	}
	
	/** Draw our image on the screen.  In order to draw an object onto a canvas
	 * in JavaFX we use a graphics context object
	 * @param context
	 */
	public void render(GraphicsContext context) {
		
		// save and restore allow us to apply these transformations only to this
		// particular sprite... Explain!?
		context.save();
		
		// Following operations occur in REVERSE
		// -> we're putting the transformations onto a stack
		// -> they get processed (drawn) in the order they come off the stack (LIFO)

		// ... and finally translate our image according to position!
		context.translate(this.position.x, this.position.y);
		// ... then rotate the image (which is now centered on the origin...
		context.rotate(this.rotation);
		// ... then center the on the origin...
		// The default pivot point is '0,0' or the origin (top left corner of the
		// screen)! We want to rotate about the center of the sprite. So we slide
		// the image to the left and up (note the negative numbers)
		context.translate( -(this.image.getWidth() / 2), -(this.image.getHeight() / 2) );
		// Draw the image at the origin...
		context.drawImage(this.image, 0, 0);
		
		// Pop the state off of the stack, setting the following attributes to
		// their value at the time when that state was pushed onto the stack.
		context.restore();
		
		// See below for better rotate, that specifies pivot point!!
		// https://stackoverflow.com/questions/18260421/how-to-draw-image-rotated-on-javafx-canvas
//		//creating the rotation transformation
//		Rotate rotate = new Rotate();
//		//Setting pivot points for the rotation
//		rotate.setPivotX(300);
//		rotate.setPivotY(100);
	}
}
