
package snake;

import java.awt.Component;
import java.awt.event.*;
import java.io.File;
import javax.swing.*;
import javax.swing.filechooser.*;
import snake.GamePanel;

public class MainWindow extends JFrame {
	public static void main(String[] args)
	{
		MainWindow window = new MainWindow();
		window.pack();
		window.setVisible(true);
	}

	private static MainWindow instance;

	public MainWindow()
	{
		super("Snake");

		instance = this;

		setDefaultCloseOperation(EXIT_ON_CLOSE);

		final Component parent = this;
		final GamePanel game = new GamePanel();

		JMenuBar menubar = new JMenuBar();
		JMenu menu = new JMenu("File");

		menu.add("Load Map...").addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e)
			{
				JFileChooser chooser = new JFileChooser();

				FileNameExtensionFilter filter = new FileNameExtensionFilter("Snake Maps", "smg");
				chooser.setFileFilter(filter);
				chooser.setCurrentDirectory(new File("."));

				if (chooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION)
					game.load(chooser.getSelectedFile().getPath());
			}
		});

		menubar.add(menu);
		setJMenuBar(menubar);

		add(game);
	}

	public static void showError(String msg)
	{
		System.err.println(msg);
		JOptionPane.showMessageDialog(instance, msg, "Error", JOptionPane.ERROR_MESSAGE);
	}
}
