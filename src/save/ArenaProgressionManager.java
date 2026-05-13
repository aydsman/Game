package save;

import java.util.*;

/**
 * Manages arena progression and unlocks for the game.
 * Tracks which arenas have been completed and which are currently unlocked.
 */
public class ArenaProgressionManager {
    
    // Define all arenas (in order of unlock progression)
    private static final String[] ALL_ARENAS = {
        "PlainsI", "PlainsII", "PlainsIII", "PlainsIV", "PlainsV"
    };
    
    private Set<String> unlockedArenas;
    private String lastCompletedArena;
    
    public ArenaProgressionManager() {
        this.unlockedArenas = new HashSet<>();
        // Plains I is always unlocked by default
        this.unlockedArenas.add("PlainsI");
        this.lastCompletedArena = null;
    }
    
    /**
     * Check if an arena is unlocked
     */
    public boolean isUnlocked(String arenaName) {
        return unlockedArenas.contains(arenaName);
    }
    
    /**
     * Unlock an arena (typically by beating the previous one)
     */
    public void unlockArena(String arenaName) {
        unlockedArenas.add(arenaName);
    }
    
    /**
     * Mark an arena as completed
     */
    public void completeArena(String arenaName) {
        lastCompletedArena = arenaName;
        // Unlock the next arena in sequence
        int currentIndex = Arrays.asList(ALL_ARENAS).indexOf(arenaName);
        if (currentIndex >= 0 && currentIndex < ALL_ARENAS.length - 1) {
            String nextArena = ALL_ARENAS[currentIndex + 1];
            unlockArena(nextArena);
        }
    }
    
    /**
     * Get the last completed arena
     */
    public String getLastCompletedArena() {
        return lastCompletedArena;
    }
    
    /**
     * Set the last completed arena
     */
    public void setLastCompletedArena(String arenaName) {
        this.lastCompletedArena = arenaName;
    }
    
    /**
     * Get all unlocked arenas
     */
    public Set<String> getUnlockedArenas() {
        return new HashSet<>(unlockedArenas);
    }
    
    /**
     * Get all available arenas
     */
    public static String[] getAllArenas() {
        return ALL_ARENAS.clone();
    }
    
    /**
     * Get the unlock order of arenas
     */
    public List<String> getArenaHierarchy() {
        return new ArrayList<>(Arrays.asList(ALL_ARENAS));
    }
    
    /**
     * Unlock all arenas (debug mode)
     */
    public void unlockAll() {
        for (String arena : ALL_ARENAS) {
            unlockedArenas.add(arena);
        }
    }
    
    /**
     * Reset all progression
     */
    public void reset() {
        unlockedArenas.clear();
        unlockedArenas.add("PlainsI");
        lastCompletedArena = null;
    }
    
    /**
     * Set unlocked arenas from save data
     */
    public void setUnlockedArenas(Set<String> arenas) {
        this.unlockedArenas = new HashSet<>(arenas);
        // Ensure Plains I is always unlocked
        this.unlockedArenas.add("PlainsI");
    }
}

