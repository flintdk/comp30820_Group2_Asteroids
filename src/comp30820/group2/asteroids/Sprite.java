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
    	ASTEROIDS_LARGE(Resource.IMG.path + "asteroids_large.png"),
    	ASTEROIDS_SMALL(Resource.IMG.path + "asteroids_small.png"),
        SPACE(Resource.IMG.path + "space.png"),
        SPACESHIP(Resource.IMG.path + "spaceship.png"),
        ASTEROID(Resource.IMG.path + "asteroid.png"),
    	FIRE(Resource.IMG.path + "fire.png"),
        LASER(Resource.IMG.path + "laser.png");
        
        public final String path;

        private Graphics(String path) {
            this.path = path;
        }
    };

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
	public Sprite(String imageFileName) {
		// Call the no arguments constructor...
		this();
		this.setImage(imageFileName, null, null);
	}
	
	/** Parameterised constructor.
	 * 
	 */
	public Sprite(String imageFileName, Double requiredWidth, Double requiredHeight) {
		// Call the no arguments constructor...
		this();
		this.setImage(imageFileName, requiredWidth, requiredHeight);
	}
	
	/** Set the image attribute for this Sprite
	 * 
	 * NOTE: The image file name is relative to the classloader!!
	 * @param imageFileName
	 */
	private void setImage(String imageFileName, Double requiredWidth, Double requiredHeight) {
		try {
			// First attempt loaded a file from the filesystem...
			//this.image = new Image(imageFileName);
			if (requiredWidth != null && requiredHeight != null ) {
				// We use Asteroids as our resource-anchor class...
				this.image = new Image(
						Main.class.getResource(imageFileName).toURI().toString(),
						requiredWidth, requiredHeight,
						false, true
						);
			}
			else {
				// No dimensions specified...
				this.image = new Image( Main.class.getResource(imageFileName).toURI().toString() );
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
