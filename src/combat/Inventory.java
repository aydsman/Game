package combat;

import combat.charms.Charm;
import combat.powers.Power;
import combat.summons.Summon;
import entity.Player;

public class Inventory {

    private Charm[] charms;
    private Power power;
    private Summon summon;
    private static final int MAX_CHARMS = 3;
    private Player player; // Reference to player for applying effects

    public Inventory() {
        charms = new Charm[MAX_CHARMS];
        power = null;
        summon = null;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Charm[] getCharms() {
        return charms;
    }

    public Charm getCharm(int index) {
        if (index >= 0 && index < MAX_CHARMS) {
            return charms[index];
        }
        return null;
    }

    public boolean equipCharm(int index, Charm charm) {
        if (index >= 0 && index < MAX_CHARMS && charm != null) {
            charms[index] = charm;
            // Recalculate all charm effects (additive stacking)
            if (player != null) {
                player.recalculateCharmEffects();
            }
            return true;
        }
        return false;
    }

    public Charm removeCharm(int index) {
        if (index >= 0 && index < MAX_CHARMS) {
            Charm removed = charms[index];
            charms[index] = null;
            // Recalculate all charm effects (additive stacking)
            if (player != null) {
                player.recalculateCharmEffects();
            }
            return removed;
        }
        return null;
    }

    public Power getPower() {
        return power;
    }

    public boolean equipPower(Power power) {
        if (power != null) {
            this.power = power;
            return true;
        }
        return false;
    }

    public Power removePower() {
        Power removed = power;
        power = null;
        return removed;
    }

    public Summon getSummon() {
        return summon;
    }

    public boolean equipSummon(Summon summon) {
        if (summon != null) {
            this.summon = summon;
            return true;
        }
        return false;
    }

    public Summon removeSummon() {
        Summon removed = summon;
        summon = null;
        return removed;
    }

    public int getMaxCharms() {
        return MAX_CHARMS;
    }
}
