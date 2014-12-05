package gameState;

import java.awt.*;
import java.util.Random;

import object.Tile;
import object.movable.Enemy;
import object.movable.Player;

public class Level2State extends LevelState {
	
	private static final int TILESIZE = 50;
	private static final int CHANCE = 20;
	
	public Level2State() {
		super("Level 2");
	}
	
	public void init() {
		
		super.init(TILESIZE,"/maps/level2-1.map",new Point(100,0));
		tileMap.randomizeBreakableBlocks(50);
		addPlayer(new Player("player1", 3, "/sprites/player/whitespritesheet.png", new Dimension(50,70), new Point(1,1)));
	
		Random r = new Random();
		for (int i = 0; i < 10; i++) {
			int x = 0;
			int y = 0;
			
			while (tileMap.getCell(new Point(x,y)).getTile().getType() == Tile.UNBREAKABLE) {
				x = r.nextInt(tileMap.getMapColumns());
				y = r.nextInt(tileMap.getMapRows());
			}
			
			
			int n = new Random().nextInt(2)+1;
			// n=1 -> cinghiale
			// n=2 -> ape
			
			int lifes = n==1?2:1;
			float speed = n==1?1:3;
			int[] numSprites = n==1 ? new int[] { 1,2 } : new int[] { 3,3 };
			
			addEnemy(new Enemy(lifes, speed,"/sprites/enemy/enemy"+n+"Spritesheet.png", new Dimension(50,50), new Point(x,y),numSprites));
		}
	}

	@Override
	public Dimension getTileSize() {
		return tileMap.getEffectiveTileSize();
	}

	@Override
	public Stage getStage() {
		return LevelState.Stage.classic;
	}

	@Override
	public int getChanceToSpawnPowerup() {
		return CHANCE;
	}
	
	@Override
	public int getLevelTime() {
		return 60 * 3;
	}
}
