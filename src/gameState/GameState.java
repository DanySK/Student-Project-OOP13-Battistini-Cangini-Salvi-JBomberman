package gameState;


public interface GameState {
	
	/**
	 * Initializes all the starting parameters
	 */
	public void init();
	/**
	 * Does all the necessary updates
	 */
	public void update();
	/**
	 * Draws what the user sees
	 */
	public void draw(java.awt.Graphics2D g);
	/**
	 * Does specific actions when a key is pressed
	 * @param k the pressed key
	 */
	public void keyPressed(int k);
	/**
	 * Does specific actions when a key is released
	 * @param k the released key
	 */
	public void keyReleased(int k);
	
}
