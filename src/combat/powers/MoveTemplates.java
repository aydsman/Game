package combat.powers;

/**
 * Shared move template factories to reduce repetitive power definitions.
 * Use these when two powers should feel similar with different stats/assets.
 */
public final class MoveTemplates {
    private MoveTemplates() {}

    public static MoveTemplate projectileBomb(int cooldownMs,
                                              int basePower,
                                              int range,
                                              int radius,
                                              String castAssetPath,
                                              String impactAssetPath) {
        return new MoveTemplate(
                "projectile_bomb",
                MoveRole.CORE_DAMAGE,
                cooldownMs,
                basePower,
                range,
                radius,
                0,
                castAssetPath,
                impactAssetPath
        );
    }

    public static MoveTemplate dash(int cooldownMs,
                                    int basePower,
                                    int range,
                                    int durationMs,
                                    String castAssetPath) {
        return new MoveTemplate(
                "dash",
                MoveRole.MOBILITY,
                cooldownMs,
                basePower,
                range,
                0,
                durationMs,
                castAssetPath,
                null
        );
    }

    public static MoveTemplate slowField(int cooldownMs,
                                         int basePower,
                                         int range,
                                         int radius,
                                         int durationMs,
                                         String castAssetPath,
                                         String impactAssetPath) {
        return new MoveTemplate(
                "slow_field",
                MoveRole.CONTROL,
                cooldownMs,
                basePower,
                range,
                radius,
                durationMs,
                castAssetPath,
                impactAssetPath
        );
    }

    public static MoveTemplate heavyFinisher(int cooldownMs,
                                             int basePower,
                                             int range,
                                             int radius,
                                             String castAssetPath,
                                             String impactAssetPath) {
        return new MoveTemplate(
                "heavy_finisher",
                MoveRole.FINISHER,
                cooldownMs,
                basePower,
                range,
                radius,
                0,
                castAssetPath,
                impactAssetPath
        );
    }
}
