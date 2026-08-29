package combat.powers;

public class Water extends Power {
    public Water() {
        super(1);
        this.name = "Water";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
