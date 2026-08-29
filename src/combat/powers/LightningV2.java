package combat.powers;

public class LightningV2 extends Power {
    public LightningV2() {
        super(4);
        this.name = "LightningV2";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
