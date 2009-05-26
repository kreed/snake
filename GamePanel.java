
package snake;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;
import snake.*;
import static java.lang.Math.abs;

class GamePanel extends JPanel {
	private class MouseHandler extends MouseInputAdapter {
		public void mouseMoved(MouseEvent e)
		{
			calcVelocity(e.getPoint());
		}

		public void mouseClicked(MouseEvent e)
		{
			if (state == State.Fail)
				init();
			calcVelocity(e.getPoint());
			if (state != State.Running)
				unpause();
		}
	}

	private void setStep(int dx, int dy)
	{
		if (state == State.Fail)
			init();
		snake.setStep(dx, dy);
		if (state != State.Running)
			unpause();
	}

	private class MoveTask extends TimerTask {
		public void run()
		{
			step();
		}
	}

	private Grid grid;
	private Snake snake;

	private Image failStamp;
	private java.util.Timer timer;

	private int period;

	enum State { Paused, Fail, Running };
	private State state;

	private String smg;

	public GamePanel()
	{
		grid = new Grid();

		MouseHandler mh = new MouseHandler();
		addMouseListener(mh);
		addMouseMotionListener(mh);

		try {
			failStamp = ImageIO.read(getClass().getResourceAsStream("/snake/failstamp.png"));
		} catch (Exception e) {
			MainWindow.showError("Epic fail when loading fail stamp " + e.getMessage());
		}

		InputMap im = getInputMap();
		ActionMap am = getActionMap();

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "up");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0), "up");
		am.put("up", new AbstractAction() {
			public void actionPerformed(ActionEvent e)
			{
				setStep(0, -1);
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "down");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0), "down");
		am.put("down", new AbstractAction() {
			public void actionPerformed(ActionEvent e)
			{
				setStep(0, 1);
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0), "left");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0), "left");
		am.put("left", new AbstractAction() {
			public void actionPerformed(ActionEvent e)
			{
				setStep(-1, 0);
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0), "right");
		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0), "right");
		am.put("right", new AbstractAction() {
			public void actionPerformed(ActionEvent e)
			{
				setStep(1, 0);
			}
		});

		im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "pause");
		am.put("pause", new AbstractAction() {
			public void actionPerformed(ActionEvent e)
			{
				if (state == State.Running)
					pause();
				else if (state == State.Paused)
					unpause();
			}
		});

		state = State.Paused;

		init();
	}

	public Dimension getPreferredSize()
	{
		return new Dimension(400, 300);
	}

	private void init()
	{
		if (!grid.init(smg))
			grid.init(null);
		snake = new Snake(grid, grid.startLoc());
		period = 200;
		repaint();
	}

	public void load(String map)
	{
		if (state != State.Paused)
			pause();

		smg = map;
		init();
	}

	private void pause()
	{
		state = State.Paused;
		timer.cancel();
		timer = null;
	}

	private void unpause()
	{
		if (timer == null)
			timer = new java.util.Timer();

		state = State.Running;
		step();
	}

	private void step()
	{
		Rectangle dirty = new Rectangle();

		switch (snake.step(dirty)) {
		case Crash:
			state = State.Fail;
			repaint();
			break;
		case Grow:
			period = period * 9 / 10;
			// fall through
		default:
			repaint(dirty);
			timer.schedule(new MoveTask(), period);
			break;
		}
	}

	private void calcVelocity(Point cursor)
	{
		Rectangle2D.Double head = grid.blockRect(snake.getLoc());
		double x = cursor.x - head.getCenterX();
		double y = cursor.y - head.getCenterY();
		if (x == 0 && y == 0)
			snake.setStep(0, 1);
		else if (abs(x) > abs(y))
			snake.setStep((int)(x / abs(x)), 0);
		else
			snake.setStep(0, (int)(y / abs(y)));
	}

	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D)g;
		Rectangle bounds = getBounds();

		g.setColor(Color.white);
		g2.fill(bounds);
		grid.setBounds(bounds);
		grid.render(g2);

		if (state == State.Fail)
			g.drawImage(failStamp, bounds.x, bounds.y, bounds.width, bounds.height, null, null);
	}
}
