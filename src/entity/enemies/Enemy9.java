package entity.enemies;

import entity.MeleeEnemy;
import java.awt.Color;

public class Enemy9 extends MeleeEnemy { // Scythe Enemy

    public Enemy9(int x, int y) {
        super(x, y);
        color = Color.GREEN;
        selectWeapon("scythe", 1);
        // Scythe enemies: low damage but good for hordes, medium speed
        hp = 110;
        maxHp = 110;
        speed = 4.2;
        damage = 15.0;
        preferredDistance = 55.0; // Longer reach for scythe
        // Medium attack speed, good for crowd control
        minAttackCooldown = 0.8;
        maxAttackCooldown = 1.2;
        xpValue = 26;
        goldValue = 10;
    }
}
