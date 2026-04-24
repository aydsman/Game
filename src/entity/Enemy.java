package entity;

import java.awt.Color;

// placeholder enemy class
public class Enemy extends Entity {

    public Enemy(int x, int y) {
        super(x, y);
        // health
        hp = 10;
        maxHp = 10;
        // multipliers
        damage = 1.0;
        speed = 1.0;
        // visuals
        color = Color.WHITE;
    }
}
