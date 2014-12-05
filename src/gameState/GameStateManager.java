package gameState;

import java.util.ArrayList;

public class GameStateManager {
	
	private ArrayList<GameState> gameStates;
	private int currentState;

	public static final int MENUSTATE = 0;
	public static final int MULTIPLAYERSTATE = 1;
	public static final int CONTROLSSTATE = 2;
	public static final int LEVEL1STATE = 3;
	public static final int LEVEL2STATE = 4;
	
	private static GameStateManager gsm;
	
	private GameStateManager() {
		
		gameStates = new ArrayList<GameState>();
		
		currentState = MENUSTATE;
		gameStates.add(new MenuState());
		gameStates.add(new MultiplayerLevelState());
		gameStates.add(new ControlsState());
		gameStates.add(new Level1State());
		gameStates.add(new Level2State());
	}
	
	/**
	 * Using the singleton pattern, obtains the only possible instance {@link GameStateManager}
	 * @return the {@link GameStateManager}
	 */
	public static GameStateManager getManager() {
		if (gsm == null) {
			gsm = new GameStateManager();
		}
		return gsm;
	}
	
	/**
	 * @return the current game state
	 */
	public int getCurrentState() {
		return this.currentState;
	}
	
	/**
	 * Sets a state of the game
	 * @param state {@link Integer} representing the state to set.
	 */
	public void setState(int state) {
		
		gameStates.get(state).init();
		currentState = state;
	}
	
	/**
	 * Does the updates of the current state
	 */
	public void update() {
		gameStates.get(currentState).update();
	}
	
	/**
	 * Draws the current state
	 * @param g
	 */
	public void draw(java.awt.Graphics2D g) {
		gameStates.get(currentState).draw(g);
	}
	
	/**
	 * Does a specific action when a key is pressed in the current state
	 * @param k the pressed key
	 */
	public void keyPressed(int k) {
		gameStates.get(currentState).keyPressed(k);
	}
	
	/**
	 * Does a specific action when a key is released in the current state
	 * @param k the released key
	 */
	public void keyReleased(int k) {
		gameStates.get(currentState).keyReleased(k);
	}
	

}
