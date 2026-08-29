package combat.powers;

public class Overgrowth extends Power {
    public Overgrowth() {
        super(2);
        this.name = "Overgrowth";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
