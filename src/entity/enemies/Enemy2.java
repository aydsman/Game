package entity.enemies;

import entity.Enemy;
import java.awt.Color;

public class Enemy2 extends Enemy {

    public Enemy2(int x, int y) {
        super(x, y);
        hp = 15;
        maxHp = 15;
        damage = 1.0;
        speed = 4.5;
        color = new Color(128, 0, 128); // purple
        ranged = true;
        selectWeapon("rifle", 1);
        detectionRadius = 350;
    }
}
