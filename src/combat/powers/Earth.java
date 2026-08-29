package combat.powers;

public class Earth extends Power {
    public Earth() {
        super(1);
        this.name = "Earth";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
