package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class BombFruit extends Power {
    public BombFruit() { this(2); }
    public BombFruit(int tier) {
        super(tier);
        // Bomb Fruit: explosive burst and blast pressure.
        name = "Bomb Fruit";
        iconPath = "assets/items/powers/fruits/bomb_icon.png";
        description = "Turn pressure into blasts and explosive chain hits.";
        addMove(new Move("Blast Palm", 1));
        addMove(new Move("Bomb Scatter", 2));
        addMove(new Move("Detonation Burst", 3));
    }
}
