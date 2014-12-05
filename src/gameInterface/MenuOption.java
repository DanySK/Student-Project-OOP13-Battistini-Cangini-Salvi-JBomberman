package gameInterface;

import java.awt.*;
import java.awt.image.*;

import javax.imageio.ImageIO;

public class MenuOption {
	
	private BufferedImage unchecked;
	private BufferedImage checked;
	
	private int x;
	private int y;
	
	/**
	 * Creates a menu option
	 * @param u path to the unchecked version of the image
	 * @param c path to the checked version of the image
	 */
	public MenuOption(String u, String c) {
		
		try {
			unchecked = ImageIO.read(getClass().getResourceAsStream(u));
			checked = ImageIO.read(getClass().getResourceAsStream(c));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the position of the "button"
	 * @param x abscissa where the image will be printed
	 * @param y ordinate where the image will be printed
	 */
	public void setPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * draws the "button"
	 * @param b <code>true</code> if selected
	 */
	public void draw(Graphics2D g, boolean b) {
		g.drawImage( !b ? unchecked : checked , x, y, null);
	}
}
