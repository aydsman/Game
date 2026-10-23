package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class GasFruit extends Power {
    public GasFruit() { this(2); }
    public GasFruit(int tier) {
        super(tier);
        // Gas Fruit: area denial and damage-over-time clouds.
        name = "Gas Fruit";
        iconPath = "assets/items/powers/fruits/gas_icon.png";
        description = "Flood zones with volatile gas that weakens and burns.";
        addMove(new Move("Toxic Cloud", 1));
        addMove(new Move("Gas Lance", 2));
        addMove(new Move("Ignite Fumes", 3));
    }
}
