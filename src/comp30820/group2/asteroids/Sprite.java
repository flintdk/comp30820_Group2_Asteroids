package comp30820.group2.asteroids;

import java.net.URISyntaxException;

import comp30820.group2.asteroids.Configuration.Resource;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.shape.Polygon;
//import javafx.scene.transform.Rotate;

/** On-screen images (Sprites) that can move around in the game.
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
public class Sprite extends GameObject {
	
    // Graphics resources
    public enum Graphics {
    	// The default background for the game..
    	BACKGROUND(GO_CLASS.BACKGROUND, Resource.IMG.path + "space.png"),
    	// Following are in-game objects...
    	ASTEROID(GO_CLASS.ASTEROID_MEDIUM, Resource.IMG.path + "asteroid.png"),
    	SPACESHIP(GO_CLASS.SPACESHIP, Resource.IMG.path + "spaceship.png"),
    	MUZZLE_FLARE(GO_CLASS.MUZZLE_FLARE, Resource.IMG.path + "fire.png"),
        LASER(GO_CLASS.BULLET, Resource.IMG.path + "laser.png"),
        ALIEN(GO_CLASS.ALIEN, "ALIEN"),
    	ALIEN_BULLET(GO_CLASS.ALIEN_BULLET, Resource.IMG.path + "fire.png");
    	
    	public final GO_CLASS goClass;
    	public final String path;

        private Graphics(GO_CLASS goClass, String path) {
        	this.goClass = goClass;
            this.path = path;
        }
    };
    // This Sprite will have an assigned type.
    public Graphics type;

	public Image image;
	
	/** Default constructor, creates an 'empty' (0-positioned, no-velocity,
	 * 0-angled, minimum-HitBox sprite).
	 * 
	 */
	public Sprite() {
		super();
	}
	
	/** Parameterised constructor.
	 * 
	 */
	public Sprite(Graphics graphics) {
		// Call the no arguments constructor...
		this();
		this.type = graphics;
		this.setImage(graphics, null, null);
	}
	
	/** Parameterised constructor.
	 * 
	 */
	public Sprite(Graphics graphics, Double requiredWidth, Double requiredHeight) {
		// Call the no arguments constructor...
		this();
		this.setImage(graphics, requiredWidth, requiredHeight);
	}
	
	/** Set the image attribute for this Sprite
	 * 
	 * NOTE: The image file name is relative to the classloader!!
	 * @param graphics
	 */
	private void setImage(Graphics graphics, Double requiredWidth, Double requiredHeight) {
		try {
			// First attempt loaded a file from the filesystem...
			//this.image = new Image(imageFileName);
			if (requiredWidth != null && requiredHeight != null ) {
				// We use Asteroids as our resource-anchor class...
				this.image = new Image(
						Main.class.getResource(graphics.path).toURI().toString(),
						requiredWidth, requiredHeight,
						false, true
						);
			}
			else {
				// No dimensions specified...
				this.image = new Image( Main.class.getResource(graphics.path).toURI().toString() );
			}
		}
		catch (URISyntaxException USE) {
			// ####################################################### LOGGING??
			System.out.println(USE.getStackTrace());
		}
		// The default hitModel for an image is just a rectangle, based on the 
		// image dimensions (nothing fancy - just a sensible default)
		double width = this.image.getWidth();
		double height = this.image.getHeight();
		this.hitModel = new Polygon(0, 0, width, 0, width, height, 0, height);
	}
	
	/** Draw the Image represented by this GameObject to the supplied Graphics
	 * Context.
	 * 
	 * @param context
	 */
	public void drawObject(GraphicsContext context) {
		context.drawImage(this.image, 0, 0);
	}

}
