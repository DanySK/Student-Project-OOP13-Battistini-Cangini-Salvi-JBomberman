package tileMap;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import audio.AudioPlayer;
import object.Explosion;
import object.MapObject;
import object.PowerUp;
import object.Tile;
import object.movable.Bomb;
import object.movable.Enemy;
import object.movable.Player;

//CopyOnWriteArrayList � una versione particolare di ArrayList creata per funzionare con pi� thread
//usando tale classe si evitano problemi di concorrenza

@SuppressWarnings("serial")
public class Cell extends CopyOnWriteArrayList<MapObject> {

	// Audio player
	private HashMap<String, AudioPlayer> sfx;
	
	// Explosion stuff
	public boolean destroy;
	private boolean spawnExplosion;
	
	public Cell(Tile tile) {
		try {
			this.add(tile.clone());
			
			sfx = new HashMap<String, AudioPlayer>();
			sfx.put("boom", new AudioPlayer("/sfx/boom.wav"));
			
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		
		destroy = false;
	}
	
	public boolean canSpawnExplosion() {
		return spawnExplosion;
	}
	
	public Tile getTile() {
		return Get(Tile.class).get(0);
	}
	
	public Bomb getBomb() {
		return Get(Bomb.class).get(0);
	}
	
	public void spawnExplosion(boolean b) {
		this.spawnExplosion = b;
	}

	public void setDestroy(Boolean flag) {
		destroy = flag;
	}

	
	private boolean Contains(Class<? extends MapObject> c) {
		for (MapObject entity : this) {
			if( entity.isOfType(c) ) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Used to find if certain classes are contained in this {@link Cell}
	 * @param classes the classes the check
	 * @return <code>true</code> if all the given classes are contained in this {@link Cell}
	 */
	@SafeVarargs
	public final boolean Contains(Class<? extends MapObject> ... classes) {
		for (Class<? extends MapObject> c : classes) {
			if(!this.Contains(c)) {
				return false;
			}
		}
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends MapObject> List<T> Get(Class<T> c) {
		List<T> tmp = new ArrayList<>();
		for (MapObject entity : this) {
			if( entity.isOfType(c) ) {
				tmp.add((T)entity);
			}
		}
		return tmp;
	}
	
	public void update(int chancePowerup) {

		for (MapObject gameEntity : this) {
			gameEntity.update();
			
			if (gameEntity.hasToDie()) {
				this.remove(gameEntity);
			}
			
			if (destroy) {
				if (spawnExplosion) {
					sfx.get("boom").play();
					gameEntity.kill();
					this.add(new Explosion("/sprites/bombs/explosion.png", new Point(0,0), new Dimension(50,50)));
				} 
				else if (gameEntity instanceof Tile) {
					gameEntity.kill();
				}
			}	
		}
		
		destroy = false;
		
		if (this.getTile().CanSpawnPowerUp() && this.canSpawnExplosion()) {
			
			//genero un intero fra 1 e 100, se viene <= della prob. del livello allora spawno un power-up
			if(new java.util.Random().nextInt(100) + 1 <= chancePowerup) {
				this.add(new PowerUp(PowerUp.Type.random()));
			}
		}
		
		this.getTile().setCanSpawnPowerUp(false);
		
		if (this.Contains(Enemy.class, Player.class)) {
			this.Get(Player.class).get(0).kill();
		}
		
		if (this.Contains(PowerUp.class, Player.class)) {
			Player pl = this.Get(Player.class).get(0);
			PowerUp po = this.Get(PowerUp.class).get(0);
			
			po.pickedUp(pl);
			
			this.remove(po);
		}
	}

}
