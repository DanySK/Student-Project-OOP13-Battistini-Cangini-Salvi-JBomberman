package gameState;

import gameInterface.Background;
import gameInterface.MenuOption;

import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.util.HashMap;

import audio.AudioPlayer;

public class ControlsState implements GameState {
	
	private Background abg;
	private Background bg;
	
	private MenuOption mo;
	private int currentChoice = -1;
	
	private HashMap<String, AudioPlayer> sfx;
	
	public ControlsState() {
		
		mo = new MenuOption("/buttons/mmu.png","/buttons/mmc.png");
		
		abg = new Background("/backgrounds/animatedbg.png");
		abg.setVector(-1.5, 0);
		
		bg = new Background("/backgrounds/controlsbg.png");
		
		mo.setPosition(770, 450);
		
		sfx = new HashMap<String, AudioPlayer>();
		sfx.put("select", new AudioPlayer("/sfx/select.wav"));
		sfx.put("confirm", new AudioPlayer("/sfx/confirm.wav"));
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
		mo.draw(g, currentChoice == 0);
	}	
	
	private void select() {
		if (currentChoice == 0) {
			GameStateManager.getManager().setState(GameStateManager.MENUSTATE);
		}
	}
	
	public void keyPressed(int k) {
		if (k == KeyEvent.VK_ENTER) {
			sfx.get("confirm").play();
			select();
		}
		if (k == KeyEvent.VK_UP || k == KeyEvent.VK_DOWN) {
			sfx.get("select").play();
			currentChoice++;
			if (currentChoice == 1) {
				currentChoice = -1;
			}	
		}
	}
	public void keyReleased(int k) {}
	
}
