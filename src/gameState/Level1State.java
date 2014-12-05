package gameState;

import java.awt.*;

import object.movable.Player;

public class Level1State extends LevelState {
	
	private static final int TILESIZE = 50;
	private static final int CHANCE = 50;
	
	public Level1State() {
		super("Level 1");
	}

	public void init() {
		super.init(TILESIZE,"/maps/level1-1.map",new Point(100,0));
		tileMap.randomizeBreakableBlocks(50);
		addPlayer(new Player("player1", 3, "/sprites/player/whitespritesheet.png", new Dimension(50,70), new Point(1,1)));
	
		spawnEnemies(10);
	}
	
	@Override
	public void update() {
		super.update();

		if (enemies.isEmpty()) {
			for (int i = 0; i < tileMap.getMap().length; i++) {
				for (int j = 0; j < tileMap.getMap()[0].length; j++) {
					tileMap.getMap()[i][j] = null;
				}
			}
			GameStateManager.getManager().setState(GameStateManager.LEVEL2STATE);
			return;
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
		return 60 * 6;
	}
}
