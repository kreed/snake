
package snake;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import snake.Grid;

class Snake {
	enum MoveResult { Crash, Grow, Move };

	private Grid grid;
	private LinkedList<Point> snake;
	private Point pos;
	private int dx;
	private int dy;
	private int snakeLength;

	public Snake(Grid grid, Point pos)
	{
		this.grid = grid;
		this.pos = new Point(pos);

		snake = new LinkedList<Point>();
		snakeLength = 1;

		grid.set(pos, Grid.Block.Snake);
		snake.add(new Point(pos));
	}

	private void addDirty(Rectangle dirty, Rectangle2D.Double rect)
	{
		dirty.add(new Rectangle((int)rect.x, (int)rect.y, (int)rect.width + 2, (int)rect.height + 2));
	}

	public MoveResult step(Rectangle dirty)
	{
		MoveResult result = MoveResult.Move;

		pos.x += dx;
		pos.y += dy;

		if (grid.get(pos) != null)
			switch(grid.get(pos)) {
			case Snake:
			case Wall:
				return MoveResult.Crash;
			case Food:
				snakeLength += 2;
				result = MoveResult.Grow;
				addDirty(dirty, grid.blockRect(grid.dropFood()));
				break;
			}

		grid.set(pos, Grid.Block.Snake);
		snake.add(new Point(pos));
		addDirty(dirty, grid.blockRect(pos));

		if (snake.size() > snakeLength) {
			Point p = snake.remove();
			grid.set(p, null);
			addDirty(dirty, grid.blockRect(p));
		}

		return result;
	}

	public void setStep(int dx, int dy)
	{
		this.dx = dx;
		this.dy = dy;
	}

	public Point getLoc()
	{
		return pos;
	}
}
