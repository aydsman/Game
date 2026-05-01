package entity.enemies;

import entity.Enemy;
import java.awt.Color;

public class Enemy4 extends Enemy {

    public Enemy4(int x, int y) {
        super(x, y);
        hp = 400;
        maxHp = 400;
        damage = 20.0;
        speed = 3;
        color = Color.ORANGE;
        selectWeapon("sniper", 3);
        detectionRadius = 600;
    }
}
