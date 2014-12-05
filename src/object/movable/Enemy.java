package object.movable;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class Enemy extends MovableEntity{

	private int[] numFrames;
	
	/**
	 * Creates an {@link Enemy} with the given parameters
	 * @param lifes the number of times this {@link Enemy} can be hit by a Bomb
	 * @param speed how often the {@link Enemy} will choose a new direction
	 * @param pathImage path to the image this {@link Enemy} will use
	 * @param spriteSize the size of a sprite
	 * @param position where this entity will be spawned
	 * @param sprites number of sprites (will be used as output for {@link #getNumFrames()})
	 */
	public Enemy(int lifes,float speed, String pathImage, Dimension spriteSize, Point position, int[] sprites) {
		super(lifes, pathImage, new Point(0,0), spriteSize, position);
		
		numFrames = sprites;
		
		createAnimation();
		
		new Timer().scheduleAtFixedRate(new TimerTask() {
			
			@Override
			public void run() {
				animation.nextFrame();
				
				int direction = new Random().nextInt()%4;
			
				Enemy.this.setLeft(false);
				Enemy.this.setRight(false);
				Enemy.this.setUp(false);
				Enemy.this.setDown(false);
				
				switch (direction) {
				case 0: //left
					Enemy.this.setLeft(true);
					break;
				case 1: //right
					Enemy.this.setRight(true);
					break;
				case 2: //up
					Enemy.this.setUp(true);
					break;
				case 3: //down
					Enemy.this.setDown(true);
					break;
				}
				
			}
		}, 0, 1000/(long)speed);
	}

	
	@Override
	protected int[] getNumFrames() {
		return numFrames;
	}
	@Override
	protected int getTilesetRows() {
		return 3;
	}

	public boolean isGoingLeft() {
		return left;
	}
	public boolean isGoingRight() {
		return right;
	}
	public boolean isGoingUp() {
		return up;
	}
	public boolean isGoingDown() {
		return down;
	}
	
	@Override
	public void draw(Graphics2D g, Point p) {
		super.draw(g, p);
		
		g.setFont(new Font("Arial", Font.BOLD, 12));
		g.setColor(new Color(0xFF, 0, 0));
		g.drawString(this.lifes + "♥", p.x + currentStep.x + getTileSize().width/2-5, p.y + currentStep.y);
	}
	
}
