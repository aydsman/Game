package combat;

import combat.charms.Charm;
import combat.powers.Power;
import combat.summons.Summon;
import entity.Player;

public class Inventory {
    private static final int MAX_CHARMS = 3;
    private final Charm[] charms = new Charm[MAX_CHARMS];
    private Power power = null;
    private Summon summon = null;
    private Player player;

    public void setPlayer(Player player) { this.player = player; }

    public int getMaxCharms() { return MAX_CHARMS; }
    public Charm getCharm(int slot) {
        if (slot < 0 || slot >= MAX_CHARMS) return null;
        return charms[slot];
    }
    public void equipCharm(int slot, Charm charm) {
        if (slot >= 0 && slot < MAX_CHARMS) charms[slot] = charm;
        if (player != null) player.recalculateCharmEffects();
    }
    public void removeCharm(int slot) {
        if (slot >= 0 && slot < MAX_CHARMS) charms[slot] = null;
        if (player != null) player.recalculateCharmEffects();
    }
    public Power getPower() { return power; }
    public void equipPower(Power p) { power = p; }
    public void removePower() { power = null; }
    public Summon getSummon() { return summon; }
    public void equipSummon(Summon s) { summon = s; }
    public void removeSummon() { summon = null; }
}
