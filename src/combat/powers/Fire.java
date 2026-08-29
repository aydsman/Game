package combat.powers;

public class Fire extends Power {
    public Fire() {
        super(1);
        this.name = "Fire";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
