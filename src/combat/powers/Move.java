package combat.powers;

public class Move {
    private String name;
    private int slot;
    private boolean unlocked;
    private MoveTemplate template;
    private int durationMs;
    private int cooldownMs;
    private String tempVisualAssetPath;
    private long lastUsedAtMs = -1;
    private long activeUntilMs = -1;

    public Move(String name, int slot) {
        this(name, slot, null, 800, 4000, null);
    }

    public Move(String name, int slot, MoveTemplate template) {
        this(
                name,
                slot,
                template,
                template != null ? Math.max(0, template.getDurationMs()) : 800,
                template != null ? Math.max(0, template.getCooldownMs()) : 4000,
                template != null ? template.getCastAssetPath() : null
        );
    }

    public Move(String name, int slot, int durationMs, int cooldownMs, String tempVisualAssetPath) {
        this(name, slot, null, durationMs, cooldownMs, tempVisualAssetPath);
    }

    public Move(String name, int slot, MoveTemplate template, int durationMs, int cooldownMs, String tempVisualAssetPath) {
        this.name = name;
        this.slot = slot;
        this.unlocked = true; // For now all moves are unlocked
        this.template = template;
        this.durationMs = Math.max(0, durationMs);
        this.cooldownMs = Math.max(0, cooldownMs);
        this.tempVisualAssetPath = tempVisualAssetPath;
    }

    public String getName() { return name; }
    public int getSlot() { return slot; }
    public boolean isUnlocked() { return unlocked; }
    public MoveTemplate getTemplate() { return template; }
    public int getDurationMs() { return durationMs; }
    public int getCooldownMs() { return cooldownMs; }
    public String getTempVisualAssetPath() { return tempVisualAssetPath; }
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
    public void setDurationMs(int durationMs) { this.durationMs = Math.max(0, durationMs); }
    public void setCooldownMs(int cooldownMs) { this.cooldownMs = Math.max(0, cooldownMs); }
    public void setTempVisualAssetPath(String tempVisualAssetPath) { this.tempVisualAssetPath = tempVisualAssetPath; }

    public boolean canUseNow(long nowMs) {
        return getRemainingCooldownMs(nowMs) <= 0;
    }

    public void markUsed(long nowMs) {
        lastUsedAtMs = nowMs;
        activeUntilMs = nowMs + durationMs;
    }

    public boolean isActive(long nowMs) {
        return activeUntilMs > nowMs;
    }

    public long getRemainingActiveMs(long nowMs) {
        return Math.max(0, activeUntilMs - nowMs);
    }

    public long getRemainingCooldownMs(long nowMs) {
        if (lastUsedAtMs < 0) {
            return 0;
        }
        return Math.max(0, (lastUsedAtMs + cooldownMs) - nowMs);
    }
}
