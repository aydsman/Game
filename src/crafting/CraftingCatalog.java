package crafting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CraftingCatalog {
    private static final List<CraftingRecipe> RECIPES = new ArrayList<>();

    static {
        // Core fusion recipes
        addRecipe("fuse_magma_t2", "Magma Fusion", "Fuse fire and earth into Magma.",
                List.of(
                        new CraftingIngredient(CraftingItemType.POWER, "Fire", 1, 1),
                        new CraftingIngredient(CraftingItemType.POWER, "Earth", 1, 1)
                ),
                new CraftingOutput(CraftingItemType.POWER, "Magma", 2, 1));

        addRecipe("fuse_overgrowth_t2", "Overgrowth Fusion", "Fuse earth and water into Overgrowth.",
                List.of(
                        new CraftingIngredient(CraftingItemType.POWER, "Earth", 1, 1),
                        new CraftingIngredient(CraftingItemType.POWER, "Water", 1, 1)
                ),
                new CraftingOutput(CraftingItemType.POWER, "Overgrowth", 2, 1));

        addRecipe("fuse_eclipse_t3", "Eclipse Fusion", "Fuse light and shadow into Eclipse.",
                List.of(
                        new CraftingIngredient(CraftingItemType.POWER, "Light", 1, 1),
                        new CraftingIngredient(CraftingItemType.POWER, "Shadow", 2, 1)
                ),
                new CraftingOutput(CraftingItemType.POWER, "Eclipse", 3, 1));

        addRecipe("fuse_crystal_t2", "Crystal Fusion", "Fuse earth and light into Crystal.",
                List.of(
                        new CraftingIngredient(CraftingItemType.POWER, "Earth", 1, 1),
                        new CraftingIngredient(CraftingItemType.POWER, "Light", 1, 1)
                ),
                new CraftingOutput(CraftingItemType.POWER, "Crystal", 2, 1));

        addRecipe("fuse_storm_t2", "Storm Fusion", "Fuse water and lightning into Storm.",
                List.of(
                        new CraftingIngredient(CraftingItemType.POWER, "Water", 1, 1),
                        new CraftingIngredient(CraftingItemType.POWER, "Lightning", 1, 1)
                ),
                new CraftingOutput(CraftingItemType.POWER, "Storm", 2, 1));

        addRecipe("fuse_metal_t2", "Metal Fusion", "Fuse earth and lightning into Metal.",
                List.of(
                        new CraftingIngredient(CraftingItemType.POWER, "Earth", 1, 1),
                        new CraftingIngredient(CraftingItemType.POWER, "Lightning", 1, 1)
                ),
                new CraftingOutput(CraftingItemType.POWER, "Metal", 2, 1));

        addRecipe("fuse_plasma_t3", "Plasma Fusion", "Fuse fire, lightning, and earth into Plasma.",
                List.of(
                        new CraftingIngredient(CraftingItemType.POWER, "Fire", 1, 1),
                        new CraftingIngredient(CraftingItemType.POWER, "Lightning", 1, 1),
                        new CraftingIngredient(CraftingItemType.POWER, "Earth", 1, 1)
                ),
                new CraftingOutput(CraftingItemType.POWER, "Plasma", 3, 1));

        addRecipe("fuse_rinnesharingan_t5", "Rinne Sharingan Fusion", "Fuse Rinnegan and Sharingan.",
                List.of(
                        new CraftingIngredient(CraftingItemType.POWER, "Rinnegan", 4, 1),
                        new CraftingIngredient(CraftingItemType.POWER, "Sharingan", 5, 1)
                ),
                new CraftingOutput(CraftingItemType.POWER, "RinneSharingan", 5, 1));

        addRecipe("fuse_magmav2_t4", "Magma V2 Fusion", "Fuse FireV2 and EarthV2.",
                List.of(
                        new CraftingIngredient(CraftingItemType.POWER, "FireV2", 4, 1),
                        new CraftingIngredient(CraftingItemType.POWER, "EarthV2", 4, 1)
                ),
                new CraftingOutput(CraftingItemType.POWER, "MagmaV2", 4, 1));

        addRecipe("fuse_cataclysm_t5", "Cataclysm Fusion", "Fuse MagmaV2 and Eclipse into Cataclysm.",
                List.of(
                        new CraftingIngredient(CraftingItemType.POWER, "MagmaV2", 4, 1),
                        new CraftingIngredient(CraftingItemType.POWER, "Eclipse", 3, 1)
                ),
                new CraftingOutput(CraftingItemType.POWER, "Cataclysm", 5, 1));

        addRecipe("fuse_nightbloom_t4", "Nightbloom Fusion", "Fuse Overgrowth and Eclipse into Nightbloom.",
                List.of(
                        new CraftingIngredient(CraftingItemType.POWER, "Overgrowth", 2, 1),
                        new CraftingIngredient(CraftingItemType.POWER, "Eclipse", 3, 1)
                ),
                new CraftingOutput(CraftingItemType.POWER, "Nightbloom", 4, 1));

        addRecipe("roll_fruit_from_haki_overgrowth", "Fruit Roll", "Haki + Overgrowth rolls one guaranteed random fruit.",
                List.of(
                        new CraftingIngredient(CraftingItemType.POWER, "Haki", 2, 1),
                        new CraftingIngredient(CraftingItemType.POWER, "Overgrowth", 2, 1)
                ),
                new CraftingOutput(CraftingItemType.POWER, "FruitRoll", 2, 1));

        // Charm reinforcement (same line only)
        addRecipe("reinforce_charm_health_t1_to_t2", "Health Charm Upgrade I", "5x Charm1 -> 1x HealthCharm2",
                List.of(new CraftingIngredient(CraftingItemType.CHARM, "Charm1", 1, 5)),
                new CraftingOutput(CraftingItemType.CHARM, "HealthCharm2", 2, 1));
        addRecipe("reinforce_charm_health_t2_to_t3", "Health Charm Upgrade II", "10x HealthCharm2 -> 1x HealthCharm3",
                List.of(new CraftingIngredient(CraftingItemType.CHARM, "HealthCharm2", 2, 10)),
                new CraftingOutput(CraftingItemType.CHARM, "HealthCharm3", 3, 1));
        addRecipe("reinforce_charm_damage_t1_to_t2", "Damage Charm Upgrade I", "5x DamageCharm1 -> 1x DamageCharm2",
                List.of(new CraftingIngredient(CraftingItemType.CHARM, "DamageCharm1", 1, 5)),
                new CraftingOutput(CraftingItemType.CHARM, "DamageCharm2", 2, 1));
        addRecipe("reinforce_charm_damage_t2_to_t3", "Damage Charm Upgrade II", "10x DamageCharm2 -> 1x DamageCharm3",
                List.of(new CraftingIngredient(CraftingItemType.CHARM, "DamageCharm2", 2, 10)),
                new CraftingOutput(CraftingItemType.CHARM, "DamageCharm3", 3, 1));
        addRecipe("reinforce_charm_speed_t1_to_t2", "Speed Charm Upgrade I", "5x SpeedCharm -> 1x SpeedCharm2",
                List.of(new CraftingIngredient(CraftingItemType.CHARM, "SpeedCharm", 1, 5)),
                new CraftingOutput(CraftingItemType.CHARM, "SpeedCharm2", 2, 1));
        addRecipe("reinforce_charm_speed_t2_to_t3", "Speed Charm Upgrade II", "10x SpeedCharm2 -> 1x SpeedCharm3",
                List.of(new CraftingIngredient(CraftingItemType.CHARM, "SpeedCharm2", 2, 10)),
                new CraftingOutput(CraftingItemType.CHARM, "SpeedCharm3", 3, 1));

        // Weapon reinforcement (same weapon only)
        String[] weapons = {
                "Pistol1", "Rifle1", "Shotgun1", "SMG1", "Sniper1",
                "Sword1", "Hammer1", "Dagger1", "Mace1", "Scythe1"
        };
        for (String weapon : weapons) {
            addRecipe("reinforce_" + weapon.toLowerCase() + "_t1_to_t2", weapon + " Upgrade I", "5x T1 -> 1x T2",
                    List.of(new CraftingIngredient(CraftingItemType.WEAPON, weapon, 1, 5)),
                    new CraftingOutput(CraftingItemType.WEAPON, weapon, 2, 1));
            addRecipe("reinforce_" + weapon.toLowerCase() + "_t2_to_t3", weapon + " Upgrade II", "5x T2 -> 1x T3",
                    List.of(new CraftingIngredient(CraftingItemType.WEAPON, weapon, 2, 5)),
                    new CraftingOutput(CraftingItemType.WEAPON, weapon, 3, 1));
            addRecipe("reinforce_" + weapon.toLowerCase() + "_t3_to_t4", weapon + " Upgrade III", "5x T3 -> 1x T4",
                    List.of(new CraftingIngredient(CraftingItemType.WEAPON, weapon, 3, 5)),
                    new CraftingOutput(CraftingItemType.WEAPON, weapon, 4, 1));
            addRecipe("reinforce_" + weapon.toLowerCase() + "_t4_to_t5", weapon + " Upgrade IV", "5x T4 -> 1x T5",
                    List.of(new CraftingIngredient(CraftingItemType.WEAPON, weapon, 4, 5)),
                    new CraftingOutput(CraftingItemType.WEAPON, weapon, 5, 1));
        }
    }

    private CraftingCatalog() {
    }

    private static void addRecipe(
            String id,
            String name,
            String description,
            List<CraftingIngredient> ingredients,
            CraftingOutput output) {
        RECIPES.add(new CraftingRecipe(id, name, description, ingredients, output, 0, 0, 0));
    }

    public static List<CraftingRecipe> all() {
        return Collections.unmodifiableList(RECIPES);
    }
}
