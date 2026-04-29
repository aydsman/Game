package entity.miniboss;

import entity.Boss;
import java.awt.Color;

public class Miniboss1 extends Boss {

    public Miniboss1(int x, int y) {
        super(x, y);
        hp = 50;
        maxHp = 50;
        damage = 1.5;
        speed = 2.0;
        color = Color.RED;
        w = 60;
        l = 60;
    }
}