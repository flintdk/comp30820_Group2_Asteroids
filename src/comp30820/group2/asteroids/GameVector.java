package comp30820.group2.asteroids;

// Getting a weird Point2D class is not accessible error?  Check out the following
// article - it details an access error that sometimes occurs:
//   -> https://stackoverflow.com/questions/22812488/using-javafx-in-jre-8-access-restriction-error
import javafx.geometry.Point2D;

/** <p>Store pairs of numbers and perform operations - the heart of Asteroids! This
 * class gives us everything we need to navigate the two dimensional space of the
 * Asteroids game.</p>
 * <p>GameVector extends javafx.gemoetry.Point2D.  The {@code Point2D} class describes:</p>
 * <ul>
 * 	<li>
 * 		{@code A 2D geometric point that usually represents the x, y coordinates}<br>
 * 		We use it exactly for this purpose - to represent a point in two dimensional
 *      space and position our objects on the screen (x, y coordinates)
 *  </li>
 *  <li>
 *  	{@code It can also represent a relative magnitude vector's x, y magnitudes}<br>
 *  	We use these "magnitudes" to represent velocity.
 *  	<ul>
 *  		<li>direction is given by angle with reference to the origin of a coordinate system</li>
 *  		<li>speed is given by the length of the line</li>
 *  	</ul>
 *  </li>
 *  </li>
 * </ul>
 * <p>The Point2D class contains nearly all the functionality we need by itself, we
 * just introduce a handful of helper methods to allow us to set magnitude
 * (velocity), etc..</p>  
 * 
 * @author B. Agar Cox, E. Brard, T. Kelly, W. Song 
 *
 */
/*MODIFICATIONS:
 * 22/03/nn ??; 
 * 
 */
public class GameVector extends Point2D {

	/** Default constructor, creates a '0,0 GameVector'
	 * 
	 */
	public GameVector() {
		super(0,0);
	}
	
	/** Parameterised constructor.
	 * @param x
	 * @param y
	 */
	public GameVector(double x, double y) {
		super(x,y);
	}
	
	/** Parameterised constructor.
	 * @param x
	 * @param y
	 */
	public GameVector(Point2D point) {
		super(point.getX(),point.getY());
	}
	
	/** Convenience method, return a new GameVector with dx added to x and dy
	 * added to y
	 * @param x
	 */
	public GameVector add(double dx, double dy) {
		// Point2D has no methods to directly set the x and y coordinates. So
		// we exploit the add method and simply add:
		//   -> the difference between the new desired value and the current value
		return new GameVector( super.add(dx, dy) );
	}

	/** Convenience method, return a new GameVector with this.x moved to x
	 * @param x
	 */
	public GameVector xMovedTo(double x) {
		// Point2D has no methods to directly set the x and y coordinates. So
		// we exploit the add method and simply add:
		//   -> the difference between the new desired value and the current value
		return new GameVector( x, this.getY() );
	}

	/** Convenience method, return a new GameVector with this.y moved to y
	 * @param y
	 */
	public GameVector yMovedTo(double y) {
		// Point2D has no methods to directly set the x and y coordinates. So
		// we exploit the add method and simply add:
		//   -> the difference between the new desired value and the current value
		return new GameVector( this.getX(), y );
	}
	
	/** Vector Operations, get the length of our Vector (distance from origin)
	 * @return
	 */
	public double getLength() {
		return magnitude();
	}
	
	/** Vector Operations, return a new GameVector with the specified length but
	 * the same angle as this GameVector.
	 * 
	 * @param newLength
	 */
	public GameVector lengthSetTo(double newLength) {
		// First - Vector might have a length, not equal to newLength
		double currentLength = this.getLength();
		// If currentLenght is 0, then 'currentAngle' must be undefined!
		// In this case we simply assume the currentAngle is 0 (pointing right!)
		if (currentLength == 0) {
			return new GameVector( newLength, 0 );
		}
		else {  // We can preserve the current angle
			// Scale Vector to have length 1, * new length...
			return new GameVector( this.multiply(newLength/currentLength) );
		}
	}
	
	/** Vector Operations, get the angle a line drawn from the origin to the
	 * point represented by this GameVector makes, relative to the X-axis (in
	 * degrees).
	 */
	public double getAngle() {
		// We know tan(ourAngle) = y/x
		// => ourAngle = atan (y/x)
		// The Math package has a function atan2 that handles both negative and
		// positive values of y and x, use this:
		double oldSchoolAngleRadians = Math.atan2(this.getY(), this.getX());
		double newFangledAngleRadians = angle(Point2D.ZERO);
		return Math.toDegrees(angle(Point2D.ZERO));
	}
	
	/** Vector Operations, return a new GameVector with the specified angle,
	 * relative to the X-axis (in degrees) but the same length as this GameVector.
	 * @param newAngleDegrees
	 */
	public GameVector angleSetTo(double newAngleDegrees) {
		// First - Vector might have a length, not equal to newLength
		double currentLength = this.getLength();
		double newAngleRadians = Math.toRadians(newAngleDegrees);
		
		return new GameVector(
				currentLength * Math.cos(newAngleRadians),
				currentLength * Math.sin(newAngleRadians));
	}
	
	
}
