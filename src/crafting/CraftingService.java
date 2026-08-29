package crafting;

import save.SaveData;
import java.util.*;

public final class CraftingService {
    private static final List<CraftingRecipe> ALL = List.of(
            new CraftingRecipe("fuse_pistol", "Fuse Pistol T2"),
            new CraftingRecipe("fuse_charm", "Fuse Charm T2")
    );

    private CraftingService() {}

    public static List<CraftingRecipe> findNewlyCraftableRecipes(SaveData data, Set<String> knownIds) {
        List<CraftingRecipe> result = new ArrayList<>();
        for (CraftingRecipe recipe : ALL) {
            if (!knownIds.contains(recipe.getId()) && canCraft(data, recipe)) {
                result.add(recipe);
            }
        }
        return result;
    }

    private static boolean canCraft(SaveData data, CraftingRecipe recipe) {
        return data.getTotalKills() >= 5;
    }
}
