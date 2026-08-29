package save;

import player.CharacterAppearance;
import java.util.*;

public class SaveData {
    private int playerLevel = 1;
    private int playerXP = 0;
    private int cash = 0;
    private int gems = 0;
    private int totalKills = 0;
    private int totalDamageDealt = 0;
    private int highestWave = 0;
    private int gamesPlayed = 0;
    private int skillPoints = 0;

    private String selectedWeapon = "Pistol1";
    private int selectedWeaponTier = 1;
    private String selectedCharm = "None";
    private int selectedCharmTier = 1;
    private String selectedPower = "None";
    private int selectedPowerTier = 1;
    private String selectedSummon = "None";
    private int selectedSummonTier = 1;

    private Map<String, Set<Integer>> unlockedWeapons = new HashMap<>();
    private Map<String, Set<Integer>> unlockedCharms = new HashMap<>();
    private Map<String, Set<Integer>> unlockedPowers = new HashMap<>();
    private Map<String, Set<Integer>> unlockedSummons = new HashMap<>();
    private Map<String, Set<Integer>> unlockedConsumables = new HashMap<>();
    private Map<String, Map<String, Integer>> weaponStacks = new HashMap<>();
    private Map<String, Map<String, Integer>> consumableStacks = new HashMap<>();

    private Set<String> unlockedClothingIds = new HashSet<>();
    private Map<String, Set<String>> unlockedClothingStyles = new HashMap<>();
    private Map<String, String> equippedClothingStyles = new HashMap<>();
    private CharacterAppearance appearance = new CharacterAppearance();

    private Set<String> unlockedArenas = new HashSet<>();
    private String lastCompletedArena = null;
    private Set<String> discoveredCraftingRecipeIds = new HashSet<>();
    private Map<String, Integer> lootboxTokens = new HashMap<>();

    private int sessionSkillStipend = 0;
    private Map<String, Integer> sessionLootboxStipends = new HashMap<>();

    public int getPlayerLevel() { return playerLevel; }
    public void setPlayerLevel(int playerLevel) { this.playerLevel = playerLevel; }
    public int getPlayerXP() { return playerXP; }
    public void setPlayerXP(int playerXP) { this.playerXP = playerXP; }
    public int getCash() { return cash; }
    public void setCash(int cash) { this.cash = cash; }
    public int getGems() { return gems; }
    public void setGems(int gems) { this.gems = gems; }
    public int getTotalKills() { return totalKills; }
    public void setTotalKills(int totalKills) { this.totalKills = totalKills; }
    public int getTotalDamageDealt() { return totalDamageDealt; }
    public void setTotalDamageDealt(int totalDamageDealt) { this.totalDamageDealt = totalDamageDealt; }
    public int getHighestWave() { return highestWave; }
    public void setHighestWave(int highestWave) { this.highestWave = highestWave; }
    public int getGamesPlayed() { return gamesPlayed; }
    public void setGamesPlayed(int gamesPlayed) { this.gamesPlayed = gamesPlayed; }
    public int getSkillPoints() { return skillPoints; }
    public void setSkillPoints(int skillPoints) { this.skillPoints = skillPoints; }

    public String getSelectedWeapon() { return selectedWeapon; }
    public void setSelectedWeapon(String selectedWeapon) { this.selectedWeapon = selectedWeapon; }
    public int getSelectedWeaponTier() { return selectedWeaponTier; }
    public void setSelectedWeaponTier(int selectedWeaponTier) { this.selectedWeaponTier = selectedWeaponTier; }
    public String getSelectedCharm() { return selectedCharm; }
    public void setSelectedCharm(String selectedCharm) { this.selectedCharm = selectedCharm; }
    public int getSelectedCharmTier() { return selectedCharmTier; }
    public void setSelectedCharmTier(int selectedCharmTier) { this.selectedCharmTier = selectedCharmTier; }
    public String getSelectedPower() { return selectedPower; }
    public void setSelectedPower(String selectedPower) { this.selectedPower = selectedPower; }
    public int getSelectedPowerTier() { return selectedPowerTier; }
    public void setSelectedPowerTier(int selectedPowerTier) { this.selectedPowerTier = selectedPowerTier; }
    public String getSelectedSummon() { return selectedSummon; }
    public void setSelectedSummon(String selectedSummon) { this.selectedSummon = selectedSummon; }
    public int getSelectedSummonTier() { return selectedSummonTier; }
    public void setSelectedSummonTier(int selectedSummonTier) { this.selectedSummonTier = selectedSummonTier; }

    public Map<String, Set<Integer>> getUnlockedWeapons() { return unlockedWeapons; }
    public void setUnlockedWeapons(Map<String, Set<Integer>> m) { unlockedWeapons = m != null ? m : new HashMap<>(); }
    public Map<String, Set<Integer>> getUnlockedCharms() { return unlockedCharms; }
    public void setUnlockedCharms(Map<String, Set<Integer>> m) { unlockedCharms = m != null ? m : new HashMap<>(); }
    public Map<String, Set<Integer>> getUnlockedPowers() { return unlockedPowers; }
    public void setUnlockedPowers(Map<String, Set<Integer>> m) { unlockedPowers = m != null ? m : new HashMap<>(); }
    public Map<String, Set<Integer>> getUnlockedSummons() { return unlockedSummons; }
    public void setUnlockedSummons(Map<String, Set<Integer>> m) { unlockedSummons = m != null ? m : new HashMap<>(); }
    public Map<String, Set<Integer>> getUnlockedConsumables() { return unlockedConsumables; }
    public void setUnlockedConsumables(Map<String, Set<Integer>> m) { unlockedConsumables = m != null ? m : new HashMap<>(); }
    public Map<String, Map<String, Integer>> getConsumableStacks() { return consumableStacks; }
    public void setConsumableStacks(Map<String, Map<String, Integer>> m) { consumableStacks = m != null ? m : new HashMap<>(); }

    public Set<String> getUnlockedClothingIds() { return unlockedClothingIds; }
    public void setUnlockedClothingIds(Set<String> ids) {
        unlockedClothingIds = ids != null ? new HashSet<>(ids) : new HashSet<>();
    }
    public Map<String, Set<String>> getUnlockedClothingStyles() { return unlockedClothingStyles; }
    public void setUnlockedClothingStyles(Map<String, Set<String>> m) { unlockedClothingStyles = m != null ? m : new HashMap<>(); }
    public Map<String, String> getEquippedClothingStyles() { return equippedClothingStyles; }
    public void setEquippedClothingStyles(Map<String, String> m) {
        equippedClothingStyles = m != null ? m : new HashMap<>();
    }
    public CharacterAppearance getAppearance() { return appearance; }
    public void setAppearance(CharacterAppearance appearance) { this.appearance = appearance != null ? appearance : new CharacterAppearance(); }

    public Set<String> getUnlockedArenas() { return unlockedArenas; }
    public void setUnlockedArenas(Set<String> arenas) {
        unlockedArenas = arenas != null ? new HashSet<>(arenas) : new HashSet<>();
        unlockedArenas.add("PlainsI");
    }
    public String getLastCompletedArena() { return lastCompletedArena; }
    public void setLastCompletedArena(String lastCompletedArena) { this.lastCompletedArena = lastCompletedArena; }

    public Set<String> getDiscoveredCraftingRecipeIds() { return discoveredCraftingRecipeIds; }
    public void setDiscoveredCraftingRecipeIds(Set<String> ids) { discoveredCraftingRecipeIds = ids != null ? ids : new HashSet<>(); }

    public Map<String, Integer> getLootboxTokens() { return lootboxTokens; }
    public void setLootboxTokens(Map<String, Integer> tokens) { lootboxTokens = tokens != null ? tokens : new HashMap<>(); }

    public int getLootboxTokenCount(String id) { return lootboxTokens.getOrDefault(id, 0); }
    public void addLootboxTokens(String id, int count) {
        lootboxTokens.put(id, getLootboxTokenCount(id) + count);
    }
    public boolean tryUseLootboxToken(String id, int count) {
        int have = getLootboxTokenCount(id);
        if (have < count) return false;
        lootboxTokens.put(id, have - count);
        return true;
    }

    public void activateSessionAdminSkillStipend(int cap) { sessionSkillStipend = cap; }
    public void clearSessionSkillStipend() { sessionSkillStipend = 0; }
    public void activateSessionAdminLootboxStipends(int cap) {
        sessionLootboxStipends.put("all", cap);
    }
    public void clearSessionLootboxStipends() { sessionLootboxStipends.clear(); }
}
