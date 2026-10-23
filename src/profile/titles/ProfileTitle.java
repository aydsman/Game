package profile.titles;

import java.awt.Color;
import profile.ProfileItem;

public class ProfileTitle extends ProfileItem {
    private final Color textColor;

    public ProfileTitle(String id, String displayName, String description, Color textColor) {
        super(id, displayName, description);
        this.textColor = textColor;
    }

    public Color getTextColor() {
        return textColor;
    }
}
