package util;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelListener;

public class MouseHandler implements MouseListener, MouseMotionListener, MouseWheelListener {

    public int mouseX = 0;
    public int mouseY = 0;
    public boolean leftPressed = false;
    public boolean leftClicked = false;
    public int scrollDirection = 0; // 1 for scroll down, -1 for scroll up, 0 for no scroll

    @Override
    public void mouseMoved(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // Not used - click detection handled in mousePressed
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftPressed = true;
            leftClicked = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            leftPressed = false;
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseWheelMoved(java.awt.event.MouseWheelEvent e) {
        int rotation = e.getWheelRotation();
        if (rotation < 0) {
            scrollDirection = -1; // Scroll up
        } else {
            scrollDirection = 1; // Scroll down
        }
    }

    public void resetScroll() {
        scrollDirection = 0;
    }
}
