package ui;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import javax.swing.JFrame;

public class Game {

    public Game() {
        JFrame window = new JFrame("Abyss");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setUndecorated(true);
        window.setResizable(false);

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        GraphicsDevice device = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice();
        window.setBounds(device.getDefaultConfiguration().getBounds());
        window.setVisible(true);
        gamePanel.requestFocusInWindow();
    }
}
