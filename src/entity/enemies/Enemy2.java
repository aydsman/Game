package entity.enemies;

import entity.Enemy;
import entity.ai.EnemyRole;
import java.awt.Color;

public class Enemy2 extends Enemy {

    public Enemy2(int x, int y) {
        super(x, y);
        hp = 150;
        maxHp = 150;
        damage = 10.0;
        speed = 2.25;
        color = new Color(128, 0, 128); // purple
        ranged = true;
        selectWeapon("rifle", 1);
        detectionRadius = 350;
        role = EnemyRole.KITER;
        kiteDistancePx = 250;
    }
}
