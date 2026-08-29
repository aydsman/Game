package combat.powers;

public class Light extends Power {
    public Light() {
        super(1);
        this.name = "Light";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
