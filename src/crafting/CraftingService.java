package crafting;

import currency.CurrencyManager;
import combat.powers.fruits.FruitPool;
import save.SaveData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Random;

public final class CraftingService {
    private static final Random RANDOM = new Random();

    private CraftingService() {
    }

    public static List<CraftingRecipe> getRecipesSortedByCraftability(SaveData data, CurrencyManager currencyManager) {
        List<CraftingRecipe> recipes = new ArrayList<>(CraftingCatalog.all());
        recipes.sort(Comparator
                .comparing((CraftingRecipe r) -> !evaluate(data, r, currencyManager).isCraftable())
                .thenComparing(CraftingRecipe::getDisplayName, String.CASE_INSENSITIVE_ORDER));
        return recipes;
    }

    public static List<CraftingRecipe> findNewlyCraftableRecipes(SaveData data, Set<String> discoveredRecipeIds) {
        List<CraftingRecipe> newlyCraftable = new ArrayList<>();
        for (CraftingRecipe recipe : CraftingCatalog.all()) {
            if (discoveredRecipeIds.contains(recipe.getId())) {
                continue;
            }
            CraftingEvaluation evaluation = evaluate(data, recipe, null);
            if (evaluation.isCraftable()) {
                newlyCraftable.add(recipe);
            }
        }
        newlyCraftable.sort(Comparator.comparing(CraftingRecipe::getDisplayName, String.CASE_INSENSITIVE_ORDER));
        return newlyCraftable;
    }

    public static CraftingEvaluation evaluate(SaveData data, CraftingRecipe recipe, CurrencyManager currencyManager) {
        List<String> missing = new ArrayList<>();
        boolean blockedByLoadout = false;

        for (CraftingIngredient ingredient : recipe.getIngredients()) {
            int owned = getStackCount(data, ingredient.getItemType(), ingredient.getItemId(), ingredient.getTier());
            int reserved = getReservedByLoadout(data, ingredient);
            int available = Math.max(0, owned - reserved);
            if (reserved > 0 && available < ingredient.getQuantity()) {
                blockedByLoadout = true;
            }
            if (available < ingredient.getQuantity()) {
                missing.add(formatIngredient(ingredient) + " x" + (ingredient.getQuantity() - available));
            }
        }

        boolean affordable = hasEnoughCurrency(data, currencyManager, recipe);
        boolean craftable = missing.isEmpty() && affordable;
        return new CraftingEvaluation(craftable, blockedByLoadout, affordable, missing);
    }

    public static CraftingResult craft(SaveData data, CurrencyManager currencyManager, CraftingRecipe recipe) {
        CraftingEvaluation evaluation = evaluate(data, recipe, currencyManager);
        if (!evaluation.isCraftable()) {
            if (!evaluation.isAffordable()) {
                return CraftingResult.fail("Not enough currency for this recipe.");
            }
            if (evaluation.isBlockedByLoadout()) {
                return CraftingResult.fail("Unequip required loadout item(s) before crafting.");
            }
            return CraftingResult.fail("Missing ingredients.");
        }

        if (!spendCurrency(data, currencyManager, recipe)) {
            return CraftingResult.fail("Unable to spend crafting currency.");
        }

        for (CraftingIngredient ingredient : recipe.getIngredients()) {
            decrementItem(data, ingredient.getItemType(), ingredient.getItemId(), ingredient.getTier(), ingredient.getQuantity());
        }

        if ("roll_fruit_from_haki_overgrowth".equals(recipe.getId())) {
            FruitPool.FruitEntry fruit = FruitPool.roll(RANDOM);
            incrementItem(data, CraftingItemType.POWER, fruit.itemId(), fruit.tier(), 1);
            return CraftingResult.ok("Fruit roll: " + fruit.itemId() + " (T" + fruit.tier() + ")");
        }

        CraftingOutput output = recipe.getOutput();
        incrementItem(data, output.getItemType(), output.getItemId(), output.getTier(), output.getQuantity());
        return CraftingResult.ok("Crafted " + output.getItemId() + " (T" + output.getTier() + ")");
    }

    private static boolean hasEnoughCurrency(SaveData data, CurrencyManager currencyManager, CraftingRecipe recipe) {
        int gold = currencyManager != null ? currencyManager.getGold().getAmount() : 0;
        int cash = currencyManager != null ? currencyManager.getCash().getAmount() : data.getCash();
        int gems = currencyManager != null ? currencyManager.getGems().getAmount() : data.getGems();
        return gold >= recipe.getGoldCost()
                && cash >= recipe.getCashCost()
                && gems >= recipe.getGemsCost();
    }

    private static boolean spendCurrency(SaveData data, CurrencyManager currencyManager, CraftingRecipe recipe) {
        if (currencyManager == null) {
            if (!hasEnoughCurrency(data, null, recipe)) {
                return false;
            }
            data.setCash(data.getCash() - recipe.getCashCost());
            data.setGems(data.getGems() - recipe.getGemsCost());
            return true;
        }

        if (!hasEnoughCurrency(data, currencyManager, recipe)) {
            return false;
        }
        if (!currencyManager.spendGold(recipe.getGoldCost())) {
            return false;
        }
        if (!currencyManager.spendCash(recipe.getCashCost())) {
            return false;
        }
        if (!currencyManager.spendGems(recipe.getGemsCost())) {
            return false;
        }
        data.setCash(currencyManager.getPersistableCash());
        data.setGems(currencyManager.getPersistableGems());
        return true;
    }

    private static String formatIngredient(CraftingIngredient ingredient) {
        return ingredient.getItemId() + " (T" + ingredient.getTier() + ")";
    }

    private static int getReservedByLoadout(SaveData data, CraftingIngredient ingredient) {
        return switch (ingredient.getItemType()) {
            case WEAPON -> matchesWeaponLoadout(data, ingredient) ? 1 : 0;
            case CHARM -> matches(data.getSelectedCharm(), data.getSelectedCharmTier(), ingredient);
            case POWER -> matches(data.getSelectedPower(), data.getSelectedPowerTier(), ingredient);
            case SUMMON -> matches(data.getSelectedSummon(), data.getSelectedSummonTier(), ingredient);
            case CONSUMABLE -> matches(data.getSelectedConsumable(), data.getSelectedConsumableTier(), ingredient);
        };
    }

    private static boolean matchesWeaponLoadout(SaveData data, CraftingIngredient ingredient) {
        String selected = normalizeWeaponId(data.getSelectedWeapon());
        return selected.equals(normalizeWeaponId(ingredient.getItemId()))
                && data.getSelectedWeaponTier() == ingredient.getTier();
    }

    private static int matches(String selectedId, int selectedTier, CraftingIngredient ingredient) {
        if (selectedId == null || "None".equalsIgnoreCase(selectedId)) {
            return 0;
        }
        return selectedId.equals(ingredient.getItemId()) && selectedTier == ingredient.getTier() ? 1 : 0;
    }

    private static int getStackCount(SaveData data, CraftingItemType type, String itemId, int tier) {
        Map<Integer, Integer> tierMap = getStackMap(data, type).get(itemId);
        if (tierMap == null) {
            return 0;
        }
        return tierMap.getOrDefault(tier, 0);
    }

    public static int getAvailableCount(SaveData data, CraftingIngredient ingredient) {
        int owned = getStackCount(data, ingredient.getItemType(), ingredient.getItemId(), ingredient.getTier());
        int reserved = getReservedByLoadout(data, ingredient);
        return Math.max(0, owned - reserved);
    }

    private static void decrementItem(SaveData data, CraftingItemType type, String itemId, int tier, int amount) {
        Map<String, Map<Integer, Integer>> stacks = getStackMap(data, type);
        Map<Integer, Integer> tierMap = stacks.get(itemId);
        if (tierMap == null) {
            return;
        }
        int next = tierMap.getOrDefault(tier, 0) - amount;
        if (next <= 0) {
            tierMap.remove(tier);
        } else {
            tierMap.put(tier, next);
        }
        if (tierMap.isEmpty()) {
            stacks.remove(itemId);
        }
        removeTierIfNoStacks(data, type, itemId, tier);
    }

    private static void incrementItem(SaveData data, CraftingItemType type, String itemId, int tier, int amount) {
        Map<String, Map<Integer, Integer>> stacks = getStackMap(data, type);
        Map<Integer, Integer> tierMap = stacks.computeIfAbsent(itemId, ignored -> new HashMap<>());
        tierMap.put(tier, tierMap.getOrDefault(tier, 0) + amount);

        Map<String, List<Integer>> unlocked = getUnlockedMap(data, type);
        List<Integer> tiers = unlocked.computeIfAbsent(itemId, ignored -> new ArrayList<>());
        if (!tiers.contains(tier)) {
            tiers.add(tier);
            tiers.sort(Integer::compareTo);
        }
    }

    private static void removeTierIfNoStacks(SaveData data, CraftingItemType type, String itemId, int tier) {
        Map<String, Map<Integer, Integer>> stacks = getStackMap(data, type);
        int remaining = stacks.getOrDefault(itemId, new HashMap<>()).getOrDefault(tier, 0);
        if (remaining > 0) {
            return;
        }
        Map<String, List<Integer>> unlocked = getUnlockedMap(data, type);
        List<Integer> tiers = unlocked.get(itemId);
        if (tiers == null) {
            return;
        }
        tiers.removeIf(t -> t == tier);
        if (tiers.isEmpty()) {
            unlocked.remove(itemId);
        }
    }

    private static Map<String, Map<Integer, Integer>> getStackMap(SaveData data, CraftingItemType type) {
        return switch (type) {
            case WEAPON -> data.getWeaponStacks();
            case CHARM -> data.getCharmStacks();
            case POWER -> data.getPowerStacks();
            case SUMMON -> data.getSummonStacks();
            case CONSUMABLE -> data.getConsumableStacks();
        };
    }

    private static Map<String, List<Integer>> getUnlockedMap(SaveData data, CraftingItemType type) {
        return switch (type) {
            case WEAPON -> data.getUnlockedWeapons();
            case CHARM -> data.getUnlockedCharms();
            case POWER -> data.getUnlockedPowers();
            case SUMMON -> data.getUnlockedSummons();
            case CONSUMABLE -> data.getUnlockedConsumables();
        };
    }

    private static String normalizeWeaponId(String weaponId) {
        if (weaponId == null) {
            return "";
        }
        return switch (weaponId) {
            case "Glock", "Pistol" -> "Pistol1";
            case "AK-47", "Rifle" -> "Rifle1";
            case "Shotgun", "Pump - Action", "Pump-Action" -> "Shotgun1";
            case "SMG", "P90" -> "SMG1";
            case "Sniper" -> "Sniper1";
            case "Sword" -> "Sword1";
            case "Hammer" -> "Hammer1";
            case "Dagger" -> "Dagger1";
            case "Mace" -> "Mace1";
            case "Scythe" -> "Scythe1";
            default -> weaponId;
        };
    }
}
