package object;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javax.imageio.ImageIO;

/**
 * @author Loris<br/>
 * Abstract class that implements {@link IMapObject}, it provides basic implementations of {@link IMapObject#kill()}, 
 * {@link IMapObject#hasToDie()}, {@link IMapObject#draw(Graphics2D, Point)}.<br/>
 * It also adds support for images.
 */
public abstract class MapObject implements IMapObject, Cloneable{

	// Object stuff
	protected int lifes;
	
	// Sprites stuff
	private BufferedImage image;
	protected BufferedImage currentSprite;
	
	/**
	 * creates a {@link MapObject} with the specified parameters.
	 * @param lifes number of times this {@link MapObject} can be damaged by something
	 * @param pathImage path to the image this {@link MapObject} will use
	 * @param origin Point from where the sprite this {@link MapObject} will use will be taken
	 * @param spriteSize the size of the sprite to use<br/>
	 * (the sprite is the part of the image this {@link MapObject} will use.)
	 */
	public MapObject(int lifes, String pathImage,Point origin, Dimension spriteSize) {
		this.lifes = lifes;
        try {
            this.image = ImageIO.read(getClass().getResourceAsStream(pathImage));
            currentSprite = this.image.getSubimage(origin.x, origin.y, spriteSize.width, spriteSize.height);
        } catch (Exception e) {
        	currentSprite = this.image;
        }
    }

	@Override
	public void kill() {
		this.lifes--;
	}
	
	@Override
	public boolean hasToDie() {
		return lifes <= 0;
	}
	
	public BufferedImage getImage() {
		return image;
	}
	public void setImage(BufferedImage image) {
		this.image = image;
	}
	
	@Override
	public void draw(Graphics2D g, Point p) {
		g.drawImage(currentSprite,p.x,p.y,null);
	}
	
	/**
	 * Used to find if, at Runtime,this is an instance of the given class (c) 
	 * @param c class to check
	 * @return <code>true</code> if this {@link MapObject} is of class c or if c is a superclass of this object's class
	 */
	@SuppressWarnings("unchecked")
	public <T extends MapObject> boolean isOfType(Class <T> c) {
		Class<? extends MapObject> t = this.getClass();
		do {
			if(t == c) return true; 
		} while ((t = (Class<? extends MapObject>) t.getSuperclass()) != null);
		
		return false;
	}
}
