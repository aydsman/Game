package profile.banners;

import java.awt.Color;

public class DefaultBanner extends ProfileBanner {
    public DefaultBanner() {
        super(
                "Default",
                "Default",
                "Starter banner for all players.",
                new Color(52, 73, 107),
                new Color(33, 44, 70));
    }
}
