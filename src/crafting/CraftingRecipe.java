package crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CraftingRecipe {
    private final String id;
    private final String displayName;
    private final String description;
    private final List<CraftingIngredient> ingredients;
    private final CraftingOutput output;
    private final int goldCost;
    private final int cashCost;
    private final int gemsCost;

    public CraftingRecipe(
            String id,
            String displayName,
            String description,
            List<CraftingIngredient> ingredients,
            CraftingOutput output,
            int goldCost,
            int cashCost,
            int gemsCost) {
        this.id = id;
        this.displayName = displayName;
        this.description = description;
        this.ingredients = new ArrayList<>(ingredients);
        this.output = output;
        this.goldCost = Math.max(0, goldCost);
        this.cashCost = Math.max(0, cashCost);
        this.gemsCost = Math.max(0, gemsCost);
    }

    public String getId() {
        return id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public List<CraftingIngredient> getIngredients() {
        return Collections.unmodifiableList(ingredients);
    }

    public CraftingOutput getOutput() {
        return output;
    }

    public int getGoldCost() {
        return goldCost;
    }

    public int getCashCost() {
        return cashCost;
    }

    public int getGemsCost() {
        return gemsCost;
    }
}
