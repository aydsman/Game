package profile;

import java.util.List;
import profile.banners.AbyssBanner;
import profile.banners.DefaultBanner;
import profile.banners.MasteredBanner;
import profile.banners.ProfileBanner;
import profile.killeffects.KillEffect;
import profile.killeffects.NoKillEffect;
import profile.killeffects.SparkKillEffect;
import profile.killeffects.VoidKillEffect;
import profile.titles.NoTitle;
import profile.titles.ProfileTitle;
import profile.titles.WandererTitle;
import profile.titles.WavebreakerTitle;

public final class ProfileCatalog {
    private static final List<ProfileBanner> BANNERS = List.of(
            new DefaultBanner(),
            new AbyssBanner(),
            new MasteredBanner());

    private static final List<ProfileTitle> TITLES = List.of(
            new NoTitle(),
            new WandererTitle(),
            new WavebreakerTitle());

    private static final List<KillEffect> KILL_EFFECTS = List.of(
            new NoKillEffect(),
            new SparkKillEffect(),
            new VoidKillEffect());

    private ProfileCatalog() {}

    public static List<ProfileBanner> banners() {
        return BANNERS;
    }

    public static List<ProfileTitle> titles() {
        return TITLES;
    }

    public static List<KillEffect> killEffects() {
        return KILL_EFFECTS;
    }

    public static ProfileBanner findBanner(String id) {
        for (ProfileBanner banner : BANNERS) {
            if (banner.getId().equals(id)) {
                return banner;
            }
        }
        return BANNERS.get(0);
    }

    public static ProfileTitle findTitle(String id) {
        for (ProfileTitle title : TITLES) {
            if (title.getId().equals(id)) {
                return title;
            }
        }
        return TITLES.get(0);
    }

    public static KillEffect findKillEffect(String id) {
        for (KillEffect effect : KILL_EFFECTS) {
            if (effect.getId().equals(id)) {
                return effect;
            }
        }
        return KILL_EFFECTS.get(0);
    }
}
