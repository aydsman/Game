package entity;

import java.awt.Color;
import util.KeyHandler;

public class Player extends Entity {

    public Player(int x, int y) {
        super(x, y);
        // health
        hp = 10;
        maxHp = 10;
        // multipliers
        damage = 1.0;
        speed = 10.0;
        // visuals
        color = Color.RED;
    }

    public void update(KeyHandler key, int arenaWidth, int arenaHeight) {
        if (key.upPressed)    y -= (int) speed;
        if (key.downPressed)  y += (int) speed;
        if (key.leftPressed)  x -= (int) speed;
        if (key.rightPressed) x += (int) speed;

        // clamp player to arena bounds
        x = Math.max(0, Math.min(x, arenaWidth - w));
        y = Math.max(0, Math.min(y, arenaHeight - l));
    }
}
