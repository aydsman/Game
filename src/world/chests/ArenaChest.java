package world.chests;

public class ArenaChest extends Chest {

    public ArenaChest(int x, int y, int tier) {
        super(x, y, tier, getSlotCountFromTier(tier));
    }

    private static int getSlotCountFromTier(int tier) {
        if (tier == 1 || tier == 2) {
            return 3;
        } else if (tier == 3 || tier == 4) {
            return 4;
        } else if (tier == 5) {
            return 5;
        }
        return 3;
    }
}
