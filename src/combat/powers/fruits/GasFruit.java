package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class GasFruit extends Power {
    public GasFruit() {
        super(2);
        this.name = "GasFruit";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
