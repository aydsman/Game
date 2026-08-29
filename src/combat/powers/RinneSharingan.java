package combat.powers;

public class RinneSharingan extends Power {
    public RinneSharingan() {
        super(5);
        this.name = "RinneSharingan";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
