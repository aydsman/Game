package combat.powers;

public class Lightning extends Power {
    public Lightning() {
        super(1);
        this.name = "Lightning";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
