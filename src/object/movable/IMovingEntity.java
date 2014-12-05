package object.movable;

import java.awt.Dimension;
import java.awt.Point;

import object.IMapObject;
import tileMap.Cell;
import tileMap.TileMap;

/**
 * @author Loris<br/>
 * Interface for all the Movable entities.<br/>
 * provides methods to create motions of these entities between {@link Cell}s of a {@link TileMap}
 */
public interface IMovingEntity extends IMapObject {

	/**
	 * Creates the "animation" for the movement of this entity between the actual position and newPosition
	 * @param newPosition where this entity must arrive
	 * @param tileSize the size of a tile of the map
	 */
	public void createAnimation(Point newPosition, Dimension tileSize);
	
	/**
	 * obtains the actual logical position of this Entity
	 */
	public Point getPosition();
	/**
	 * set the position of this Entity
	 * @param p the new position of this entity
	 */
	public void setPosition(Point p);
	/**
	 * obtains the speed of this entity
	 */
	public int getSpeed();
	/**
	 * obtains the number of steps this entity will do to move from a Cell to another
	 */
	public int getSteps();
	/**
	 * sets the actual step of this entity.
	 * @param x
	 * @param y
	 */
	public void setActualStep(int x, int y);
}
