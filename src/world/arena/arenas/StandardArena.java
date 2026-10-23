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
     * Outer rim plus an inner courtyard. The old layout left the inner walls short of each other
     * at every corner, which read as a broken border rather than cover with doorways.
     */
    protected void populateDefaultObstacles() {
        clearObstacles();
        addPerimeterWalls(48);

        int W = getWidth();
        int H = getHeight();
        int cx = W / 2;
        int cy = H / 2;

        addCourtyardFrame(cx, cy, 840, 840, 44, 180);

        // Corner cover, inset so it does not sit on the perimeter.
        addObstacle(200, 200, 140, 140);
        addObstacle(W - 340, 200, 140, 140);
        addObstacle(200, H - 340, 140, 140);
        addObstacle(W - 340, H - 340, 140, 140);
    }

    /**
     * Axis-aligned rectangle of walls with a centered doorway on each side.
     *
     * @param frameW   outer width of the courtyard
     * @param frameH   outer height of the courtyard
     * @param wall     wall thickness
     * @param doorway  opening on each side; must leave a wall stub on both ends
     */
    protected void addCourtyardFrame(int centerX, int centerY, int frameW, int frameH, int wall, int doorway) {
        if (frameW <= 0 || frameH <= 0 || wall <= 0) {
            return;
        }
        int left = centerX - frameW / 2;
        int top = centerY - frameH / 2;
        int right = left + frameW;
        int bottom = top + frameH;
        int door = Math.max(0, Math.min(doorway, Math.min(frameW, frameH) - wall * 2));

        int topSpan = frameW;
        int topSeg = Math.max(wall, (topSpan - door) / 2);
        addObstacle(left, top, topSeg, wall);
        addObstacle(right - topSeg, top, topSeg, wall);
        addObstacle(left, bottom - wall, topSeg, wall);
        addObstacle(right - topSeg, bottom - wall, topSeg, wall);

        int innerTop = top + wall;
        int innerH = frameH - wall * 2;
        int sideSeg = Math.max(wall, (innerH - door) / 2);
        addObstacle(left, innerTop, wall, sideSeg);
        addObstacle(left, bottom - wall - sideSeg, wall, sideSeg);
        addObstacle(right - wall, innerTop, wall, sideSeg);
        addObstacle(right - wall, bottom - wall - sideSeg, wall, sideSeg);
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
