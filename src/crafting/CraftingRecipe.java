package crafting;

import save.SaveData;
import java.util.*;

public class CraftingRecipe {
    private final String id;
    private final String displayName;

    public CraftingRecipe(String id, String displayName) {
        this.id = id;
        this.displayName = displayName;
    }

    public String getId() { return id; }
    public String getDisplayName() { return displayName; }
}
