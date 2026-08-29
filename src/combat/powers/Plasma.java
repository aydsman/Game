package combat.powers;

public class Plasma extends Power {
    public Plasma() {
        super(3);
        this.name = "Plasma";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
