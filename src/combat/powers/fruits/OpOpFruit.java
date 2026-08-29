package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class OpOpFruit extends Power {
    public OpOpFruit() {
        super(4);
        this.name = "OpOpFruit";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
