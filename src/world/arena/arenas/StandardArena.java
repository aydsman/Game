package world.arena.arenas;

import world.arena.Arena;
import java.util.ArrayList;
import java.util.List;

public abstract class StandardArena extends Arena {

    protected int difficultyLevel;
    protected String biomeName;
    protected List<String> availableEnemyTypes;
    protected int baseEnemyCount;
    protected double enemyHealthMultiplier;
    protected double enemySpeedMultiplier;
    protected double enemyDamageMultiplier;

    public StandardArena(int width, int height, int difficultyLevel, String biomeName) {
        super(width, height);
        this.difficultyLevel = difficultyLevel;
        this.biomeName = biomeName;
        this.availableEnemyTypes = new ArrayList<>();
        this.baseEnemyCount = 3 + (difficultyLevel - 1); // Increases with difficulty

        // Difficulty scaling
        this.enemyHealthMultiplier = 1.0 + (difficultyLevel - 1) * 0.25; // +25% per level
        this.enemySpeedMultiplier = 1.0 + (difficultyLevel - 1) * 0.15;  // +15% per level
        this.enemyDamageMultiplier = 1.0 + (difficultyLevel - 1) * 0.2;  // +20% per level

        setupEnemyTypes();
        populateDefaultObstacles();
    }

    /**
     * Layout cover pieces so arenas are not empty flat planes (axis-aligned “walls”).
     */
    protected void populateDefaultObstacles() {
        clearObstacles();
        int W = getWidth();
        int H = getHeight();
        int cx = W / 2;
        int cy = H / 2;

        // Tall thin pillars (“sideways” walls)
        addObstacle(cx - 420, cy - 280, 44, 560);
        addObstacle(cx + 376, cy - 280, 44, 560);

        // Wide low bands (“front-facing” walls)
        addObstacle(cx - 380, cy - 420, 760, 40);
        addObstacle(cx - 380, cy + 380, 760, 40);

        // Corner blocks
        addObstacle(120, 120, 160, 160);
        addObstacle(W - 280, 120, 160, 160);
        addObstacle(120, H - 280, 160, 160);
        addObstacle(W - 280, H - 280, 160, 160);

        // Inner quarter barriers
        addObstacle(cx - 520, cy - 40, 200, 80);
        addObstacle(cx + 320, cy - 40, 200, 80);
    }

    protected abstract void setupEnemyTypes();

    public int getDifficultyLevel() {
        return difficultyLevel;
    }

    public String getBiomeName() {
        return biomeName;
    }

    public List<String> getAvailableEnemyTypes() {
        return availableEnemyTypes;
    }

    public int getBaseEnemyCount() {
        return baseEnemyCount;
    }

    public double getEnemyHealthMultiplier() {
        return enemyHealthMultiplier;
    }

    public double getEnemySpeedMultiplier() {
        return enemySpeedMultiplier;
    }

    public double getEnemyDamageMultiplier() {
        return enemyDamageMultiplier;
    }

    public String getArenaName() {
        return biomeName + " " + getRomanNumeral(difficultyLevel);
    }

    private String getRomanNumeral(int num) {
        switch (num) {
            case 1: return "I";
            case 2: return "II";
            case 3: return "III";
            case 4: return "IV";
            case 5: return "V";
            default: return String.valueOf(num);
        }
    }
}
