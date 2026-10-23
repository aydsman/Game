package crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CraftingEvaluation {
    private final boolean craftable;
    private final boolean blockedByLoadout;
    private final boolean affordable;
    private final List<String> missingIngredients;

    public CraftingEvaluation(boolean craftable, boolean blockedByLoadout, boolean affordable, List<String> missingIngredients) {
        this.craftable = craftable;
        this.blockedByLoadout = blockedByLoadout;
        this.affordable = affordable;
        this.missingIngredients = new ArrayList<>(missingIngredients);
    }

    public boolean isCraftable() {
        return craftable;
    }

    public boolean isBlockedByLoadout() {
        return blockedByLoadout;
    }

    public boolean isAffordable() {
        return affordable;
    }

    public List<String> getMissingIngredients() {
        return Collections.unmodifiableList(missingIngredients);
    }
}
