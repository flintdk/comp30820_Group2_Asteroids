package comp30820.group2.asteroids;

/** Basic Collision Detection
 * 
 * @author B. Agar Cox, E. Brard, T. Kelly, W. Song
 *
 */
/*MODIFICATIONS:
 * 22/03/nn ??; 
 * 
 */
public class Rectangle {
	// NOTE: x,y represents the top left corner of our Rectangle (a.k.a. Hit-Box)
	//       It seems that a traditional computer graphics "local" coordinate
	//       system is one in which the x axis increases to the right and the y
	//       axis increases downwards
	double x;
	double y;
	double width;
	double height;
	
	/** Default constructor, creates a '0 positioned', '1 unit' rectangle
	 * 
	 */
	public Rectangle() {
		this.setPosition(0, 0);
		this.setSize(1, 1);
	}
	
	/** Parameterised constructor, creates a '0 Vector'
	 * 
	 */
	public Rectangle(double x, double y, double width, double height) {
		this.setPosition(x, y);
		this.setSize(width, height);
	}
	
	/** Set the position of our rectangle
	 * @param x
	 * @param y
	 */
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/** Set the dimensions of our rectangle
	 * @param width
	 * @param height
	 */
	public void setSize(double width, double height) {
		this.width = width;
		this.height = height;
	}
	
	/** Check of two rectangles overlap (for collision detection!)
	 * @param other
	 * @return
	 */
	public boolean overlaps(Rectangle other) {
		// We assume they do overlap...
		boolean overlaps = true;

		// ... then, to see if our hit boxes overlap, we check if there are
		// 'separating axes' between the two boxes.  There are only four possible
		// separating axes for two boxes, so we check them all and if any one of
		// them exists then our boxes don't overlap!
		if (
			// 'this' is to the left of 'other'...
			this.x + this.width < other.x
			|| 
			// 'other' is to the left of 'this'...
			other.x + other.width < this.x
			||
			// 'this' is below 'other'...
			this.y + this.height < other.y
			||
			// 'other' is above 'this'...
			other.y + other.height < this.y
			)
		{
			overlaps = false;
		}
		
		return overlaps;
	}

}
