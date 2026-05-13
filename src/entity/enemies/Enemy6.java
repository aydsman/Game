package entity.enemies;

import entity.MeleeEnemy;
import java.awt.Color;

public class Enemy6 extends MeleeEnemy { // Sword Enemy

    public Enemy6(int x, int y) {
        super(x, y);
        color = Color.CYAN;
        selectWeapon("sword", 1);
        // Sword enemies: balanced stats
        hp = 130;
        maxHp = 130;
        speed = 3.8;
        damage = 25.0;
        preferredDistance = 45.0; // Slightly farther for sword swings
        xpValue = 30;
        goldValue = 12;
    }
}
