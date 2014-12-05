package object.movable;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.imageio.ImageIO;

import object.PowerUp;
import tileMap.Cell;

public class Player extends MovableEntity {
	
	public static int MAXPOWERUPS = 5;
	
	// player stuff
	private String name;
	private BufferedImage icon;
	
	// bomb stuff
	private int allowedBombs;
	private int bombRange;
	public int placedBombs;
	private int placedBombsType;
	
	// power up stuff
	private ArrayList<PowerUp> powerUps;
	private boolean increaseRemainingTime;
	private boolean isImmune;
	private boolean kick;
	private boolean canWalkThroughBlocks = false;
	private boolean canWalkThroughBombs = false;	
	
	public Player(String name, int lifes, String pathImage,Dimension dimension, Point position) {
		super(lifes, pathImage, new Point(0,0), dimension,position );
		
		this.name = name;
		this.allowedBombs = 1;
		this.placedBombs = 0;
		this.bombRange = 1;
		this.placedBombsType = Bomb.Type.NORMAL;
		this.kick = false;
		
		facingBack = true;
		facingForth = false;
		facingRight = false;
		
		powerUps = new ArrayList<>();
		
		try {
			this.icon = ImageIO.read(getClass().getResourceAsStream(pathImage.replace("spritesheet", "playericon")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public String getName() {
		return name;
	}

	public ArrayList<PowerUp> getPowerUps() {
		return powerUps;
	}
	
	@Override
	protected int[] getNumFrames() {
		return new int[] {1, 6};
	}
	
	@Override
	protected int getTilesetRows() {
		return 3;
	}
	
	public int getAllowedBombs() {
		return allowedBombs;
	}
	
	public int getBombRange() {
		return bombRange;
	}
	
	public int getPlacedBombsType() {
		return placedBombsType;
	}
	
	public BufferedImage getIcon() {
		return icon;
	}
	
	public boolean isImmune() {
		return isImmune;
	}
	
	public void IncreaseLifes() {
		this.lifes++;
	}
	
	public void addPowerUp(PowerUp powerUp) {
		this.powerUps.add(powerUp);
	}

	public void decreaseAllowedBombs() {
		if(this.allowedBombs > 1) {
			this.allowedBombs--;
		}
	}
	
	public void IncreaseAllowedBombs() {
		this.allowedBombs++;
	}

	public void decreaseBombRange() {
		if(this.bombRange > 1) {
			this.bombRange--;
		}
	}
	
	public void IncreaseBombRange() {
		this.bombRange++;	
	}
	
	public void decreaseSpeed() {
		if(this.speed > 1) {
			this.speed--;
		}
	}
	public void IncreaseSpeed() {
		this.speed++;
	}
	
	public void placePowerBombs() {
		placedBombsType = Bomb.Type.POWER;
	}	

	public void kick() {
		this.kick = true;
	}
	public boolean canKick() {
		return this.kick;
	}
	
	public void walkThroughBlocks() {
		this.canWalkThroughBlocks = true;
	}
	
	public boolean canWalkThroughBlocks() {
		return canWalkThroughBlocks;
	}

	public void walkThroughBombs() {
		this.canWalkThroughBombs = true;
	}
	
	public boolean canWalkThroughBombs() {
		return canWalkThroughBombs;
	}
	
	public boolean increaseTime() {
		return increaseRemainingTime;
	}
	
	public void setIncreaseTime(boolean increase) {
		this.increaseRemainingTime = increase;
	}
	
	public void PlaceBomb(Cell cell) {
		cell.add(1, new Bomb("/sprites/bombs/bomb.png",new Dimension(50,50),3,this) );
		this.placedBombs++;
	}
	
	public void immunize(int milliseconds) {
		this.isImmune = true;
		new Timer().schedule(new TimerTask() {
			
			@Override
			public void run() {
				Player.this.isImmune = false;
			}
		}, milliseconds);
	}
	
	@Override
	public void kill() {
		if(!this.isImmune) {
			super.kill();
			
			this.immunize(3000);
		}
	}
	
	@Override
	public void update() {
		super.update();
		
		powerUps.removeIf(p -> p.hasToDie());
		powerUps.forEach(p -> p.update());
	}
	
	@Override
	public void draw(Graphics2D g, Point p) {
		super.draw(g, p);
		
		g.setFont(new Font("Arial", Font.BOLD, 12));
		g.setColor(!isImmune ? new Color(0x00, 0xFF, 0xFF) : new Color(0xFF, 0xFF, 0x00));
		g.drawString(this.name + " - " + this.lifes + "♥", p.x + currentStep.x - 10, p.y + currentStep.y);

	}
}
