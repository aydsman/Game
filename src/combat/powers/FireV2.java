package combat.powers;

public class FireV2 extends Power {
    public FireV2() {
        super(4);
        this.name = "FireV2";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
