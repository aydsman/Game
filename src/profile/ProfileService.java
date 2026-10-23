package profile;

import java.util.Set;
import save.SaveData;
import save.SaveManager;

public final class ProfileService {
    private ProfileService() {}

    public static void ensureDefaultsUnlocked() {
        SaveData data = SaveManager.load();
        boolean changed = false;
        changed |= data.getUnlockedProfileBanners().add("Default");
        changed |= data.getUnlockedKillEffects().add("None");
        changed |= data.getUnlockedTitles().add("None");

        if (!data.getUnlockedProfileBanners().contains(data.getEquippedProfileBanner())) {
            data.setEquippedProfileBanner("Default");
            changed = true;
        }
        if (!data.getUnlockedKillEffects().contains(data.getEquippedKillEffect())) {
            data.setEquippedKillEffect("None");
            changed = true;
        }
        if (!data.getUnlockedTitles().contains(data.getEquippedTitle())) {
            data.setEquippedTitle("None");
            changed = true;
        }
        if (data.getProfileUsername() == null || data.getProfileUsername().isBlank()) {
            data.setProfileUsername("Player");
            changed = true;
        }
        if (changed) {
            SaveManager.save(data);
        }
    }

    public static SaveData getProfileData() {
        ensureDefaultsUnlocked();
        return SaveManager.load();
    }

    public static boolean equipBanner(String bannerId) {
        SaveData data = getProfileData();
        if (!data.getUnlockedProfileBanners().contains(bannerId)) {
            return false;
        }
        data.setEquippedProfileBanner(bannerId);
        SaveManager.save(data);
        return true;
    }

    public static boolean equipTitle(String titleId) {
        SaveData data = getProfileData();
        if (!data.getUnlockedTitles().contains(titleId)) {
            return false;
        }
        data.setEquippedTitle(titleId);
        SaveManager.save(data);
        return true;
    }

    public static boolean equipKillEffect(String effectId) {
        SaveData data = getProfileData();
        if (!data.getUnlockedKillEffects().contains(effectId)) {
            return false;
        }
        data.setEquippedKillEffect(effectId);
        SaveManager.save(data);
        return true;
    }

    public static void unlockBanner(String bannerId) {
        SaveData data = getProfileData();
        data.getUnlockedProfileBanners().add(bannerId);
        SaveManager.save(data);
    }

    public static void unlockTitle(String titleId) {
        SaveData data = getProfileData();
        data.getUnlockedTitles().add(titleId);
        SaveManager.save(data);
    }

    public static void unlockKillEffect(String effectId) {
        SaveData data = getProfileData();
        data.getUnlockedKillEffects().add(effectId);
        SaveManager.save(data);
    }

    public static String validateUsername(String rawInput) {
        if (rawInput == null) {
            return "Username change canceled.";
        }
        String trimmed = rawInput.strip();
        if (trimmed.length() < 3 || trimmed.length() > 16) {
            return "Username must be 3 to 16 characters.";
        }
        if (!trimmed.matches("[A-Za-z0-9 ]+")) {
            return "Only letters, numbers, and spaces are allowed.";
        }
        return null;
    }

    public static String normalizeUsername(String rawInput) {
        return rawInput == null ? null : rawInput.strip();
    }

    public static void setUsername(String username) {
        SaveData data = getProfileData();
        data.setProfileUsername(username);
        SaveManager.save(data);
    }

    public static Set<String> getUnlockedBannerIds() {
        return getProfileData().getUnlockedProfileBanners();
    }

    public static Set<String> getUnlockedTitleIds() {
        return getProfileData().getUnlockedTitles();
    }

    public static Set<String> getUnlockedKillEffectIds() {
        return getProfileData().getUnlockedKillEffects();
    }
}
