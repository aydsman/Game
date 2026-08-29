package ui.screens;

import ui.GamePanel;
import save.SaveData;
import save.SaveManager;
import java.awt.*;
import java.util.*;

public class LoadoutScreen {
    private final GamePanel gamePanel;
    private String selectedWeapon = "Pistol1";
    private int selectedWeaponTier = 1;
    private String selectedCharm = "None";
    private int selectedCharmTier = 1;
    private String selectedPower = "None";
    private int selectedPowerTier = 1;
    private String selectedSummon = "None";
    private int selectedSummonTier = 1;
    private boolean debugVisible = false;
    private Rectangle backBtn = new Rectangle(50, 820, 120, 50);

    public LoadoutScreen(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        refreshFromSaveData();
    }

    public void refreshFromSaveData() {
        SaveData d = SaveManager.load();
        selectedWeapon = d.getSelectedWeapon();
        selectedWeaponTier = d.getSelectedWeaponTier();
        selectedCharm = d.getSelectedCharm();
        selectedCharmTier = d.getSelectedCharmTier();
        selectedPower = d.getSelectedPower();
        selectedPowerTier = d.getSelectedPowerTier();
        selectedSummon = d.getSelectedSummon();
        selectedSummonTier = d.getSelectedSummonTier();
    }

    public void handleClick(int x, int y) {
        if (backBtn.contains(x, y)) gamePanel.switchScreen("menu");
    }

    public void draw(Graphics2D g, int w, int h) {
        g.setColor(new Color(25, 28, 35));
        g.fillRect(0, 0, w, h);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Segoe UI", Font.BOLD, 28));
        g.drawString("Loadout", 60, 60);
        g.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        g.drawString("Weapon: " + selectedWeapon + " (T" + selectedWeaponTier + ")", 60, 120);
        g.drawString("Charm: " + selectedCharm + " (T" + selectedCharmTier + ")", 60, 150);
        g.drawString("Power: " + selectedPower + " (T" + selectedPowerTier + ")", 60, 180);
        g.drawString("Summon: " + selectedSummon + " (T" + selectedSummonTier + ")", 60, 210);
        if (debugVisible) {
            g.drawString("Debug: unlocked items in save file", 60, 260);
        }
        g.setColor(Color.DARK_GRAY);
        g.fillRect(backBtn.x, backBtn.y, backBtn.width, backBtn.height);
        g.setColor(Color.WHITE);
        g.drawString("Back", backBtn.x + 36, backBtn.y + 32);
    }

    public void toggleDebugPanel() { debugVisible = !debugVisible; }

    public void unlockItem(String type, String itemName, int tier) {
        SaveData d = SaveManager.load();
        Map<String, Set<Integer>> map = switch (type) {
            case "Weapon" -> d.getUnlockedWeapons();
            case "Charm" -> d.getUnlockedCharms();
            case "Power" -> d.getUnlockedPowers();
            case "Summon" -> d.getUnlockedSummons();
            case "Consumable" -> d.getUnlockedConsumables();
            default -> null;
        };
        if (map != null) {
            map.computeIfAbsent(itemName, k -> new HashSet<>()).add(tier);
            SaveManager.save(d);
        }
    }

    public void resetOwnedInventoryForTesting() {
        SaveData d = SaveManager.load();
        d.getUnlockedWeapons().clear();
        d.getUnlockedCharms().clear();
        d.getUnlockedPowers().clear();
        d.getUnlockedSummons().clear();
        d.getUnlockedConsumables().clear();
        d.getUnlockedWeapons().computeIfAbsent("Pistol1", k -> new HashSet<>()).add(1);
        SaveManager.save(d);
        refreshFromSaveData();
    }

    public String getSelectedWeapon() { return selectedWeapon; }
    public int getSelectedWeaponTier() { return selectedWeaponTier; }
    public String getSelectedCharm() { return selectedCharm; }
    public int getSelectedCharmTier() { return selectedCharmTier; }
    public String getSelectedPower() { return selectedPower; }
    public int getSelectedPowerTier() { return selectedPowerTier; }
    public String getSelectedSummon() { return selectedSummon; }
    public int getSelectedSummonTier() { return selectedSummonTier; }
}
