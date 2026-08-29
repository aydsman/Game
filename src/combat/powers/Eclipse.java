package combat.powers;

public class Eclipse extends Power {
    public Eclipse() {
        super(3);
        this.name = "Eclipse";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
