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
import combat.charms.Charm;
import combat.charms.Charm1;
import combat.summons.Summon;
import combat.summons.Summon1;
import combat.powers.Power;
import combat.powers.Power1;
import combat.consumables.Consumable1;
import combat.consumables.Consumable2;
import combat.consumables.Consumable3;

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
        {35, 45, 10,  6,  4},  // Tier II chest
        {15, 25, 40, 15,  5},  // Tier III chest
        { 0, 10, 45, 30, 15},  // Tier IV chest
        { 0,  0,  0, 65, 35},  // Tier V chest
    };

    public ItemRegistry() {
        // Register weapons (tier 1 variants for now)
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

        // Register other item types (no tiers for now)
        charms.add(new Charm1());
        summons.add(new Summon1());
        powers.add(new Power1());
        consumables.add(new Consumable1());
        consumables.add(new Consumable2());
        consumables.add(new Consumable3());

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
            double secondGunChance = (chestTier == 4) ? 0.25 : 0.66;
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
        Item weapon = getRandomItemFromListByTier(weapons, chestTier);
        Item selected = weapon != null ? weapon : weapons.get(random.nextInt(weapons.size()));
        return selected.clone();
    }

    private Item getRandomConsumable(int chestTier) {
        Item consumable = getRandomItemFromListByTier(consumables, chestTier);
        Item selected = consumable != null ? consumable : consumables.get(random.nextInt(consumables.size()));
        return selected.clone();
    }

    private Item getRandomCharm(int chestTier) {
        Item charm = getRandomItemFromListByTier(charms, chestTier);
        Item selected = charm != null ? charm : charms.get(random.nextInt(charms.size()));
        return selected.clone();
    }

    private Item getRandomSummon(int chestTier) {
        Item summon = getRandomItemFromListByTier(summons, chestTier);
        Item selected = summon != null ? summon : summons.get(random.nextInt(summons.size()));
        return selected.clone();
    }

    private Item getRandomPower(int chestTier) {
        Item power = getRandomItemFromListByTier(powers, chestTier);
        Item selected = power != null ? power : powers.get(random.nextInt(powers.size()));
        return selected.clone();
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
