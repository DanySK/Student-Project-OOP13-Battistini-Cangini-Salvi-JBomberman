package gameState;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

import audio.AudioPlayer;
import object.Tile;
import object.movable.*;
import tileMap.*;
import tileMap.TileMap.TilesNotLoadedException;
import main.GamePanel;
import gameInterface.*;

public abstract class LevelState implements GameState {

	// Audio player
	private HashMap<String, AudioPlayer> sfx;

	// Map stuff
	protected TileMap tileMap;
	protected ConcurrentHashMap<Integer,Player> players;
	protected CopyOnWriteArrayList<Enemy> enemies;
	private Background bg;

	// Level information
	private String name;
	private HUD hud;
	private Timer levelTimer;
	private int remainingSeconds;

	// Entity timers
	private boolean T1Running;
	private boolean T2Running;
	private Timer enemyTimer;

	// Pressed keys
	protected Map<Integer,LinkedList<Integer>> controls = new HashMap<>();
	private CopyOnWriteArraySet<Integer> keys;

	// Graphic style 
	public static enum Stage {
		classic		
	}	

	public LevelState(String name) {

		keys = new CopyOnWriteArraySet<>();
		players = new ConcurrentHashMap<>();
		enemies = new CopyOnWriteArrayList<>();
		enemyTimer = null;
		levelTimer = null;

		// player1 controls
		LinkedList<Integer> tmp1 = new LinkedList<>();
		tmp1.add(KeyEvent.VK_ENTER);
		tmp1.add(KeyEvent.VK_LEFT);
		tmp1.add(KeyEvent.VK_RIGHT);
		tmp1.add(KeyEvent.VK_UP);
		tmp1.add(KeyEvent.VK_DOWN);
		controls.put(0, tmp1);

		// player2 controls
		LinkedList<Integer> tmp2 = new LinkedList<>();
		tmp2.add(KeyEvent.VK_SPACE);
		tmp2.add(KeyEvent.VK_A);
		tmp2.add(KeyEvent.VK_D);
		tmp2.add(KeyEvent.VK_W);
		tmp2.add(KeyEvent.VK_S);
		controls.put(1, tmp2);

		this.name = name;

		bg = new Background("/backgrounds/" + getStage() + "ingamebg.png");
		bg.setPosition(100, 0);

		hud = new HUD(this,players);

		sfx = new HashMap<String, AudioPlayer>();
		sfx.put("place", new AudioPlayer("/sfx/place.wav"));
		sfx.put("kick", new AudioPlayer("/sfx/kick.wav"));		
	}

	public void init(int tilesize, String pathMap, Point position) {

		players.clear();
		enemies.clear();
		enemyTimer = null;

		tileMap = new TileMap(tilesize);
		tileMap.loadTiles("/tilesets/" + getStage() + "tileset.png");
		try {
			tileMap.loadMap(pathMap);
		} catch (TilesNotLoadedException e) {
			e.printStackTrace();
		}
		tileMap.setPosition(position);

		T1Running = false;
		T2Running = false;

		this.remainingSeconds = getLevelTime();

		if (levelTimer == null) {

			levelTimer = new Timer();
			levelTimer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					LevelState.this.remainingSeconds--;
				}
			}, 0, 1000);
		}
	}

	/**Gets the percentage of breakable blocks that will spawn a Power-up when broken.
	 * @return the percentage
	 */
	public abstract int getChanceToSpawnPowerup();
	public abstract Dimension getTileSize();
	public abstract Stage getStage();
	public abstract int getLevelTime();


	public int getRemainingSeconds() {
		return remainingSeconds;
	}

	public String getName() {
		return this.name;
	}

	public void IncreaseRemainingSeconds() {
		this.remainingSeconds += 60;
	}

	/**
	 * given a {@link MovableEntity} and a {@link Cell}, it says if the entity can walk on the cell
	 * @param cell the cell to check
	 * @param me the entity to check
	 * @return <code>true</code> if the entity can walk on the cell, <code>false</code> otherwise.
	 */
	public boolean isWalkable(Cell cell, object.movable.MovableEntity me) {

		if (me instanceof Player) {
			Player p = (Player)me;
			if (cell.getTile().getType() == Tile.BREAKABLE && !p.canWalkThroughBlocks()) {
				return false;
			}
			if (cell.Contains(Bomb.class) && !p.canWalkThroughBombs() && !p.canKick()) {
				return false;
			}
		} 
		else { 
			if (me instanceof Bomb) {
				if (cell.contains(((Bomb)me).getOwner()) && cell.Get(MovableEntity.class).size() > 1) {
					return false; 
				}
				if (!cell.contains(((Bomb)me).getOwner()) && cell.Contains((MovableEntity.class))) {
					return false;
				}
				//la bomba non può essere piazzata se c'è qualcuno, oltre a colui che la piazza, nella stessa cella
			} 
			if (me instanceof Enemy){
				if (cell.Contains(MovableEntity.class) && !cell.Contains(Player.class)) {
					return false; 
				}
				//un nemico non può andare su una cella con un altro nemico
				//al contrario può e deve andare su una cella contenente un player
			}
			if (cell.getTile().getType() == Tile.BREAKABLE) {
				return false; //bombe e nemici non possono attraversare blocchi distruttibili
			}
		}
		if (cell.getTile().getType() == Tile.UNBREAKABLE) {
			return false; //nessuno può attraversare blocchi indistruttibili
		}

		return true;
	}

	private void move(MovableEntity p, Point newpos) {
		Point oldPos = (Point)p.getPosition().clone();

		int o = (int) Math.signum(newpos.x - oldPos.x);
		int v = (int) Math.signum(newpos.y - oldPos.y);

		if (isWalkable(tileMap.getCell(newpos),p)) {

			if (p instanceof Player && tileMap.getCell(newpos).Contains(Bomb.class) && ((Player)p).canKick()) {
				final Bomb bomb = tileMap.getCell(newpos).getBomb();
				final Point bombPos = new Point(newpos.x, newpos.y);
				bombPos.x += o;
				bombPos.y += v;
				for(int i =2;isWalkable(tileMap.getCell(bombPos), bomb) ;i++) {
					bombPos.x = o*i+newpos.x;
					bombPos.y = v*i+newpos.y;
				}
				bombPos.x -= o;
				bombPos.y -= v;

				sfx.get("kick").play();
				bomb.createAnimation(bombPos, getTileSize());	
				tileMap.getCell(newpos).remove(bomb);
				bomb.setActualStep(0, 0);
				tileMap.getCell(bombPos).add(bomb);
			} 
			else {
				p.createAnimation(newpos,getTileSize());
				tileMap.getCell(oldPos).remove(p);
				tileMap.getCell(newpos).add(p);
			}
		}
	}

	public void addPlayer(Player p) {
		players.put(players.size(), p);
		ClearSpawnZone(p);
		tileMap.getCell(p.getPosition()).add(p);
	}

	public void addEnemy(Enemy enemy) {

		enemies.add(enemy);

		ClearSpawnZone(enemy);	
		tileMap.getCell(enemy.getPosition()).add(enemy);

		if (enemyTimer == null) {

			enemyTimer = new Timer();
			enemyTimer.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {
					enemies.removeIf(e -> e.hasToDie());
					if (enemies.isEmpty() || players.isEmpty()) {
						enemyTimer.cancel();
					}
					
					for (Enemy enemy : enemies) {
						try {
							if (enemy.isGoingLeft()) {
								move(enemy, new Point(enemy.getPosition().x-1, enemy.getPosition().y));
							}
							if (enemy.isGoingRight()) {
								move(enemy, new Point(enemy.getPosition().x+1, enemy.getPosition().y));
							}
							if (enemy.isGoingUp()) {
								move(enemy, new Point(enemy.getPosition().x, enemy.getPosition().y-1));
							}
							if (enemy.isGoingDown()) {
								move(enemy, new Point(enemy.getPosition().x, enemy.getPosition().y+1));
							}
						}
						catch (Exception e) {
							break;
						}
					}
				}
			}, 0, 100);
		}
	}

	protected void spawnEnemies(int numEnemies) {

		Random r = new Random();
		for (int i = 0; i < numEnemies; i++) {
			int x = 0;
			int y = 0;

			while (tileMap.getCell(new Point(x,y)).getTile().getType() == Tile.UNBREAKABLE) {
				x = r.nextInt(tileMap.getMapColumns());
				y = r.nextInt(tileMap.getMapRows());
			}


			int n = r.nextInt(2)+1;
			//n=1 -> cinghiale
					//n=2 -> ape

			int lifes = n==1?2:1;
			float speed = n==1?1:3;
			int[] numSprites = n==1 ? new int[] { 1,2 } : new int[] { 3,3 };

			addEnemy(new Enemy(lifes, speed,"/sprites/enemy/enemy"+n+"Spritesheet.png", new Dimension(50,50), new Point(x,y),numSprites));
		}
	}

	private void ClearSpawnZone(MovableEntity p) {
		tileMap.getCell(p.getPosition()).add(new Bomb(p));

		if(p instanceof Player) {
			((Player)p).placedBombs++;
		}
	}

	@Override
	public void update() {
		for (Player p : players.values()) {
			if(p.increaseTime()) {
				this.remainingSeconds += 60;
				p.setIncreaseTime(false);
			}
		}

		if (players.isEmpty() || remainingSeconds == 0) {
			enemyTimer.cancel();
			GameStateManager.getManager().setState(GameStateManager.MENUSTATE);
		} 
		else {
			tileMap.update(getChanceToSpawnPowerup(),players.get(0));
		}
	}

	@Override
	public void draw(Graphics2D g) {

		// clear screen
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, GamePanel.WIDTH, GamePanel.HEIGHT);

		// draw background
		bg.draw(g);

		// draw map and player
		tileMap.draw(g);

		// draw hud
		hud.draw(g);
	}

	/**
	 * Does the actions associated to the actually pressed keys for the given {@link Player}
	 * @param n index representing the player, 0 for player1 and 1 for player2
	 * @return <code>false</code> is the given {@link Player} is died, <code>true</code> otherwise.
	 */
	private boolean doInputStuff(int n) {
		Player player = players.get(n);

		if (player == null || player.hasToDie()) {
			players.remove(n);
			return false;
		}

		for (Integer k : keys) {

			if (k == controls.get(n).get(0)) /*enter o spazio*/{
				if(player.placedBombs < player.getAllowedBombs() && !tileMap.getCell(player.getPosition()).Contains(Bomb.class)) {
					
					//la seconda condizione serve ad evitare che, nel caso di player in grado di piazzare + bombe,
					//tutte le bombe vengano messe una sull'altra nella stessa cella
					//l'1 nell'add serve per inserire sempre la bomba sotto il player
					if(isWalkable( tileMap.getCell(player.getPosition()),new Bomb(player) )) {
						player.PlaceBomb(tileMap.getCell(player.getPosition()));
						sfx.get("place").play();
					}
				}
			}
			// freccia sinistra o A
			else if (k == controls.get(n).get(1)) { 
				player.setLeft(true);
				move(player, new Point(player.getPosition().x-1, player.getPosition().y) );
			} 
			// freccia destra o D
			else if (k == controls.get(n).get(2)) {
				player.setRight(true);
				move(player, new Point(player.getPosition().x+1, player.getPosition().y) );
			} 
			// freccia su o W
			else if (k == controls.get(n).get(3)){
				player.setUp(true);
				move(player, new Point(player.getPosition().x, player.getPosition().y-1) );
			} 
			// freccia giù o S
			else if (k == controls.get(n).get(4)){
				player.setDown(true);
				move(player, new Point(player.getPosition().x, player.getPosition().y+1) );
			}
		}
		return true;
	}

	@Override
	public void keyPressed(int k) {
		if (k == KeyEvent.VK_ESCAPE) {
			remainingSeconds = 0;
			return;
		}

		if ((players.size() >= 1 && controls.get(0).contains(k)) || 
				(players.size() >= 2 && controls.get(1).contains(k)) ) {
			keys.add(k);
		}

		// faccio partire il thread che si preoccuperà del movimento se non è già partito
		if (!T1Running) {
			Timer T1 = new Timer();
			T1.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {

					T1Running = true;

					if(!doInputStuff(0)){
						this.cancel();
					}
				}
			}, 00,10);
		}

		if (!T2Running && players.containsKey(1)) {
			Timer T2 = new Timer();
			T2.scheduleAtFixedRate(new TimerTask() {

				@Override
				public void run() {

					T2Running = true;

					if(!doInputStuff(1)){
						this.cancel();
					}
				}
			}, 00,10);
		}
	}

	@Override
	public void keyReleased(int k) {
		if (keys.remove(k))
		{
			switch(k) {
			case KeyEvent.VK_LEFT:
				players.get(0).setLeft(false);
				break;
			case KeyEvent.VK_RIGHT:
				players.get(0).setRight(false);
				break;
			case KeyEvent.VK_DOWN:
				players.get(0).setDown(false);
				break;
			case KeyEvent.VK_UP:
				players.get(0).setUp(false);
				break;
			case KeyEvent.VK_A:
				players.get(1).setLeft(false);
				break;
			case KeyEvent.VK_D:
				players.get(1).setRight(false);
				break;
			case KeyEvent.VK_S:
				players.get(1).setDown(false);
				break;
			case KeyEvent.VK_W:
				players.get(1).setUp(false);
				break;
			}
		}
	}
}
