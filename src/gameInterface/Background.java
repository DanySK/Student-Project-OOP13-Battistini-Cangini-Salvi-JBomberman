package gameInterface;

import java.awt.*;
import java.awt.image.*;

import javax.imageio.ImageIO;

import main.GamePanel;

public class Background {
	
	private BufferedImage image;
	
	private double x;
	private double y;
	private double dx;
	private double dy;
	
	/**
	 * Loads an image from the given string
	 * @param s path to the image
	 */
	public Background(String s) {
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream(s));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Sets the position of the image on the frame
	 * @param x abscissa where the image will be printed
	 * @param y ordinate where the image will be printed
	 */
	public void setPosition(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Sets the vector for an animated background
	 * @param dx vector abscissa
	 * @param dy vector ordinate
	 */
	public void setVector(double dx, double dy) {
		this.dx = dx;
		this.dy = dy;
	}
	
	/**
	 * updates the position using vector parameters
	 */
	public void update() {
		x += dx;
		y += dy;
	}
	
	/**
	 * draws the background
	 */
	public void draw(Graphics2D g) {
		if (Math.abs(x) >= image.getWidth()) { 
			x = 0;
		}
		g.drawImage(image, (int)x, (int)y, null);

		if (image.getWidth() > GamePanel.WIDTH) {
			if(x < 0) {
				g.drawImage(image, (int)x + image.getWidth(), (int)y, null);
			}
			if(x > 0) {
				g.drawImage(image, (int)x - image.getWidth(), (int)y, null);
			}
		}
	}

}

