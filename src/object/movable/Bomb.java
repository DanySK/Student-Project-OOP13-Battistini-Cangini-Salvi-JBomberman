package object.movable;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Timer;
import java.util.TimerTask;

import object.Tile;
import tileMap.TileMap;


public class Bomb extends MovableEntity {
	
	public static class Type { 
		
		public static final int NORMAL = 0; // bomba classica in tutti i punti cardinali
		public static final int POWER = 1;  // bomba che distrugge tanti cubi quanti il range
	}
	
	private int range;
	private int type;
	private MovableEntity owner; 
	private Timer timer;
	private int time;
	private boolean spawnExplosion;
	private int counter;
	private int changesOfSprite;
	private byte actual;
	
	public MovableEntity getOwner() {
		return owner;
	}
	
	@Override
	protected int[] getNumFrames() {
		return new int[] {1, 1};
	}
	@Override
	protected int getTilesetRows() {
		return 1;
	}
	
	/**
	 * Creates a Bomb with the given parameters
	 * @param pathImage the path to the image of the bomb
	 * @param spriteSize the size of a sprite
	 * @param time the seconds the bomb takes to explode
	 * @param owner the {@link MovableEntity} that placed this {@link Bomb}
	 */
	public Bomb(String pathImage, Dimension spriteSize, int time,MovableEntity owner) {
		super(1, pathImage, new Point(0,0), new Dimension(spriteSize),owner.getPosition());
		this.owner = owner;
		if(owner instanceof Player) {
			this.range = ((Player)owner).getBombRange();
			this.type = ((Player)owner).getPlacedBombsType();
		} else { //succede quando arrivo qui dopo aver chiamato clearSpawnZone() in LevelState per un Enemy
			this.range = 1;
			this.type = Bomb.Type.NORMAL;
		}
		this.time = time*1000;
		this.speed = 5;
		
		this.spawnExplosion = true;
		

		this.counter = 0;
		this.changesOfSprite = 6;
		this.actual = 0;
		
		if(time != 0) {
			startTimer();
		}
		
	}
	
	public Bomb(MovableEntity entity) {
		this("",new Dimension(),0, entity);
		
		this.spawnExplosion = false;
		this.lifes = 0;
		
		this.setPosition(position);
	}
	
	private boolean anim(int counter,int changesOfSprite ) {
		if(counter < changesOfSprite) {
			actual ^= 1;
			getAnimation().setFrames(actual);
			return false;
		} else {
			return true;
		}
		
	}
	
	public void detonate(TileMap map, Point position) {
		map.getCell(position).setDestroy(true);

		switch (type) {
		case Bomb.Type.NORMAL:
		case Bomb.Type.POWER:
			map.getCell(this.getPosition()).setDestroy(true);
			map.getCell(this.getPosition()).spawnExplosion(this.spawnExplosion);

			for (int t = 0; t < 4; t++) {
				//c e d servono ad ottenere le 4 direzioni
				int c= (int)(-Math.signum(t-1.5)*(t+1)%2);
				int d = (int)(-Math.signum(t-1.5)*t%2);
				for (int i = 1; i <= range; i++) {
					Point p = new Point(position.x + c*i,position.y+d*i);
					if (map.getCell(p).getTile().getType() == Tile.UNBREAKABLE) {
						break;
					} else {
						map.getCell(p).setDestroy(true);
						map.getCell(p).spawnExplosion(this.spawnExplosion);

						if(type == Bomb.Type.NORMAL && map.getCell(p).getTile().getType() == Tile.BREAKABLE) {
							break;
						}
					}
				}
			}

			break;
		}
	}
	
	/**
	 * Useful to make this bomb move when kicked
	 * @param newPosition where this {@link Bomb} should arrive
	 * @param tileSize the size of a map's Tile, useful to make some operations
	 */
	@Override
	public void createAnimation(Point newPosition, Dimension tileSize) {
		
		try {
			timer.cancel();
			super.createAnimation(newPosition, tileSize);
			startTimer();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void startTimer() {
		timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if( Bomb.this.anim(counter++,changesOfSprite) ){
					Bomb.this.lifes=0;
					this.cancel();
				}
			}
			
		}, 0, this.time*this.lifes / changesOfSprite);
	}

	@Override
	public int getSteps() {
		return 4;
	}
	
	@Override
	public void draw(Graphics2D g, Point p) {
		if(getAnimation() != null) {
			g.drawImage(getAnimation().getImage(0), p.x+currentStep.x, p.y+currentStep.y,null); 
		}
	}
}
