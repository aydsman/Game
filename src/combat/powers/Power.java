package combat.powers;

import combat.Item;
import mastery.MasteryService;

public class Power extends Item {
    protected int maxMoves = 0;
    protected Move[] moves = new Move[5];
    /** Bypass mastery-based slot locking so every registered move is usable. */
    protected boolean bypassMasteryUnlocks = false;

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
        if (slot >= 1 && slot <= 5) {
            return moves[slot - 1];
        }
        return null;
    }

    protected void addMove(Move move) {
        if (move.getSlot() >= 1 && move.getSlot() <= 5) {
            moves[move.getSlot() - 1] = move;
            if (move.getSlot() > maxMoves) {
                maxMoves = move.getSlot();
            }
            // Keep unlock state synced as moves are registered.
            applyMasteryMoveUnlocks();
        }
    }

    /**
     * Convenience helper for quickly registering simple name-only moves.
     */
    protected void addMove(String name, int slot) {
        addMove(new Move(name, slot));
    }

    protected void addMove(String name, int slot, int durationMs, int cooldownMs, String tempVisualAssetPath) {
        addMove(new Move(name, slot, durationMs, cooldownMs, tempVisualAssetPath));
    }

    /**
     * Convenience helper for registering moves backed by shared templates.
     */
    protected void addTemplatedMove(String name, int slot, MoveTemplate template) {
        addMove(new Move(name, slot, template));
    }

    public void applyMasteryMoveUnlocks() {
        boolean unlockAll = bypassMasteryUnlocks || ui.GamePanel.isAdminModeActive();
        int unlockedMoves = unlockAll
                ? maxMoves
                : MasteryService.getUnlockedPowerCount(name, maxMoves);
        for (int slot = 1; slot <= maxMoves; slot++) {
            Move move = getMove(slot);
            if (move != null) {
                move.setUnlocked(slot <= unlockedMoves);
            }
        }
    }

    public Move useMoveWithResult(int slot) {
        Move move = getMove(slot);
        if (move != null && move.isUnlocked()) {
            long nowMs = System.currentTimeMillis();
            if (!move.canUseNow(nowMs)) {
                return null;
            }
            move.markUsed(nowMs);
            System.out.println("Move " + move.getName() + " was used by clicking " + slot);
            return move;
        }
        return null;
    }

    public void useMove(int slot) {
        useMoveWithResult(slot);
    }
}
