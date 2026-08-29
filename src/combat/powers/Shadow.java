package combat.powers;

public class Shadow extends Power {
    public Shadow() {
        super(2);
        this.name = "Shadow";
        addMove(new Move("Move 1", 1));
        addMove(new Move("Move 2", 2));
        addMove(new Move("Move 3", 3));
    }
}
