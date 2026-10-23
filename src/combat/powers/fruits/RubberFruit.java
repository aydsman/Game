package combat.powers.fruits;

import combat.powers.Power;

public class RubberFruit extends Power {
    public RubberFruit() { this(3); }

    public RubberFruit(int tier) {
        super(tier);
        // Rubber Fruit: elastic mobility and impact attacks.
        name = "Rubber Fruit";
        iconPath = "assets/items/powers/fruits/rubber_icon.png";
        description = "Stretch and snap with elastic strikes that control spacing.";

        // Move 1: Rapid Gear 2 punch barrage. Forward rectangle simulates the punching reach.
        addMove("Jet Gatling", 1, 3000, 7000, "assets/effects/temp/rubber_jet_gatling.png");

        // Move 2: High-speed Gear 2 burst step that teleports the player to the mouse position.
        addMove("Shave", 2, 250, 5000, "assets/effects/temp/rubber_shave.png");

        // Move 3: Gear 3 finisher. Wind-up phase grows the fist, then a forward punch hits in a radius.
        addMove("Elephant Gun", 3, 1400, 9000, "assets/effects/temp/rubber_elephant_gun.png");
    }
}
