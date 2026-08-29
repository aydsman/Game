package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class MochiFruit extends Power {
    public MochiFruit() {
        super(4);
        this.name = "MochiFruit";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
