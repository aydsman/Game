package entity.enemies;

import entity.Enemy;
import java.awt.Color;

public class Enemy5 extends Enemy {

    public Enemy5(int x, int y) {
        super(x, y);
        w = 15;
        l = 15;
        hp = 50;
        maxHp = 50;
        damage = 10.0;
        speed = 3.0;
        color = Color.YELLOW;
        ranged = true;
        selectWeapon("smg", 5);
        detectionRadius = 300;
    }
}
