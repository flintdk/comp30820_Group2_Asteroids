package comp30820.group2.asteroids;

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
    	SPACEHIP("Triangular spaceship"),
        ASTEROID("Irregular shaped Asteroid!"),
    	FIRE("Muzzle flare for when our cannon fires"),
        BULLET("The bullet shot out by our cannon");
        
        public final String description;

        private InGameShape(String description) {
            this.description = description;
        }
    };
	
	// We require two seperate arrays, the set of x-coordinates and the set of 
    // y-coordinates for each vertex in the Polygon used for the hitModel for
    // this GameObject, so we can draw it on-screen.  See 'drawObject()' method
    // below...
    private double[] xpoints;
	private double[] ypoints;
	
	/** Default constructor, creates an 'empty' (0-positioned, no-velocity,
	 * 0-angled, minimum-HitBox polygon).
	 * 
	 */
	public AsteroidsShape() {
		super();
	}
	
	/** Parameterised constructor.
	 * 
	 */
	public AsteroidsShape(InGameShape shapeName) {
		// Call the no arguments constructor...
		this();
		setAsteroidsShape(shapeName);
	}
	
	/** Set the polygon type for this AsterPolygon
	 * 
	 * @param InGameShape
	 */
	public void setAsteroidsShape(InGameShape shapeName) {
		if (shapeName == InGameShape.SPACEHIP) {
			// We're using a graphics context to draw our shape, rather than a layout
			// pane, so as well as the Shape we store a the list of vertices for this
			// shape as a pair of lists of x-points and y-points....
			xpoints = new double[]{0,21,0};
			ypoints = new double[]{0,7,14};
		}
		else if (shapeName == InGameShape.ASTEROID) {
//			https://stackoverflow.com/questions/61403966/random-x-y-coordinate-within-an-irregular-polygon-given-a-list-of-x-y-points-wi
//			// The resulting x,y-coordinate, starting uninitialized
//			double x,y;
//			// Flag to indicate whether the random x,y-coordinate is on a corner or edge, starting truthy
//			boolean flag;
//			// Do-while the Path2D polygon does not contain the random x,y-coordinate:
//			do{
//			  // Select a random x,y-coordinate within the Rectangle
//			  x = rect.getX() + Math.random()*rect.getWidht();
//			  y = rect.getY() + Math.random()*rect.getHeight();
//			  // Set the flag to false:
//			  flag = false;
//			  // Loop over the pair of x,y-coordinates of the input:
//			  for(int j=0; j<X.length; )
//			    // Create a Line2D using the current pair of x,y-coordinates:
//			    if(new java.awt.geom.Line2D.Double(X[j],Y[j++], X[j],Y[j])
//			    // And if it contains the random x,y-coordinate:
//			       .contains(x,y))
//			      // Change the flag to true
//			      flag = true;
//			}while(!path.contains(x,y) || flag);
			xpoints = new double[]{0,1,1,0};
			ypoints = new double[]{0,0,1,1};  //########################### FIX ME
		}
		else if (shapeName == InGameShape.FIRE) {
			xpoints = new double[]{0,1,1,0};
			ypoints = new double[]{0,0,1,1};  //create a square of 1 by 1
		}
		else if (shapeName == InGameShape.BULLET) {
			xpoints = new double[]{0,1,1,0};
			ypoints = new double[]{0,0,1,1};  //########################### FIX ME
			wrap = false ;
		}
		else {
			// Boring old 1x1 square for a default!!
			xpoints = new double[]{0,1,1,0};
			ypoints = new double[]{0,0,1,1};  
		}

		this.hitModel = new Polygon(getPointsAsCoordinates());

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
		context.setStroke(Color.WHITE);
		context.setLineWidth(2);
		context.strokePolygon(xpoints,ypoints,xpoints.length);
		context.setFill(Color.BLACK);
		context.fillPolygon(xpoints,ypoints,xpoints.length);
	}

}
