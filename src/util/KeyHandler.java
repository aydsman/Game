package util;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    public boolean upPressed, downPressed, leftPressed, rightPressed;
    public boolean rPressed = false;
    public boolean lPressed = false;
    public boolean oPressed = false;
    public boolean ePressed = false;
    public boolean spacePressed = false;
    public boolean onePressed = false;
    public boolean twoPressed = false;
    public boolean threePressed = false;
    public boolean fourPressed = false;
    public boolean fivePressed = false;
    public boolean kPressed = false;
    public boolean uPressed = false;
    public boolean fPressed = false;

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP)    upPressed = true;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN)  downPressed = true;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT)  leftPressed = true;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = true;
        if (code == KeyEvent.VK_R) rPressed = true;
        if (code == KeyEvent.VK_L) lPressed = true;
        if (code == KeyEvent.VK_O) oPressed = true;
        if (code == KeyEvent.VK_E) ePressed = true;
        if (code == KeyEvent.VK_SPACE) spacePressed = true;
        if (code == KeyEvent.VK_1) onePressed = true;
        if (code == KeyEvent.VK_2) twoPressed = true;
        if (code == KeyEvent.VK_3) threePressed = true;
        if (code == KeyEvent.VK_4) fourPressed = true;
        if (code == KeyEvent.VK_5) fivePressed = true;
        if (code == KeyEvent.VK_K) kPressed = true;
        if (code == KeyEvent.VK_U) uPressed = true;
        if (code == KeyEvent.VK_F) fPressed = true;
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP)    upPressed = false;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN)  downPressed = false;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT)  leftPressed = false;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = false;
        if (code == KeyEvent.VK_R) rPressed = false;
        if (code == KeyEvent.VK_L) lPressed = false;
        if (code == KeyEvent.VK_O) oPressed = false;
        if (code == KeyEvent.VK_E) ePressed = false;
        if (code == KeyEvent.VK_SPACE) spacePressed = false;
        if (code == KeyEvent.VK_1) onePressed = false;
        if (code == KeyEvent.VK_2) twoPressed = false;
        if (code == KeyEvent.VK_3) threePressed = false;
        if (code == KeyEvent.VK_4) fourPressed = false;
        if (code == KeyEvent.VK_5) fivePressed = false;
        if (code == KeyEvent.VK_K) kPressed = false;
        if (code == KeyEvent.VK_U) uPressed = false;
        if (code == KeyEvent.VK_F) fPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}
