package combat.powers;

public class Infinity extends Power {
    public Infinity() {
        super(5);
        this.name = "Infinity";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
