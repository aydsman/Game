package ui.screens;

import combat.Item;
import combat.ranged.pistols.Pistol1;
import combat.ranged.rifles.Rifle1;
import combat.ranged.shotguns.Shotgun1;
import combat.ranged.smgs.SMG1;
import combat.ranged.snipers.Sniper1;
import combat.melee.swords.Sword1;
import combat.melee.hammers.Hammer1;
import combat.melee.daggers.Dagger1;
import combat.melee.maces.Mace1;
import combat.melee.scythes.Scythe1;
import ui.GamePanel;
import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.List;
import java.util.Map;

public class LoadoutScreen {

    private GamePanel gamePanel;
    private Rectangle backBtn = new Rectangle(50, 820, 180, 60);
    private Rectangle masteryBtn = new Rectangle(320, 100, 150, 40);
    private Rectangle skinsBtn = new Rectangle(480, 100, 150, 40);

    // Loadout slots (reorganized to avoid overlap)
    private Rectangle weaponSlot = new Rectangle(50, 150, 220, 70);
    private Rectangle charmSlot1 = new Rectangle(50, 240, 220, 70);
    private Rectangle charmSlot2 = new Rectangle(50, 330, 220, 70);  // Locked initially
    private Rectangle charmSlot3 = new Rectangle(50, 420, 220, 70);  // Locked initially
    private Rectangle powerSlot = new Rectangle(50, 510, 220, 70);
    private Rectangle summonSlot = new Rectangle(50, 600, 220, 70);
    // REMOVED: consumableSlot (consumables are no longer part of starting loadout)

    // Hover state tracking
    private String hoveredButton = null;

    // ========== UNLOCKED WEAPONS (with tiers) ==========
    // Maps weapon name -> list of unlocked tiers
    private Map<String, List<Integer>> unlockedWeapons = new HashMap<>();

    // Icon cache
    private Map<String, BufferedImage> iconCache = new HashMap<>();

    // ========== UNLOCKED OTHER ITEMS (with tiers, like weapons) ==========
    private Map<String, List<Integer>> unlockedCharms = new HashMap<>();
    private Map<String, List<Integer>> unlockedPowers = new HashMap<>();
    private Map<String, List<Integer>> unlockedSummons = new HashMap<>();

    // ========== STACK COUNTS (how many of each item+tier acquired) ==========
    private Map<String, Map<Integer, Integer>> weaponStacks = new HashMap<>();
    private Map<String, Map<Integer, Integer>> charmStacks = new HashMap<>();
    private Map<String, Map<Integer, Integer>> powerStacks = new HashMap<>();
    private Map<String, Map<Integer, Integer>> summonStacks = new HashMap<>();

    // ========== STARTING LOADOUT SELECTIONS ==========
    private String selectedWeapon = "Glock";   // Must be from unlockedWeapons
    private int selectedWeaponTier = 1;          // Weapon tier (1-5)
    private String selectedCharm = "None";
    private int selectedCharmTier = 1;           // Charm tier (1-5)
    private String selectedPower = "None";
    private int selectedPowerTier = 1;           // Power tier (1-5)
    private String selectedSummon = "None";
    private int selectedSummonTier = 1;          // Summon tier (1-5)

    // ========== UNLOCKED SLOTS ==========
    // These control how many slots player can use in loadout (meta-progression)
    private int unlockedCharmSlots = 1;    // Start with 1 charm slot

    // ========== DEBUG & UI STATE ==========
    private boolean showUnlockedItemsDebug = false; // Press U to toggle (debug only)
    private String openPicker = null; // "weapon", "charm", "power", "summon", "consumable", or null

    // Button hover states
    private Rectangle[] clickableAreas;

    public LoadoutScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        applyStarterInventoryDefaults();
        loadFromSaveData();
    }

    private void applyStarterInventoryDefaults() {
        unlockedWeapons.clear();
        unlockedCharms.clear();
        unlockedPowers.clear();
        unlockedSummons.clear();
        weaponStacks.clear();
        charmStacks.clear();
        powerStacks.clear();
        summonStacks.clear();

        // Initialize default unlocked weapons (only start with Glock and AK)
        unlockedWeapons.put("Pistol1", List.of(1));
        unlockedWeapons.put("Rifle1", List.of(1));
        incrementStack(weaponStacks, "Pistol1", 1);
        incrementStack(weaponStacks, "Rifle1", 1);

        // Charms, powers, and summons are unlocked through gameplay (loot boxes, etc.)
        // Do not add any default unlocked charms, powers, or summons
    }

    public void refreshFromSaveData() {
        applyStarterInventoryDefaults();
        loadFromSaveData();
    }
    
    /**
     * Load inventory and loadout selections from save file
     */
    private void loadFromSaveData() {
        save.SaveData saveData = save.SaveManager.load();
        
        // Load unlocked items (merge with defaults instead of replacing)
        if (saveData != null && !saveData.getUnlockedWeapons().isEmpty()) {
            // Merge saved weapons with defaults
            for (String weapon : saveData.getUnlockedWeapons().keySet()) {
                List<Integer> savedTiers = saveData.getUnlockedWeapons().get(weapon);
                List<Integer> existingTiers = new ArrayList<>(unlockedWeapons.getOrDefault(weapon, new ArrayList<>()));
                for (Integer tier : savedTiers) {
                    if (!existingTiers.contains(tier)) {
                        existingTiers.add(tier);
                    }
                }
                existingTiers.sort(null);
                unlockedWeapons.put(weapon, existingTiers);
            }
            
            // Merge charms
            if (!saveData.getUnlockedCharms().isEmpty()) {
                for (String charm : saveData.getUnlockedCharms().keySet()) {
                    List<Integer> savedTiers = saveData.getUnlockedCharms().get(charm);
                    List<Integer> existingTiers = new ArrayList<>(unlockedCharms.getOrDefault(charm, new ArrayList<>()));
                    for (Integer tier : savedTiers) {
                        if (!existingTiers.contains(tier)) {
                            existingTiers.add(tier);
                        }
                    }
                    existingTiers.sort(null);
                    unlockedCharms.put(charm, existingTiers);
                }
            }
            
            // Merge powers (preserves Infinity default)
            if (!saveData.getUnlockedPowers().isEmpty()) {
                for (String power : saveData.getUnlockedPowers().keySet()) {
                    List<Integer> savedTiers = saveData.getUnlockedPowers().get(power);
                    List<Integer> existingTiers = new ArrayList<>(unlockedPowers.getOrDefault(power, new ArrayList<>()));
                    for (Integer tier : savedTiers) {
                        if (!existingTiers.contains(tier)) {
                            existingTiers.add(tier);
                        }
                    }
                    existingTiers.sort(null);
                    unlockedPowers.put(power, existingTiers);
                }
            }
            
            // Merge summons
            if (!saveData.getUnlockedSummons().isEmpty()) {
                for (String summon : saveData.getUnlockedSummons().keySet()) {
                    List<Integer> savedTiers = saveData.getUnlockedSummons().get(summon);
                    List<Integer> existingTiers = new ArrayList<>(unlockedSummons.getOrDefault(summon, new ArrayList<>()));
                    for (Integer tier : savedTiers) {
                        if (!existingTiers.contains(tier)) {
                            existingTiers.add(tier);
                        }
                    }
                    existingTiers.sort(null);
                    unlockedSummons.put(summon, existingTiers);
                }
            }
            
            // Merge stacks
            weaponStacks.putAll(saveData.getWeaponStacks());
            charmStacks.putAll(saveData.getCharmStacks());
            powerStacks.putAll(saveData.getPowerStacks());
            summonStacks.putAll(saveData.getSummonStacks());
            
            // Load loadout selections
            selectedWeapon = saveData.getSelectedWeapon();
            selectedWeaponTier = saveData.getSelectedWeaponTier();
            selectedCharm = saveData.getSelectedCharm();
            selectedCharmTier = saveData.getSelectedCharmTier();
            selectedPower = saveData.getSelectedPower();
            selectedPowerTier = saveData.getSelectedPowerTier();
            selectedSummon = saveData.getSelectedSummon();
            selectedSummonTier = saveData.getSelectedSummonTier();
            enforceOwnedSelections();
            
            System.out.println("Loaded loadout: " + selectedWeapon + " T" + selectedWeaponTier);
        }
    }

    private void enforceOwnedSelections() {
        if (!hasUsableStack("weapon", selectedWeapon, selectedWeaponTier)) {
            selectedWeapon = "Pistol1";
            selectedWeaponTier = 1;
        }
        if (!"None".equals(selectedCharm) && !hasUsableStack("charm", selectedCharm, selectedCharmTier)) {
            selectedCharm = "None";
            selectedCharmTier = 1;
        }
        if (!"None".equals(selectedPower) && !hasUsableStack("power", selectedPower, selectedPowerTier)) {
            selectedPower = "None";
            selectedPowerTier = 1;
        }
        if (!"None".equals(selectedSummon) && !hasUsableStack("summon", selectedSummon, selectedSummonTier)) {
            selectedSummon = "None";
            selectedSummonTier = 1;
        }
    }

    public void toggleDebugPanel() {
        showUnlockedItemsDebug = !showUnlockedItemsDebug;
    }

    public void handleClick(int x, int y) {
        // Check back button first (always highest priority)
        if (backBtn.contains(x, y)) {
            openPicker = null; // Close any open picker
            gamePanel.switchScreen("inventory");
            return;
        }
        if (masteryBtn.contains(x, y)) {
            openPicker = null;
            gamePanel.switchScreen("mastery");
            return;
        }
        if (skinsBtn.contains(x, y)) {
            JOptionPane.showMessageDialog(null, "Weapon skins screen coming soon.", "Skins", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Picker open: inside = select item; another slot = switch picker; else close
        if (openPicker != null) {
            if (isClickInPickerArea(x, y)) {
                handlePickerClick(x, y);
                return;
            }
            if (tryOpenPickerFromSlotAt(x, y)) {
                return;
            }
            openPicker = null;
            return;
        }

        tryOpenPickerFromSlotAt(x, y);
    }

    /** Opens the picker for whichever slot was hit; returns true if a slot handled the click. */
    private boolean tryOpenPickerFromSlotAt(int x, int y) {
        if (weaponSlot.contains(x, y)) {
            if (!unlockedWeapons.isEmpty()) {
                openPicker = "weapon";
                return true;
            }
            return false;
        }
        if (charmSlot1.contains(x, y)) {
            openPicker = "charm";
            return true;
        }
        if (charmSlot2.contains(x, y) && unlockedCharmSlots >= 2) {
            openPicker = "charm";
            return true;
        }
        if (charmSlot3.contains(x, y) && unlockedCharmSlots >= 3) {
            openPicker = "charm";
            return true;
        }
        if (powerSlot.contains(x, y)) {
            openPicker = "power";
            return true;
        }
        if (summonSlot.contains(x, y)) {
            openPicker = "summon";
            return true;
        }
        return false;
    }

    private boolean isClickInPickerArea(int x, int y) {
        // Picker area: 800x100, width 700, height 850 (fits 5 tier sections)
        int pickerX = 800, pickerY = 100, pickerW = 700, pickerH = 850;
        return x >= pickerX && x <= pickerX + pickerW && y >= pickerY && y <= pickerY + pickerH;
    }

    private void handlePickerClick(int x, int y) {
        if (openPicker == null) return;

        // For weapons, handle weapon row and tier row separately
        if ("weapon".equals(openPicker)) {
            handleWeaponPickerClick(x, y);
            return;
        }

        // For non-weapons, handle tiered layout like weapons
        handleNonWeaponPickerClick(x, y);
    }

    private void handleNonWeaponPickerClick(int x, int y) {
        int pickerX = 800, pickerY = 100;
        int startX = pickerX + 20, startY = pickerY + 80;
        int slotSize = 70, spacing = 10;
        int itemsPerRow = 8;

        int currentY = startY;

        // Get items with their tiers
        Map<String, List<Integer>> itemsMap = getItemsForPicker(openPicker);

        // Check each tier section (same logic as weapon picker)
        for (int tier = 1; tier <= 5; tier++) {
            // Get items in this tier
            List<String> itemsInTier = new ArrayList<>();

            // Add "None" option at the beginning of Tier 1 only
            if (tier == 1) {
                itemsInTier.add("None");
            }

            for (String itemName : itemsMap.keySet()) {
                List<Integer> tiers = itemsMap.get(itemName);
                if (tiers.contains(tier) && hasUsableStack(openPicker, itemName, tier)) {
                    itemsInTier.add(itemName);
                }
            }

            if (!itemsInTier.isEmpty()) {
                // Move past tier label
                currentY += 30;

                // Check clicks in this tier's grid
                int rows = (itemsInTier.size() + itemsPerRow - 1) / itemsPerRow;
                for (int i = 0; i < itemsInTier.size(); i++) {
                    int col = i % itemsPerRow;
                    int row = i / itemsPerRow;

                    int itemX = startX + col * (slotSize + spacing);
                    int itemY = currentY + row * (slotSize + spacing);

                    if (x >= itemX && x <= itemX + slotSize && y >= itemY && y <= itemY + slotSize) {
                        String selectedItem = itemsInTier.get(i);
                        selectItemWithTier(openPicker, selectedItem, tier);
                        System.out.println("Selected: " + selectedItem + " T" + tier);
                    }
                }

                // Move to next tier section
                currentY += rows * (slotSize + spacing) + 20;
            }
        }
    }

    private void selectItemWithTier(String pickerType, String itemName, int tier) {
         switch (pickerType) {
             case "charm" -> {
                 selectedCharm = itemName;
                 selectedCharmTier = tier;
             }
             case "power" -> {
                 selectedPower = itemName;
                 selectedPowerTier = tier;
             }
             case "summon" -> {
                 selectedSummon = itemName;
                 selectedSummonTier = tier;
             }
         }
         // Auto-save loadout selection to file
         saveLoadoutToFile();
     }
    
    private void selectWeapon(String weaponName, int tier) {
        // Normalize weapon name for consistent storage
        String normalizedName = normalizeWeaponName(weaponName);
        selectedWeapon = normalizedName;
        selectedWeaponTier = tier;
        // Auto-save loadout selection to file
        saveLoadoutToFile();
        System.out.println("Selected weapon: " + normalizedName + " (T" + tier + ")");
    }
    
    /**
     * Save current loadout selection to save file
     */
    private void saveLoadoutToFile() {
        save.SaveManager.saveLoadoutSelection(
            selectedWeapon, selectedWeaponTier,
            selectedCharm, selectedCharmTier,
            selectedPower, selectedPowerTier,
            selectedSummon, selectedSummonTier,
            "None", 1  // consumable removed from loadout
        );
        System.out.println("Loadout saved: " + selectedWeapon + " T" + selectedWeaponTier);
    }
    
    /**
     * Save current inventory to save file
     */
    private void saveInventoryToFile() {
        save.SaveManager.saveInventory(
            unlockedWeapons, unlockedCharms, unlockedPowers, unlockedSummons, new HashMap<>(), // consumables removed
            weaponStacks, charmStacks, powerStacks, summonStacks, new HashMap<>() // consumableStacks removed
        );
        System.out.println("Inventory saved");
    }

    private void handleWeaponPickerClick(int x, int y) {
        int pickerX = 800, pickerY = 100;
        int startX = pickerX + 20, startY = pickerY + 80;
        int slotSize = 70, spacing = 10;
        int itemsPerRow = 8;

        // Check each tier section
        int currentY = startY;
        for (int tier = 1; tier <= 5; tier++) {
            // Get weapons that have this tier unlocked (deduplicated by class name)
            List<Item> weaponsInTier = new ArrayList<>();
            Set<String> addedClassNames = new HashSet<>();
            for (String weaponName : unlockedWeapons.keySet()) {
                List<Integer> tiers = unlockedWeapons.get(weaponName);
                if (tiers.contains(tier) && hasUsableStack("weapon", weaponName, tier)) {
                    Item weapon = createWeapon(weaponName, tier);
                    if (weapon != null) {
                        String className = weapon.getClass().getSimpleName();
                        if (!addedClassNames.contains(className)) {
                            addedClassNames.add(className);
                            weaponsInTier.add(weapon);
                        }
                    }
                }
            }

            if (!weaponsInTier.isEmpty()) {
                // Check if clicked on any weapon in this tier's grid
                for (int i = 0; i < weaponsInTier.size(); i++) {
                    int col = i % itemsPerRow;
                    int row = i / itemsPerRow;
                    int itemX = startX + col * (slotSize + spacing);
                    int itemY = currentY + row * (slotSize + spacing);

                    if (x >= itemX && x <= itemX + slotSize && y >= itemY && y <= itemY + slotSize) {
                        Item selectedItem = weaponsInTier.get(i);
                        selectWeapon(selectedItem.getName(), tier);
                        // Don't close picker - let user select different weapons
                        return;
                    }
                }

                // Update currentY for next tier section (title + grid rows)
                int rows = (weaponsInTier.size() + itemsPerRow - 1) / itemsPerRow;
                currentY += 40 + rows * (slotSize + spacing) + 20;
            }
        }
    }

    private Map<String, List<Integer>> getItemsForPicker(String pickerType) {
        return switch (pickerType) {
            case "charm" -> unlockedCharms;
            case "power" -> unlockedPowers;
            case "summon" -> unlockedSummons;
            default -> new HashMap<>();
        };
    }

    private Item createWeapon(String weaponName, int tier) {
        // First try the direct switch cases for known aliases
        Item weapon = switch (weaponName) {
            case "Glock", "Pistol1" -> new Pistol1(tier);
            case "AK-47", "Rifle1" -> new Rifle1(tier);
            case "Shotgun1", "Pump - Action" -> new Shotgun1(tier);
            case "SMG1", "P90" -> new SMG1(tier);
            case "Sniper1" -> new Sniper1(tier);
            case "Sword1" -> new Sword1(tier);
            case "Hammer1" -> new Hammer1(tier);
            case "Dagger1" -> new Dagger1(tier);
            case "Mace1" -> new Mace1(tier);
            case "Scythe1" -> new Scythe1(tier);
            default -> null;
        };
        
        // If not found in switch, try creating dynamically using reflection (like LootBox does)
        if (weapon == null) {
            try {
                Class<?> itemClass = findWeaponClass(weaponName);
                if (itemClass != null) {
                    // Try constructor with tier parameter
                    try {
                        return (Item) itemClass.getConstructor(int.class).newInstance(tier);
                    } catch (NoSuchMethodException e) {
                        // Try no-arg constructor
                        return (Item) itemClass.getConstructor().newInstance();
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to create weapon: " + weaponName + " (tier " + tier + ")");
            }
        }
        return weapon;
    }
    
    private Class<?> findWeaponClass(String weaponName) {
        String[] packages = {
            "combat.ranged.pistols.",
            "combat.ranged.rifles.",
            "combat.ranged.shotguns.",
            "combat.ranged.smgs.",
            "combat.ranged.snipers.",
            "combat.melee.swords.",
            "combat.melee.hammers.",
            "combat.melee.daggers.",
            "combat.melee.maces.",
            "combat.melee.scythes."
        };
        
        for (String pkg : packages) {
            try {
                return Class.forName(pkg + weaponName);
            } catch (ClassNotFoundException e) {
                // Continue searching
            }
        }
        return null;
    }

    private Item createConsumable(String consumableName, int tier) {
        // Map consumable names to their classes
        Item consumable = switch (consumableName) {
            case "Small Health Potion" -> new combat.consumables.Consumable1(tier);
            case "Medium Health Potion" -> new combat.consumables.Consumable2(tier);
            case "Large Health Potion" -> new combat.consumables.Consumable3(tier);
            case "Small Damage Potion" -> new combat.consumables.DamagePotion(tier);
            case "Medium Damage Potion" -> new combat.consumables.DamagePotion2(tier);
            case "Large Damage Potion" -> new combat.consumables.DamagePotion3(tier);
            case "Small Speed Potion" -> new combat.consumables.SpeedPotion1(tier);
            case "Medium Speed Potion" -> new combat.consumables.SpeedPotion2(tier);
            case "Large Speed Potion" -> new combat.consumables.SpeedPotion3(tier);
            default -> null;
        };
        
        // If not found in switch, try creating dynamically using reflection
        if (consumable == null) {
            try {
                Class<?> itemClass = findConsumableClass(consumableName);
                if (itemClass != null) {
                    // Try constructor with tier parameter
                    try {
                        return (Item) itemClass.getConstructor(int.class).newInstance(tier);
                    } catch (NoSuchMethodException e) {
                        // Try no-arg constructor
                        return (Item) itemClass.getConstructor().newInstance();
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to create consumable: " + consumableName + " (tier " + tier + ")");
            }
        }
        return consumable;
    }
    
    private Class<?> findConsumableClass(String consumableName) {
        String[] packages = {
            "combat.consumables."
        };
        
        // Try different naming conventions
        String[] possibleNames = {
            consumableName.replace(" ", ""),
            consumableName.replace(" ", "").toLowerCase(),
            consumableName.replace(" ", "_").toLowerCase()
        };
        
        for (String pkg : packages) {
            for (String name : possibleNames) {
                try {
                    return Class.forName(pkg + name);
                } catch (ClassNotFoundException e) {
                    // Continue searching
                }
            }
        }
        return null;
    }

    private BufferedImage loadIcon(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        if (iconCache.containsKey(path)) {
            return iconCache.get(path);
        }
        try {
            BufferedImage img = ImageIO.read(new File(path));
            iconCache.put(path, img);
            return img;
        } catch (IOException e) {
            return null;
        }
    }

    private void selectItem(String pickerType, String item) {
         switch (pickerType) {
             case "weapon" -> selectedWeapon = item;
             case "charm" -> selectedCharm = item;
             case "power" -> selectedPower = item;
             case "summon" -> selectedSummon = item;
         }
         System.out.println("Selected " + pickerType + ": " + item);
     }

    public void draw(Graphics2D g, int width, int height) {
        updateHoverState();

        // Modern gradient background
        GradientPaint bgGradient = new GradientPaint(0, 0, new Color(30, 30, 40), 0, height, new Color(20, 20, 30));
        g.setPaint(bgGradient);
        g.fillRect(0, 0, width, height);

        // Add subtle pattern overlay
        drawBackgroundPattern(g, width, height);

        // Title with glow effect
        g.setColor(new Color(100, 200, 255));
        g.setFont(new Font("Segoe UI", Font.BOLD, 42));
        g.drawString("Starting Loadout", 50, 70);

        // Subtitle
        g.setColor(new Color(150, 150, 170));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g.drawString("Choose your equipment for the battle", 50, 95);

        // Section label
        g.setColor(new Color(180, 180, 200));
        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g.drawString("EQUIPMENT", 50, 130);

        drawStyledButton(g, masteryBtn, "Mastery", new Color(129, 111, 211), "mastery".equals(hoveredButton));
        drawStyledButton(g, skinsBtn, "Skins", new Color(205, 122, 73), "skins".equals(hoveredButton));

        // Draw main slots with modern styling
        String weaponDisplay = selectedWeapon + " (T" + selectedWeaponTier + ")";
        drawStyledSlot(g, weaponSlot, "Weapon", weaponDisplay, new Color(231, 76, 60), "weapon".equals(hoveredButton));
        drawStyledSlot(g, charmSlot1, "Charm 1", selectedCharm, new Color(147, 112, 219), "charm1".equals(hoveredButton));
        drawStyledSlot(g, powerSlot, "Power", selectedPower, new Color(52, 152, 219), "power".equals(hoveredButton));
        drawStyledSlot(g, summonSlot, "Summon", selectedSummon, new Color(46, 204, 113), "summon".equals(hoveredButton));

        // Draw locked/unlocked charm slots 2 & 3
        if (unlockedCharmSlots >= 2) {
            drawStyledSlot(g, charmSlot2, "Charm 2", selectedCharm, new Color(147, 112, 219), "charm2".equals(hoveredButton));
        } else {
            drawStyledLockedSlot(g, charmSlot2, "Charm 2");
        }

        if (unlockedCharmSlots >= 3) {
            drawStyledSlot(g, charmSlot3, "Charm 3", selectedCharm, new Color(147, 112, 219), "charm3".equals(hoveredButton));
        } else {
            drawStyledLockedSlot(g, charmSlot3, "Charm 3");
        }


        // Draw "Unlocked Items" panel on the right (DEBUG ONLY - press U to toggle)
        if (showUnlockedItemsDebug) {
            drawUnlockedItemsPanel(g, 800, 100);
        }

        // Draw item picker if open
        if (openPicker != null) {
            drawItemPicker(g, openPicker);
        }

        // Debug hint
        if (!showUnlockedItemsDebug) {
            g.setColor(new Color(100, 100, 120));
            g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            g.drawString("Press U to show unlocked items (debug)", 800, 100);
        }

        // Back button
        drawStyledButton(g, backBtn, "Back", new Color(149, 165, 166), "back".equals(hoveredButton));
    }

    private void updateHoverState() {
        if (gamePanel == null) {
            hoveredButton = null;
            return;
        }
        int mx = gamePanel.getMouseX();
        int my = gamePanel.getMouseY();
        hoveredButton = null;

        if (backBtn.contains(mx, my)) hoveredButton = "back";
        if (masteryBtn.contains(mx, my)) hoveredButton = "mastery";
        if (skinsBtn.contains(mx, my)) hoveredButton = "skins";

        if (weaponSlot.contains(mx, my)) hoveredButton = "weapon";
        if (charmSlot1.contains(mx, my)) hoveredButton = "charm1";
        if (powerSlot.contains(mx, my)) hoveredButton = "power";
        if (summonSlot.contains(mx, my)) hoveredButton = "summon";
        if (unlockedCharmSlots >= 2 && charmSlot2.contains(mx, my)) hoveredButton = "charm2";
        if (unlockedCharmSlots >= 3 && charmSlot3.contains(mx, my)) hoveredButton = "charm3";
    }

    private void drawBackgroundPattern(Graphics2D g, int width, int height) {
        g.setColor(new Color(255, 255, 255, 5));
        for (int i = 0; i < width; i += 40) {
            g.drawLine(i, 0, i, height);
        }
        for (int i = 0; i < height; i += 40) {
            g.drawLine(0, i, width, i);
        }
    }

    private void drawItemPicker(Graphics2D g, String pickerType) {
        // Picker background with modern styling
        int pickerX = 800, pickerY = 100, pickerW = 700, pickerH = 850;
        g.setColor(new Color(0, 0, 0, 200));
        g.fillRoundRect(pickerX, pickerY, pickerW, pickerH, 16, 16);

        // Gradient border
        GradientPaint borderGradient = new GradientPaint(
            pickerX, pickerY, new Color(100, 200, 255),
            pickerX + pickerW, pickerY + pickerH, new Color(147, 112, 219)
        );
        g.setPaint(borderGradient);
        g.setStroke(new BasicStroke(3));
        g.drawRoundRect(pickerX, pickerY, pickerW, pickerH, 16, 16);
        g.setStroke(new BasicStroke(1));
        g.setPaint(null);

        // Title with styling
        g.setColor(new Color(100, 200, 255));
        g.setFont(new Font("Segoe UI", Font.BOLD, 28));
        String title = "Select " + pickerType.substring(0, 1).toUpperCase() + pickerType.substring(1);
        g.drawString(title, pickerX + 30, pickerY + 40);

        // Subtitle
        g.setColor(new Color(150, 150, 170));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        g.drawString("Click an item to equip it", pickerX + 30, pickerY + 65);

        // For weapons, show gallery-style grid organized by tier
        if ("weapon".equals(pickerType)) {
            drawWeaponPickerGrid(g, pickerX, pickerY);
        } else {
            // Grid of items (for non-weapons) - same style as weapon picker
            drawNonWeaponPickerGrid(g, pickerType, pickerX, pickerY);
        }

        // Click to select hint
        g.setColor(new Color(100, 100, 120));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        if ("weapon".equals(pickerType)) {
            g.drawString("Click any weapon to select, click outside to cancel", pickerX + 30, pickerY + pickerH - 20);
        } else {
            g.drawString("Click an item to select, click outside to cancel", pickerX + 30, pickerY + pickerH - 20);
        }
    }

    private void drawWeaponPickerGrid(Graphics2D g, int pickerX, int pickerY) {
        int startX = pickerX + 30, startY = pickerY + 90;
        int slotSize = 70, spacing = 12;
        int itemsPerRow = 8;

        int currentY = startY;

        // Draw each tier section with its own grid
        for (int tier = 1; tier <= 5; tier++) {
            // Get all weapons that have this tier unlocked (deduplicated by class name)
            List<Item> weaponsInTier = new ArrayList<>();
            Set<String> addedClassNames = new HashSet<>();
            for (String weaponName : unlockedWeapons.keySet()) {
                List<Integer> tiers = unlockedWeapons.get(weaponName);
                if (tiers.contains(tier) && hasUsableStack("weapon", weaponName, tier)) {
                    Item weapon = createWeapon(weaponName, tier);
                    if (weapon != null) {
                        String className = weapon.getClass().getSimpleName();
                        if (!addedClassNames.contains(className)) {
                            addedClassNames.add(className);
                            weaponsInTier.add(weapon);
                        }
                    }
                }
            }

            // Only draw this tier section if there are weapons
            if (!weaponsInTier.isEmpty()) {
                // Tier label with color
                g.setFont(new Font("Segoe UI", Font.BOLD, 20));
                Color tierColor = switch (tier) {
                    case 1 -> new Color(200, 200, 200); // Common
                    case 2 -> new Color(100, 200, 100); // Uncommon
                    case 3 -> new Color(100, 150, 255); // Rare
                    case 4 -> new Color(200, 100, 255); // Epic
                    case 5 -> new Color(255, 215, 0); // Legendary
                    default -> Color.WHITE;
                };
                g.setColor(tierColor);
                g.drawString("Tier " + tier, startX, currentY);
                currentY += 30;

                // Draw grid for this tier (same as ItemGalleryScreen)
                for (int i = 0; i < weaponsInTier.size(); i++) {
                    Item weapon = weaponsInTier.get(i);
                    int col = i % itemsPerRow;
                    int row = i / itemsPerRow;

                    int itemX = startX + col * (slotSize + spacing);
                    int itemY = currentY + row * (slotSize + spacing);

                    // Check if this weapon is currently selected
                    boolean isSelected = normalizeWeaponName(weapon.getName()).equals(selectedWeapon) && tier == selectedWeaponTier;

                    // Draw slot background with rounded corners
                    if (isSelected) {
                        GradientPaint gradient = new GradientPaint(itemX, itemY, new Color(100, 150, 200), itemX, itemY + slotSize, new Color(50, 100, 150));
                        g.setPaint(gradient);
                    } else {
                        GradientPaint gradient = new GradientPaint(itemX, itemY, new Color(50, 50, 60), itemX, itemY + slotSize, new Color(40, 40, 50));
                        g.setPaint(gradient);
                    }
                    g.fillRoundRect(itemX, itemY, slotSize, slotSize, 8, 8);
                    g.setPaint(null);

// Draw weapon icon FIRST
                    BufferedImage icon = loadIcon(weapon.getIconPath());
                    if (icon != null) {
                        int maxWidth = slotSize - 4;
                        int maxHeight = slotSize - 14;
                        double scaleX = (double) maxWidth / icon.getWidth();
                        double scaleY = (double) maxHeight / icon.getHeight();
                        double scale = Math.min(scaleX, scaleY);
                        int drawWidth = (int) (icon.getWidth() * scale);
                        int drawHeight = (int) (icon.getHeight() * scale);
                        int drawX = itemX + (slotSize - drawWidth) / 2;
                        int drawY = itemY + (slotSize - 14 - drawHeight) / 2;
                        g.drawImage(icon, drawX, drawY, drawWidth, drawHeight, null);
                    } else {
                        g.setColor(new Color(200, 100, 100));
                        g.fillRect(itemX + 10, itemY + 10, slotSize - 20, slotSize - 20);
                    }

                    // Draw border ON TOP of icon
                    if (isSelected) {
                        // Thick glowing border
                        g.setColor(new Color(100, 200, 255));
                        g.setStroke(new BasicStroke(4));
                        g.drawRoundRect(itemX + 1, itemY + 1, slotSize - 2, slotSize - 2, 8, 8);
                        // Semi-transparent blue overlay so selection is visible even over icons
                        g.setColor(new Color(100, 200, 255, 70));
                        g.fillRoundRect(itemX, itemY, slotSize, slotSize, 8, 8);
                    } else {
                        g.setColor(new Color(70, 70, 80));
                        g.setStroke(new BasicStroke(2));
                        g.drawRoundRect(itemX, itemY, slotSize, slotSize, 8, 8);
                    }
                    g.setStroke(new BasicStroke(1));

                    // Draw weapon name (abbreviated)
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Arial", Font.PLAIN, 10));
                    String name = weapon.getName();
                    if (name.length() > 8) {
                        name = name.substring(0, 8) + ".";
                    }
                    g.drawString(name, itemX + 2, itemY + slotSize - 2);

                    // Draw stack count at top right (if > 1)
                    String weaponClassName = weapon.getClass().getSimpleName();
                    int stackCount = getStackCount("weapon", weaponClassName, tier);
                    if (stackCount > 1) {
                        String stackText = "x" + stackCount;
                        g.setFont(new Font("Arial", Font.BOLD, 12));
                        FontMetrics fm = g.getFontMetrics();
                        int textWidth = fm.stringWidth(stackText);
                        // Draw small background for visibility
                        g.setColor(new Color(0, 0, 0, 180));
                        g.fillRect(itemX + slotSize - textWidth - 4, itemY, textWidth + 4, 14);
                        // Draw text
                        g.setColor(Color.YELLOW);
                        g.drawString(stackText, itemX + slotSize - textWidth - 2, itemY + 12);
                    }
                }

                // Move to next tier section
                int rows = (weaponsInTier.size() + itemsPerRow - 1) / itemsPerRow;
                currentY += rows * (slotSize + spacing) + 20;
            }
        }
    }

    private void drawNonWeaponPickerGrid(Graphics2D g, String pickerType, int pickerX, int pickerY) {
        int startX = pickerX + 30, startY = pickerY + 90;
        int slotSize = 70, spacing = 12;
        int itemsPerRow = 8;

        int currentY = startY;

        // Get items with their tiers
        Map<String, List<Integer>> itemsMap = getItemsForPicker(pickerType);

        // Draw each tier section (same as weapon picker)
        for (int tier = 1; tier <= 5; tier++) {
            // Get all items that have this tier unlocked
            List<String> itemsInTier = new ArrayList<>();
            
            // Add "None" option at the beginning of Tier 1 only
            if (tier == 1) {
                itemsInTier.add("None");
            }
            
            for (String itemName : itemsMap.keySet()) {
                List<Integer> tiers = itemsMap.get(itemName);
                if (tiers.contains(tier) && hasUsableStack(pickerType, itemName, tier)) {
                    itemsInTier.add(itemName);
                }
            }

            // Only draw this tier section if there are items
            if (!itemsInTier.isEmpty()) {
                // Tier label with color
                g.setFont(new Font("Segoe UI", Font.BOLD, 20));
                Color tierColor = switch (tier) {
                    case 1 -> new Color(200, 200, 200); // Common
                    case 2 -> new Color(100, 200, 100); // Uncommon
                    case 3 -> new Color(100, 150, 255); // Rare
                    case 4 -> new Color(200, 100, 255); // Epic
                    case 5 -> new Color(255, 215, 0); // Legendary
                    default -> Color.WHITE;
                };
                g.setColor(tierColor);
                g.drawString("Tier " + tier, startX, currentY);
                currentY += 30;

                // Draw grid for this tier
                for (int i = 0; i < itemsInTier.size(); i++) {
                    String itemName = itemsInTier.get(i);
                    int col = i % itemsPerRow;
                    int row = i / itemsPerRow;

                    int itemX = startX + col * (slotSize + spacing);
                    int itemY = currentY + row * (slotSize + spacing);

                    // Check if this item/tier is currently selected
                    boolean isSelected = isItemSelected(pickerType, itemName, tier);

                    // Draw slot background with rounded corners
                    if (isSelected) {
                        GradientPaint gradient = new GradientPaint(itemX, itemY, new Color(100, 150, 200), itemX, itemY + slotSize, new Color(50, 100, 150));
                        g.setPaint(gradient);
                    } else {
                        GradientPaint gradient = new GradientPaint(itemX, itemY, new Color(50, 50, 60), itemX, itemY + slotSize, new Color(40, 40, 50));
                        g.setPaint(gradient);
                    }
                    g.fillRoundRect(itemX, itemY, slotSize, slotSize, 8, 8);
                    g.setPaint(null);

                    // Border
                    if (isSelected) {
                        // Thick glowing border
                        g.setColor(new Color(100, 200, 255));
                        g.setStroke(new BasicStroke(4));
                        g.drawRoundRect(itemX + 1, itemY + 1, slotSize - 2, slotSize - 2, 8, 8);
                        // Semi-transparent blue overlay so selection is visible even over icons
                        g.setColor(new Color(100, 200, 255, 70));
                        g.fillRoundRect(itemX, itemY, slotSize, slotSize, 8, 8);
                    } else {
                        g.setColor(new Color(70, 70, 80));
                        g.setStroke(new BasicStroke(2));
                        g.drawRoundRect(itemX, itemY, slotSize, slotSize, 8, 8);
                    }
                    g.setStroke(new BasicStroke(1));

                    // Try to load item icon
                    BufferedImage icon = null;
                    if ("None".equals(itemName)) {
                        // Draw a simple X for "None"
                        g.setColor(new Color(150, 150, 150));
                        g.drawLine(itemX + 15, itemY + 15, itemX + slotSize - 15, itemY + slotSize - 15);
                        g.drawLine(itemX + slotSize - 15, itemY + 15, itemX + 15, itemY + slotSize - 15);
                    } else if ("consumable".equals(pickerType)) {
                        // For consumables, create the instance to get the iconPath
                        Item consumable = createConsumable(itemName, tier);
                        if (consumable != null) {
                            icon = loadIcon(consumable.getIconPath());
                        }
                    } else if ("charm".equals(pickerType)) {
                        // For charms, create the instance to get the iconPath
                        Item charm = createCharm(itemName, tier);
                        if (charm != null) {
                            icon = loadIcon(charm.getIconPath());
                        }
                    } else {
                        String iconPath = getItemIconPath(pickerType, itemName);
                        icon = loadIcon(iconPath);
                    }
                    
                    if (icon != null) {
                        int maxWidth = slotSize - 4;
                        int maxHeight = slotSize - 14;
                        double scaleX = (double) maxWidth / icon.getWidth();
                        double scaleY = (double) maxHeight / icon.getHeight();
                        double scale = Math.min(scaleX, scaleY);
                        int drawWidth = (int) (icon.getWidth() * scale);
                        int drawHeight = (int) (icon.getHeight() * scale);
                        int drawX = itemX + (slotSize - drawWidth) / 2;
                        int drawY = itemY + (slotSize - 14 - drawHeight) / 2;
                        g.drawImage(icon, drawX, drawY, drawWidth, drawHeight, null);
                    } else if (!"None".equals(itemName)) {
                        // Fallback colored square based on item type (not for None)
                        Color fallbackColor = switch (pickerType) {
                            case "charm" -> new Color(100, 50, 150);
                            case "power" -> new Color(50, 100, 150);
                            case "summon" -> new Color(50, 150, 100);
                            case "consumable" -> new Color(150, 100, 50);
                            default -> new Color(100, 100, 100);
                        };
                        g.setColor(fallbackColor);
                        g.fillRoundRect(itemX + 10, itemY + 10, slotSize - 20, slotSize - 20, 4, 4);
                    }

                    // Draw item name (abbreviated)
                    g.setColor(Color.WHITE);
                    g.setFont(new Font("Segoe UI", Font.PLAIN, 10));
                    String name = itemName;
                    if (name.length() > 8) {
                        name = name.substring(0, 8) + ".";
                    }
                    g.drawString(name, itemX + 2, itemY + slotSize - 2);

                    // Draw stack count at top right (if > 1)
                    int stackCount = getStackCount(pickerType, itemName, tier);
                    if (stackCount > 1) {
                        String stackText = "x" + stackCount;
                        g.setFont(new Font("Segoe UI", Font.BOLD, 12));
                        FontMetrics fm = g.getFontMetrics();
                        int textWidth = fm.stringWidth(stackText);
                        // Draw small background for visibility
                        g.setColor(new Color(0, 0, 0, 180));
                        g.fillRoundRect(itemX + slotSize - textWidth - 4, itemY, textWidth + 4, 14, 4, 4);
                        // Draw text
                        g.setColor(Color.YELLOW);
                        g.drawString(stackText, itemX + slotSize - textWidth - 2, itemY + 12);
                    }
                }

                // Move to next tier section
                int rows = (itemsInTier.size() + itemsPerRow - 1) / itemsPerRow;
                currentY += rows * (slotSize + spacing) + 20;
            }
        }
    }

    private String getItemIconPath(String itemType, String itemName) {
        // Map item names to their icon paths
        return switch (itemType) {
            case "charm" -> getCharmIconPath(itemName);
            case "power" -> getPowerIconPath(itemName);
            case "summon" -> "assets/items/summons/" + itemName.toLowerCase() + ".png";
            case "consumable" -> getConsumableIconPath(itemName);
            default -> null;
        };
    }

    private String getConsumableIconPath(String consumableName) {
        // Consumables have icon paths defined in their classes
        return switch (consumableName) {
            case "Small Health Potion" -> "assets/items/consumables/potions/health/small_health_potion.png";
            case "Medium Health Potion" -> "assets/items/consumables/potions/health/medium_health_potion.png";
            case "Large Health Potion" -> "assets/items/consumables/potions/health/large_health_potion.png";
            case "Small Damage Potion" -> "assets/items/consumables/potions/damage/small_damage_potion.png";
            case "Medium Damage Potion" -> "assets/items/consumables/potions/damage/medium_damage_potion.png";
            case "Large Damage Potion" -> "assets/items/consumables/potions/damage/large_damage_potion.png";
            case "Small Speed Potion" -> "assets/items/consumables/potions/speed/small_speed_potion.png";
            case "Medium Speed Potion" -> "assets/items/consumables/potions/speed/medium_speed_potion.png";
            case "Large Speed Potion" -> "assets/items/consumables/potions/speed/large_speed_potion.png";
            default -> "assets/items/consumables/" + consumableName.toLowerCase().replace(" ", "_") + ".png";
        };
    }
    
    private String getPowerIconPath(String powerName) {
        // Powers have different path structures - map each power to its correct path
        return switch (powerName) {
            case "Fire" -> "assets/items/powers/fire/fire_icon.png";
            case "Water" -> "assets/items/powers/water/water_icon.png";
            case "Earth" -> "assets/items/powers/earth/earth_icon.png";
            case "Light" -> "assets/items/powers/light/light_icon.png";
            case "Lightning" -> "assets/items/powers/lightning/lightning_icon.png";
            case "Magma" -> "assets/items/powers/magma/magma_icon.png";
            case "Overgrowth" -> "assets/items/powers/overgrowth/overgrowth_icon.png";
            case "Shadow" -> "assets/items/powers/shadow/shadow_icon.png";
            case "Crystal" -> "assets/items/powers/crystal/crystal_icon.png";
            case "Storm" -> "assets/items/powers/storm/storm_icon.png";
            case "Metal" -> "assets/items/powers/metal/metal_icon.png";
            case "Vessel" -> "assets/items/powers/vessel/vessel_icon.png";
            case "Limitless" -> "assets/items/powers/limitless/limitless_icon.png";
            case "Plasma" -> "assets/items/powers/plasma/plasma_icon.png";
            case "Eclipse" -> "assets/items/powers/eclipse/eclipse_icon.png";
            case "Nightbloom" -> "assets/items/powers/nightbloom/nightbloom_icon.png";
            case "Cataclysm" -> "assets/items/powers/cataclysm/cataclysm_icon.png";
            case "Haki" -> "assets/items/powers/haki/haki_icon.png";
            case "FireV2" -> "assets/items/powers/fire/fire_icon.png";
            case "WaterV2" -> "assets/items/powers/water/water_icon.png";
            case "EarthV2" -> "assets/items/powers/earth/earth_icon.png";
            case "LightningV2" -> "assets/items/powers/lightning/lightning_icon.png";
            case "MagmaV2" -> "assets/items/powers/magma/magma_icon.png";
            case "Sharingan" -> "assets/items/powers/sharingan/sharingan_icon.png";
            case "Rinnegan" -> "assets/items/powers/rinnegan/rinnegan_icon.png";
            case "Rinne Sharingan", "RinneSharingan" -> "assets/items/powers/rine-sharingan/rinnesharingan_icon.png";
            case "Infinity" -> "assets/items/powers/infinity/infinity_icon.png";
            case "King of Curses", "KingOfCurses" -> "assets/items/powers/king of curses/king_of_curses_icon.png";
            case "BombFruit" -> "assets/items/powers/fruits/bomb_icon.png";
            case "SmokeFruit" -> "assets/items/powers/fruits/smoke_icon.png";
            case "GasFruit" -> "assets/items/powers/fruits/gas_icon.png";
            case "StringFruit" -> "assets/items/powers/fruits/string_icon.png";
            case "SandFruit" -> "assets/items/powers/fruits/sand_icon.png";
            case "RubberFruit" -> "assets/items/powers/fruits/rubber_icon.png";
            case "VenomFruit" -> "assets/items/powers/fruits/venom_icon.png";
            case "MagnetFruit" -> "assets/items/powers/fruits/magnet_icon.png";
            case "GravityFruit" -> "assets/items/powers/fruits/gravity_fruit.png";
            case "MochiFruit" -> "assets/items/powers/fruits/mochi_icon.png";
            case "OpOpFruit" -> "assets/items/powers/fruits/op_icon.png";
            case "LeopardFruit" -> "assets/items/powers/fruits/leopard_fruit.png";
            case "QuakeFruit" -> "assets/items/powers/fruits/quake_icon.png";
            case "NikaFruit" -> "assets/items/powers/fruits/nika_icon.png";
            default -> "assets/items/powers/" + powerName.toLowerCase() + "/" + powerName.toLowerCase() + "_icon.png";
        };
    }
    
    private String getCharmIconPath(String charmName) {
        // Charms have different path structures - map each charm to its correct path
        return switch (charmName) {
            case "Simple Health Charm" -> "assets/items/charms/health/simple_health_charm.png";
            case "Health Charm" -> "assets/items/charms/health/health_charm.png";
            case "Advanced Health Charm" -> "assets/items/charms/health/advanced_health_charm.png";
            case "Simple Speed Charm" -> "assets/items/charms/speed/simple_speed_charm.png";
            case "Speed Charm" -> "assets/items/charms/speed/speed_charm.png";
            case "Advanced Speed Charm" -> "assets/items/charms/speed/advanced_speed_charm.png";
            default -> "assets/items/charms/" + charmName.toLowerCase().replace(" ", "_") + ".png";
        };
    }
    
    private boolean isItemSelected(String pickerType, String item, int tier) {
         return switch (pickerType) {
             case "weapon" -> selectedWeapon.equals(item) && selectedWeaponTier == tier;
             case "charm" -> selectedCharm.equals(item) && selectedCharmTier == tier;
             case "power" -> selectedPower.equals(item) && selectedPowerTier == tier;
             case "summon" -> selectedSummon.equals(item) && selectedSummonTier == tier;
             default -> false;
         };
     }

    private void drawUnlockedItemsPanel(Graphics2D g, int x, int y) {
        // Panel background with modern styling
        g.setColor(new Color(40, 40, 50));
        g.fillRoundRect(x, y, 350, 400, 12, 12);
        
        // Gradient border
        GradientPaint borderGradient = new GradientPaint(x, y, new Color(100, 200, 255), x + 350, y + 400, new Color(147, 112, 219));
        g.setPaint(borderGradient);
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(x, y, 350, 400, 12, 12);
        g.setStroke(new BasicStroke(1));
        g.setPaint(null);

        // Title
        g.setColor(new Color(100, 200, 255));
        g.setFont(new Font("Segoe UI", Font.BOLD, 18));
        g.drawString("UNLOCKED ITEMS", x + 15, y + 30);

        // List items by category
        g.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        int lineY = y + 60;
        int lineHeight = 22;

        // Weapons
        g.setColor(new Color(255, 215, 0));
        g.drawString("Weapons:", x + 15, lineY);
        lineY += lineHeight;
        g.setColor(Color.WHITE);
        if (unlockedWeapons.isEmpty()) {
            g.drawString("  None", x + 15, lineY);
            lineY += lineHeight;
        } else {
            for (String weaponName : unlockedWeapons.keySet()) {
                List<Integer> tiers = unlockedWeapons.get(weaponName);
                String tierStr = tiers.toString().replace("[", "T").replace("]", "").replace(", ", ",T");
                g.drawString("  - " + weaponName + " (" + tierStr + ")", x + 15, lineY);
                lineY += lineHeight;
            }
        }
        lineY += 10;

        // Charms
        g.setColor(Color.YELLOW);
        g.drawString("Charms:", x + 10, lineY);
        lineY += lineHeight;
        g.setColor(Color.WHITE);
        if (unlockedCharms.isEmpty()) {
            g.drawString("  None", x + 10, lineY);
            lineY += lineHeight;
        } else {
            for (Map.Entry<String, List<Integer>> entry : unlockedCharms.entrySet()) {
                g.drawString("  - " + entry.getKey() + " (T" + entry.getValue() + ")", x + 10, lineY);
                lineY += lineHeight;
            }
        }
        lineY += 10;

        // Powers
        g.setColor(Color.YELLOW);
        g.drawString("Powers:", x + 10, lineY);
        lineY += lineHeight;
        g.setColor(Color.WHITE);
        if (unlockedPowers.isEmpty()) {
            g.drawString("  None", x + 10, lineY);
            lineY += lineHeight;
        } else {
            for (Map.Entry<String, List<Integer>> entry : unlockedPowers.entrySet()) {
                g.drawString("  - " + entry.getKey() + " (T" + entry.getValue() + ")", x + 10, lineY);
                lineY += lineHeight;
            }
        }
        lineY += 10;

        // Summons
        g.setColor(Color.YELLOW);
        g.drawString("Summons:", x + 10, lineY);
        lineY += lineHeight;
        g.setColor(Color.WHITE);
        if (unlockedSummons.isEmpty()) {
            g.drawString("  None", x + 10, lineY);
            lineY += lineHeight;
        } else {
            for (Map.Entry<String, List<Integer>> entry : unlockedSummons.entrySet()) {
                g.drawString("  - " + entry.getKey() + " (T" + entry.getValue() + ")", x + 10, lineY);
                lineY += lineHeight;
            }
        }
    }

    private void drawStyledLockedSlot(Graphics2D g, Rectangle slot, String label) {
        // Locked slot background
        g.setColor(new Color(35, 35, 40));
        g.fillRoundRect(slot.x, slot.y, slot.width, slot.height, 12, 12);
        
        // Border
        g.setColor(new Color(80, 80, 90));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(slot.x, slot.y, slot.width, slot.height, 12, 12);
        g.setStroke(new BasicStroke(1));

        // Label
        g.setColor(new Color(150, 150, 170));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.drawString(label, slot.x + 15, slot.y + 25);

        // LOCKED text with modern styling
        g.setColor(new Color(200, 100, 100));
        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        g.drawString("LOCKED", slot.x + 15, slot.y + 48);
    }

    private void drawStyledSlot(Graphics2D g, Rectangle slot, String label, String equippedItem, Color accentColor, boolean isHovered) {
        // Slot background with gradient
        if (isHovered) {
            GradientPaint gradient = new GradientPaint(slot.x, slot.y, accentColor.brighter(), slot.x, slot.y + slot.height, accentColor);
            g.setPaint(gradient);
        } else {
            GradientPaint gradient = new GradientPaint(slot.x, slot.y, new Color(50, 50, 60), slot.x, slot.y + slot.height, new Color(40, 40, 50));
            g.setPaint(gradient);
        }
        g.fillRoundRect(slot.x, slot.y, slot.width, slot.height, 12, 12);
        g.setPaint(null);

        // Border
        g.setColor(isHovered ? accentColor : new Color(80, 80, 90));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(slot.x, slot.y, slot.width, slot.height, 12, 12);
        g.setStroke(new BasicStroke(1));

        // Draw icon for charm slots
        if (label.startsWith("Charm") && !"None".equals(equippedItem)) {
            Item charm = createCharm(equippedItem, selectedCharmTier);
            if (charm != null) {
                BufferedImage icon = loadIcon(charm.getIconPath());
                if (icon != null) {
                    int iconSize = 40;
                    int iconX = slot.x + slot.width - iconSize - 10;
                    int iconY = slot.y + (slot.height - iconSize) / 2;
                    double scale = Math.min((double)iconSize / icon.getWidth(), (double)iconSize / icon.getHeight());
                    int drawWidth = (int) (icon.getWidth() * scale);
                    int drawHeight = (int) (icon.getHeight() * scale);
                    int drawX = iconX + (iconSize - drawWidth) / 2;
                    int drawY = iconY + (iconSize - drawHeight) / 2;
                    g.drawImage(icon, drawX, drawY, drawWidth, drawHeight, null);
                }
            }
        }

        // Label
        g.setColor(new Color(150, 150, 170));
        g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        g.drawString(label, slot.x + 15, slot.y + 25);

        // Equipped item
        g.setColor(accentColor);
        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        String displayItem = equippedItem;
        if (displayItem.length() > 20) {
            displayItem = displayItem.substring(0, 20) + "...";
        }
        g.drawString(displayItem, slot.x + 15, slot.y + 48);
    }

    private void drawStyledButton(Graphics2D g, Rectangle btn, String label, Color accentColor, boolean isHovered) {
        // Button background with gradient
        if (isHovered) {
            GradientPaint gradient = new GradientPaint(btn.x, btn.y, accentColor.brighter(), btn.x, btn.y + btn.height, accentColor);
            g.setPaint(gradient);
        } else {
            GradientPaint gradient = new GradientPaint(btn.x, btn.y, new Color(50, 50, 60), btn.x, btn.y + btn.height, new Color(40, 40, 50));
            g.setPaint(gradient);
        }
        g.fillRoundRect(btn.x, btn.y, btn.width, btn.height, 12, 12);
        g.setPaint(null);

        // Border
        g.setColor(isHovered ? accentColor : new Color(80, 80, 90));
        g.setStroke(new BasicStroke(2));
        g.drawRoundRect(btn.x, btn.y, btn.width, btn.height, 12, 12);
        g.setStroke(new BasicStroke(1));

        // Text
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 16));
        FontMetrics fm = g.getFontMetrics();
        int textX = btn.x + (btn.width - fm.stringWidth(label)) / 2;
        int textY = btn.y + (btn.height + fm.getAscent() - fm.getDescent()) / 2;
        g.drawString(label, textX, textY);
    }

    // ========== GETTERS FOR GAME TO USE LOADOUT ==========
    public String getSelectedWeapon() { return selectedWeapon; }
    public int getSelectedWeaponTier() { return selectedWeaponTier; }
    public String getSelectedCharm() { return selectedCharm; }
    public int getSelectedCharmTier() { return selectedCharmTier; }
    public String getSelectedPower() { return selectedPower; }
    public int getSelectedPowerTier() { return selectedPowerTier; }
    public String getSelectedSummon() { return selectedSummon; }
    public int getSelectedSummonTier() { return selectedSummonTier; }

    /**
     * Testing utility: clears all owned non-clothing items and restores starter loadout inventory.
     */
    public void resetOwnedInventoryForTesting() {
        unlockedWeapons.clear();
        unlockedCharms.clear();
        unlockedPowers.clear();
        unlockedSummons.clear();
        weaponStacks.clear();
        charmStacks.clear();
        powerStacks.clear();
        summonStacks.clear();

        unlockedWeapons.put("Pistol1", List.of(1));
        unlockedWeapons.put("Rifle1", List.of(1));
        incrementStack(weaponStacks, "Pistol1", 1);
        incrementStack(weaponStacks, "Rifle1", 1);

        selectedWeapon = "Pistol1";
        selectedWeaponTier = 1;
        selectedCharm = "None";
        selectedCharmTier = 1;
        selectedPower = "None";
        selectedPowerTier = 1;
        selectedSummon = "None";
        selectedSummonTier = 1;

        saveLoadoutToFile();
        saveInventoryToFile();
    }

    // ========== UNLOCK METHODS (for loot boxes) ==========

    /**
     * Unlock a weapon with specific tiers.
     * @param weaponName The weapon name (e.g., "Pistol1", "Rifle1")
     * @param tiers List of tiers to unlock for this weapon
     */
    public void unlockWeapon(String weaponName, List<Integer> tiers) {
        // Normalize weapon name to class name for consistent storage
        String normalizedName = normalizeWeaponName(weaponName);

        // Merge with existing tiers instead of overwriting
        List<Integer> existingTiers = unlockedWeapons.getOrDefault(normalizedName, new ArrayList<>());
        List<Integer> mergedTiers = new ArrayList<>(existingTiers);
        for (Integer tier : tiers) {
            if (!mergedTiers.contains(tier)) {
                mergedTiers.add(tier);
            }
            // Increment stack count for this weapon+tier (using normalized name)
            incrementStack(weaponStacks, normalizedName, tier);
        }
        // Sort tiers for consistent display
        mergedTiers.sort(null);
        unlockedWeapons.put(normalizedName, mergedTiers);
        System.out.println("Unlocked weapon: " + normalizedName + " (tiers: " + mergedTiers + ")");
        // Auto-save inventory
        saveInventoryToFile();
    }

    private String normalizeWeaponName(String weaponName) {
        // Convert display names to class names for consistency
        return switch (weaponName) {
            case "Glock", "Pistol1", "Pistol" -> "Pistol1";
            case "AK-47", "Rifle1", "Rifle" -> "Rifle1";
            case "Shotgun1", "Shotgun", "Pump - Action", "Pump-Action" -> "Shotgun1";
            case "SMG1", "SMG", "P90" -> "SMG1";
            case "Sniper1", "Sniper" -> "Sniper1";
            case "Sword1", "Sword" -> "Sword1";
            case "Hammer1", "Hammer" -> "Hammer1";
            case "Dagger1", "Dagger" -> "Dagger1";
            case "Mace1", "Mace" -> "Mace1";
            case "Scythe1", "Scythe" -> "Scythe1";
            default -> weaponName; // Already normalized or unknown
        };
    }

    /**
     * Unlock a weapon with a single tier.
     * @param weaponName The weapon name
     * @param tier The tier to unlock
     */
    public void unlockWeapon(String weaponName, int tier) {
        unlockWeapon(weaponName, List.of(tier));
    }

    /**
     * Unlock a charm with specific tiers.
     * @param charmName The charm name (e.g., "HealthCharm", "SpeedCharm")
     * @param tiers List of tiers to unlock for this charm
     */
    public void unlockCharm(String charmName, List<Integer> tiers) {
        List<Integer> existingTiers = unlockedCharms.getOrDefault(charmName, new ArrayList<>());
        List<Integer> mergedTiers = new ArrayList<>(existingTiers);
        for (Integer tier : tiers) {
            if (!mergedTiers.contains(tier)) {
                mergedTiers.add(tier);
            }
            incrementStack(charmStacks, charmName, tier);
        }
        mergedTiers.sort(null);
        unlockedCharms.put(charmName, mergedTiers);
        System.out.println("Unlocked charm: " + charmName + " (tiers: " + mergedTiers + ")");
        // Auto-save inventory
        saveInventoryToFile();
    }

    public void unlockCharm(String charmName, int tier) {
        unlockCharm(charmName, List.of(tier));
    }

    /**
     * Unlock a power with specific tiers.
     * @param powerName The power name (e.g., "Fire", "Lightning")
     * @param tiers List of tiers to unlock for this power
     */
    public void unlockPower(String powerName, List<Integer> tiers) {
        List<Integer> existingTiers = unlockedPowers.getOrDefault(powerName, new ArrayList<>());
        List<Integer> mergedTiers = new ArrayList<>(existingTiers);
        for (Integer tier : tiers) {
            if (!mergedTiers.contains(tier)) {
                mergedTiers.add(tier);
            }
            incrementStack(powerStacks, powerName, tier);
        }
        mergedTiers.sort(null);
        unlockedPowers.put(powerName, mergedTiers);
        System.out.println("Unlocked power: " + powerName + " (tiers: " + mergedTiers + ")");
        // Auto-save inventory
        saveInventoryToFile();
    }

    public void unlockPower(String powerName, int tier) {
        unlockPower(powerName, List.of(tier));
    }

    /**
     * Unlock a summon with specific tiers.
     * @param summonName The summon name (e.g., "Summon1", "Summon2")
     * @param tiers List of tiers to unlock for this summon
     */
    public void unlockSummon(String summonName, List<Integer> tiers) {
        List<Integer> existingTiers = unlockedSummons.getOrDefault(summonName, new ArrayList<>());
        List<Integer> mergedTiers = new ArrayList<>(existingTiers);
        for (Integer tier : tiers) {
            if (!mergedTiers.contains(tier)) {
                mergedTiers.add(tier);
            }
            incrementStack(summonStacks, summonName, tier);
        }
        mergedTiers.sort(null);
        unlockedSummons.put(summonName, mergedTiers);
        System.out.println("Unlocked summon: " + summonName + " (tiers: " + mergedTiers + ")");
        // Auto-save inventory
        saveInventoryToFile();
    }

    public void unlockSummon(String summonName, int tier) {
        unlockSummon(summonName, List.of(tier));
    }

    /**
     * Unlock a consumable with specific tiers.
     * @param consumableName The consumable name (e.g., "DamagePotion")
     * @param tiers List of tiers to unlock for this consumable
     */
    @Deprecated
    public void unlockConsumable(String consumableName, List<Integer> tiers) {
        // CONSUMABLES REMOVED FROM LOADOUT - This method is kept for compatibility only
    }

    @Deprecated
    public void unlockConsumable(String consumableName, int tier) {
        // CONSUMABLES REMOVED FROM LOADOUT - This method is kept for compatibility only
    }

    /**
     * Unlock an item based on its type and name.
     * @param itemType The item type ("Weapon", "Charm", "Power", "Summon", "Consumable")
     * @param itemName The item name
     * @param tier The tier that was rolled (applies to all item types)
     */
    public void unlockItem(String itemType, String itemName, int tier) {
        switch (itemType) {
            case "Weapon" -> unlockWeapon(itemName, tier);
            case "Charm" -> unlockCharm(itemName, tier);
            case "Power" -> unlockPower(itemName, tier);
            case "Summon" -> unlockSummon(itemName, tier);
            case "Consumable" -> {} // CONSUMABLES REMOVED FROM LOADOUT
            default -> System.out.println("Unknown item type: " + itemType);
        }
    }

    // ========== STACK TRACKING METHODS ==========

    private void incrementStack(Map<String, Map<Integer, Integer>> stackMap, String itemName, int tier) {
        stackMap.computeIfAbsent(itemName, k -> new HashMap<>())
              .merge(tier, 1, Integer::sum);
    }

    public int getStackCount(String pickerType, String itemName, int tier) {
        Map<String, Map<Integer, Integer>> stackMap = switch (pickerType) {
            case "weapon" -> weaponStacks;
            case "charm" -> charmStacks;
            case "power" -> powerStacks;
            case "summon" -> summonStacks;
            default -> null;
        };
        if (stackMap == null) return 0;
        return stackMap.getOrDefault(itemName, new HashMap<>()).getOrDefault(tier, 0);
    }

    private boolean hasUsableStack(String pickerType, String itemName, int tier) {
        if (itemName == null || "None".equals(itemName)) return true;
        String key = "weapon".equals(pickerType) ? normalizeWeaponName(itemName) : itemName;
        return getStackCount(pickerType, key, tier) >= 1;
    }
    
    private Item createCharm(String charmName, int tier) {
        // Map charm names to their classes
        Item charm = switch (charmName) {
            case "Charm1" -> new combat.charms.Charm1(tier);
            case "SpeedCharm" -> new combat.charms.SpeedCharm(tier);
            case "HealthCharm2" -> new combat.charms.HealthCharm2(tier);
            case "HealthCharm3" -> new combat.charms.HealthCharm3(tier);
            case "SpeedCharm2" -> new combat.charms.SpeedCharm2(tier);
            case "SpeedCharm3" -> new combat.charms.SpeedCharm3(tier);
            case "DamageCharm1" -> new combat.charms.DamageCharm1(tier);
            case "DamageCharm2" -> new combat.charms.DamageCharm2(tier);
            case "DamageCharm3" -> new combat.charms.DamageCharm3(tier);
            default -> null;
        };
        
        // If not found in switch, try creating dynamically using reflection
        if (charm == null) {
            try {
                Class<?> itemClass = findCharmClass(charmName);
                if (itemClass != null) {
                    // Try constructor with tier parameter
                    try {
                        return (Item) itemClass.getConstructor(int.class).newInstance(tier);
                    } catch (NoSuchMethodException e) {
                        // Try no-arg constructor
                        return (Item) itemClass.getConstructor().newInstance();
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to create charm: " + charmName + " (tier " + tier + ")");
            }
        }
        return charm;
    }
    
    private Class<?> findCharmClass(String charmName) {
        String[] packages = {
            "combat.charms."
        };
        
        for (String pkg : packages) {
            try {
                return Class.forName(pkg + charmName);
            } catch (ClassNotFoundException e) {
                // Continue searching
            }
        }
        return null;
    }
}






