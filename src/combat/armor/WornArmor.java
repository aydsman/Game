package combat.armor;

import player.CharacterAppearance;

/**
 * Defense granted by the clothing the player is wearing.
 *
 * <p>Clothes stay cosmetic in the wardrobe UI; this is the only combat read of equipped pieces.
 * Values are fractions of incoming damage removed, stacked additively and later clamped by
 * {@link entity.Player} so a full outfit cannot make the player unkillable.
 */
public final class WornArmor {

    public static final double TOP_MITIGATION = 0.03;
    public static final double BOTTOM_MITIGATION = 0.02;
    public static final double ACCESSORY_MITIGATION = 0.01;

    private WornArmor() {}

    /**
     * @param appearance current look; {@code null} or an empty outfit yields 0
     * @return stacked mitigation in {@code [0, 1)}
     */
    public static double mitigationFor(CharacterAppearance appearance) {
        if (appearance == null) {
            return 0.0;
        }
        double total = 0.0;
        if (isWorn(appearance.getEquippedTopId())) {
            total += TOP_MITIGATION;
        }
        if (isWorn(appearance.getEquippedBottomId())) {
            total += BOTTOM_MITIGATION;
        }
        if (isWorn(appearance.getEquippedAccessoryId())) {
            total += ACCESSORY_MITIGATION;
        }
        return total;
    }

    private static boolean isWorn(String id) {
        return id != null && !id.isBlank() && !"none".equalsIgnoreCase(id);
    }
}
