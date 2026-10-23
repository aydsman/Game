package entity.miniboss;

import entity.Boss;
import java.awt.Color;

public class Miniboss1 extends Boss {

    public Miniboss1(int x, int y) {
        super(x, y);
        hp = 500;
        maxHp = 500;
        damage = 15.0;
        speed = 1.0;
        color = Color.RED;
        w = 60;
        l = 60;
        xpValue = 120;
        goldValue = 90;
        enableSlam();
    }
}