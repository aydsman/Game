package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class SandFruit extends Power {
    public SandFruit() { this(2); }
    public SandFruit(int tier) {
        super(tier);
        // Sand Fruit: erosion damage and battlefield control.
        name = "Sand Fruit";
        iconPath = "assets/items/powers/fruits/sand_icon.png";
        description = "Scour enemies with abrasive storms and draining dunes.";

        // Move 1: Quick sand slash used as your core poke and combo starter.
        addMove(new Move("Sand Slice", 1));

        // Move 2: Traps enemies in a sand control zone to lock space and set up pressure.
        addMove(new Move("Desert Prison", 2));

        // Move 3: High-impact finisher that crushes trapped or slowed targets.
        addMove(new Move("Sand Coffin", 3));
    }
}
