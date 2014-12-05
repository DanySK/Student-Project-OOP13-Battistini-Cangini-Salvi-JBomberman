package object;

import java.awt.Graphics2D;
import java.awt.Point;

/**
 * @author Loris<br/>
 * Interface for all the objects that stay in the game map
 */
public interface IMapObject {

	/**
	 * returns true if the Object has to die
	 * @return
	 */
	public boolean hasToDie();
	
	/**
	 * Object hit by something that make it lose a life
	 */
	public void kill();
	
	/**
	 * necessary operations to be done continuously
	 */
	public void update();
	
	/**
	 * draws this Object at the specified point
	 * @param g {@link Graphics2D} object to use to draw
	 * @param p Where this entity will be drawn
	 */
	public void draw(Graphics2D g, Point p);
}
