package combat.powers;

public class KingOfCurses extends Power {
    public KingOfCurses() {
        super(5);
        this.name = "KingOfCurses";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
