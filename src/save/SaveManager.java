package save;

import entity.PlayerStats;
import player.CharacterAppearance;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class SaveManager {
    private static final String SAVE_FILE = "save_data.json";
    private static SaveData cache = null;
    private static Consumer<SaveData> preSaveMutator = null;

    private SaveManager() {}

    public static void setPreSaveMutator(Consumer<SaveData> mutator) { preSaveMutator = mutator; }
    public static void clearCache() { cache = null; }

    public static SaveData load() {
        if (cache != null) return cache;
        SaveData data = new SaveData();
        File f = new File(SAVE_FILE);
        if (!f.exists()) {
            initDefaults(data);
            cache = data;
            return data;
        }
        try {
            String json = new String(java.nio.file.Files.readAllBytes(f.toPath()), StandardCharsets.UTF_8);
            parseInto(data, json);
        } catch (Exception e) {
            System.err.println("Failed to load save: " + e.getMessage());
            initDefaults(data);
        }
        cache = data;
        return data;
    }

    public static void save(SaveData data) {
        if (data == null) return;
        if (preSaveMutator != null) preSaveMutator.accept(data);
        cache = data;
        try {
            java.nio.file.Files.write(new File(SAVE_FILE).toPath(), toJson(data).getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.err.println("Failed to save: " + e.getMessage());
        }
    }

    public static void resetSave() {
        cache = null;
        new File(SAVE_FILE).delete();
        SaveData d = new SaveData();
        initDefaults(d);
        save(d);
    }

    public static void quickSaveStats(int kills, int damage) {
        SaveData d = load();
        d.setTotalKills(d.getTotalKills() + kills);
        d.setTotalDamageDealt(d.getTotalDamageDealt() + damage);
        save(d);
    }

    public static void syncPlayerStats(PlayerStats stats, int wave, int dungeonLevel) {
        SaveData d = load();
        d.setTotalKills(d.getTotalKills() + stats.getKills());
        d.setTotalDamageDealt(d.getTotalDamageDealt() + (int) stats.getDamageDealt());
        if (wave > d.getHighestWave()) d.setHighestWave(wave);
        d.setGamesPlayed(d.getGamesPlayed() + 1);
        save(d);
    }

    public static void addXP(int xp) {
        SaveData d = load();
        int newXp = d.getPlayerXP() + xp;
        int level = d.getPlayerLevel();
        int threshold = 100 * level;
        while (newXp >= threshold) {
            newXp -= threshold;
            level++;
            threshold = 100 * level;
        }
        d.setPlayerXP(newXp);
        d.setPlayerLevel(level);
        save(d);
    }

    private static void initDefaults(SaveData d) {
        d.setSelectedWeapon("Pistol1");
        d.setSelectedWeaponTier(1);
        d.getUnlockedWeapons().computeIfAbsent("Pistol1", k -> new HashSet<>()).add(1);
        d.getUnlockedArenas().add("PlainsI");
        d.getUnlockedClothingIds().add("tshirt");
        d.getUnlockedClothingIds().add("shorts");
    }

    private static String toJson(SaveData d) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        sb.append("\"playerLevel\":").append(d.getPlayerLevel()).append(",");
        sb.append("\"playerXP\":").append(d.getPlayerXP()).append(",");
        sb.append("\"cash\":").append(d.getCash()).append(",");
        sb.append("\"gems\":").append(d.getGems()).append(",");
        sb.append("\"totalKills\":").append(d.getTotalKills()).append(",");
        sb.append("\"totalDamageDealt\":").append(d.getTotalDamageDealt()).append(",");
        sb.append("\"highestWave\":").append(d.getHighestWave()).append(",");
        sb.append("\"gamesPlayed\":").append(d.getGamesPlayed()).append(",");
        sb.append("\"skillPoints\":").append(d.getSkillPoints()).append(",");
        sb.append("\"selectedWeapon\":\"").append(esc(d.getSelectedWeapon())).append("\",");
        sb.append("\"selectedWeaponTier\":").append(d.getSelectedWeaponTier()).append(",");
        sb.append("\"selectedCharm\":\"").append(esc(d.getSelectedCharm())).append("\",");
        sb.append("\"selectedCharmTier\":").append(d.getSelectedCharmTier()).append(",");
        sb.append("\"selectedPower\":\"").append(esc(d.getSelectedPower())).append("\",");
        sb.append("\"selectedPowerTier\":").append(d.getSelectedPowerTier()).append(",");
        sb.append("\"selectedSummon\":\"").append(esc(d.getSelectedSummon())).append("\",");
        sb.append("\"selectedSummonTier\":").append(d.getSelectedSummonTier()).append(",");
        sb.append("\"lastCompletedArena\":").append(d.getLastCompletedArena() == null ? "null" : "\"" + esc(d.getLastCompletedArena()) + "\"");
        sb.append("\n}");
        return sb.toString();
    }

    private static String esc(String s) { return s == null ? "" : s.replace("\\", "\\\\").replace("\"", "\\\""); }

    private static void parseInto(SaveData d, String json) {
        d.setPlayerLevel(intVal(json, "playerLevel", 1));
        d.setPlayerXP(intVal(json, "playerXP", 0));
        d.setCash(intVal(json, "cash", 0));
        d.setGems(intVal(json, "gems", 0));
        d.setTotalKills(intVal(json, "totalKills", 0));
        d.setTotalDamageDealt(intVal(json, "totalDamageDealt", 0));
        d.setHighestWave(intVal(json, "highestWave", 0));
        d.setGamesPlayed(intVal(json, "gamesPlayed", 0));
        d.setSkillPoints(intVal(json, "skillPoints", 0));
        d.setSelectedWeapon(strVal(json, "selectedWeapon", "Pistol1"));
        d.setSelectedWeaponTier(intVal(json, "selectedWeaponTier", 1));
        d.setSelectedCharm(strVal(json, "selectedCharm", "None"));
        d.setSelectedCharmTier(intVal(json, "selectedCharmTier", 1));
        d.setSelectedPower(strVal(json, "selectedPower", "None"));
        d.setSelectedPowerTier(intVal(json, "selectedPowerTier", 1));
        d.setSelectedSummon(strVal(json, "selectedSummon", "None"));
        d.setSelectedSummonTier(intVal(json, "selectedSummonTier", 1));
        String lastArena = strVal(json, "lastCompletedArena", null);
        if (lastArena != null && !lastArena.equals("null")) d.setLastCompletedArena(lastArena);
    }

    private static int intVal(String json, String key, int def) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*(-?\\d+)").matcher(json);
        return m.find() ? Integer.parseInt(m.group(1)) : def;
    }

    private static String strVal(String json, String key, String def) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"").matcher(json);
        return m.find() ? m.group(1) : def;
    }
}
