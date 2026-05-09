package combat;

import combat.melee.daggers.Dagger1;
import combat.melee.hammers.Hammer1;
import combat.melee.maces.Mace1;
import combat.melee.scythes.Scythe1;
import combat.melee.swords.Sword1;
import combat.ranged.pistols.Pistol1;
import combat.ranged.rifles.Rifle1;
import combat.ranged.shotguns.Shotgun1;
import combat.ranged.smgs.SMG1;
import combat.ranged.snipers.Sniper1;
import combat.charms.Charm1;
import combat.charms.SpeedCharm;
import combat.charms.SpeedCharm2;
import combat.charms.SpeedCharm3;
import combat.charms.HealthCharm2;
import combat.charms.HealthCharm3;
import combat.summons.Summon1;
import combat.powers.Fire;
import combat.powers.Light;
import combat.powers.Earth;
import combat.powers.Lightning;
import combat.powers.Water;
import combat.powers.Magma;
import combat.powers.EarthV2;
import combat.powers.FireV2;
import combat.powers.LightningV2;
import combat.powers.MagmaV2;
import combat.powers.WaterV2;
import combat.powers.Infinity;
import combat.powers.KingOfCurses;
import combat.powers.RinneSharingan;
import combat.powers.Rinnegan;
import combat.powers.Sharingan;
import combat.consumables.Consumable1;
import combat.consumables.Consumable2;
import combat.consumables.Consumable3;
import combat.consumables.DamagePotion;
import combat.consumables.DamagePotion2;
import combat.consumables.DamagePotion3;
import combat.consumables.SpeedPotion1;
import combat.consumables.SpeedPotion2;
import combat.consumables.SpeedPotion3;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ItemRegistry {

    private List<Item> allItems = new ArrayList<>();
    private List<Item> weapons = new ArrayList<>();
    private List<Item> charms = new ArrayList<>();
    private List<Item> summons = new ArrayList<>();
    private List<Item> powers = new ArrayList<>();
    private List<Item> consumables = new ArrayList<>();
    private Random random = new Random();

    // Loot tier probabilities per chest tier [T1%, T2%, T3%, T4%, T5%]
    private static final double[][] LOOT_TABLE = {
        {80, 15,  5,  0,  0},  // Tier I chest
        {50, 35, 12,  3,  0},  // Tier II chest
        {20, 40, 30,  8,  2},  // Tier III chest
        { 0,  0, 35, 45, 20},  // Tier IV chest
        { 0,  0,  0, 60, 40},  // Tier V chest
    };

    public ItemRegistry() {
        // Register weapons (base templates - tier will be rolled separately)
        // Ranged
        weapons.add(new Pistol1(1));
        weapons.add(new Rifle1(1));
        weapons.add(new Shotgun1(1));
        weapons.add(new SMG1(1));
        weapons.add(new Sniper1(1));
        // Melee
        weapons.add(new Sword1(1));
        weapons.add(new Hammer1(1));
        weapons.add(new Dagger1(1));
        weapons.add(new Mace1(1));
        weapons.add(new Scythe1(1));

        // Register other item types with their fixed tiers
        // Charms sorted by type: Health charms first (tier 1-3), then Speed charms (tier 1-3)
        charms.add(new Charm1()); // Simple Health Charm - Tier 1
        charms.add(new HealthCharm2()); // Health Charm - Tier 2
        charms.add(new HealthCharm3()); // Advanced Health Charm - Tier 3
        charms.add(new SpeedCharm()); // Simple Speed Charm - Tier 1
        charms.add(new SpeedCharm2()); // Speed Charm - Tier 2
        charms.add(new SpeedCharm3()); // Advanced Speed Charm - Tier 3
        summons.add(new Summon1()); // Tier 1

        // Powers sorted by name, then by tier
        powers.add(new Earth()); // Tier 1
        powers.add(new Fire()); // Tier 1
        powers.add(new Light()); // Tier 1
        powers.add(new Lightning()); // Tier 1
        powers.add(new Water()); // Tier 1
        powers.add(new Magma()); // Tier 2
        powers.add(new Sharingan(3)); // Tier 3
        powers.add(new EarthV2()); // Tier 4
        powers.add(new FireV2()); // Tier 4
        powers.add(new LightningV2()); // Tier 4
        powers.add(new MagmaV2()); // Tier 4
        powers.add(new Rinnegan(4)); // Tier 4
        powers.add(new WaterV2()); // Tier 4
        powers.add(new Infinity()); // Tier 5
        powers.add(new KingOfCurses()); // Tier 5
        powers.add(new RinneSharingan()); // Tier 5

        // Consumables sorted by name, then by tier
        consumables.add(new Consumable1()); // Tier 2 (like Fortnite mini shields)
        consumables.add(new Consumable2()); // Tier 3 (like big shields)
        consumables.add(new Consumable3()); // Tier 4
        consumables.add(new DamagePotion()); // Tier 1 - doubles damage for 10 seconds
        consumables.add(new DamagePotion2()); // Tier 3 - doubles damage for 15 seconds
        consumables.add(new DamagePotion3()); // Tier 4 - doubles damage for 20 seconds
        consumables.add(new SpeedPotion1()); // Tier 1 - small speed boost
        consumables.add(new SpeedPotion2()); // Tier 2 - medium speed boost
        consumables.add(new SpeedPotion3()); // Tier 3 - large speed boost

        // Add all to combined list
        allItems.addAll(weapons);
        allItems.addAll(charms);
        allItems.addAll(summons);
        allItems.addAll(powers);
        allItems.addAll(consumables);
    }

    public List<Item> getAllItems() {
        return allItems;
    }

    public List<Item> getWeapons() {
        return weapons;
    }

    public List<Item> getCharms() {
        return charms;
    }

    public List<Item> getSummons() {
        return summons;
    }

    public List<Item> getPowers() {
        return powers;
    }

    public List<Item> getConsumables() {
        return consumables;
    }

    public List<Item> getItemsByTier(int tier) {
        return allItems.stream()
                .filter(item -> item.getTier() == tier)
                .collect(Collectors.toList());
    }

    public Item getRandomItem() {
        if (allItems.isEmpty()) return null;
        return allItems.get(random.nextInt(allItems.size()));
    }

    /**
     * Rolls a random item using the percentage-based loot table for the given chest tier.
     * First rolls which item tier to pick, then picks a random item of that tier.
     */
    public Item getRandomItemForChest(int chestTier) {
        if (chestTier < 1 || chestTier > 5) return null;

        double[] chances = LOOT_TABLE[chestTier - 1];
        double roll = random.nextDouble() * 100;

        int selectedTier = 1;
        double cumulative = 0;
        for (int i = 0; i < chances.length; i++) {
            cumulative += chances[i];
            if (roll < cumulative) {
                selectedTier = i + 1;
                break;
            }
        }

        List<Item> tierItems = getItemsByTier(selectedTier);
        if (tierItems.isEmpty()) {
            // Fallback: try lower tiers until we find items
            for (int t = selectedTier - 1; t >= 1; t--) {
                tierItems = getItemsByTier(t);
                if (!tierItems.isEmpty()) break;
            }
        }
        if (tierItems.isEmpty()) return null;
        return tierItems.get(random.nextInt(tierItems.size()));
    }

    /**
     * Generates loot for a chest based on tier and slot count.
     * Follows tier-specific rules for guaranteed items and type distributions.
     * Empty slots are placed at the end of the chest.
     */
    public Item[] generateChestLoot(int chestTier, int slotCount) {
        List<Item> lootList = new ArrayList<>();

        if (chestTier >= 1 && chestTier <= 3) {
            // Tiers 1-3: Guaranteed weapon in first slot
            lootList.add(getRandomWeapon(chestTier));
        } else if (chestTier == 4 || chestTier == 5) {
            // Tiers 4-5: Guaranteed gun(s)
            Item firstGun = getRandomWeapon(chestTier);
            lootList.add(firstGun);

            // Roll for second gun
            double secondGunChance = (chestTier == 4) ? 0.25 : 1.0; // Tier 5 always gets 2 guns
            if (random.nextDouble() < secondGunChance) {
                Item secondGun;
                do {
                    secondGun = getRandomWeapon(chestTier);
                } while (secondGun.getClass().equals(firstGun.getClass()) && weapons.size() > 1);
                lootList.add(secondGun);
            }

            // Tier 4: Guaranteed consumable
            // Tier 5: Guaranteed consumable + charm
            if (lootList.size() < slotCount) {
                lootList.add(getRandomConsumable(chestTier));
            }
            if (chestTier == 5 && lootList.size() < slotCount) {
                lootList.add(getRandomCharm(chestTier));
            }
        }

        // Fill remaining slots based on tier-specific distributions
        while (lootList.size() < slotCount) {
            lootList.add(rollItemForSlot(chestTier));
        }

        // Sort so empty slots (null) are at the end
        lootList.sort((a, b) -> {
            if (a == null && b == null) return 0;
            if (a == null) return 1;  // nulls go to end
            if (b == null) return -1; // non-nulls come first
            return 0;
        });

        // Convert to array
        return lootList.toArray(new Item[0]);
    }

    private Item getRandomWeapon(int chestTier) {
        // Roll for tier using LOOT_TABLE
        int itemTier = rollTierFromChest(chestTier);
        // Pick random weapon from weapons list
        Item weaponTemplate = weapons.get(random.nextInt(weapons.size()));
        // Clone with the rolled tier
        Item weapon = weaponTemplate.clone();
        weapon.setTier(itemTier);
        return weapon;
    }

    private Item getRandomConsumable(int chestTier) {
        // Roll for tier using LOOT_TABLE
        int itemTier = rollTierFromChest(chestTier);
        // Filter combat.consumables by rolled tier
        List<Item> tierConsumables = consumables.stream()
                .filter(item -> item.getTier() == itemTier)
                .collect(Collectors.toList());
        // If no items at this tier, try lower tiers
        if (tierConsumables.isEmpty()) {
            for (int t = itemTier - 1; t >= 1; t--) {
                final int tier = t;
                tierConsumables = consumables.stream()
                        .filter(item -> item.getTier() == tier)
                        .collect(Collectors.toList());
                if (!tierConsumables.isEmpty()) break;
            }
        }
        // Fallback to random if still empty
        if (tierConsumables.isEmpty()) {
            return consumables.get(random.nextInt(consumables.size())).clone();
        }
        return tierConsumables.get(random.nextInt(tierConsumables.size())).clone();
    }

    private Item getRandomCharm(int chestTier) {
        // Roll for tier using LOOT_TABLE
        int itemTier = rollTierFromChest(chestTier);
        // Filter combat.charms by rolled tier
        List<Item> tierCharms = charms.stream()
                .filter(item -> item.getTier() == itemTier)
                .collect(Collectors.toList());
        // If no items at this tier, try lower tiers
        if (tierCharms.isEmpty()) {
            for (int t = itemTier - 1; t >= 1; t--) {
                final int tier = t;
                tierCharms = charms.stream()
                        .filter(item -> item.getTier() == tier)
                        .collect(Collectors.toList());
                if (!tierCharms.isEmpty()) break;
            }
        }
        // Fallback to random if still empty
        if (tierCharms.isEmpty()) {
            return charms.get(random.nextInt(charms.size())).clone();
        }
        return tierCharms.get(random.nextInt(tierCharms.size())).clone();
    }

    private Item getRandomSummon(int chestTier) {
        // Roll for tier using LOOT_TABLE
        int itemTier = rollTierFromChest(chestTier);
        // Filter summons by rolled tier
        List<Item> tierSummons = summons.stream()
                .filter(item -> item.getTier() == itemTier)
                .collect(Collectors.toList());
        // If no items at this tier, try lower tiers
        if (tierSummons.isEmpty()) {
            for (int t = itemTier - 1; t >= 1; t--) {
                final int tier = t;
                tierSummons = summons.stream()
                        .filter(item -> item.getTier() == tier)
                        .collect(Collectors.toList());
                if (!tierSummons.isEmpty()) break;
            }
        }
        // Fallback to random if still empty
        if (tierSummons.isEmpty()) {
            return summons.get(random.nextInt(summons.size())).clone();
        }
        return tierSummons.get(random.nextInt(tierSummons.size())).clone();
    }

    private Item getRandomPower(int chestTier) {
        // Roll for tier using LOOT_TABLE
        int itemTier = rollTierFromChest(chestTier);
        // Filter combat.powers by rolled tier
        List<Item> tierPowers = powers.stream()
                .filter(item -> item.getTier() == itemTier)
                .collect(Collectors.toList());
        // If no items at this tier, try lower tiers
        if (tierPowers.isEmpty()) {
            for (int t = itemTier - 1; t >= 1; t--) {
                final int tier = t;
                tierPowers = powers.stream()
                        .filter(item -> item.getTier() == tier)
                        .collect(Collectors.toList());
                if (!tierPowers.isEmpty()) break;
            }
        }
        // Fallback to random if still empty
        if (tierPowers.isEmpty()) {
            return powers.get(random.nextInt(powers.size())).clone();
        }
        return tierPowers.get(random.nextInt(tierPowers.size())).clone();
    }

    private int rollTierFromChest(int chestTier) {
        if (chestTier < 1 || chestTier > 5) return 1;

        double[] chances = LOOT_TABLE[chestTier - 1];
        double roll = random.nextDouble() * 100;

        int selectedTier = 1;
        double cumulative = 0;
        for (int i = 0; i < chances.length; i++) {
            cumulative += chances[i];
            if (roll < cumulative) {
                selectedTier = i + 1;
                break;
            }
        }
        return selectedTier;
    }

    private Item getRandomItemFromListByTier(List<Item> itemList, int chestTier) {
        if (itemList.isEmpty()) return null;

        double[] chances = LOOT_TABLE[chestTier - 1];
        double roll = random.nextDouble() * 100;

        int selectedTier = 1;
        double cumulative = 0;
        for (int i = 0; i < chances.length; i++) {
            cumulative += chances[i];
            if (roll < cumulative) {
                selectedTier = i + 1;
                break;
            }
        }

        // Filter items by selected tier
        final int targetTier = selectedTier;
        List<Item> tierItems = itemList.stream()
                .filter(item -> item.getTier() == targetTier)
                .collect(Collectors.toList());

        if (tierItems.isEmpty()) {
            // Fallback: try lower tiers
            for (int t = selectedTier - 1; t >= 1; t--) {
                final int tier = t;
                tierItems = itemList.stream()
                        .filter(item -> item.getTier() == tier)
                        .collect(Collectors.toList());
                if (!tierItems.isEmpty()) break;
            }
        }

        if (tierItems.isEmpty()) {
            // Fallback: return random from entire list
            return itemList.get(random.nextInt(itemList.size()));
        }
        return tierItems.get(random.nextInt(tierItems.size()));
    }

    private Item rollItemForSlot(int chestTier) {
        double roll = random.nextDouble() * 100;

        switch (chestTier) {
            case 1:
                // Tier 1: 35% empty, 35% consumable, 15% charm, 5% summon, 5% power
                if (roll < 35) return null; // Empty
                else if (roll < 70) return getRandomConsumable(chestTier);
                else if (roll < 85) return getRandomCharm(chestTier);
                else if (roll < 90) return getRandomSummon(chestTier);
                else return getRandomPower(chestTier);

            case 2:
                // Tier 2: 25% empty, 25% consumable, 25% charm, 12% power, 13% summon
                if (roll < 25) return null; // Empty
                else if (roll < 50) return getRandomConsumable(chestTier);
                else if (roll < 75) return getRandomCharm(chestTier);
                else if (roll < 87) return getRandomPower(chestTier);
                else return getRandomSummon(chestTier);

            case 3:
                // Tier 3: 20% empty, 25% consumable, 25% charm, 15% summon, 15% power
                if (roll < 20) return null; // Empty
                else if (roll < 45) return getRandomConsumable(chestTier);
                else if (roll < 70) return getRandomCharm(chestTier);
                else if (roll < 85) return getRandomSummon(chestTier);
                else return getRandomPower(chestTier);

            case 4:
                // Tier 4: Equal chance summon/charm/power (33.3% each)
                double roll4 = random.nextDouble();
                if (roll4 < 0.333) return getRandomSummon(chestTier);
                else if (roll4 < 0.666) return getRandomCharm(chestTier);
                else return getRandomPower(chestTier);

            case 5:
                // Tier 5: 50% power, 50% summon
                if (random.nextDouble() < 0.5) return getRandomPower(chestTier);
                else return getRandomSummon(chestTier);

            default:
                return null;
        }
    }
}
