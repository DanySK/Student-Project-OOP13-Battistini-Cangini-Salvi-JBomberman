package main;

import javax.swing.*;

public class Game {
	
	private static JFrame window;
	
	public static void main(String[] args) {	
		window = new JFrame("JBomberman");		
		window.getContentPane().add(new GamePanel());
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setVisible(true);
	}
}
