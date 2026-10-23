package save;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SaveManager {
    private static final String SAVE_FILE = "data/player_save.json";
    private static SaveData cachedData = null;
    private static java.util.function.Consumer<SaveData> preSaveMutator;

    public static void setPreSaveMutator(java.util.function.Consumer<SaveData> mutator) {
        preSaveMutator = mutator;
    }
    
    public static SaveData load() {
        if (cachedData != null) {
            return cachedData;
        }
        
        File file = new File(SAVE_FILE);
        if (!file.exists()) {
            cachedData = new SaveData();
            save(cachedData);
            return cachedData;
        }
        
        try {
            String json = new String(Files.readAllBytes(Paths.get(SAVE_FILE)));
            cachedData = fromJson(json);
            return cachedData;
        } catch (IOException e) {
            System.err.println("Error loading save: " + e.getMessage());
            cachedData = new SaveData();
            return cachedData;
        }
    }
    
    private static SaveData fromJson(String json) {
        SaveData data = new SaveData();
        Map<String, Object> map = SimpleJson.fromJson(json);
        
        if (map.containsKey("playerLevel")) data.setPlayerLevel(((Number) map.get("playerLevel")).intValue());
        if (map.containsKey("playerXP")) data.setPlayerXP(((Number) map.get("playerXP")).intValue());
        if (map.containsKey("skillPoints")) {
            data.setSkillPoints(((Number) map.get("skillPoints")).intValue());
        } else {
            // Legacy saves: grant debug pool once (then persists after next save)
            data.setSkillPoints(SaveData.DEBUG_STARTING_SKILL_POINTS);
        }
        if (map.containsKey("skillRanks")) {
            @SuppressWarnings("unchecked")
            Map<String, Number> rawRanks = (Map<String, Number>) map.get("skillRanks");
            Map<String, Integer> ranks = new HashMap<>();
            for (Map.Entry<String, Number> e : rawRanks.entrySet()) {
                ranks.put(e.getKey(), e.getValue().intValue());
            }
            data.setSkillRanks(ranks);
        } else {
            data.setSkillRanks(new HashMap<>());
        }
        if (map.containsKey("cash")) data.setCash(((Number) map.get("cash")).intValue());
        if (map.containsKey("gems")) data.setGems(((Number) map.get("gems")).intValue());
        if (map.containsKey("totalKills")) data.setTotalKills(((Number) map.get("totalKills")).intValue());
        if (map.containsKey("totalDamageDealt")) data.setTotalDamageDealt(((Number) map.get("totalDamageDealt")).intValue());
        if (map.containsKey("highestWave")) data.setHighestWave(((Number) map.get("highestWave")).intValue());
        if (map.containsKey("gamesPlayed")) data.setGamesPlayed(((Number) map.get("gamesPlayed")).intValue());
        if (map.containsKey("masteryMedals")) data.setMasteryMedals(((Number) map.get("masteryMedals")).intValue());

        if (map.containsKey("weaponMasteryXpByType")) {
            @SuppressWarnings("unchecked")
            Map<String, Number> raw = (Map<String, Number>) map.get("weaponMasteryXpByType");
            data.setWeaponMasteryXpByType(convertNumberMapToInteger(raw));
        }
        if (map.containsKey("powerMasteryXpByType")) {
            @SuppressWarnings("unchecked")
            Map<String, Number> raw = (Map<String, Number>) map.get("powerMasteryXpByType");
            data.setPowerMasteryXpByType(convertNumberMapToInteger(raw));
        }
        if (map.containsKey("weaponMasteryClaimedLevelsByType")) {
            @SuppressWarnings("unchecked")
            Map<String, List<Number>> raw = (Map<String, List<Number>>) map.get("weaponMasteryClaimedLevelsByType");
            data.setWeaponMasteryClaimedLevelsByType(convertNumberListMapToIntSetMap(raw));
        }
        if (map.containsKey("powerMasteryClaimedLevelsByType")) {
            @SuppressWarnings("unchecked")
            Map<String, List<Number>> raw = (Map<String, List<Number>>) map.get("powerMasteryClaimedLevelsByType");
            data.setPowerMasteryClaimedLevelsByType(convertNumberListMapToIntSetMap(raw));
        }
        
        if (map.containsKey("unlockedEquipment")) {
            @SuppressWarnings("unchecked")
            List<String> equipment = (List<String>) map.get("unlockedEquipment");
            data.setUnlockedEquipment(equipment);
        }
        
        if (map.containsKey("equippedLoadout")) {
            @SuppressWarnings("unchecked")
            Map<String, String> loadout = (Map<String, String>) map.get("equippedLoadout");
            data.setEquippedLoadout(loadout);
        }
        
        // Load inventory data
        if (map.containsKey("unlockedWeapons")) {
            @SuppressWarnings("unchecked")
            Map<String, List<Integer>> weapons = (Map<String, List<Integer>>) map.get("unlockedWeapons");
            data.setUnlockedWeapons(weapons);
        }
        if (map.containsKey("unlockedCharms")) {
            @SuppressWarnings("unchecked")
            Map<String, List<Integer>> charms = (Map<String, List<Integer>>) map.get("unlockedCharms");
            data.setUnlockedCharms(charms);
        }
        if (map.containsKey("unlockedPowers")) {
            @SuppressWarnings("unchecked")
            Map<String, List<Integer>> powers = (Map<String, List<Integer>>) map.get("unlockedPowers");
            data.setUnlockedPowers(powers);
        }
        if (map.containsKey("unlockedSummons")) {
            @SuppressWarnings("unchecked")
            Map<String, List<Integer>> summons = (Map<String, List<Integer>>) map.get("unlockedSummons");
            data.setUnlockedSummons(summons);
        }
        if (map.containsKey("unlockedConsumables")) {
            @SuppressWarnings("unchecked")
            Map<String, List<Integer>> consumables = (Map<String, List<Integer>>) map.get("unlockedConsumables");
            data.setUnlockedConsumables(consumables);
        }
        
        // Load stack counts
        if (map.containsKey("weaponStacks")) {
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Integer>> stacks = (Map<String, Map<String, Integer>>) map.get("weaponStacks");
            data.setWeaponStacks(convertStringKeyMapToInteger(stacks));
        }
        if (map.containsKey("charmStacks")) {
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Integer>> stacks = (Map<String, Map<String, Integer>>) map.get("charmStacks");
            data.setCharmStacks(convertStringKeyMapToInteger(stacks));
        }
        if (map.containsKey("powerStacks")) {
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Integer>> stacks = (Map<String, Map<String, Integer>>) map.get("powerStacks");
            data.setPowerStacks(convertStringKeyMapToInteger(stacks));
        }
        if (map.containsKey("summonStacks")) {
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Integer>> stacks = (Map<String, Map<String, Integer>>) map.get("summonStacks");
            data.setSummonStacks(convertStringKeyMapToInteger(stacks));
        }
        if (map.containsKey("consumableStacks")) {
            @SuppressWarnings("unchecked")
            Map<String, Map<String, Integer>> stacks = (Map<String, Map<String, Integer>>) map.get("consumableStacks");
            data.setConsumableStacks(convertStringKeyMapToInteger(stacks));
        }
        if (map.containsKey("lootboxTokens")) {
            @SuppressWarnings("unchecked")
            Map<String, Number> tokens = (Map<String, Number>) map.get("lootboxTokens");
            Map<String, Integer> parsed = new HashMap<>();
            for (Map.Entry<String, Number> e : tokens.entrySet()) {
                parsed.put(e.getKey(), e.getValue().intValue());
            }
            data.setLootboxTokens(parsed);
        }
        
        // Load loadout selections
        if (map.containsKey("selectedWeapon")) data.setSelectedWeapon((String) map.get("selectedWeapon"));
        if (map.containsKey("selectedWeaponTier")) data.setSelectedWeaponTier(((Number) map.get("selectedWeaponTier")).intValue());
        if (map.containsKey("selectedCharm")) data.setSelectedCharm((String) map.get("selectedCharm"));
        if (map.containsKey("selectedCharmTier")) data.setSelectedCharmTier(((Number) map.get("selectedCharmTier")).intValue());
        if (map.containsKey("selectedPower")) data.setSelectedPower((String) map.get("selectedPower"));
        if (map.containsKey("selectedPowerTier")) data.setSelectedPowerTier(((Number) map.get("selectedPowerTier")).intValue());
        if (map.containsKey("selectedSummon")) data.setSelectedSummon((String) map.get("selectedSummon"));
        if (map.containsKey("selectedSummonTier")) data.setSelectedSummonTier(((Number) map.get("selectedSummonTier")).intValue());
        if (map.containsKey("selectedConsumable")) data.setSelectedConsumable((String) map.get("selectedConsumable"));
        if (map.containsKey("selectedConsumableTier")) data.setSelectedConsumableTier(((Number) map.get("selectedConsumableTier")).intValue());
        
        // Load character appearance
        if (map.containsKey("appearance")) {
            @SuppressWarnings("unchecked")
            Map<String, Object> appearanceMap = (Map<String, Object>) map.get("appearance");
            player.CharacterAppearance appearance = new player.CharacterAppearance();
            if (appearanceMap.containsKey("gender")) appearance.setGender(player.CharacterAppearance.Gender.valueOf((String) appearanceMap.get("gender")));
            if (appearanceMap.containsKey("skinColorIndex")) appearance.setSkinColorIndex(((Number) appearanceMap.get("skinColorIndex")).intValue());
            if (appearanceMap.containsKey("eyeShape")) appearance.setEyeShape((String) appearanceMap.get("eyeShape"));
            if (appearanceMap.containsKey("eyeColorIndex")) appearance.setEyeColorIndex(((Number) appearanceMap.get("eyeColorIndex")).intValue());
            if (appearanceMap.containsKey("mouthExpression")) appearance.setMouthExpression((String) appearanceMap.get("mouthExpression"));
            if (appearanceMap.containsKey("hairStyle")) appearance.setHairStyle((String) appearanceMap.get("hairStyle"));
            if (appearanceMap.containsKey("hairColorIndex")) appearance.setHairColorIndex(((Number) appearanceMap.get("hairColorIndex")).intValue());
            if (appearanceMap.containsKey("faceMarkingId")) appearance.setFaceMarkingId((String) appearanceMap.get("faceMarkingId"));
            if (appearanceMap.containsKey("bodyMarkingId")) appearance.setBodyMarkingId((String) appearanceMap.get("bodyMarkingId"));
            if (appearanceMap.containsKey("equippedTopId")) appearance.setEquippedTopId((String) appearanceMap.get("equippedTopId"));
            if (appearanceMap.containsKey("equippedBottomId")) appearance.setEquippedBottomId((String) appearanceMap.get("equippedBottomId"));
            if (appearanceMap.containsKey("equippedAccessoryId")) appearance.setEquippedAccessoryId((String) appearanceMap.get("equippedAccessoryId"));
            data.setAppearance(appearance);
        }
        
        if (map.containsKey("unlockedClothingIds")) {
            @SuppressWarnings("unchecked")
            java.util.Set<String> clothing = new java.util.HashSet<>((java.util.List<String>) map.get("unlockedClothingIds"));
            data.setUnlockedClothingIds(clothing);
        }
        
        // Load clothing styles
        if (map.containsKey("unlockedClothingStyles")) {
            try {
                @SuppressWarnings("unchecked")
                Map<String, Object> stylesMap = (Map<String, Object>) map.get("unlockedClothingStyles");
                Map<String, Set<String>> unlockedStyles = new HashMap<>();
                for (Map.Entry<String, Object> entry : stylesMap.entrySet()) {
                    @SuppressWarnings("unchecked")
                    List<String> stylesList = (List<String>) entry.getValue();
                    unlockedStyles.put(entry.getKey(), new HashSet<>(stylesList));
                }
                data.setUnlockedClothingStyles(unlockedStyles);
            } catch (Exception e) {
                // If deserialization fails, keep default empty map
                System.err.println("Warning: Failed to load clothing styles: " + e.getMessage());
            }
        }
        
        if (map.containsKey("equippedClothingStyles")) {
            @SuppressWarnings("unchecked")
            Map<String, String> equipped = (Map<String, String>) map.get("equippedClothingStyles");
            data.setEquippedClothingStyles(equipped);
        }

        if (map.containsKey("unlockedProfileBanners")) {
            @SuppressWarnings("unchecked")
            List<String> banners = (List<String>) map.get("unlockedProfileBanners");
            data.setUnlockedProfileBanners(new HashSet<>(banners));
        }
        if (map.containsKey("unlockedKillEffects")) {
            @SuppressWarnings("unchecked")
            List<String> effects = (List<String>) map.get("unlockedKillEffects");
            data.setUnlockedKillEffects(new HashSet<>(effects));
        }
        if (map.containsKey("unlockedTitles")) {
            @SuppressWarnings("unchecked")
            List<String> titles = (List<String>) map.get("unlockedTitles");
            data.setUnlockedTitles(new HashSet<>(titles));
        }
        if (map.containsKey("equippedProfileBanner")) {
            data.setEquippedProfileBanner(String.valueOf(map.get("equippedProfileBanner")));
        }
        if (map.containsKey("equippedKillEffect")) {
            data.setEquippedKillEffect(String.valueOf(map.get("equippedKillEffect")));
        }
        if (map.containsKey("equippedTitle")) {
            data.setEquippedTitle(String.valueOf(map.get("equippedTitle")));
        }
        if (map.containsKey("profileUsername")) {
            data.setProfileUsername(String.valueOf(map.get("profileUsername")));
        }
        
        if (map.containsKey("unlockedArenas")) {
            @SuppressWarnings("unchecked")
            List<String> arenas = (List<String>) map.get("unlockedArenas");
            data.setUnlockedArenas(new HashSet<>(arenas));
        }
        if (map.containsKey("lastCompletedArena")) {
            Object v = map.get("lastCompletedArena");
            data.setLastCompletedArena(v == null ? null : String.valueOf(v));
        }
        if (map.containsKey("discoveredCraftingRecipeIds")) {
            @SuppressWarnings("unchecked")
            List<String> recipeIds = (List<String>) map.get("discoveredCraftingRecipeIds");
            data.setDiscoveredCraftingRecipeIds(new HashSet<>(recipeIds));
        }
        if (map.containsKey("claimedRewardIds")) {
            @SuppressWarnings("unchecked")
            List<String> claimedRewards = (List<String>) map.get("claimedRewardIds");
            data.setClaimedRewardIds(new HashSet<>(claimedRewards));
        }
        
        return data;
    }

    private static String toJson(SaveData data) {
        Map<String, Object> map = new HashMap<>();
        map.put("playerLevel", data.getPlayerLevel());
        map.put("playerXP", data.getPlayerXP());
        map.put("skillPoints", data.getSkillPointsPersisted());
        map.put("skillRanks", data.getSkillRanks());
        map.put("cash", data.getCash());
        map.put("gems", data.getGems());
        map.put("totalKills", data.getTotalKills());
        map.put("totalDamageDealt", data.getTotalDamageDealt());
        map.put("highestWave", data.getHighestWave());
        map.put("gamesPlayed", data.getGamesPlayed());
        map.put("masteryMedals", data.getMasteryMedals());
        map.put("weaponMasteryXpByType", data.getWeaponMasteryXpByType());
        map.put("powerMasteryXpByType", data.getPowerMasteryXpByType());
        map.put("weaponMasteryClaimedLevelsByType", convertIntSetMapToListMap(data.getWeaponMasteryClaimedLevelsByType()));
        map.put("powerMasteryClaimedLevelsByType", convertIntSetMapToListMap(data.getPowerMasteryClaimedLevelsByType()));
        map.put("unlockedEquipment", data.getUnlockedEquipment());
        map.put("equippedLoadout", data.getEquippedLoadout());

        map.put("unlockedWeapons", data.getUnlockedWeapons());
        map.put("unlockedCharms", data.getUnlockedCharms());
        map.put("unlockedPowers", data.getUnlockedPowers());
        map.put("unlockedSummons", data.getUnlockedSummons());
        map.put("unlockedConsumables", data.getUnlockedConsumables());

        map.put("weaponStacks", convertIntegerKeyMapToString(data.getWeaponStacks()));
        map.put("charmStacks", convertIntegerKeyMapToString(data.getCharmStacks()));
        map.put("powerStacks", convertIntegerKeyMapToString(data.getPowerStacks()));
        map.put("summonStacks", convertIntegerKeyMapToString(data.getSummonStacks()));
        map.put("consumableStacks", convertIntegerKeyMapToString(data.getConsumableStacks()));
        map.put("lootboxTokens", data.getLootboxTokensPersistedMap());

        map.put("selectedWeapon", data.getSelectedWeapon());
        map.put("selectedWeaponTier", data.getSelectedWeaponTier());
        map.put("selectedCharm", data.getSelectedCharm());
        map.put("selectedCharmTier", data.getSelectedCharmTier());
        map.put("selectedPower", data.getSelectedPower());
        map.put("selectedPowerTier", data.getSelectedPowerTier());
        map.put("selectedSummon", data.getSelectedSummon());
        map.put("selectedSummonTier", data.getSelectedSummonTier());
        map.put("selectedConsumable", data.getSelectedConsumable());
        map.put("selectedConsumableTier", data.getSelectedConsumableTier());

        // Serialize appearance (equipped clothing + body customization)
        if (data.getAppearance() != null) {
            player.CharacterAppearance a = data.getAppearance();
            Map<String, Object> appearanceMap = new HashMap<>();
            appearanceMap.put("gender", a.getGender().toString());
            appearanceMap.put("skinColorIndex", a.getSkinColorIndex());
            appearanceMap.put("eyeShape", a.getEyeShape());
            appearanceMap.put("eyeColorIndex", a.getEyeColorIndex());
            appearanceMap.put("mouthExpression", a.getMouthExpression());
            appearanceMap.put("hairStyle", a.getHairStyle());
            appearanceMap.put("hairColorIndex", a.getHairColorIndex());
            appearanceMap.put("faceMarkingId", a.getFaceMarkingId());
            appearanceMap.put("bodyMarkingId", a.getBodyMarkingId());
            appearanceMap.put("equippedTopId", a.getEquippedTopId());
            appearanceMap.put("equippedBottomId", a.getEquippedBottomId());
            appearanceMap.put("equippedAccessoryId", a.getEquippedAccessoryId());
            map.put("appearance", appearanceMap);
        }

        Map<String, List<String>> clothingStylesForJson = new HashMap<>();
        for (Map.Entry<String, Set<String>> entry : data.getUnlockedClothingStyles().entrySet()) {
            clothingStylesForJson.put(entry.getKey(), new ArrayList<>(entry.getValue()));
        }
        map.put("unlockedClothingStyles", clothingStylesForJson);
        map.put("equippedClothingStyles", data.getEquippedClothingStyles());
        map.put("unlockedClothingIds", new ArrayList<>(data.getUnlockedClothingIds()));
        map.put("unlockedProfileBanners", new ArrayList<>(data.getUnlockedProfileBanners()));
        map.put("unlockedKillEffects", new ArrayList<>(data.getUnlockedKillEffects()));
        map.put("unlockedTitles", new ArrayList<>(data.getUnlockedTitles()));
        map.put("equippedProfileBanner", data.getEquippedProfileBanner());
        map.put("equippedKillEffect", data.getEquippedKillEffect());
        map.put("equippedTitle", data.getEquippedTitle());
        map.put("profileUsername", data.getProfileUsername());
        
        // Save arena progression
        map.put("unlockedArenas", new ArrayList<>(data.getUnlockedArenas()));
        map.put("lastCompletedArena", data.getLastCompletedArena());
        map.put("discoveredCraftingRecipeIds", new ArrayList<>(data.getDiscoveredCraftingRecipeIds()));
        map.put("claimedRewardIds", new ArrayList<>(data.getClaimedRewardIds()));

        return SimpleJson.toJson(map);
    }
    
    /**
     * Convert Map<String, Map<Integer, Integer>> to Map<String, Map<String, Integer>>
     * for JSON serialization (SimpleJson expects String keys)
     */
    private static Map<String, Map<String, Integer>> convertIntegerKeyMapToString(Map<String, Map<Integer, Integer>> input) {
        Map<String, Map<String, Integer>> result = new HashMap<>();
        for (Map.Entry<String, Map<Integer, Integer>> entry : input.entrySet()) {
            Map<String, Integer> stringKeyMap = new HashMap<>();
            for (Map.Entry<Integer, Integer> innerEntry : entry.getValue().entrySet()) {
                stringKeyMap.put(innerEntry.getKey().toString(), innerEntry.getValue());
            }
            result.put(entry.getKey(), stringKeyMap);
        }
        return result;
    }
    
    /**
     * Convert Map<String, Map<String, Integer>> to Map<String, Map<Integer, Integer>>
     * for deserialization (convert String keys back to Integer keys)
     */
    private static Map<String, Map<Integer, Integer>> convertStringKeyMapToInteger(Map<String, Map<String, Integer>> input) {
        Map<String, Map<Integer, Integer>> result = new HashMap<>();
        for (Map.Entry<String, Map<String, Integer>> entry : input.entrySet()) {
            Map<Integer, Integer> intKeyMap = new HashMap<>();
            for (Map.Entry<String, Integer> innerEntry : entry.getValue().entrySet()) {
                try {
                    intKeyMap.put(Integer.parseInt(innerEntry.getKey()), innerEntry.getValue());
                } catch (NumberFormatException e) {
                    // Skip invalid keys
                }
            }
            result.put(entry.getKey(), intKeyMap);
        }
        return result;
    }

    private static Map<String, Integer> convertNumberMapToInteger(Map<String, Number> input) {
        Map<String, Integer> output = new HashMap<>();
        if (input == null) {
            return output;
        }
        for (Map.Entry<String, Number> entry : input.entrySet()) {
            output.put(entry.getKey(), entry.getValue() == null ? 0 : entry.getValue().intValue());
        }
        return output;
    }

    private static Map<String, Set<Integer>> convertNumberListMapToIntSetMap(Map<String, List<Number>> input) {
        Map<String, Set<Integer>> output = new HashMap<>();
        if (input == null) {
            return output;
        }
        for (Map.Entry<String, List<Number>> entry : input.entrySet()) {
            Set<Integer> levels = new HashSet<>();
            if (entry.getValue() != null) {
                for (Number n : entry.getValue()) {
                    if (n != null) {
                        levels.add(n.intValue());
                    }
                }
            }
            output.put(entry.getKey(), levels);
        }
        return output;
    }

    private static Map<String, List<Integer>> convertIntSetMapToListMap(Map<String, Set<Integer>> input) {
        Map<String, List<Integer>> output = new HashMap<>();
        if (input == null) {
            return output;
        }
        for (Map.Entry<String, Set<Integer>> entry : input.entrySet()) {
            List<Integer> levels = new ArrayList<>();
            if (entry.getValue() != null) {
                levels.addAll(entry.getValue());
                Collections.sort(levels);
            }
            output.put(entry.getKey(), levels);
        }
        return output;
    }
    
    public static void save(SaveData data) {
        if (preSaveMutator != null) {
            preSaveMutator.accept(data);
        }
        cachedData = data;
        
        // Ensure directory exists
        File dir = new File("data");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // Write to temp file first, then rename (prevents corruption)
        File tempFile = new File(SAVE_FILE + ".tmp");
        File finalFile = new File(SAVE_FILE);
        
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(toJson(data));
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error saving game: " + e.getMessage());
            return;
        }
        
        // Rename temp to final
        if (finalFile.exists()) {
            finalFile.delete();
        }
        tempFile.renameTo(finalFile);
    }
    
    public static void forceSave() {
        if (cachedData != null) {
            save(cachedData);
            System.out.println("Game saved!");
        }
    }
    
    public static void resetSave() {
        cachedData = new SaveData();
        File file = new File(SAVE_FILE);
        if (file.exists()) {
            file.delete();
        }
        save(cachedData);
        System.out.println("Save data reset!");
    }
    
    public static boolean exists() {
        return new File(SAVE_FILE).exists();
    }
    
    // Update methods for auto-save integration
    public static void updateCurrency(int cash, int gems) {
        if (cachedData == null) {
            load();
        }
        cachedData.setCash(cash);
        cachedData.setGems(gems);
    }

    public static int getMasteryMedals() {
        if (cachedData == null) {
            load();
        }
        return cachedData.getMasteryMedals();
    }

    public static void addMasteryMedals(int amount) {
        if (amount <= 0) {
            return;
        }
        if (cachedData == null) {
            load();
        }
        cachedData.addMasteryMedals(amount);
        save(cachedData);
    }
    
    public static void updateStats(int totalKills, int totalDamage, int highestWave, int gamesPlayed) {
        if (cachedData == null) {
            load();
        }
        cachedData.setTotalKills(totalKills);
        cachedData.setTotalDamageDealt(totalDamage);
        cachedData.setHighestWave(highestWave);
        cachedData.setGamesPlayed(gamesPlayed);
    }
    
    public static void updatePlayerLevel(int level, int xp) {
        if (cachedData == null) {
            load();
        }
        int oldLevel = cachedData.getPlayerLevel();
        cachedData.setPlayerLevel(level);
        cachedData.setPlayerXP(xp);
        for (int lv = oldLevel + 1; lv <= level; lv++) {
            cachedData.addSkillPoints(skillPointsForReachingLevel(lv));
        }
    }
    
    public static void updateEquipment(SaveData data) {
        if (cachedData == null) {
            load();
        }
        cachedData.setUnlockedEquipment(data.getUnlockedEquipment());
        cachedData.setEquippedLoadout(data.getEquippedLoadout());
    }
    
    public static void syncPlayerStats(entity.PlayerStats stats, int highestWave, int gamesPlayed) {
        if (cachedData == null) {
            load();
        }
        // Accumulate stats (add to existing totals)
        cachedData.setTotalKills(cachedData.getTotalKills() + stats.getKills());
        cachedData.setTotalDamageDealt(cachedData.getTotalDamageDealt() + (int) stats.getDamageDealt());
        if (highestWave > cachedData.getHighestWave()) {
            cachedData.setHighestWave(highestWave);
        }
        // Only increment games played if we actually played (had some activity)
        if (stats.getKills() > 0 || stats.getShotsFired() > 0) {
            cachedData.setGamesPlayed(cachedData.getGamesPlayed() + gamesPlayed);
        }
        // Save immediately when syncing stats
        save(cachedData);
    }
    
    /**
     * Skill points granted when the player reaches {@code level} (the level number after leveling up).
     * 1 by default; 2 if level ends in 5; 3 if level ends in 0 (10, 20, …).
     */
    public static int skillPointsForReachingLevel(int level) {
        if (level <= 0) {
            return 0;
        }
        int ones = level % 10;
        if (ones == 0) {
            return 3;
        }
        if (ones == 5) {
            return 2;
        }
        return 1;
    }

    public static void addXP(int xpGained) {
        if (cachedData == null) {
            load();
        }
        int currentXP = cachedData.getPlayerXP() + xpGained;
        int currentLevel = cachedData.getPlayerLevel();
        int xpNeeded = currentLevel * 100;
        
        // Level up if enough XP
        while (currentXP >= xpNeeded) {
            currentXP -= xpNeeded;
            currentLevel++;
            cachedData.addSkillPoints(skillPointsForReachingLevel(currentLevel));
            xpNeeded = currentLevel * 100;
        }
        
        cachedData.setPlayerXP(currentXP);
        cachedData.setPlayerLevel(currentLevel);
        save(cachedData);
    }
    
    // ========== LOADOUT PERSISTENCE ==========
    
    public static void saveLoadoutSelection(String selectedWeapon, int weaponTier, String selectedCharm, int charmTier,
                                            String selectedPower, int powerTier, String selectedSummon, int summonTier,
                                            String selectedConsumable, int consumableTier) {
        if (cachedData == null) {
            load();
        }
        cachedData.setSelectedWeapon(selectedWeapon);
        cachedData.setSelectedWeaponTier(weaponTier);
        cachedData.setSelectedCharm(selectedCharm);
        cachedData.setSelectedCharmTier(charmTier);
        cachedData.setSelectedPower(selectedPower);
        cachedData.setSelectedPowerTier(powerTier);
        cachedData.setSelectedSummon(selectedSummon);
        cachedData.setSelectedSummonTier(summonTier);
        cachedData.setSelectedConsumable(selectedConsumable);
        cachedData.setSelectedConsumableTier(consumableTier);
        save(cachedData);
    }
    
    public static Map<String, Object> getLoadoutSelection() {
        if (cachedData == null) {
            load();
        }
        Map<String, Object> loadout = new HashMap<>();
        loadout.put("selectedWeapon", cachedData.getSelectedWeapon());
        loadout.put("selectedWeaponTier", cachedData.getSelectedWeaponTier());
        loadout.put("selectedCharm", cachedData.getSelectedCharm());
        loadout.put("selectedCharmTier", cachedData.getSelectedCharmTier());
        loadout.put("selectedPower", cachedData.getSelectedPower());
        loadout.put("selectedPowerTier", cachedData.getSelectedPowerTier());
        loadout.put("selectedSummon", cachedData.getSelectedSummon());
        loadout.put("selectedSummonTier", cachedData.getSelectedSummonTier());
        loadout.put("selectedConsumable", cachedData.getSelectedConsumable());
        loadout.put("selectedConsumableTier", cachedData.getSelectedConsumableTier());
        return loadout;
    }
    
    // ========== INVENTORY PERSISTENCE ==========
    
    public static void saveInventory(Map<String, List<Integer>> unlockedWeapons,
                                     Map<String, List<Integer>> unlockedCharms,
                                     Map<String, List<Integer>> unlockedPowers,
                                     Map<String, List<Integer>> unlockedSummons,
                                     Map<String, List<Integer>> unlockedConsumables,
                                     Map<String, Map<Integer, Integer>> weaponStacks,
                                     Map<String, Map<Integer, Integer>> charmStacks,
                                     Map<String, Map<Integer, Integer>> powerStacks,
                                     Map<String, Map<Integer, Integer>> summonStacks,
                                     Map<String, Map<Integer, Integer>> consumableStacks) {
        if (cachedData == null) {
            load();
        }
        cachedData.setUnlockedWeapons(unlockedWeapons);
        cachedData.setUnlockedCharms(unlockedCharms);
        cachedData.setUnlockedPowers(unlockedPowers);
        cachedData.setUnlockedSummons(unlockedSummons);
        cachedData.setUnlockedConsumables(unlockedConsumables);
        
        cachedData.setWeaponStacks(weaponStacks);
        cachedData.setCharmStacks(charmStacks);
        cachedData.setPowerStacks(powerStacks);
        cachedData.setSummonStacks(summonStacks);
        cachedData.setConsumableStacks(consumableStacks);
        
        save(cachedData);
    }
    
    public static SaveData getInventoryData() {
        if (cachedData == null) {
            load();
        }
        return cachedData;
    }

    /**
     * Sell an inventory stack unit for cash.
     * Keeps tier unlock metadata for collection (Option C), but reduces actual count.
     *
     * @return true if sale succeeded and quantity was reduced.
     */
    public static boolean sellInventoryItem(String category, String itemName, int tier, int quantity, int cashPerUnit) {
        if (cachedData == null) {
            load();
        }
        if (itemName == null || itemName.isEmpty() || tier < 1 || quantity <= 0 || cashPerUnit < 0) {
            return false;
        }
        Map<String, Map<Integer, Integer>> stackMap = switch (category) {
            case "weapon" -> cachedData.getWeaponStacks();
            case "charm" -> cachedData.getCharmStacks();
            case "power" -> cachedData.getPowerStacks();
            case "summon" -> cachedData.getSummonStacks();
            case "consumable" -> cachedData.getConsumableStacks();
            default -> null;
        };
        if (stackMap == null) {
            return false;
        }
        Map<Integer, Integer> tierMap = stackMap.get(itemName);
        if (tierMap == null) {
            return false;
        }
        int current = tierMap.getOrDefault(tier, 0);
        if (current < quantity) {
            return false;
        }
        int remaining = current - quantity;
        if (remaining <= 0) {
            tierMap.remove(tier);
        } else {
            tierMap.put(tier, remaining);
        }
        if (tierMap.isEmpty()) {
            stackMap.remove(itemName);
        }
        cachedData.setCash(cachedData.getCash() + (quantity * cashPerUnit));
        save(cachedData);
        return true;
    }
    
    // ========== AUTO-SAVE FOR GAMEPLAY STATS ==========
    
    /**
     * Quick save for player stats during gameplay
     * Call this whenever kills, damage, or other stats change
     */
    public static void quickSaveStats(int totalKills, int totalDamage) {
        if (cachedData == null) {
            load();
        }
        // Update current session stats
        cachedData.setTotalKills(totalKills);
        cachedData.setTotalDamageDealt(totalDamage);
        // Don't call save here - batch saves to reduce file I/O
    }
    
    /**
     * Batch save stats after a game session ends
     */
    public static void saveSessionStats(int totalKills, int totalDamage, int highestWave, int gamesPlayed) {
        if (cachedData == null) {
            load();
        }
        cachedData.setTotalKills(totalKills);
        cachedData.setTotalDamageDealt(totalDamage);
        cachedData.setHighestWave(highestWave);
        cachedData.setGamesPlayed(gamesPlayed);
        save(cachedData);
        System.out.println("Session stats saved!");
    }
    
    /**
     * Clear the cached save data (forces reload from file on next load() call)
     */
    public static void clearCache() {
        cachedData = null;
    }
}
