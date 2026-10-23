package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class SmokeFruit extends Power {
    public SmokeFruit() { this(2); }
    public SmokeFruit(int tier) {
        super(tier);
        // Smoke Fruit: evasive cloud control and vision denial.
        name = "Smoke Fruit";
        iconPath = "assets/items/powers/fruits/smoke_icon.png";
        description = "Wrap the battlefield in smoke to reposition and choke lanes.";
        addMove(new Move("Smoke Screen", 1));
        addMove(new Move("Ashen Drift", 2));
        addMove(new Move("Smog Slam", 3));
    }
}
