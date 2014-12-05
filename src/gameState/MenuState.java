package gameState;

import audio.AudioPlayer;
import gameInterface.Background;
import gameInterface.MenuOption;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

public class MenuState implements GameState {
	
	private Background abg;
	private Background bg;
	
	private ArrayList<MenuOption> mo;
	private int currentChoice = 0;
	
	private HashMap<String, AudioPlayer> sfx;
	
	public MenuState() {
		
		try {
			mo = new ArrayList<MenuOption>();
			
			abg = new Background("/backgrounds/animatedbg.png");
			abg.setVector(-1.5, 0);
			
			bg = new Background("/backgrounds/menubg.png");
			
			mo.add(new MenuOption("/buttons/spu.png","/buttons/spc.png"));
			mo.add(new MenuOption("/buttons/mpu.png","/buttons/mpc.png"));
			mo.add(new MenuOption("/buttons/cu.png","/buttons/cc.png"));
			mo.add(new MenuOption("/buttons/qu.png","/buttons/qc.png"));
			
			mo.get(0).setPosition(200, 125);
			mo.get(1).setPosition(230, 220);
			mo.get(2).setPosition(175, 330);
			mo.get(3).setPosition(280, 425);
			
			sfx = new HashMap<String, AudioPlayer>();
			sfx.put("select", new AudioPlayer("/sfx/select.wav"));
			sfx.put("confirm", new AudioPlayer("/sfx/confirm.wav"));
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void init() {
		abg.setPosition(0, 0);
	}
	
	public void update() {
		abg.update();
	}
	
	public void draw(Graphics2D g) {
		// draw background
		abg.draw(g);
		bg.draw(g);
		
		// draw menu options
		for (int i = 0; i < mo.size(); i++) {
			mo.get(i).draw(g, i == currentChoice);
		}
	}	
	
	private void select() {
		if (currentChoice == 0) {
			GameStateManager.getManager().setState(GameStateManager.LEVEL1STATE);
		}
		if (currentChoice == 1) {
			GameStateManager.getManager().setState(GameStateManager.MULTIPLAYERSTATE);
		}
		if (currentChoice == 2) {
			GameStateManager.getManager().setState(GameStateManager.CONTROLSSTATE);
		}
		if (currentChoice == 3) {
			// exit
			System.exit(0);
		}
	}
	
	public void keyPressed(int k) {
		if (k == KeyEvent.VK_ENTER) {
			sfx.get("confirm").play();
			select();
		}
		if (k == KeyEvent.VK_UP) {
			sfx.get("select").play();
			currentChoice--;
			if (currentChoice == -1) {
				currentChoice = 3;
			}
		}
		if (k == KeyEvent.VK_DOWN) {
			sfx.get("select").play();
			currentChoice++;
			if (currentChoice == 4) {
				currentChoice = 0;
			}		
		}
	}
	public void keyReleased(int k) {}
	
}

