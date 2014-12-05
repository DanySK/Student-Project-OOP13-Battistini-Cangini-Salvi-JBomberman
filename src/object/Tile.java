package object;

import java.awt.Dimension;
import java.awt.Point;

public class Tile extends MapObject {
	
	// Tile types
	public static final int WALKABLE = 0;
	public static final int UNBREAKABLE = 1;
	public static final int BREAKABLE = 2;
	
	private int type;
	private boolean canSpawnPowerUp;
	
	public Tile(String path, int type, Point origin, Dimension sprite) {
		super(1, path, origin, sprite);
		
		this.type = type;
		this.canSpawnPowerUp = false;
	}

	private void changeType(int newType) {
		type = newType;
		currentSprite = this.getImage().getSubimage(0, newType * currentSprite.getWidth(), currentSprite.getWidth(), currentSprite.getHeight());
	}

    @Override
    public void kill() {
    	if(type == BREAKABLE) {
    		changeType(WALKABLE);
    		canSpawnPowerUp = true;
    	}
    }

	public Tile clone() throws CloneNotSupportedException {
		return (Tile)super.clone();
    }

	@Override
	public void update() { }
	
	public boolean canSpawnPowerUp() {
		return this.canSpawnPowerUp;
	}

	public void setCanSpawnPowerUp(boolean canSpawnPowerUp) {
		this.canSpawnPowerUp = canSpawnPowerUp;
	}
	public boolean CanSpawnPowerUp() {
		return canSpawnPowerUp;
	}
	
	public int getType() { 
		return type;
	}
}
