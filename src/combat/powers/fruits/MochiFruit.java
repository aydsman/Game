package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class MochiFruit extends Power {
    public MochiFruit() { this(4); }
    public MochiFruit(int tier) {
        super(tier);
        // Mochi Fruit: adaptive offense with sticky control and heavy combos.
        name = "Mochi Fruit";
        iconPath = "assets/items/powers/fruits/mochi_icon.png";
        description = "Stretch and harden mochi to trap targets and punish with relentless strikes.";
        addMove(new Move("Sticky Barrage", 1));
        addMove(new Move("Mochi Trap", 2));
        addMove(new Move("King Cobra", 3));
    }
}
