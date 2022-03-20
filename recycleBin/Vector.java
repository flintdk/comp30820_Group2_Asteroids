package comp30820.group2.asteroids;

/** Store pairs of numbers and perform operations - the heart of Asteroids!
 * Vectors can:
 * <ul>
 * 	<li>represent a point in two dimensional space (x, y cordinates)</li>
 *  <li>represent velocity
 *  	<ul>
 *  		<li>direction is given by angle with reference to the origin of a coordinate system</li>
 *  		<li>speed is given by the length of the line</li>
 *  	</ul>
 *  </li>
 * </ul>
 * This Vector class gives us everything we need to navigate the two dimensional
 * space of the Asteroids game.
 * 
 * @author B. Agar Cox, E. Brard, T. Kelly, W. Song 
 *
 */
/*MODIFICATIONS:
 * 22/03/nn ??; 
 * 
 */
public class Vector {
	public double x;
	public double y;

	/** Default constructor, creates a '0 Vector'
	 * 
	 */
	public Vector() {
		this.set(0,0);
	}
	
	/** Parameterised constructor.
	 * @param x
	 * @param y
	 */
	public Vector(double x, double y) {
		this.set(x,y);
	}
	
	/** Convenience method, set both position co-ordinates
	 * @param x
	 * @param y
	 */
	public void set(double x, double y ) {
		this.x = x;
		this.y = y;
	}
	
	/** Vector Operations, add two values to x and y
	 * @param dx
	 * @param dy
	 */
	public void add(double dx, double dy) {
		this.x += dx;
		this.y += dy;
	}
	
	/** Vector Operations, multiply a Vector by a constant (scale or shrink our
	 *  vector!).
	 * @param factor
	 */
	public void multiply(double factor) {
		this.x *= factor;
		this.y *= factor;
	}
	
	/** Vector Operations, get the length of our Vector (distance from origin)
	 * @return
	 */
	public double getLength() {
		// Use Pythagoras theorem to get the length of our Vector (distance from
		// origin)
		return Math.sqrt( Math.pow(this.x, 2) + Math.pow(this.y, 2) );
	}
	
	/** Vector Operations, set the vector to have a particular length
	 * @param newLength
	 */
	public void setLength(double newLength) {
		// First - Vector might have a length, not equal to newLength
		double currentLength = this.getLength();
		// If currentLenght is 0, then 'currentAngle' must be undefined!
		// In this case we simply assume the currentAngle is 0 (pointing right!)
		if (currentLength == 0) {
			this.set(newLength, 0);
		}
		else {  // We can preserve the current angle
			// Scale Vector to have length 1
			this.multiply(1/currentLength);
			// Scale Vector to have length newLength
			this.multiply(currentLength);
		}
	}
	
	/** Vector Operations, get the angle our Vector makes with the X-axis (in degrees)
	 */
	public double getAngle() {
		// We know tan(ourAngle) = y/x
		// => ourAngle = atan (y/x)
		// The Math package has a function atan2 that handles both negative and
		// positive values of y and x, use this:
		double currentAngleRadians = Math.atan2(this.y, this.x); 
		return Math.toDegrees(currentAngleRadians);
	}
	
	/** Vector Operations, set the angle our vector has, relative to the X-axis (in degrees)
	 * @param newAngleDegrees
	 */
	public void setAngle(double newAngleDegrees) {
		// First - Vector might have a length, not equal to newLength
		double currentLength = this.getLength();
		double newAngleRadians = Math.toRadians(newAngleDegrees);
		
		this.x = currentLength * Math.cos(newAngleRadians);
		this.y = currentLength * Math.sin(newAngleRadians);
	}
	
	
}
