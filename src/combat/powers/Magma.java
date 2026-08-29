package combat.powers;

public class Magma extends Power {
    public Magma() {
        super(2);
        this.name = "Magma";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
