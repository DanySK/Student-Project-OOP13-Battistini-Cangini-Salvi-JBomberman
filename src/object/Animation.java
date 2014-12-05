package object;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class Animation {
	
	// Animation actions
	public static final int IDLE = 0;
	public static final int MOVING = 1;
	
	// Sprites stuff
	private ArrayList<BufferedImage[][]> sprites;
	private BufferedImage[][] frames;
	private int currentFrame;
	
	private boolean playedOnce;
	
	
	public Animation(BufferedImage image, Dimension spriteSize,int tilesetRows, int[] numFrames) {
		playedOnce = false;
		
		sprites = new ArrayList<BufferedImage[][]>();
		
		for (int i = 0; i < numFrames.length; i++) {
			
			BufferedImage[][] bi = new BufferedImage[tilesetRows][numFrames[i]];
			
			for (int j = 0; j < numFrames[i]; j++) {
				for (int r = 0; r < tilesetRows; r++) {
					bi[r][j] = image.getSubimage((j+ (i>0 ? numFrames[i-1] : 0)) * spriteSize.width, spriteSize.height * r, spriteSize.width, spriteSize.height);	
				}
			}
			
			sprites.add(bi);
		}
		
		this.setFrames(IDLE);
	}
	
	public void setFrames(int frames) {
		this.frames = sprites.get(frames);
		currentFrame = 0;
	}
	
	public void setFrame(int i) { 
		currentFrame = i; 
	}
	
	public void nextFrame() {
		
		currentFrame++;
		if(currentFrame == frames[0].length) {
			playedOnce = true;
			currentFrame = 0;
		}
	}
	
	public BufferedImage getImage(int currentSide) { 
		return frames[currentSide][currentFrame]; 
	}

	public boolean hasPlayedOnce() {
		return playedOnce;
	}
}
