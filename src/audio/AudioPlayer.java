package audio;

import java.io.IOException;

import javax.sound.sampled.*;

public class AudioPlayer {
	
	private static boolean canPlayAudio = true;
	private Clip clip;
	
	/**
	 * Obtains an audio player that creates an audio clip ready to be played.
	 * @param s path to the file to play
	 */
	public AudioPlayer(String s) {
		if (canPlayAudio) {
			try {
				AudioInputStream ais = AudioSystem.getAudioInputStream(getClass().getResource(s));
				clip = AudioSystem.getClip();
				clip.open(ais);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
			catch (Exception e) {
				canPlayAudio = false;
			}
		}
	}
	
	/**
	 * Plays the clip from the start
	 */
	public void play() {
		if (clip == null){
			return;
		}
		stop();
		clip.setFramePosition(0);
		clip.start();
	}
	
	/**
	 * Stops the clip
	 */
	public void stop() {
		if(clip.isRunning()) {
			clip.stop();
		}
	}
	
	/**
	 * Quits the audio player.
	 */
	public void close() {
		stop();
		clip.close();
	}
	
}