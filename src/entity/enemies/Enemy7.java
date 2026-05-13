package entity.enemies;

import entity.MeleeEnemy;
import java.awt.Color;

public class Enemy7 extends MeleeEnemy { // Hammer Enemy

    public Enemy7(int x, int y) {
        super(x, y);
        color = Color.ORANGE;
        selectWeapon("hammer", 1);
        // Hammer enemies: high damage, slow attacks, more health
        hp = 160;
        maxHp = 160;
        speed = 2.8;
        damage = 40.0;
        preferredDistance = 50.0; // Need room for hammer swings
        // Slower attack speed for hammers
        minAttackCooldown = 1.5;
        maxAttackCooldown = 2.2;
        xpValue = 35;
        goldValue = 15;
    }
}
