package combat.powers.fruits;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public final class FruitPool {
    public static final double TIER2_CHANCE = 0.65;
    public static final double TIER3_CHANCE = 0.25;
    public static final double TIER4_CHANCE = 0.07;
    public static final double TIER5_CHANCE = 0.03;

    public record FruitEntry(String itemId, int tier) {
    }

    private static final List<FruitEntry> FRUITS = List.of(
            new FruitEntry("BombFruit", 2),
            new FruitEntry("SmokeFruit", 2),
            new FruitEntry("GasFruit", 2),
            new FruitEntry("StringFruit", 2),
            new FruitEntry("SandFruit", 2),
            new FruitEntry("RubberFruit", 3),
            new FruitEntry("VenomFruit", 3),
            new FruitEntry("MagnetFruit", 3),
            new FruitEntry("GravityFruit", 3),
            new FruitEntry("MochiFruit", 4),
            new FruitEntry("OpOpFruit", 4),
            new FruitEntry("LeopardFruit", 5),
            new FruitEntry("QuakeFruit", 4),
            new FruitEntry("NikaFruit", 5)
    );

    private FruitPool() {
    }

    public static List<FruitEntry> all() {
        return Collections.unmodifiableList(FRUITS);
    }

    public static List<FruitEntry> byTier(int tier) {
        List<FruitEntry> result = new ArrayList<>();
        for (FruitEntry entry : FRUITS) {
            if (entry.tier() == tier) {
                result.add(entry);
            }
        }
        return result;
    }

    public static FruitEntry roll(Random random) {
        int tier = rollTier(random);
        List<FruitEntry> tierPool = byTier(tier);
        if (tierPool.isEmpty()) {
            return FRUITS.get(random.nextInt(FRUITS.size()));
        }
        return tierPool.get(random.nextInt(tierPool.size()));
    }

    public static int rollTier(Random random) {
        double roll = random.nextDouble();
        if (roll < TIER2_CHANCE) {
            return 2;
        }
        roll -= TIER2_CHANCE;
        if (roll < TIER3_CHANCE) {
            return 3;
        }
        roll -= TIER3_CHANCE;
        if (roll < TIER4_CHANCE) {
            return 4;
        }
        return 5;
    }
}
