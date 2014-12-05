package object;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import object.movable.Player;

public class PowerUp extends MapObject {
	
	public static class Type { 
		
		public static final int INCREASE_BOMB_RANGE = 0;
		public static final int INCREASE_ALLOWED_BOMBS = 1;
		public static final int INCREASE_SPEED = 2;
		public static final int POWER_BOMB = 3;
		public static final int WALK_THROUGH_WALLS = 4;
		public static final int WALK_THROUGH_BOMBS = 5;
		public static final int LIFE_UP = 6;
		public static final int KICK_BOMB = 7;
		public static final int GRAB_BOMB = 8;
		public static final int IMMUNITY = 9;
		public static final int INCREASE_TIME = 10;
		public static final int REMOTE_CONTROL = 11;
		public static final int DECREASE_BOMB_RANGE = 12;
		public static final int DECREASE_ALLOWED_BOMBS = 13;
		public static final int DECREASE_SPEED = 14;
		
		public static int random() {
			return new Random().nextInt(15);
		}
	}
	
	
	// Power-up stuff
	private final static int SPRITESIZE = 50;	
	private final static String PATH = "/sprites/powerups/pups.png";
	
	Player owner;
	private int type;
	
	/**
	 * creates a Power-up of the specified type.
	 * @param type The type of the power-up to create. All the possible values can be found in PowerUp.Type
	 */
	public PowerUp(int type) {
		super(1, PATH, new Point(type*SPRITESIZE,0), new Dimension(SPRITESIZE,SPRITESIZE));
		this.type = type;
		this.owner = null;
		
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				if(PowerUp.this.owner == null) {
					PowerUp.this.kill();
				}
			}
		}, 10000);
	}
	
	public void pickedUp(Player p) {
		this.owner = p;
		
		if(p.getPowerUps().size() == Player.MAXPOWERUPS){
			p.getPowerUps().remove(0);
		}
		p.addPowerUp(this);
		
		switch (type) {
			case PowerUp.Type.INCREASE_BOMB_RANGE:
				p.IncreaseBombRange();
				break;
			case PowerUp.Type.INCREASE_ALLOWED_BOMBS:
				p.IncreaseAllowedBombs();
				break;
			case PowerUp.Type.INCREASE_SPEED:
				p.IncreaseSpeed();
				break;
			case PowerUp.Type.POWER_BOMB:
				p.placePowerBombs();
				break;
			case PowerUp.Type.WALK_THROUGH_WALLS:
				p.walkThroughBlocks();
				break;
			case PowerUp.Type.WALK_THROUGH_BOMBS:
				p.walkThroughBombs();
				break;
			case PowerUp.Type.LIFE_UP:
				p.IncreaseLifes();
				break;
			case PowerUp.Type.KICK_BOMB:
				p.kick();
				break;
			case PowerUp.Type.GRAB_BOMB:
				//TODO
				break;
			case PowerUp.Type.IMMUNITY: //immunità per un certo tot di tempo (5 sec)
				p.immunize(5000);
				break;
			case PowerUp.Type.INCREASE_TIME:
				p.setIncreaseTime(true);
				break;
			case PowerUp.Type.REMOTE_CONTROL:
				//TODO
				break;
			case PowerUp.Type.DECREASE_ALLOWED_BOMBS: 
				p.decreaseAllowedBombs();
				break;
			case PowerUp.Type.DECREASE_BOMB_RANGE:
				p.decreaseBombRange();
				break;
			case PowerUp.Type.DECREASE_SPEED:
				p.decreaseSpeed();
				break;
			
		}
	}
	
	@Override
	public void update() {
		if(owner != null && this.type == PowerUp.Type.IMMUNITY && !owner.isImmune()) {
			this.kill();
		}
	}
	
	public BufferedImage getCurretSprite() {
		return this.currentSprite;
	}
}
