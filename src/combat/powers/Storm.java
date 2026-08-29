package combat.powers;

public class Storm extends Power {
    public Storm() {
        super(2);
        this.name = "Storm";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
