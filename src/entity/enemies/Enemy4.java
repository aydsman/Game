package entity.enemies;

import entity.Enemy;
import java.awt.Color;

public class Enemy4 extends Enemy {

    public Enemy4(int x, int y) {
        super(x, y);
        hp = 40;
        maxHp = 40;
        damage = 2.0;
        speed = 0.5;
        color = Color.ORANGE;
    }
}
