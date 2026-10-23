package profile.banners;

import java.awt.Color;
import profile.ProfileItem;

public class ProfileBanner extends ProfileItem {
    private final Color topColor;
    private final Color bottomColor;

    public ProfileBanner(String id, String displayName, String description, Color topColor, Color bottomColor) {
        super(id, displayName, description);
        this.topColor = topColor;
        this.bottomColor = bottomColor;
    }

    public Color getTopColor() {
        return topColor;
    }

    public Color getBottomColor() {
        return bottomColor;
    }
}
