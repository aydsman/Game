package combat.powers;

public class Nightbloom extends Power {
    public Nightbloom() {
        super(4);
        this.name = "Nightbloom";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
