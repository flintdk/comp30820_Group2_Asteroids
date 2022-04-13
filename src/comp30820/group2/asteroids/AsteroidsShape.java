package comp30820.group2.asteroids;

import java.util.Random;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
//import javafx.scene.transform.Rotate;

/** On-screen geometric shapes that can move around in the game.
 * 
 * This class ties the vertex coordinates of the hitModel (it is a Polygon after
 * all) to the shapes displayed on screen.
 * 
 * @author B. Agar Cox, E. Brard, T. Kelly, W. Song 
 *
 */
/*MODIFICATIONS:
 * 22/03/nn ??; 
 * 
 */
public class AsteroidsShape extends GameObject {
	
    // Polygons Supported by the game
    public enum InGameShape {
    	SPACESHIP(GoClass.SPACESHIP),
    	ASTEROID_LARGE(GoClass.ASTEROID_LARGE),
        ASTEROID_MEDIUM(GoClass.ASTEROID_MEDIUM),
        ASTEROID_SMALL(GoClass.ASTEROID_SMALL),
    	MUZZLE_FLARE(GoClass.MUZZLE_FLARE),
        BULLET(GoClass.BULLET),
    	ALIEN(GoClass.ALIEN),
    	ALIEN_BULLET(GoClass.ALIEN_BULLET);
        
        public final GoClass goClass;

        private InGameShape(GoClass goClass) {
            this.goClass = goClass;
        }
    };
    // This AsteroidsShape will have an assigned type.
    public InGameShape type;
	
	/** Parameterised constructor.
	 * 
	 */
	public AsteroidsShape(InGameShape shapeName) {
		// Call the no arguments constructor...
		super(shapeName.goClass);
		this.type = shapeName;
		setAsteroidsShape(shapeName);
	}
	
	/** Set the polygon type for this AsterPolygon
	 * 
	 * @param InGameShape
	 */
	public void setAsteroidsShape(InGameShape shapeName) {
		if (shapeName == InGameShape.SPACESHIP) {
			// We're using a graphics context to draw our shape, rather than a layout
			// pane, so as well as the Shape we store a the list of vertices for this
			// shape as a pair of lists of x-points and y-points....
			xpoints = new double[]{0,21,0};
			ypoints = new double[]{0,7,14};
		}
		else if (shapeName == InGameShape.ASTEROID_LARGE) {
			// To generate an asteroid we want to generate an irregular shaped
			// polygon. Our team decided to generate the irregular shaped polygon
			// inside squares of different sizes.  That way we have one method
			// to generate the shape, and just call it with different sizes to
			// create asteroids of different sizes.
			generateAsteroidPolygon(Configuration.ASTEROID_LRG_SIZE);
		}
		else if (shapeName == InGameShape.ASTEROID_MEDIUM) {
			// To generate an asteroid we want to generate an irregular shaped
			// polygon. Our team decided to generate the irregular shaped polygon
			// inside squares of different sizes.  That way we have one method
			// to generate the shape, and just call it with different sizes to
			// create asteroids of different sizes.
			generateAsteroidPolygon(Configuration.ASTEROID_MED_SIZE);
		}
		else if (shapeName == InGameShape.ASTEROID_SMALL) {
			// To generate an asteroid we want to generate an irregular shaped
			// polygon. Our team decided to generate the irregular shaped polygon
			// inside squares of different sizes.  That way we have one method
			// to generate the shape, and just call it with different sizes to
			// create asteroids of different sizes.
			generateAsteroidPolygon(Configuration.ASTEROID_SML_SIZE);
		}
		else if (shapeName == InGameShape.MUZZLE_FLARE) {
			xpoints = new double[]{0,1,1,0};
			ypoints = new double[]{0,0,1,1};  //create a square of 1 by 1
		}
		else if (shapeName == InGameShape.BULLET) {
			xpoints = new double[]{0,1,1,0};
			ypoints = new double[]{0,0,1,1};  //########################### FIX ME
			wrap = false ;
		}else if(shapeName == InGameShape.ALIEN) {
			xpoints = new double[]{5,-5,-10,-20,-10,10,20,10};
			ypoints = new double[]{-10,-10,-5,0,10,10,0,-5};
		}else if(shapeName == InGameShape.ALIEN_BULLET) {
			xpoints = new double[]{0,1,1,0};
			ypoints = new double[]{0,0,1,1};
			wrap = false;
		}
		else{
			// Boring old 1x1 square for a default!!
			xpoints = new double[]{0,1,1,0};
			ypoints = new double[]{0,0,1,1};  
		}

		this.hitModel = new Polygon(getPointsAsCoordinates());
		// Once we have a polygon - make sure to populate or PivotOffsets so we
		// can keep our hit model / displayed objects in sync.
		setRotationPivotOffsets();
	}

	/** Generate an irregular polygon inside a circle of a specified maximum size.  The
	 * size parameter allows us to scale our polygon so the asteroids can be made
	 * large, medium or small.
	 * 
	 * @param size
	 */
	private void generateAsteroidPolygon(double maxRadius) {
		// The approach we take in our project is pretty much entirely based on
		// a visualisation we came across here:
		//   https://codepen.io/radu_gaspar/pen/xRgjMq
		// ... just customised to suit our needs (i.e. to generate our xpoints and
		// ypoints arrays) and - obviously - implemented in java.

		// We receive the maxRadius, 
		double minRadius=maxRadius * (100 - Configuration.ASTEROID_RADIUS_VARIANCE) / 100.0 ;
		
		// We are going to go around the outside of our circle and generate one
		// coordinate for every 360/granularity degrees.  So we know how big to
		// make our arrays:
		xpoints = new double[Configuration.ASTEROID_GRANULARITY];
		ypoints = new double[Configuration.ASTEROID_GRANULARITY];

		Random r = new Random();

		// Do-while the Path2D polygon does not contain the random x,y-coordinate:
		double wholeCircle = Math.PI*2;  // Number of radians in a circle, equivalent to 360 degreess
		double increment = wholeCircle / Configuration.ASTEROID_GRANULARITY;  // we choose an new random dimension every 'increment' radians
		// We want a polygon fully in the positive space, not centered on zero
		// (so all our maths is the same for every object).
		double offset = maxRadius;

	    // We want to iterate around the outside of the circle, generating points
		// in between our two bounding circles as we go.
		int index = 0;
		for (double angle = 0; angle < wholeCircle; angle += increment) {
			double radius = (r.nextDouble() * (maxRadius-minRadius)) + minRadius;
			xpoints[index] = offset + Math.sin(angle) * radius;
			ypoints[index] = offset + Math.cos(angle) * radius;
			index += 1;
	    }
	}
	
	/** Helper method to take the xpoints and ypoints lists and return them as
	 * a list of coordinates (suitable for building a polygon).
	 * 
	 * @return a list of doubles, set to the coordinates.
	 */
	private double[] getPointsAsCoordinates() {
		double[] pointsAsCoordinates = new double[xpoints.length * 2];
		for (int i=0; i<xpoints.length; i++) {
			int index = i*2;
			pointsAsCoordinates[index] = xpoints[i];
			pointsAsCoordinates[++index] = ypoints[i];
		}
		return pointsAsCoordinates;
	}
	
	/** Draw the Polygon represented by this GameObject to the supplied Graphics
	 * Context.
	 * 
	 * @param context
	 */
	public void drawObject(GraphicsContext context) {
//		if (this.type == InGameShape.BULLET) {
//			System.out.println("Drawing a bullet");
//		}
		context.setStroke(Color.WHITE);
		context.setLineWidth(2);
		context.strokePolygon(xpoints,ypoints,xpoints.length);
		context.setFill(Color.BLACK);
		context.fillPolygon(xpoints,ypoints,xpoints.length);
	}
	
	/** For the specified superclass enumeration value 'goClass', return the
	 * corresponding InGameShape value for an AsteroidsShape.
	 * 
	 * @param goClass
	 * @return null if none found.
	 */
	public static InGameShape getInGameShapeForGOClass(GoClass goClass ) {
		InGameShape inGameShape = null;
		for (InGameShape nextShape : InGameShape.values()) { 
		    if (nextShape.goClass == goClass) {
		    	inGameShape = nextShape;
		    	break;
		    }
		}
		return inGameShape;
	}

}
