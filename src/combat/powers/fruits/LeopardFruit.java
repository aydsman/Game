package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class LeopardFruit extends Power {
    public LeopardFruit() {
        super(4);
        this.name = "LeopardFruit";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
