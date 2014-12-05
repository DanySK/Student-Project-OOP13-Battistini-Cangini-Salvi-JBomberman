package object;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.util.Timer;
import java.util.TimerTask;

public class Explosion extends MapObject {

	Animation animation;
	
	public Explosion(String pathImage, Point origin,Dimension spriteSize) {
		super(1, pathImage, origin, spriteSize);
		
		animation = new Animation(this.getImage(), spriteSize, 1, new int[] {9});

		new Timer().scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if(animation.hasPlayedOnce()) {
					this.cancel();
				} else {
					animation.nextFrame();
				}
			}
		}, 0, 100);
	}

	@Override
	public void update() {
		 if (this.animation.hasPlayedOnce()) {
			 this.kill();
		 }
	}
	
	@Override
	public void draw(Graphics2D g, Point p) {
		g.drawImage(animation.getImage(0), p.x, p.y,null); 
	}

}
