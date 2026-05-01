package combat.powers;

import combat.Item;

public class Power extends Item {
    protected int maxMoves = 0;
    protected Move[] moves = new Move[4];

    public Power() {
        super();
        name = "Power";
    }

    public Power(int tier) {
        super(tier);
        name = "Power";
    }

    public int getMaxMoves() { return maxMoves; }
    public Move[] getMoves() { return moves; }
    public Move getMove(int slot) {
        if (slot >= 1 && slot <= 4) {
            return moves[slot - 1];
        }
        return null;
    }

    protected void addMove(Move move) {
        if (move.getSlot() >= 1 && move.getSlot() <= 4) {
            moves[move.getSlot() - 1] = move;
            if (move.getSlot() > maxMoves) {
                maxMoves = move.getSlot();
            }
        }
    }

    public void useMove(int slot) {
        Move move = getMove(slot);
        if (move != null && move.isUnlocked()) {
            System.out.println("Move " + move.getName() + " was used by clicking " + slot);
        }
    }
}
