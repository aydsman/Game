package entity.enemies;

import entity.Enemy;
import java.awt.Color;

public class Enemy1 extends Enemy {

    public Enemy1(int x, int y) {
        super(x, y);
        color = Color.BLUE;
        ranged = true;
        selectWeapon("pistol", 1);
    }
}
