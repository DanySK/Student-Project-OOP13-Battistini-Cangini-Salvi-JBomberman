package gameInterface;

import gameState.LevelState;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.imageio.ImageIO;

import object.PowerUp;
import object.movable.Player;

public class HUD {
	
	public static int MENU_HEIGHT = 100;
	
	private BufferedImage image;
	private BufferedImage rightimage;

	private Map<Integer,Player> players;
		private LevelState level;
	
	/**
	 * Creates the HUD (Heads-up display)
	 * @param players {@link Map} containing all the {@link Player}s
	 */
	public HUD(LevelState level, ConcurrentHashMap<Integer, Player> players) {
		
		try {
			image = ImageIO.read(getClass().getResourceAsStream("/hud/hud.png"));
			rightimage = ImageIO.read(getClass().getResourceAsStream("/hud/righthud.png"));	
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		this.players = players;
		this.level = level;
	}
	
	/**
	 * Draws the HUD with the {@link Player}s informations
	 */
	public void draw(Graphics2D g) {
		
		g.drawImage(image, 0, 0, null);

		g.setColor(Color.WHITE);
		g.setFont(new Font("Arial", Font.BOLD, 24));
		g.drawString(level.getName(), 730, 555);
		
		g.setFont(new Font("Arial", Font.BOLD, 14));
		int min = level.getRemainingSeconds() / 60;
		int sec = level.getRemainingSeconds() % 60;
		g.drawString(min + ":" + (sec<10? "0" : "") + sec, 570, 20);
		
		g.setFont(new Font("Arial", Font.BOLD, 12));
		g.setColor(new Color(131, 74, 35));
		
		if (players.containsKey(0)) {
			ArrayList<PowerUp> pw = players.get(0).getPowerUps();
			
			g.drawString(players.get(0).getName().toUpperCase(), 25, 35);
			g.drawImage(players.get(0).getIcon(), 35, 30, null);
			
			
			for (int i = 0; i < pw.size(); i++) {
				g.drawImage(pw.get(i).getCurretSprite(), 28, 105 + 90 * i, null);
			} 
		}
		if (players.containsKey(1)){
			ArrayList<PowerUp> pw = players.get(1).getPowerUps();
			
			g.drawString(players.get(1).getName().toUpperCase(), 1075, 35);
			g.drawImage(rightimage, 1057, 80, null);
			g.drawImage(players.get(1).getIcon(), 1085, 30, null);
			
			for (int i = 0; i < pw.size(); i++) {
				g.drawImage(pw.get(i).getCurretSprite(), 1078, 105 + 90 * i, null);
			}  
			
		}
	}
}
