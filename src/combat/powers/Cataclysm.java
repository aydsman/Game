package combat.powers;

public class Cataclysm extends Power {
    public Cataclysm() {
        super(5);
        this.name = "Cataclysm";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
