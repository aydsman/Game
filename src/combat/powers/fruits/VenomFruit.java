package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class VenomFruit extends Power {
    public VenomFruit() { this(3); }
    public VenomFruit(int tier) {
        super(tier);
        // Venom Fruit: poison stacking and lethal zone pressure.
        name = "Venom Fruit";
        iconPath = "assets/items/powers/fruits/venom_icon.png";
        description = "Corrode enemies with venom pools and crippling toxin bursts.";
        addMove(new Move("Poison Fang", 1));
        addMove(new Move("Venom Dome", 2));
        addMove(new Move("Hydra Toxin", 3));
    }
}
