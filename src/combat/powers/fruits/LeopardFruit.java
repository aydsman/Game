package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class LeopardFruit extends Power {
    public LeopardFruit() { this(5); }
    public LeopardFruit(int tier) {
        super(tier);
        // Leopard Fruit (awakened): high-speed predator rushdown.
        name = "Leopard Fruit";
        iconPath = "assets/items/powers/fruits/leopard_fruit.png";
        description = "Awaken feral speed and brutality to tear through front lines.";
        addMove(new Move("Predator Dash", 1));
        addMove(new Move("Claw Tempest", 2));
        addMove(new Move("Awakened Hunt", 3));
    }
}
