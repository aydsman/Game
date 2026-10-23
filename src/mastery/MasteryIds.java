package mastery;

public final class MasteryIds {
    private MasteryIds() {}

    public static String normalizeWeaponTypeId(String weaponName) {
        if (weaponName == null || weaponName.isBlank()) {
            return "UnknownWeapon";
        }
        String normalized = weaponName.trim();
        String key = normalized.toLowerCase().replace(" ", "").replace("-", "");
        return switch (key) {
            case "glock", "pistol", "pistol1" -> "Pistol1";
            case "ak47", "rifle", "rifle1" -> "Rifle1";
            case "pumpaction", "shotgun", "shotgun1" -> "Shotgun1";
            case "p90", "smg", "smg1" -> "SMG1";
            case "sniper", "sniper1" -> "Sniper1";
            case "sword", "sword1" -> "Sword1";
            case "hammer", "hammer1" -> "Hammer1";
            case "dagger", "dagger1" -> "Dagger1";
            case "mace", "mace1" -> "Mace1";
            case "scythe", "scythe1" -> "Scythe1";
            default -> normalized;
        };
    }

    public static String normalizePowerTypeId(String powerName) {
        if (powerName == null || powerName.isBlank() || "None".equalsIgnoreCase(powerName)) {
            return "None";
        }
        return powerName.trim();
    }
}
