package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class SmokeFruit extends Power {
    public SmokeFruit() {
        super(2);
        this.name = "SmokeFruit";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
