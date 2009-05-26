
package snake;

import java.awt.Color;
import java.awt.geom.Rectangle2D;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.*;
import java.util.Random;
import snake.MainWindow;

class Grid {
	enum MoveResult { Crash, Grow, Move };
	enum Block { Snake, Food, Wall };
	private static final Color[] blockColors = { Color.red, Color.green, Color.black };

	private Block grid[][];
	private Random rand;
	private Point startLoc;
	private int width;
	private int height;
	private double blockWidth;
	private double blockHeight;

	public boolean init(String file)
	{
		rand = new Random();
		startLoc = new Point();

		try {
			InputStream in = file == null ? getClass().getResourceAsStream("/snake/default.smg") : new FileInputStream(file);

			// highly arbitrary magic number
			if (in.read() != 42)
				throw new Exception(file + " (Not a snake map grid)");

			width = in.read();
			height = in.read();
			grid = new Block[width][height];

			for (int y = 0; y != height; ++y)
				for (int x = 0; x < width; x += 4) {
					int block = in.read();
					for (int x2 = x + 3; x2 >= x; --x2) {
						if (x2 >= width)
							continue;
						switch (block & 0x3) {
						case 1:
							grid[x2][y] = Block.Wall;
							break;
						case 2:
							startLoc.x = x2;
							startLoc.y = y;
							break;
						}
						block >>= 2;
					}
				}
		} catch (Exception e) {
			MainWindow.showError("Epic fail when loading map " + e.getMessage());
			return false;
		}

		dropFood();

		return true;
	}

	public Rectangle2D.Double blockRect(Point p)
	{
		return new Rectangle2D.Double(1 + p.x * blockWidth, 1 + p.y * blockHeight, blockWidth - 2, blockHeight - 2);
	}

	public void render(Graphics2D g)
	{
		Rectangle2D.Double block = blockRect(new Point());

		for (int x = 0; x != width; ++x, block.x += blockWidth) {
			block.y = 1;
			for (int y = 0; y != height; ++y, block.y += blockHeight)
				if (grid[x][y] != null) {
					g.setColor(blockColors[grid[x][y].ordinal()]);
					g.fill(block);
				}
		}
	}

	public Point dropFood()
	{
		Point p = new Point();

		do {
			p.x = rand.nextInt(width);
			p.y = rand.nextInt(height);
		} while (get(p) != null);

		set(p, Block.Food);
		return p;
	}

	public void setBounds(Rectangle bounds)
	{
		blockWidth = (double) bounds.width / width;
		blockHeight = (double) bounds.height / height;
	}

	public Block get(Point p)
	{
		return grid[p.x][p.y];
	}

	public void set(Point p, Block v)
	{
		grid[p.x][p.y] = v;
	}

	public Point startLoc()
	{
		return startLoc;
	}
}
