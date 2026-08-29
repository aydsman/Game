package combat.powers;

public class Move {
    private final String name;
    private final int slot;
    private boolean unlocked = true;

    public Move(String name, int slot) {
        this.name = name;
        this.slot = slot;
    }

    public String getName() { return name; }
    public int getSlot() { return slot; }
    public boolean isUnlocked() { return unlocked; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
}
