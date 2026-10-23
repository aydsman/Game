package combat.powers;

/**
 * Reusable move behavior template for sharing logic across powers.
 * Powers can keep different names while reusing the same behavior shape.
 */
public class MoveTemplate {
    private final String templateId;
    private final MoveRole role;
    private final int cooldownMs;
    private final int basePower;
    private final int range;
    private final int radius;
    private final int durationMs;
    private final String castAssetPath;
    private final String impactAssetPath;

    public MoveTemplate(String templateId,
                        MoveRole role,
                        int cooldownMs,
                        int basePower,
                        int range,
                        int radius,
                        int durationMs,
                        String castAssetPath,
                        String impactAssetPath) {
        this.templateId = templateId;
        this.role = role;
        this.cooldownMs = cooldownMs;
        this.basePower = basePower;
        this.range = range;
        this.radius = radius;
        this.durationMs = durationMs;
        this.castAssetPath = castAssetPath;
        this.impactAssetPath = impactAssetPath;
    }

    public String getTemplateId() { return templateId; }
    public MoveRole getRole() { return role; }
    public int getCooldownMs() { return cooldownMs; }
    public int getBasePower() { return basePower; }
    public int getRange() { return range; }
    public int getRadius() { return radius; }
    public int getDurationMs() { return durationMs; }
    public String getCastAssetPath() { return castAssetPath; }
    public String getImpactAssetPath() { return impactAssetPath; }
}
