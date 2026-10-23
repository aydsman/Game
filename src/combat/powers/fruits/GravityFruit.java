package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class GravityFruit extends Power {
    public GravityFruit() { this(3); }
    public GravityFruit(int tier) {
        super(tier);
        // Gravity Fruit: oppressive pull and meteor-like impact control.
        name = "Gravity Fruit";
        iconPath = "assets/items/powers/fruits/gravity_fruit.png";
        description = "Command gravity wells to pin, drag, and collapse enemies.";
        addMove(new Move("Gravity Well", 1));
        addMove(new Move("Repulse Arc", 2));
        addMove(new Move("Meteor Press", 3));
    }
}
