package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class MagnetFruit extends Power {
    public MagnetFruit() { this(3); }
    public MagnetFruit(int tier) {
        super(tier);
        // Magnet Fruit: metal pull/push control and crushing bursts.
        name = "Magnet Fruit";
        iconPath = "assets/items/powers/fruits/magnet_icon.png";
        description = "Bend magnetic force to drag enemies and slam them with iron storms.";
        addMove(new Move("Polar Pull", 1));
        addMove(new Move("Rail Burst", 2));
        addMove(new Move("Magnetic Crush", 3));
    }
}
