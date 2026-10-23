package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class OpOpFruit extends Power {
    public OpOpFruit() { this(4); }
    public OpOpFruit(int tier) {
        super(tier);
        // Op-Op Fruit: precision zone manipulation and surgical burst.
        name = "Op-Op Fruit";
        iconPath = "assets/items/powers/fruits/op_icon.png";
        description = "Create operation zones to dissect spacing and execute precision finishers.";
        addMove(new Move("Room", 1));
        addMove(new Move("Shambles", 2));
        addMove(new Move("Gamma Knife", 3));
    }
}
