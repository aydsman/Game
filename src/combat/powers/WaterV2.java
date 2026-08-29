package combat.powers;

public class WaterV2 extends Power {
    public WaterV2() {
        super(4);
        this.name = "WaterV2";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
