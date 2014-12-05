package tileMap;

import gameInterface.HUD;

import java.awt.*;
import java.util.List;
import java.util.Random;
import java.awt.image.*;
import java.io.*;
import java.util.LinkedList;

import javax.imageio.ImageIO;

import object.Tile;
import object.movable.Bomb;
import object.movable.Player;
import main.GamePanel;

public class TileMap {

	// Position
	private int initialX;
	private int initialY;

	// Map
	private Cell[][] map;

	// Drawing stuff
	private int tileSize;
	private double overlap;
	public int rowOffset;
	public int colOffset;

	// Tileset
	private BufferedImage tileset;
	private int tilesetColumns;
	private int tilesetRows = 3;
	private Tile[][] tiles;

	public TileMap(int tileSize) {
		this.tileSize = tileSize;
		overlap = 3/4.0;
	}
	
	public void loadTiles(String s) {

		try {

			tileset = ImageIO.read(getClass().getResourceAsStream(s));
			tilesetColumns = tileset.getWidth() / tileSize;
			tiles = new Tile[tilesetRows][tilesetColumns];


			for (int col = 0; col < tilesetColumns; col++) {
				tiles[Tile.WALKABLE][col] = new Tile (s, Tile.WALKABLE, new Point(col * tileSize,0),new Dimension(tileSize, tileSize));
				tiles[Tile.UNBREAKABLE][col] = new Tile(s, Tile.UNBREAKABLE, new Point(col * tileSize,tileSize), new Dimension(tileSize, tileSize));
				tiles[Tile.BREAKABLE][col] = new Tile(s, Tile.BREAKABLE, new Point(col * tileSize,tileSize*2), new Dimension(tileSize, tileSize));
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void loadMap(String s) throws TilesNotLoadedException {

		if (tiles == null) { 
			throw new TilesNotLoadedException();
		}

		try {

			InputStream in = getClass().getResourceAsStream(s);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));

			List<Cell[]> tmpMap = new LinkedList<>();

			String delims = "\\s+";

			int c;
			String line;
			while((line = br.readLine()) != null) {
				c = 0;
				String[] values = line.split(delims);
				Cell[] tmpLine = new Cell[values.length];
				tmpMap.add(tmpLine);
				for (String value : values) {

					int row = Integer.parseInt(value) % tilesetColumns;
					int col = Integer.parseInt(value) / tilesetColumns % tilesetColumns;

					tmpLine[c++] = new Cell( tiles[row][col] );
				}
			}

			map = new Cell[tmpMap.size()][tmpMap.get(0).length];
			map = tmpMap.toArray(map);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	public int getTileSize() { 
		return tileSize; 
	}

	public int getx() { 
		return initialX; 
	}

	public int gety() { 
		return initialY; 
	}

	/**
	 * gets the tilesize, considering the fact that they have an overlap 
	 * @return
	 */
	public Dimension getEffectiveTileSize() {
		return new Dimension(tileSize,(int)(tileSize*overlap));
	}

	public int getDrawnRows() {
		return Math.min( (GamePanel.HEIGHT - initialY - HUD.MENU_HEIGHT) / (int)(tileSize*overlap) , getMapRows() );
	}

	public int getDrawnColumns() {

		return Math.min( (GamePanel.WIDTH - initialX*2 ) / tileSize , getMapColumns() );
	}

	public int getMapRows() {
		return map.length;
	}

	public int getMapColumns() {
		return map[0].length;
	}

	public int getTilesetRows() {
		return tilesetRows;
	}

	public Cell[][] getMap() {
		return map;
	}
	public Cell getCell(Point p) {
		return getMap()[p.y][p.x];
	}

	public void setPosition(int x, int y) {

		this.initialX = x;
		this.initialY = y;
	}

	public void setPosition(Point p) {
		setPosition(p.x,p.y);
	}

	public void update(int c, Player player) {

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {
				Cell actualCell = map[i][j];

				if( actualCell.Contains(Bomb.class) && actualCell.getBomb().hasToDie() ){
					actualCell.getBomb().detonate(this, new Point(j,i));
					if(actualCell.getBomb().getOwner() instanceof Player) {
						((Player)actualCell.getBomb().getOwner()).placedBombs--;
					}
					actualCell.remove(actualCell.getBomb());
				}

				actualCell.update(c);
			}
		}


		//calcoli utili per lo spostamento della mappa nel caso sia + grande della finestra

		/* player.getPosition().x-tileMap.getDrawnColumns()/2 sono le prime colonne (da sinistra) che non vanno disegnate
		 * perch� nello schermo posso vederne solo tileMap.getDrawnColumns()
		 * Math.max([...] , 0) serve per evitare che si vada in negativo quando il player � vicino al bordo sinistro
		 * della mappa
		 * tileMap.getMapColumns()-tileMap.getDrawnColumns() � il numero di colonne che non vengono disegnate per via delle
		 * dimensioni della finestra, in pratica � il massimo numero di colonne saltabili a sinistra per evitare problemi
		 * Math.min([...] , [...]) serve ad evitare che la visuale si sposti oltre la mappa
		 */

		if(player != null) {
			this.colOffset = Math.min(Math.max(player.getPosition().x-this.getDrawnColumns()/2 , 0) , 
					this.getMapColumns()-this.getDrawnColumns());
			this.rowOffset = Math.min(Math.max(player.getPosition().y-this.getDrawnRows()/2 , 0) , 
					this.getMapRows()-this.getDrawnRows());
		}

	}

	/**
	 * {@link #LogicaltoPhysical(Point)}
	 * @param x x coordinate of the point to convert
	 * @param y y coordinate of the point to convert
	 */
	public Point LogicaltoPhysical(double x, double y){
		Point p = new Point((int)((x-colOffset) * tileSize), (int)((y-rowOffset) * tileSize * overlap));
		p.x += initialX;
		p.y += initialY;
		return p;

	} 

	/**
	 * converts a logical point into physical coordinates used to draw to screen
	 * @param p the logical point to convert
	 * @return resulting physical coordinates
	 */
	public Point LogicaltoPhysical(Point p){
		return LogicaltoPhysical(p.x,p.y);
	} 

	/**
	 * puts random breakable blocks where there isn't a unbreakable block
	 * @param percentage the chance of a non-unbreakable block to become BREAKABLE
	 */
	public void randomizeBreakableBlocks(int percentage) {
		java.util.Random r = new Random();

		for (int i = 0; i < map.length; i++) {
			for (int j = 0; j < map[0].length; j++) {

				if (map[i][j].getTile().getType() != Tile.UNBREAKABLE) {
					if (r.nextInt(100)+1 <= percentage) {
						map[i][j] = new Cell(tiles[Tile.BREAKABLE][0] );					
					} else {
						map[i][j] = new Cell(tiles[Tile.WALKABLE][0] );	
					}
				} 
			}
		}
	}

	public void draw(Graphics2D g) {

		for (int row = rowOffset; row < rowOffset + getDrawnRows(); row++) {
			for (int col = colOffset; col < colOffset + getDrawnColumns(); col++) {
				//� necessario passare prima col e poi row perch� le coordinate su schermo e gli indici
				//della matrice lavorano su basi differenti
				//su schermo se aumento la seconda coordinata equivale a spostarsi in basso, sulla matrice,
				//invece, vorrebbe dire spostarsi a destra sulla stessa riga
				Point p = LogicaltoPhysical(col,row);
				map[row][col].forEach(e -> e.draw(g,p));
			}
		}
	}
	
	@SuppressWarnings("serial")
	public class TilesNotLoadedException extends Exception {
		@Override
		public String toString() {
			return "You are trying to load a map without having loaded the tiles!\nYou have to call LoadTiles() before LoadMap().";
		}
	}

}


