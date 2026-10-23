package skilltree;

import java.awt.Color;

public enum SkillPillar {
    COMBAT("Combat", new Color(220, 70, 70), new Color(255, 120, 120)),
    VITALITY("Vitality", new Color(70, 190, 90), new Color(130, 240, 150)),
    ABUNDANCE("Abundance", new Color(220, 190, 70), new Color(255, 230, 120));

    private final String displayName;
    private final Color baseColor;
    private final Color glowColor;

    SkillPillar(String displayName, Color baseColor, Color glowColor) {
        this.displayName = displayName;
        this.baseColor = baseColor;
        this.glowColor = glowColor;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getBaseColor() {
        return baseColor;
    }

    public Color getGlowColor() {
        return glowColor;
    }
}
