package combat.lootboxes;

/**
 * Common tier loot box with the following distribution:
 * - Tier 1: 60%
 * - Tier 2: 25%
 * - Tier 3: 9%
 * - Tier 4: 5%
 * - Tier 5: 1%
 *
 * Item types:
 * - Weapon: 50%
 * - Charm: 40%
 * - Power: 10%
 */
public class Lootbox1 extends LootBox {

    public Lootbox1() {
        super("Test Crate");

        // Set tier probabilities
        setTierProbability(1, 0.60);
        setTierProbability(2, 0.25);
        setTierProbability(3, 0.09);
        setTierProbability(4, 0.05);
        setTierProbability(5, 0.01);

        // Set item type probabilities
        setItemTypeProbability("Weapon", 0.40);
        setItemTypeProbability("Charm", 0.25);
        setItemTypeProbability("Power", 0.20);
        setItemTypeProbability("Summon", 0.10);
        setItemTypeProbability("Consumable", 0.05);

        // Add items to catalog (only items from ItemRegistry)
        // Weapons can appear in multiple tiers to be rolled with tier variants
        addItem(1, "Weapon", "Pistol1");
        addItem(1, "Weapon", "Rifle1");
        addItem(1, "Weapon", "Shotgun1");
        addItem(1, "Weapon", "SMG1");
        addItem(1, "Weapon", "Sniper1");
        addItem(1, "Weapon", "Sword1");
        addItem(1, "Weapon", "Hammer1");
        addItem(1, "Weapon", "Dagger1");
        addItem(1, "Weapon", "Mace1");
        addItem(1, "Weapon", "Scythe1");

        addItem(2, "Weapon", "Pistol1");
        addItem(2, "Weapon", "Rifle1");
        addItem(2, "Weapon", "Shotgun1");
        addItem(2, "Weapon", "SMG1");
        addItem(2, "Weapon", "Sniper1");

        addItem(3, "Weapon", "Pistol1");
        addItem(3, "Weapon", "Rifle1");
        addItem(3, "Weapon", "Shotgun1");

        addItem(4, "Weapon", "Sniper1");
        addItem(4, "Weapon", "Rifle1");

        addItem(5, "Weapon", "Sniper1");

        // Charms (fixed items - use simple syntax without tier)
        addItem("Charm", "Charm1");
        addItem("Charm", "SpeedCharm");

        // Powers (use simple syntax - tier 1+ powers will be accessible)
        addItem("Power", "Earth");
        addItem("Power", "Fire");
        addItem("Power", "Light");
        addItem("Power", "Lightning");
        addItem("Power", "Water");
        addItem("Power", "Magma");
        addItem("Power", "EarthV2");
        addItem("Power", "FireV2");
        addItem("Power", "LightningV2");
        addItem("Power", "MagmaV2");
        addItem("Power", "WaterV2");
        addItem("Power", "Infinity");
        addItem("Power", "KingOfCurses");
        addItem("Power", "RinneSharingan");

        // Summons
        addItem("Summon", "Summon1");
        addItem("Summon", "Summon2");
        addItem("Summon", "Summon3");
        addItem("Summon", "Summon4");
        addItem("Summon", "Summon5");

        // Consumables
        addItem("Consumable", "Consumable1");
        addItem("Consumable", "Consumable2");
        addItem("Consumable", "Consumable3");
        addItem("Consumable", "DamagePotion");
        addItem("Consumable", "DamagePotion2");
        addItem("Consumable", "DamagePotion3");
    }
}
