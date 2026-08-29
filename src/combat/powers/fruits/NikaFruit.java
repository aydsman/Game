package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class NikaFruit extends Power {
    public NikaFruit() {
        super(5);
        this.name = "NikaFruit";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
