package comp30820.group2.asteroids;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javafx.geometry.Bounds;
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
	public double rotation;  // degrees - it's the angle of the orientation!
	public boolean willRender = true;
	public boolean canBeHit = true;
	
	public Shape hitModel;  // A polygon describing our game objects on screen
	// JavaFX rotates the coordinate space of the node (Polygon) about a specified
	// "pivot" point. The pivot point about which the rotation occurs is "the center
	// of the untransformed layoutBounds."  See:
	//   -> https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Node.html
	// We're using a graphical context (a canvas).  To ensure our hitmodel is
	// located in exactly the same place as the drawn object, we need to use the
	// same pivot offsets when rotating.
	public double rotationPivotOffsetX;
	public double rotationPivotOffsetY;
	
	public double initialX;
	public double initialY;

	public boolean wrap ;  // Some objects 'wrap' around the screen (spaceship, asteroids)
	                       // ... and some do not (bullets, alien ships)
							//it's more :  do we need to know if the object is at the end of the screen 
	
	// We require two separate arrays, the set of x-coordinates and the set of 
    // y-coordinates for each vertex in the Polygon used for the hitModel for
    // this GameObject, so we can draw it on-screen.  See 'drawObject()' method
    // below...
    protected double[] xpoints;
    protected double[] ypoints;
    
    // Polygons Supported by the game
    public enum GoClass {
    	BACKGROUND("Game Board Background"),
    	SPACESHIP("Triangular spaceship"),
    	ASTEROID_LARGE("Irregular shaped Asteroid, Large!"),
        ASTEROID_MEDIUM("Irregular shaped Asteroid, Medium!"),
        ASTEROID_SMALL("Irregular shaped Asteroid, Small!"),
    	MUZZLE_FLARE("Muzzle flare for when our cannon fires"),
        BULLET("The bullet shot out by our cannon"),
    	ALIEN("Alien Spaceship"),
    	ALIEN_BULLET("Alien Bullet");
        
        public final String description;

        private GoClass(String description) {
            this.description = description;
        }
    };
    // This AsteroidsShape will have an assigned type.
    public GoClass gameObjectClass;

	/** Private no-argument constructor, force consumers to supply a type when
	 * creating an instance.  Supplies an 'empty' (0-positioned, no-velocity,
	 * 0-angled, minimum-HitBox 'game object').
	 * 
	 */
	private GameObject() {
		this.position = new GameVector();
		this.velocity = new GameVector();
		this.rotation = 0;
		this.initialX=0;
		this.initialY=0;
		// The default hitModel is just a 1 x 1 square!
		this.hitModel = new Polygon(0, 0, 1, 0, 1, 1, 0, 1);
		setRotationPivotOffsets();
		this.wrap = true ;
	}
	/** Parameterised constructor, creates an 'empty' (0-positioned, no-velocity,
	 * 0-angled, minimum-HitBox 'game object'), of GoType supplied
	 * 
	 */
	public GameObject(GoClass goClass) {
		this();
		this.gameObjectClass = goClass;
	}
	
	/** Helper method to set a random initial screen position and velocity for
	 * game objects.  
	 * 
	 */
	public void randomPosRotVelInit() {
		Random r = new Random();
		this.position = new GameVector((Configuration.SCENE_WIDTH * r.nextDouble()),(Configuration.SCENE_HEIGHT * r.nextDouble()));
		this.rotation = r.nextDouble() * 360.0;
		double initialX
			= Math.cos(Math.toRadians(this.rotation)) * Configuration.ASTEROID_LRG_SPEED;
		double initialY
    		= Math.sin(Math.toRadians(this.rotation)) * Configuration.ASTEROID_LRG_SPEED;
		this.velocity = new GameVector(initialX, initialY);
	};
	
	/** Help ALien to have random initial position(relative)
	 * @return
	 */
	public void randomInitAlien() {
		Random r = new Random();
		this.position = new GameVector(0,Configuration.SCENE_HEIGHT * r.nextDouble());
		double initialSpeed = Configuration.SPEED_ALIEN;
		this.velocity = new GameVector(initialSpeed, 0);
	}
	
	/** Help ALien to have random path
	 * @return
	 */
	public void changePathAlien() {
		Random r = new Random();
		float m=r.nextFloat();
		double initialY = Configuration.SPEED_ALIEN;
		List<Double> list = new ArrayList<>();
		list.add(initialY);
		list.add(-initialY);
		int i = Math.round(m);
		double pointChage = Configuration.SCENE_WIDTH*r.nextDouble();
		//System.out.println(pointChage);
		if(this.position.getX() > pointChage && this.velocity.getY() == 0) {
			this.velocity = this.velocity.add(0 , list.get(i));
		}
		if(this.position.getX()>Configuration.SCENE_HEIGHT/4*3.5 && this.velocity.getY() != 0) {
			this.velocity =  new GameVector(initialY, 0);
		}
	}
	

	/** Access the hitModel for this 'game object'.  The expectation is that
	 * this is the only method that will be used to access the hitModel of this object.
	 * @return
	 */
	public Shape getHitModel() {
		this.hitModel.setRotate(this.rotation);
		this.hitModel.setTranslateX(this.position.getX()-rotationPivotOffsetX);
		this.hitModel.setTranslateY(this.position.getY()-rotationPivotOffsetY);

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
	public boolean isHitting(GameObject other) {
		if (canBeHit) {
			return Shape.intersect(getHitModel(),other.getHitModel()).getBoundsInLocal().getWidth()!= -1;
		}
		else {
			return false;
		}
	}
	
	/**
	 * let the alien fire at regular intervals and also alien fire to the spaceship
	 * @param GameObject alienOnScreen, int timerBullet,GameObject spaceship, List<GameObject> movingObjectsOnScreen
	 * @return void
	 */
	public static void alienBulletFire(GameObject alienOnScreen, int timerBullet,GameObject spaceship, List<GameObject> movingObjectsOnScreen) {
		if(timerBullet % 40 == 0) {
			double bulletAlienX = alienOnScreen.position.getX();
			double bulletAlienY = alienOnScreen.position.getY();
			GameObject alienBullet= new AsteroidsShape(AsteroidsShape.InGameShape.ALIEN_BULLET);
			movingObjectsOnScreen.add(alienBullet);
			alienBullet.initialX = bulletAlienX;
			alienBullet.initialY = bulletAlienY;
			alienBullet.position = new GameVector(bulletAlienX,bulletAlienY);
			
			double velocityBullet = Configuration.SPEED_BULLET;
			double spaceshipX = spaceship.position.getX();
			double spaceshipY = spaceship.position.getY();
			double dx = spaceshipX-bulletAlienX;
			double dy = spaceshipY-bulletAlienY;
			double d = Math.sqrt(Math.pow(dx,2)+ Math.pow(dy,2));
			
			alienBullet.velocity = new GameVector(dx/d * velocityBullet,dy/d * velocityBullet);
		}
	}
	
	/** Check if the object has gone completely off the screen.
	 * how about it returns a boolean, if boolean is true than the object is at the end oh the screen ? 
	 */
	public void wrap (double screenWidth, double screenHeight) {
		double halfShipWidth  = getHitModel().getLayoutBounds().getWidth() / 2;
		double halfShipHeight = getHitModel().getLayoutBounds().getHeight() / 2;
		
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
	 * returns a boolean to know if the element should be removed from the array of moving object
	 * @param deltaTime
	 * @return
	 */
	public void updatePosition(double deltaTime) {
		// Update the position according to velocity
		this.position
			= new GameVector(
				this.position.add(
						this.velocity.getX() * deltaTime, this.velocity.getY() * deltaTime
						)
				);

		// boolean if true then wrap the object
		//   -> some objects 'wrap' when they move past the edge of the screen (e.g. spaceship, asteroid)
		//   -> some objects disappear (e.g. bullet)
		// ... so we need to know how to behave when they arrive at the edge of the screen 
		if (this.wrap) {
			// Wrap around screen..
			this.wrap(Configuration.SCENE_WIDTH,Configuration.SCENE_HEIGHT);
		}
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
		// Only draw the game object to the graphics context when indicated...
		// (e.g. don't draw the spaceship when it's in hyperspace...)
		if (willRender) {
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
			context.translate(-rotationPivotOffsetX, -rotationPivotOffsetY);
	
			// Draw the image at the origin...
			drawObject(context);
	
			// Pop the state off of the stack, setting the following attributes to
			// their value at the time when that state was pushed onto the stack.
			context.restore();
		}
	}
	
	/** The process for drawing an image on a context is different to that used
	 * to draw primitive shapes (Polygons for example).  So we extract that
	 * block of logic.  It is implemented in the more specific subclasses.
	 * 
	 * @param context
	 */
	public abstract void drawObject(GraphicsContext context);
	
	/** Each game object has a hitmodel (a polygon).  We move the polygon around
	 * the screen - this involves rotation, transalation etc..  But we also draw
	 * the polygon on a canvas.  It's vital that the hitmodel and the canvas are
	 * in perfect synchronisation so collisions detected by the game match exactly
	 * what's on screen.
	 * To do this we need to know the pivot point that JavaFX uses.  So we work
	 * it out and store it.
	 */
	protected void setRotationPivotOffsets()
	{
		Bounds b = this.hitModel.layoutBoundsProperty().get();
		this.rotationPivotOffsetX = (b.getMinX() + b.getMaxX()) / 2.0;
		this.rotationPivotOffsetY = (b.getMinY() + b.getMaxY()) / 2.0;
	}

}
