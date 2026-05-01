package entity.boss;

import entity.Boss;
import java.awt.Color;

public class Boss1 extends Boss {

    public Boss1(int x, int y) {
        super(x, y);
        hp = 2000;
        maxHp = 2000;
        damage = 25.0;
        speed = 1.5;
        color = new Color(50, 0, 50); // Dark purple
        w = 80;
        l = 80;
    }
}
