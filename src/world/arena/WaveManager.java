package world.arena;

import entity.EnemyManager;
import entity.Player;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class WaveManager {
    private int currentWave = 1;
    private int maxWaves = 10;
    private WaveState state = WaveState.IDLE;
    private int enemiesRemaining = 0;
    private int spawnTimer = 0;
    private int spawnInterval = 60; // frames between spawns (1 second at 60 FPS)
    private int spawnIndex = 0;
    private List<WaveSpawn> currentWaveSpawns = new ArrayList<>();
    private EnemyManager enemyManager;
    private Player player;
    private int arenaWidth;
    private int arenaHeight;
    private int spawnProtectionRadius = 500; // enemies can't spawn within this radius of player
    private boolean enabled = true;
    private int gracePeriodTimer = 0;
    private int gracePeriodDuration = 1200; // 20 seconds at 60 FPS
    private boolean inGracePeriod = false;
    private world.arena.arenas.StandardArena currentArena;
    private java.util.Random random = new java.util.Random();
    private GameMode mode = GameMode.STANDARD;
    private int timeRemainingFrames = 0;
    private static final int MAX_ENEMIES_PER_WAVE = 24;

    public WaveManager(EnemyManager enemyManager, Player player, int arenaWidth, int arenaHeight) {
        this.enemyManager = enemyManager;
        this.player = player;
        this.arenaWidth = arenaWidth;
        this.arenaHeight = arenaHeight;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void update() {
        update(false);
    }

    public void update(boolean skipGracePeriod) {
        if (!enabled) return;

        if (mode.hasTimeLimit()) {
            if (timeRemainingFrames <= 0) {
                return; // clock ran out; the run is over and waiting on the results overlay
            }
            timeRemainingFrames--;
        }

        if (state == WaveState.IDLE) {
            if (currentWave == 1) {
                // Skip grace period for wave 1, start immediately
                startWave();
            } else {
                startGracePeriod();
            }
        } else if (state == WaveState.GRACE_PERIOD) {
            updateGracePeriod(skipGracePeriod);
        } else if (state == WaveState.ACTIVE) {
            spawnEnemies();
            checkWaveComplete();
        } else if (state == WaveState.COMPLETED) {
            if (currentWave < maxWaves) {
                currentWave++;
                state = WaveState.IDLE;
            } else {
                // All waves completed
            }
        }
    }

    private void startGracePeriod() {
        gracePeriodTimer = gracePeriodDuration;
        inGracePeriod = true;
        state = WaveState.GRACE_PERIOD;
    }

    // Called by GameScreen when wave completes to determine chest tier
    public int getChestTierForWave(int wave) {
        return Math.max(1, Math.min(5, (wave + 1) / 2));
    }

    private void updateGracePeriod(boolean skipPressed) {
        if (skipPressed || gracePeriodTimer <= 0) {
            inGracePeriod = false;
            startWave();
        } else {
            gracePeriodTimer--;
        }
    }

    public int getGracePeriodRemaining() {
        return inGracePeriod ? gracePeriodTimer : 0;
    }

    public boolean isInGracePeriod() {
        return inGracePeriod;
    }

    private void startWave() {
        currentWaveSpawns = getWaveConfiguration(currentWave);
        enemiesRemaining = currentWaveSpawns.size();
        spawnIndex = 0;
        spawnTimer = 0;
        state = WaveState.ACTIVE;
        applyScalingForCurrentWave();
    }

    /**
     * Hands the arena multipliers to the enemy manager before this wave spawns, so every enemy
     * is built with them. Endless-style modes add a per-wave ramp on top.
     */
    private void applyScalingForCurrentWave() {
        if (currentArena == null) {
            return;
        }
        double ramp = mode.rampsWithWaves() ? 1.0 + (currentWave - 1) * 0.06 : 1.0;
        enemyManager.setDifficultyScaling(
                currentArena.getEnemyHealthMultiplier() * ramp,
                currentArena.getEnemySpeedMultiplier() * Math.min(1.35, ramp),
                currentArena.getEnemyDamageMultiplier() * ramp,
                currentArena.getDifficultyLevel());
    }

    private void spawnEnemies() {
        if (spawnIndex < currentWaveSpawns.size()) {
            spawnTimer++;
            if (spawnTimer >= spawnInterval) {
                WaveSpawn spawn = currentWaveSpawns.get(spawnIndex);
                int fw = 50;
                int fh = 50;
                if (spawn.type == SpawnType.MINIBOSS) {
                    fw = 60;
                    fh = 60;
                } else if (spawn.type == SpawnType.BOSS) {
                    fw = 80;
                    fh = 80;
                }
                int[] pos = getValidSpawnPosition(fw, fh);
                int spawnX = pos[0];
                int spawnY = pos[1];
                
                if (spawn.type == SpawnType.MINIBOSS) {
                    enemyManager.spawnMiniboss(spawnX, spawnY, spawn.enemyId);
                } else if (spawn.type == SpawnType.BOSS) {
                    enemyManager.spawnBoss(spawnX, spawnY, spawn.enemyId);
                } else {
                    enemyManager.spawnEnemy(spawnX, spawnY, spawn.enemyId);
                }
                
                spawnIndex++;
                spawnTimer = 0;
            }
        }
    }

    private int[] getValidSpawnPosition(int footprintW, int footprintH) {
        java.util.List<Rectangle> obs = (currentArena != null)
                ? currentArena.getObstacles()
                : Collections.emptyList();
        for (int i = 0; i < 200; i++) {
            int x = getRandomSpawnX(footprintW);
            int y = getRandomSpawnY(footprintH);
            int cx = x + footprintW / 2;
            int cy = y + footprintH / 2;
            if (isWithinProtectionRadius(cx, cy)) {
                continue;
            }
            if (!obs.isEmpty() && ArenaCollision.entityIntersectsAny(x, y, footprintW, footprintH, obs)) {
                continue;
            }
            return new int[]{x, y};
        }
        for (int j = 0; j < 80; j++) {
            int x = getRandomSpawnX(footprintW);
            int y = getRandomSpawnY(footprintH);
            if (!obs.isEmpty() && ArenaCollision.entityIntersectsAny(x, y, footprintW, footprintH, obs)) {
                continue;
            }
            return new int[]{x, y};
        }
        return new int[]{getRandomSpawnX(footprintW), getRandomSpawnY(footprintH)};
    }

    private boolean isWithinProtectionRadius(int centerX, int centerY) {
        int playerX = player.getCenterX();
        int playerY = player.getCenterY();
        double distance = Math.sqrt(Math.pow(centerX - playerX, 2) + Math.pow(centerY - playerY, 2));
        return distance < spawnProtectionRadius;
    }

    private int getRandomSpawnX(int footprintW) {
        int margin = 80;
        return margin + random.nextInt(Math.max(1, arenaWidth - footprintW - margin * 2));
    }

    private int getRandomSpawnY(int footprintH) {
        int margin = 80;
        return margin + random.nextInt(Math.max(1, arenaHeight - footprintH - margin * 2));
    }

    private void checkWaveComplete() {
        enemiesRemaining = enemyManager.getEnemies().size();
        // Also count active boss if present
        if (enemyManager.getActiveBoss() != null) {
            enemiesRemaining++;
        }
        if (enemiesRemaining == 0 && spawnIndex >= currentWaveSpawns.size()) {
            state = WaveState.COMPLETED;
        }
    }

    private List<WaveSpawn> getWaveConfiguration(int wave) {
        List<WaveSpawn> spawns = new ArrayList<>();
        
        if (currentArena == null) {
            // Fallback to old configuration if no arena set
            return getLegacyWaveConfiguration(wave);
        }

        if (mode == GameMode.BOSS_RUSH) {
            return getBossRushConfiguration(wave);
        }
        
        int baseCount = currentArena.getBaseEnemyCount();
        List<String> availableTypes = currentArena.getAvailableEnemyTypes();
        
        // Adjust enemy count based on wave (more enemies in later waves), capped so endless runs stay playable
        int enemyCount = Math.min(MAX_ENEMIES_PER_WAVE, baseCount + (wave - 1));
        
        // Higher waves have more frequent harder enemies
        double hardEnemyChance = Math.min(0.8, (wave - 1) * 0.15); // Up to 80% chance for hard enemies
        
        for (int i = 0; i < enemyCount; i++) {
            int enemyId;
            if (availableTypes.size() == 1) {
                // Only one type available
                enemyId = Integer.parseInt(availableTypes.get(0).replace("Enemy", ""));
            } else if (random.nextDouble() < hardEnemyChance && availableTypes.size() > 1) {
                // Spawn harder enemy
                int randomIndex = random.nextInt(availableTypes.size());
                enemyId = Integer.parseInt(availableTypes.get(randomIndex).replace("Enemy", ""));
            } else {
                // Spawn basic enemy (first in list)
                enemyId = Integer.parseInt(availableTypes.get(0).replace("Enemy", ""));
            }
            spawns.add(new WaveSpawn(SpawnType.ENEMY, enemyId));
        }
        
        if (isBossWave(wave)) {
            // Boss waves are the boss alone
            spawns.clear();
            spawns.add(new WaveSpawn(SpawnType.BOSS, 1));
        } else if (isMinibossWave(wave)) {
            spawns.add(new WaveSpawn(SpawnType.MINIBOSS, 1));
        }
        
        return spawns;
    }

    /** Every tenth wave is a boss; every fifth wave in between is a miniboss. */
    private boolean isBossWave(int wave) {
        return wave % 10 == 0;
    }

    private boolean isMinibossWave(int wave) {
        return wave % 5 == 0 && !isBossWave(wave);
    }

    /** Boss Rush alternates miniboss and boss fights, always finishing on a boss. */
    private List<WaveSpawn> getBossRushConfiguration(int wave) {
        List<WaveSpawn> spawns = new ArrayList<>();
        boolean bossFight = wave % 2 == 0 || wave >= maxWaves;
        spawns.add(new WaveSpawn(bossFight ? SpawnType.BOSS : SpawnType.MINIBOSS, 1));
        return spawns;
    }
    
    private List<WaveSpawn> getLegacyWaveConfiguration(int wave) {
        List<WaveSpawn> spawns = new ArrayList<>();
        
        switch (wave) {
            case 1:
                // 5x Enemy1
                for (int i = 0; i < 5; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 1));
                break;
            case 2:
                // 3x Enemy1, 3x Enemy2
                for (int i = 0; i < 3; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 1));
                for (int i = 0; i < 3; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 2));
                break;
            case 3:
                // 2x Enemy1, 2x Enemy2, 2x Enemy3, 1x slammer
                for (int i = 0; i < 2; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 1));
                for (int i = 0; i < 2; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 2));
                for (int i = 0; i < 2; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 3));
                spawns.add(new WaveSpawn(SpawnType.ENEMY, 7));
                break;
            case 4:
                // 1x Enemy1, 2x Enemy2, 3x Enemy3, 2x Enemy4
                spawns.add(new WaveSpawn(SpawnType.ENEMY, 1));
                for (int i = 0; i < 2; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 2));
                for (int i = 0; i < 3; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 3));
                for (int i = 0; i < 2; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 4));
                break;
            case 5:
                // 2x Enemy1, 2x Enemy2, 2x Enemy3, 1x Miniboss1
                for (int i = 0; i < 2; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 1));
                for (int i = 0; i < 2; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 2));
                for (int i = 0; i < 2; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 3));
                spawns.add(new WaveSpawn(SpawnType.MINIBOSS, 1));
                break;
            case 6:
                // 2x Enemy2, 2x Enemy3, 3x Enemy4, 1x slammer
                for (int i = 0; i < 2; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 2));
                for (int i = 0; i < 2; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 3));
                for (int i = 0; i < 3; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 4));
                spawns.add(new WaveSpawn(SpawnType.ENEMY, 7));
                break;
            case 7:
                // 1x Enemy2, 3x Enemy3, 3x Enemy4, 2x Enemy5
                spawns.add(new WaveSpawn(SpawnType.ENEMY, 2));
                for (int i = 0; i < 3; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 3));
                for (int i = 0; i < 3; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 4));
                for (int i = 0; i < 2; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 5));
                break;
            case 8:
                // 3x Enemy3, 3x Enemy4, 3x Enemy5
                for (int i = 0; i < 3; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 3));
                for (int i = 0; i < 3; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 4));
                for (int i = 0; i < 3; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 5));
                break;
            case 9:
                // 2x Enemy3, 3x Enemy4, 4x Enemy5
                for (int i = 0; i < 2; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 3));
                for (int i = 0; i < 3; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 4));
                for (int i = 0; i < 4; i++) spawns.add(new WaveSpawn(SpawnType.ENEMY, 5));
                break;
            case 10:
                // 1x Boss1
                spawns.add(new WaveSpawn(SpawnType.BOSS, 1));
                break;
        }
        
        return spawns;
    }

    public int getCurrentWave() {
        return currentWave;
    }

    public int getMaxWaves() {
        return maxWaves;
    }

    /**
     * True when the run has ended in success: the final wave is cleared, or the clock ran out in a
     * timed mode. (State can stay {@link WaveState#COMPLETED} across frames while the UI is frozen.)
     */
    public boolean isRunComplete() {
        if (mode.hasTimeLimit() && timeRemainingFrames <= 0) {
            return true;
        }
        return state == WaveState.COMPLETED && currentWave >= maxWaves;
    }

    public void setMode(GameMode mode) {
        this.mode = (mode == null ? GameMode.STANDARD : mode);
        this.maxWaves = this.mode.getMaxWaves();
        this.gracePeriodDuration = this.mode.getGracePeriodFrames();
        reset();
    }

    public GameMode getMode() {
        return mode;
    }

    /** Frames left on the clock in a timed mode; 0 in every other mode. */
    public int getTimeRemainingFrames() {
        return mode.hasTimeLimit() ? Math.max(0, timeRemainingFrames) : 0;
    }

    /** Waves the player actually finished, used for run payouts. */
    public int getWavesSurvived() {
        boolean currentWaveCleared = state == WaveState.COMPLETED;
        return Math.max(0, currentWaveCleared ? currentWave : currentWave - 1);
    }

    /**
     * Immediately starts the given wave (used after clearing all waves to replay wave 10 only).
     */
    public void forceBeginWave(int wave) {
        currentWave = Math.max(1, Math.min(maxWaves, wave));
        startWave();
    }

    public WaveState getState() {
        return state;
    }

    public void setState(WaveState state) {
        this.state = state;
    }

    public void setCurrentArena(world.arena.arenas.StandardArena arena) {
        this.currentArena = arena;
        this.maxWaves = mode.getMaxWaves();
        reset();
    }

    public world.arena.arenas.StandardArena getCurrentArena() {
        return currentArena;
    }

    public void reset() {
        currentWave = 1;
        state = WaveState.IDLE;
        enemiesRemaining = 0;
        spawnTimer = 0;
        spawnIndex = 0;
        currentWaveSpawns.clear();
        gracePeriodTimer = 0;
        inGracePeriod = false;
        timeRemainingFrames = mode.getTimeLimitFrames();
    }

    public enum WaveState {
        IDLE,
        GRACE_PERIOD,
        ACTIVE,
        COMPLETED
    }

    private static class WaveSpawn {
        SpawnType type;
        int enemyId;

        WaveSpawn(SpawnType type, int enemyId) {
            this.type = type;
            this.enemyId = enemyId;
        }
    }

    private enum SpawnType {
        ENEMY,
        MINIBOSS,
        BOSS
    }
}
