package save;

import combat.lootboxes.LootboxCatalog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import player.CharacterAppearance;

public class SaveData {

    /** Temporary: high starting pool for skill-tree testing. Set to 0 for release. */
    public static final int DEBUG_STARTING_SKILL_POINTS = 10_000;

    // Player stats
    private int playerLevel;
    private int playerXP;
    /** Unspent skill points (meta skill tree). Earned on level-up; see SaveManager.skillPointsForReachingLevel. */
    private int skillPoints;
    /** Session-only admin stipend (never serialized). Spent before {@link #skillPoints}. */
    private int sessionSkillStipend;
    /** Session-only admin token stipends (never serialized). Spent before bank in {@link #lootboxTokens}. */
    private final Map<String, Integer> sessionLootboxStipend = new HashMap<>();
    /** Skill node unlock state: nodeId -> rank (v1 uses rank 1 for unlocked). */
    private Map<String, Integer> skillRanks;
    
    // Currency (Gold is session-only, not saved)
    private int cash;
    private int gems;
    
    // Combat stats
    private int totalKills;
    private int totalDamageDealt;
    private int highestWave;
    private int gamesPlayed;

    // Mastery progression (Part 1 scaffold)
    // Keys are normalized weapon/power type ids.
    private Map<String, Integer> weaponMasteryXpByType;
    private Map<String, Set<Integer>> weaponMasteryClaimedLevelsByType;
    private Map<String, Integer> powerMasteryXpByType;
    private Map<String, Set<Integer>> powerMasteryClaimedLevelsByType;
    private int masteryMedals;
    
    // Equipment
    private List<String> unlockedEquipment;
    private Map<String, String> equippedLoadout; // slot -> item class name
    
    // Inventory (lootbox items and selections)
    private Map<String, List<Integer>> unlockedWeapons; // weapon name -> list of tiers
    private Map<String, List<Integer>> unlockedCharms;
    private Map<String, List<Integer>> unlockedPowers;
    private Map<String, List<Integer>> unlockedSummons;
    private Map<String, List<Integer>> unlockedConsumables;
    
    // Item stack counts
    private Map<String, Map<Integer, Integer>> weaponStacks; // item name -> tier -> count
    private Map<String, Map<Integer, Integer>> charmStacks;
    private Map<String, Map<Integer, Integer>> powerStacks;
    private Map<String, Map<Integer, Integer>> summonStacks;
    private Map<String, Map<Integer, Integer>> consumableStacks;
    private Map<String, Integer> lootboxTokens; // lootbox id -> quantity
    
    // Current loadout selections
    private String selectedWeapon = "Glock";
    private int selectedWeaponTier = 1;
    private String selectedCharm = "None";
    private int selectedCharmTier = 1;
    private String selectedPower = "None";
    private int selectedPowerTier = 1;
    private String selectedSummon = "None";
    private int selectedSummonTier = 1;
    private String selectedConsumable = "None";
    private int selectedConsumableTier = 1;
    
    // Character appearance
    private CharacterAppearance appearance;
    private Set<String> unlockedClothingIds;
    
    // Clothing styles tracking (clothingId -> set of styleIds)
    private Map<String, Set<String>> unlockedClothingStyles;
    private Map<String, String> equippedClothingStyles;

    // Profile customization scaffold (for upcoming Part 2 planning/implementation)
    private String profileUsername;
    private Set<String> unlockedProfileBanners;
    private Set<String> unlockedKillEffects;
    private Set<String> unlockedTitles;
    private String equippedProfileBanner;
    private String equippedKillEffect;
    private String equippedTitle;
    
    // Arena progression
    private Set<String> unlockedArenas;
    private String lastCompletedArena;
    private Set<String> discoveredCraftingRecipeIds;

    /** Quest/reward ids already claimed, so rewards cannot be farmed across restarts. */
    private Set<String> claimedRewardIds;
    
    public SaveData() {
        // Default values for new player
        this.playerLevel = 1;
        this.playerXP = 0;
        this.skillPoints = DEBUG_STARTING_SKILL_POINTS;
        this.skillRanks = new HashMap<>();
        this.cash = 1000;
        this.gems = 100;
        this.totalKills = 0;
        this.totalDamageDealt = 0;
        this.highestWave = 0;
        this.gamesPlayed = 0;
        this.weaponMasteryXpByType = new HashMap<>();
        this.weaponMasteryClaimedLevelsByType = new HashMap<>();
        this.powerMasteryXpByType = new HashMap<>();
        this.powerMasteryClaimedLevelsByType = new HashMap<>();
        this.masteryMedals = 0;
        this.unlockedEquipment = new ArrayList<>();
        this.equippedLoadout = new HashMap<>();
        
        // Initialize inventory maps
        this.unlockedWeapons = new HashMap<>();
        this.unlockedCharms = new HashMap<>();
        this.unlockedPowers = new HashMap<>();
        this.unlockedSummons = new HashMap<>();
        this.unlockedConsumables = new HashMap<>();
        
        this.weaponStacks = new HashMap<>();
        this.charmStacks = new HashMap<>();
        this.powerStacks = new HashMap<>();
        this.summonStacks = new HashMap<>();
        this.consumableStacks = new HashMap<>();
        this.lootboxTokens = new HashMap<>();
        
        // Initialize clothing styles tracking
        this.unlockedClothingStyles = new HashMap<>();
        this.equippedClothingStyles = new HashMap<>();
        this.unlockedClothingIds = new HashSet<>();
        this.unlockedProfileBanners = new HashSet<>();
        this.unlockedKillEffects = new HashSet<>();
        this.unlockedTitles = new HashSet<>();
        this.profileUsername = "Player";
        this.equippedProfileBanner = "Default";
        this.equippedKillEffect = "None";
        this.equippedTitle = "None";
        this.unlockedProfileBanners.add("Default");
        this.unlockedKillEffects.add("None");
        this.unlockedTitles.add("None");
        
        this.unlockedArenas = new HashSet<>();
        this.unlockedArenas.add("PlainsI");
        this.lastCompletedArena = null;
        this.discoveredCraftingRecipeIds = new HashSet<>();
        this.claimedRewardIds = new HashSet<>();
        
        // Default unlocks
        unlockedEquipment.add("combat.ranged.pistols.Pistol1");
        unlockedEquipment.add("combat.melee.swords.Sword1");
        
        // Default equipped
        equippedLoadout.put("slot0", "combat.ranged.pistols.Pistol1");
    }
    
    // Getters and Setters
    public int getPlayerLevel() { return playerLevel; }
    public void setPlayerLevel(int playerLevel) { this.playerLevel = playerLevel; }
    
    public int getPlayerXP() { return playerXP; }
    public void setPlayerXP(int playerXP) { this.playerXP = playerXP; }

    /** Display / gameplay total including any session admin stipend. */
    public int getSkillPoints() {
        return skillPoints + sessionSkillStipend;
    }

    /** Bank only — used for JSON and stripping admin before save. */
    public int getSkillPointsPersisted() {
        return skillPoints;
    }

    public void setSkillPoints(int skillPoints) {
        this.skillPoints = Math.max(0, skillPoints);
        this.sessionSkillStipend = 0;
    }

    public void addSkillPoints(int amount) {
        if (amount > 0) {
            this.skillPoints += amount;
        }
    }

    /** @return false if not enough points */
    public boolean trySpendSkillPoints(int amount) {
        if (amount <= 0) {
            return true;
        }
        int need = amount;
        if (sessionSkillStipend >= need) {
            sessionSkillStipend -= need;
            return true;
        }
        need -= sessionSkillStipend;
        sessionSkillStipend = 0;
        if (skillPoints < need) {
            return false;
        }
        skillPoints -= need;
        return true;
    }

    public void clearSessionSkillStipend() {
        sessionSkillStipend = 0;
    }

    /**
     * Session admin: raise displayed skill points toward {@code cap} without persisting the stipend.
     */
    public void activateSessionAdminSkillStipend(int cap) {
        sessionSkillStipend = Math.max(0, cap - skillPoints);
    }

    public void clearSessionLootboxStipends() {
        sessionLootboxStipend.clear();
    }

    /**
     * Session admin: raise each known lootbox token count toward {@code cap} without persisting stipends.
     */
    public void activateSessionAdminLootboxStipends(int cap) {
        sessionLootboxStipend.clear();
        for (String id : LootboxCatalog.all().keySet()) {
            int bank = lootboxTokens.getOrDefault(id, 0);
            int st = Math.max(0, cap - bank);
            if (st > 0) {
                sessionLootboxStipend.put(id, st);
            }
        }
    }

    public Set<String> getClaimedRewardIds() {
        if (claimedRewardIds == null) {
            claimedRewardIds = new HashSet<>();
        }
        return claimedRewardIds;
    }

    public void setClaimedRewardIds(Set<String> claimedRewardIds) {
        this.claimedRewardIds = (claimedRewardIds == null) ? new HashSet<>() : new HashSet<>(claimedRewardIds);
    }

    public boolean isRewardClaimed(String rewardId) {
        return getClaimedRewardIds().contains(rewardId);
    }

    public void markRewardClaimed(String rewardId) {
        if (rewardId != null) {
            getClaimedRewardIds().add(rewardId);
        }
    }

    public Map<String, Integer> getSkillRanks() {
        return skillRanks;
    }

    public void setSkillRanks(Map<String, Integer> skillRanks) {
        this.skillRanks = (skillRanks == null) ? new HashMap<>() : new HashMap<>(skillRanks);
    }

    public void resetSkillTree() {
        // Keep current skill-point balance; this only clears node unlock state.
        this.skillRanks.clear();
    }
    
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

    public Map<String, Integer> getWeaponMasteryXpByType() { return weaponMasteryXpByType; }
    public void setWeaponMasteryXpByType(Map<String, Integer> weaponMasteryXpByType) {
        this.weaponMasteryXpByType = (weaponMasteryXpByType == null) ? new HashMap<>() : new HashMap<>(weaponMasteryXpByType);
    }

    public Map<String, Set<Integer>> getWeaponMasteryClaimedLevelsByType() { return weaponMasteryClaimedLevelsByType; }
    public void setWeaponMasteryClaimedLevelsByType(Map<String, Set<Integer>> weaponMasteryClaimedLevelsByType) {
        this.weaponMasteryClaimedLevelsByType = copyIntSetMap(weaponMasteryClaimedLevelsByType);
    }

    public Map<String, Integer> getPowerMasteryXpByType() { return powerMasteryXpByType; }
    public void setPowerMasteryXpByType(Map<String, Integer> powerMasteryXpByType) {
        this.powerMasteryXpByType = (powerMasteryXpByType == null) ? new HashMap<>() : new HashMap<>(powerMasteryXpByType);
    }

    public Map<String, Set<Integer>> getPowerMasteryClaimedLevelsByType() { return powerMasteryClaimedLevelsByType; }
    public void setPowerMasteryClaimedLevelsByType(Map<String, Set<Integer>> powerMasteryClaimedLevelsByType) {
        this.powerMasteryClaimedLevelsByType = copyIntSetMap(powerMasteryClaimedLevelsByType);
    }

    public int getMasteryMedals() { return masteryMedals; }
    public void setMasteryMedals(int masteryMedals) { this.masteryMedals = Math.max(0, masteryMedals); }
    public void addMasteryMedals(int amount) {
        if (amount > 0) {
            this.masteryMedals += amount;
        }
    }
    
    public List<String> getUnlockedEquipment() { return unlockedEquipment; }
    public void setUnlockedEquipment(List<String> unlockedEquipment) { this.unlockedEquipment = unlockedEquipment; }
    
    public Map<String, String> getEquippedLoadout() { return equippedLoadout; }
    public void setEquippedLoadout(Map<String, String> equippedLoadout) { this.equippedLoadout = equippedLoadout; }
    
    // Inventory getters and setters
    public Map<String, List<Integer>> getUnlockedWeapons() { return unlockedWeapons; }
    public void setUnlockedWeapons(Map<String, List<Integer>> unlockedWeapons) { this.unlockedWeapons = unlockedWeapons; }
    
    public Map<String, List<Integer>> getUnlockedCharms() { return unlockedCharms; }
    public void setUnlockedCharms(Map<String, List<Integer>> unlockedCharms) { this.unlockedCharms = unlockedCharms; }
    
    public Map<String, List<Integer>> getUnlockedPowers() { return unlockedPowers; }
    public void setUnlockedPowers(Map<String, List<Integer>> unlockedPowers) { this.unlockedPowers = unlockedPowers; }
    
    public Map<String, List<Integer>> getUnlockedSummons() { return unlockedSummons; }
    public void setUnlockedSummons(Map<String, List<Integer>> unlockedSummons) { this.unlockedSummons = unlockedSummons; }
    
    public Map<String, List<Integer>> getUnlockedConsumables() { return unlockedConsumables; }
    public void setUnlockedConsumables(Map<String, List<Integer>> unlockedConsumables) { this.unlockedConsumables = unlockedConsumables; }
    
    // Stack count getters and setters
    public Map<String, Map<Integer, Integer>> getWeaponStacks() { return weaponStacks; }
    public void setWeaponStacks(Map<String, Map<Integer, Integer>> weaponStacks) { this.weaponStacks = weaponStacks; }
    
    public Map<String, Map<Integer, Integer>> getCharmStacks() { return charmStacks; }
    public void setCharmStacks(Map<String, Map<Integer, Integer>> charmStacks) { this.charmStacks = charmStacks; }
    
    public Map<String, Map<Integer, Integer>> getPowerStacks() { return powerStacks; }
    public void setPowerStacks(Map<String, Map<Integer, Integer>> powerStacks) { this.powerStacks = powerStacks; }
    
    public Map<String, Map<Integer, Integer>> getSummonStacks() { return summonStacks; }
    public void setSummonStacks(Map<String, Map<Integer, Integer>> summonStacks) { this.summonStacks = summonStacks; }
    
    public Map<String, Map<Integer, Integer>> getConsumableStacks() { return consumableStacks; }
    public void setConsumableStacks(Map<String, Map<Integer, Integer>> consumableStacks) { this.consumableStacks = consumableStacks; }

    public Map<String, Integer> getLootboxTokens() {
        return lootboxTokens;
    }

    /** Bank counts only — for JSON. */
    public Map<String, Integer> getLootboxTokensPersistedMap() {
        return new HashMap<>(lootboxTokens);
    }

    public void setLootboxTokens(Map<String, Integer> lootboxTokens) {
        this.lootboxTokens = (lootboxTokens == null) ? new HashMap<>() : new HashMap<>(lootboxTokens);
        this.sessionLootboxStipend.clear();
    }
    public int getLootboxTokenCount(String lootboxId) {
        if (lootboxId == null || lootboxId.isEmpty()) {
            return 0;
        }
        return lootboxTokens.getOrDefault(lootboxId, 0) + sessionLootboxStipend.getOrDefault(lootboxId, 0);
    }
    public void addLootboxTokens(String lootboxId, int amount) {
        if (lootboxId == null || lootboxId.isEmpty() || amount <= 0) return;
        lootboxTokens.put(lootboxId, lootboxTokens.getOrDefault(lootboxId, 0) + amount);
    }
    public boolean tryUseLootboxToken(String lootboxId, int amount) {
        if (lootboxId == null || lootboxId.isEmpty() || amount <= 0) return false;
        if (getLootboxTokenCount(lootboxId) < amount) {
            return false;
        }
        int need = amount;
        int st = sessionLootboxStipend.getOrDefault(lootboxId, 0);
        if (st >= need) {
            sessionLootboxStipend.put(lootboxId, st - need);
            if (sessionLootboxStipend.get(lootboxId) <= 0) {
                sessionLootboxStipend.remove(lootboxId);
            }
            return true;
        }
        need -= st;
        sessionLootboxStipend.remove(lootboxId);
        int bank = lootboxTokens.getOrDefault(lootboxId, 0);
        int next = bank - need;
        if (next <= 0) {
            lootboxTokens.remove(lootboxId);
        } else {
            lootboxTokens.put(lootboxId, next);
        }
        return true;
    }
    
    // Loadout selection getters and setters
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
    
    public String getSelectedConsumable() { return selectedConsumable; }
    public void setSelectedConsumable(String selectedConsumable) { this.selectedConsumable = selectedConsumable; }
    
    public int getSelectedConsumableTier() { return selectedConsumableTier; }
    public void setSelectedConsumableTier(int selectedConsumableTier) { this.selectedConsumableTier = selectedConsumableTier; }
    
    // Character appearance getters and setters
    public CharacterAppearance getAppearance() { return appearance; }
    public void setAppearance(CharacterAppearance appearance) { this.appearance = appearance; }
    
    public Set<String> getUnlockedClothingIds() { return unlockedClothingIds; }
    public void setUnlockedClothingIds(Set<String> unlockedClothingIds) { this.unlockedClothingIds = unlockedClothingIds; }
    
    // Clothing styles tracking
    public Map<String, Set<String>> getUnlockedClothingStyles() { return unlockedClothingStyles; }
    public void setUnlockedClothingStyles(Map<String, Set<String>> unlockedClothingStyles) { this.unlockedClothingStyles = unlockedClothingStyles; }
    
    public Map<String, String> getEquippedClothingStyles() { return equippedClothingStyles; }
    public void setEquippedClothingStyles(Map<String, String> equippedClothingStyles) { this.equippedClothingStyles = equippedClothingStyles; }

    public String getProfileUsername() { return profileUsername; }
    public void setProfileUsername(String profileUsername) {
        this.profileUsername = (profileUsername == null || profileUsername.isBlank()) ? "Player" : profileUsername;
    }

    public Set<String> getUnlockedProfileBanners() { return unlockedProfileBanners; }
    public void setUnlockedProfileBanners(Set<String> unlockedProfileBanners) {
        this.unlockedProfileBanners = unlockedProfileBanners == null ? new HashSet<>() : new HashSet<>(unlockedProfileBanners);
    }

    public Set<String> getUnlockedKillEffects() { return unlockedKillEffects; }
    public void setUnlockedKillEffects(Set<String> unlockedKillEffects) {
        this.unlockedKillEffects = unlockedKillEffects == null ? new HashSet<>() : new HashSet<>(unlockedKillEffects);
    }

    public Set<String> getUnlockedTitles() { return unlockedTitles; }
    public void setUnlockedTitles(Set<String> unlockedTitles) {
        this.unlockedTitles = unlockedTitles == null ? new HashSet<>() : new HashSet<>(unlockedTitles);
    }

    public String getEquippedProfileBanner() { return equippedProfileBanner; }
    public void setEquippedProfileBanner(String equippedProfileBanner) {
        this.equippedProfileBanner = (equippedProfileBanner == null || equippedProfileBanner.isBlank()) ? "Default" : equippedProfileBanner;
    }

    public String getEquippedKillEffect() { return equippedKillEffect; }
    public void setEquippedKillEffect(String equippedKillEffect) {
        this.equippedKillEffect = (equippedKillEffect == null || equippedKillEffect.isBlank()) ? "None" : equippedKillEffect;
    }

    public String getEquippedTitle() { return equippedTitle; }
    public void setEquippedTitle(String equippedTitle) {
        this.equippedTitle = (equippedTitle == null || equippedTitle.isBlank()) ? "None" : equippedTitle;
    }
    
    public Set<String> getUnlockedArenas() { return unlockedArenas; }
    public void setUnlockedArenas(Set<String> unlockedArenas) { this.unlockedArenas = unlockedArenas; }
    
    public String getLastCompletedArena() { return lastCompletedArena; }
    public void setLastCompletedArena(String lastCompletedArena) { this.lastCompletedArena = lastCompletedArena; }

    public Set<String> getDiscoveredCraftingRecipeIds() { return discoveredCraftingRecipeIds; }
    public void setDiscoveredCraftingRecipeIds(Set<String> discoveredCraftingRecipeIds) {
        this.discoveredCraftingRecipeIds = discoveredCraftingRecipeIds == null ? new HashSet<>() : new HashSet<>(discoveredCraftingRecipeIds);
    }

    private Map<String, Set<Integer>> copyIntSetMap(Map<String, Set<Integer>> input) {
        Map<String, Set<Integer>> output = new HashMap<>();
        if (input == null) {
            return output;
        }
        for (Map.Entry<String, Set<Integer>> entry : input.entrySet()) {
            output.put(entry.getKey(), entry.getValue() == null ? new HashSet<>() : new HashSet<>(entry.getValue()));
        }
        return output;
    }
}
