package entity.enemies;

import entity.Enemy;
import java.awt.Color;

public class Enemy3 extends Enemy {

    public Enemy3(int x, int y) {
        super(x, y);
        hp = 200;
        maxHp = 200;
        damage = 15.0;
        speed = 2.5;
        color = Color.PINK;
        ranged = true;
        selectWeapon("shotgun", 3);
        detectionRadius = 200;
        role = entity.ai.EnemyRole.CLOSER;
        closerDistancePx = 120;
    }
}
