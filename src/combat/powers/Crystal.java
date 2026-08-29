package combat.powers;

public class Crystal extends Power {
    public Crystal() {
        super(2);
        this.name = "Crystal";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
