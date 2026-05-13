package entity.enemies;

import entity.MeleeEnemy;
import java.awt.Color;

public class Enemy8 extends MeleeEnemy { // Dagger Enemy

    public Enemy8(int x, int y) {
        super(x, y);
        color = Color.MAGENTA;
        selectWeapon("dagger", 1);
        // Dagger enemies: fast attacks, low damage, high speed
        hp = 100;
        maxHp = 100;
        speed = 5.2;
        damage = 18.0;
        preferredDistance = 35.0; // Close for quick stabs
        // Fast attack speed for daggers
        minAttackCooldown = 0.6;
        maxAttackCooldown = 1.0;
        xpValue = 28;
        goldValue = 11;
    }
}
