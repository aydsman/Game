package entity;

import java.awt.Color;

public class Boss extends Entity {

    public Boss(int x, int y) {
        super(x, y);
        // health
        hp = 100;
        maxHp = 100;
        // multipliers
        damage = 1.0;
        speed = 1.0;
        // visuals
        color = Color.BLACK;
    }
}
