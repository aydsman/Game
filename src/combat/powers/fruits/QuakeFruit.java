package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class QuakeFruit extends Power {
    public QuakeFruit() { this(4); }
    public QuakeFruit(int tier) {
        super(tier);
        // Quake Fruit: world-shaking AoE destruction.
        name = "Quake Fruit";
        iconPath = "assets/items/powers/fruits/quake_icon.png";
        description = "Fracture the arena with devastating shockwaves and ruptures.";
        addMove(new Move("Seismic Fist", 1));
        addMove(new Move("Crack Horizon", 2));
        addMove(new Move("World Splitter", 3));
    }
}
