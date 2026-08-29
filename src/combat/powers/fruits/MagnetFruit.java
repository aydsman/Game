package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class MagnetFruit extends Power {
    public MagnetFruit() {
        super(3);
        this.name = "MagnetFruit";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
