package gameState;

import java.awt.*;

import object.movable.Player;

public class MultiplayerLevelState extends LevelState {
	
	private static final int TILESIZE = 50;
	private static final int CHANCE = 50;
	
	public MultiplayerLevelState() {
		super("Multiplayer level");
	}

	public void init() {
		super.init(TILESIZE,"/maps/multiplayerMap.map",new Point(100,0));
		tileMap.randomizeBreakableBlocks(70);
		addPlayer(new Player("Player1", 3, "/sprites/player/whitespritesheet.png", new Dimension(50,70), new Point(1,1)));
		addPlayer(new Player("Player2", 3, "/sprites/player/blackspritesheet.png", new Dimension(50,70), new Point(10,5)));
		
		spawnEnemies(5);
	}
	
	@Override
	public void update() {
		super.update();
		
		if (players.size() == 1) {
			players.clear();
			GameStateManager.getManager().setState(GameStateManager.MENUSTATE);
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
		return 60 * 20;
	}
}
