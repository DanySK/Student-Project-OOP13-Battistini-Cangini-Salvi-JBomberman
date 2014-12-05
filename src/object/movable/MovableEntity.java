package object.movable;


import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import object.Animation;
import object.MapObject;

/**
 * @author Loris<br/>
 * abstract class that implements {@link IMovingEntity} and extends {@link MapObject}.<br/>
 * This class implements all the methods from the interface.
 */
public abstract class MovableEntity extends MapObject implements IMovingEntity {

	// animation actions
	private static final int FACINGBACK = 0;
	private static final int FACINGSIDEWAYS = 1;
	private static final int FACINGFORTH = 2;

	// animation stuff
	private ArrayList<BufferedImage[][]> sprites;
	protected Animation animation;
	protected boolean facingBack;
	protected boolean facingForth;
	protected boolean facingRight;
	protected int currentAction;
	private boolean animating = false;

	// movement
	protected boolean left;
	protected boolean right;
	protected boolean up;
	protected boolean down;
	
	// entity stuff
	protected Point position;
	protected int speed;
	private int steps;
	protected Point currentStep;
	private Dimension size;

	public MovableEntity(int lifes, String pathImage,Point origin, Dimension spriteSize,Point position) {
		super(lifes,pathImage, origin, spriteSize);
		this.currentStep = new Point(0,0);
		this.position = (Point) position.clone();
		this.steps = 5;
		this.speed = 1;
		this.size = spriteSize;

		if(this.getImage() != null && getNumFrames() != null) {
			createAnimation();
		}
	}

	/**
	 * creates an {@link Animation} object to manage the sprites of this {@link MovableEntity}
	 */
	protected void createAnimation() {
		animation = new Animation(this.getImage(),size,getTilesetRows(), getNumFrames());
		currentAction = Animation.IDLE;
	}

	/**
	 * get an array that indicates the number of frames for each status.
	 * the default statuses are IDLE = {@value Animation#IDLE} and MOVING = {@value Animation#MOVING}, so an array with 2 {@link Integer} is expected, the
	 * first one that says the number of frames for IDLE and the 2nd that says the number of frames for MOVING.
	 */
	@Override
	public void createAnimation(Point newPosition, Dimension tileSize) {

		Point oldPos = MovableEntity.this.getPosition();
		int delta = Math.abs(oldPos.x != newPosition.x ? newPosition.x - oldPos.x : newPosition.y - oldPos.y);

		int size = oldPos.x == newPosition.x ? tileSize.height : tileSize.width;

		animating  = true;

		new Thread(new Runnable() {
			@Override
			public void run() {
				for(int i=0;i< size*delta;i+= size/getSteps()) {

					MovableEntity.this.setActualStep((int)Math.signum(newPosition.x - oldPos.x)*i,(int)Math.signum(newPosition.y - oldPos.y)*i);

					try {
						Thread.sleep(500/(getSteps()*MovableEntity.this.getSpeed()));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				animating = false;
				MovableEntity.this.setPosition(newPosition);
			}
		}).run();
	}
	
	@Override
	public void update() {
		if (up) {
			facingBack = false;
			facingForth = true;
			facingRight = false;
		}
		if (down) {
			facingBack = true;
			facingForth = false;
			facingRight = false;
		}
		if (right) {
			facingBack = false;
			facingForth = false;
			facingRight = true;
		}
		if (left) {
			facingBack = false;
			facingForth = false;
			facingRight = false;
		}

		if (up || down || left || right) {
			if (currentAction != Animation.MOVING ) { 
				currentAction = Animation.MOVING;
				animation.setFrames(Animation.MOVING);
			}
		}
		else if (currentAction != Animation.IDLE && !animating) {
			currentAction = Animation.IDLE;
			animation.setFrames(Animation.IDLE);
		}
	}

	@Override
	public void draw(Graphics2D g, Point p) {

		if(facingBack) {;
			currentSprite = getAnimation().getImage(FACINGBACK);
			g.drawImage(currentSprite, p.x+currentStep.x, p.y+currentStep.y-getPadding(),null); 
		}
		else if (facingForth) {
			currentSprite = getAnimation().getImage(FACINGFORTH);
			g.drawImage(currentSprite, p.x+currentStep.x, p.y+currentStep.y-getPadding(),null); 
		}
		else if (facingRight) {
			currentSprite = getAnimation().getImage(FACINGSIDEWAYS);
			g.drawImage(currentSprite, p.x+currentStep.x, p.y+currentStep.y-getPadding(),null); 
		}
		else {
			currentSprite = getAnimation().getImage(FACINGSIDEWAYS);
			g.drawImage(currentSprite, p.x+currentStep.x + getTileSize().width, p.y+currentStep.y-getPadding(), - getTileSize().width, getTileSize().height, null);
		}
	}
	
	protected abstract int[] getNumFrames();
	
	protected abstract int getTilesetRows();
	
	protected int getPadding() {
		return size.height - 50;
	}
	
	protected Animation getAnimation() {
		return animation;
	}
	
	public ArrayList<BufferedImage[][]> getSprites() {
		return sprites;
	}

	public Dimension getTileSize() {
		return size;
	}
	
	@Override
	public Point getPosition() {
		return this.position;
	}

	@Override
	public int getSpeed() {
		return this.speed;
	}
	@Override
	public int getSteps() {
		return this.steps;
	}
	
	@Override
	public void setPosition(Point p) {
		this.position.x = p.x;
		this.position.y = p.y;
		this.currentStep = new Point(0,0);
	}
	
	@Override
	public void setActualStep(int x, int y) {
		this.currentStep = new Point(x,y);	
		animation.nextFrame();
	}

	public void setLeft(boolean b) {
		this.left = b;
	} 
	public void setRight(boolean b) {
		this.right = b;
	} 
	public void setUp(boolean b) {
		this.up = b;
	} 
	public void setDown(boolean b) {
		this.down = b;
	} 
}
