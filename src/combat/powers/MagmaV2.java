package combat.powers;

public class MagmaV2 extends Power {
    public MagmaV2() {
        super(4);
        this.name = "MagmaV2";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
