package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class SandFruit extends Power {
    public SandFruit() {
        super(2);
        this.name = "SandFruit";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
