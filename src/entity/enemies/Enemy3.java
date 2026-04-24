package entity.enemies;

import entity.Enemy;
import java.awt.Color;

public class Enemy3 extends Enemy {

    public Enemy3(int x, int y) {
        super(x, y);
        hp = 20;
        maxHp = 20;
        damage = 1.5;
        speed = 1.2;
        color = Color.PINK;
    }
}
