package profile.killeffects;

import java.awt.Color;
import profile.ProfileItem;

public class KillEffect extends ProfileItem {
    private final Color accentColor;

    public KillEffect(String id, String displayName, String description, Color accentColor) {
        super(id, displayName, description);
        this.accentColor = accentColor;
    }

    public Color getAccentColor() {
        return accentColor;
    }
}
