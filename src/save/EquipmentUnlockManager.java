package save;

import java.util.ArrayList;
import java.util.List;

public class EquipmentUnlockManager {
    private SaveData saveData;
    
    public EquipmentUnlockManager() {
        this.saveData = SaveManager.load();
    }
    
    public boolean isUnlocked(String itemClassName) {
        return saveData.getUnlockedEquipment().contains(itemClassName);
    }
    
    public void unlock(String itemClassName) {
        if (!isUnlocked(itemClassName)) {
            saveData.getUnlockedEquipment().add(itemClassName);
            SaveManager.save(saveData);
            System.out.println("Unlocked: " + itemClassName);
        }
    }
    
    public void unlockAllDefaults() {
        // Already done in SaveData constructor
    }
    
    public List<String> getUnlockedEquipment() {
        return new ArrayList<>(saveData.getUnlockedEquipment());
    }
    
    public void setEquippedSlot(int slot, String itemClassName) {
        if (itemClassName == null) {
            saveData.getEquippedLoadout().remove("slot" + slot);
        } else {
            saveData.getEquippedLoadout().put("slot" + slot, itemClassName);
        }
        SaveManager.save(saveData);
    }
    
    public String getEquippedSlot(int slot) {
        return saveData.getEquippedLoadout().get("slot" + slot);
    }
    
    public void reload() {
        this.saveData = SaveManager.load();
    }
    
    public void reset() {
        SaveManager.resetSave();
        this.saveData = SaveManager.load();
    }
}
