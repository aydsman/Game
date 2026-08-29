package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class GravityFruit extends Power {
    public GravityFruit() {
        super(3);
        this.name = "GravityFruit";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
