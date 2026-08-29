package combat.powers;

public class EarthV2 extends Power {
    public EarthV2() {
        super(4);
        this.name = "EarthV2";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
