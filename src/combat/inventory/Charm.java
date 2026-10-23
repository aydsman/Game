package combat.inventory;

import combat.Item;
import entity.Player;

public class Charm extends Item {

    public Charm() {
        super();
        this.name = "Simple Health Charm";
    }

    public Charm(int tier) {
        super(tier);
        this.name = "Simple Health Charm";
    }

    @Override
    public Charm clone() {
        Charm cloned = new Charm(this.tier);
        cloned.name = this.name;
        cloned.description = this.description;
        return cloned;
    }

    // Apply charm effect to player when equipped
    public void applyEffect(Player player) {
        // Effects are now calculated in Player.recalculateCharmEffects() for additive stacking
    }

    // Remove charm effect from player when unequipped
    public void removeEffect(Player player) {
        // Effects are now calculated in Player.recalculateCharmEffects() for additive stacking
    }
}
