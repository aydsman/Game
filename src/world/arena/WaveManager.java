package world.arena;

import entity.EnemyManager;
import entity.Player;
import world.arena.arenas.StandardArena;
import java.util.List;
import java.util.Random;

public class WaveManager {
    public enum WaveState { ACTIVE, GRACE_PERIOD, COMPLETED }

    private static final int MAX_WAVES = 10;
    private static final int GRACE_PERIOD_FRAMES = 180; // ~3s at 60fps

    private final EnemyManager enemyManager;
    private final Player player;
    private final int arenaWidth;
    private final int arenaHeight;
    private final Random random = new Random();

    private StandardArena currentArena;
    private boolean enabled = true;
    private int currentWave = 0;
    private WaveState state = WaveState.GRACE_PERIOD;
    private int graceFramesRemaining = 0;
    private int spawnCooldown = 0;
    private int enemiesToSpawn = 0;
    private int enemiesSpawnedThisWave = 0;

    public WaveManager(EnemyManager enemyManager, Player player, int arenaWidth, int arenaHeight) {
        this.enemyManager = enemyManager;
        this.player = player;
        this.arenaWidth = arenaWidth;
        this.arenaHeight = arenaHeight;
        beginWave(1);
    }

    public void setCurrentArena(StandardArena arena) { this.currentArena = arena; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public WaveState getState() { return state; }
    public int getCurrentWave() { return currentWave; }
    public int getMaxWaves() { return MAX_WAVES; }
    public boolean isInGracePeriod() { return state == WaveState.GRACE_PERIOD; }
    public int getGracePeriodRemaining() { return graceFramesRemaining; }

    public boolean isArenaRunFullyComplete() {
        return currentWave >= MAX_WAVES && state == WaveState.COMPLETED && enemyManager.getEnemies().isEmpty();
    }

    public int getChestTierForWave(int wave) {
        return Math.min(5, Math.max(1, (wave + 1) / 2));
    }

    public void forceBeginWave(int wave) {
        enemyManager.killAllEnemies();
        currentWave = Math.min(wave, MAX_WAVES);
        state = WaveState.ACTIVE;
        graceFramesRemaining = 0;
        enemiesSpawnedThisWave = 0;
        enemiesToSpawn = computeEnemyCount();
        spawnCooldown = 0;
    }

    public void update(boolean skipGrace) {
        if (!enabled) return;

        if (state == WaveState.GRACE_PERIOD) {
            if (skipGrace) {
                startNextWave();
            } else {
                graceFramesRemaining--;
                if (graceFramesRemaining <= 0) {
                    startNextWave();
                }
            }
            return;
        }

        if (state == WaveState.COMPLETED) return;

        if (spawnCooldown > 0) {
            spawnCooldown--;
        } else if (enemiesSpawnedThisWave < enemiesToSpawn) {
            spawnEnemy();
            enemiesSpawnedThisWave++;
            spawnCooldown = 30;
        }

        if (enemiesSpawnedThisWave >= enemiesToSpawn && enemyManager.getEnemies().isEmpty()) {
            if (currentWave >= MAX_WAVES) {
                state = WaveState.COMPLETED;
            } else {
                state = WaveState.GRACE_PERIOD;
                graceFramesRemaining = GRACE_PERIOD_FRAMES;
            }
        }
    }

    private void startNextWave() {
        if (currentWave >= MAX_WAVES) {
            state = WaveState.COMPLETED;
            return;
        }
        currentWave++;
        state = WaveState.ACTIVE;
        enemiesSpawnedThisWave = 0;
        enemiesToSpawn = computeEnemyCount();
        spawnCooldown = 10;
    }

    private void beginWave(int wave) {
        currentWave = wave;
        state = WaveState.ACTIVE;
        enemiesSpawnedThisWave = 0;
        enemiesToSpawn = computeEnemyCount();
    }

    private int computeEnemyCount() {
        int base = currentArena != null ? currentArena.getBaseEnemyCount() : 3;
        return base + currentWave;
    }

    private void spawnEnemy() {
        List<String> types = currentArena != null ? currentArena.getAvailableEnemyTypes() : List.of("Enemy1");
        if (types.isEmpty()) return;
        String type = types.get(random.nextInt(types.size()));
        int id = parseEnemyId(type);
        int margin = 80;
        int x = margin + random.nextInt(Math.max(1, arenaWidth - margin * 2));
        int y = margin + random.nextInt(Math.max(1, arenaHeight - margin * 2));
        enemyManager.spawnEnemy(x, y, id);
        if (currentArena != null) {
            enemyManager.applyArenaScaling(currentArena);
        }
    }

    private int parseEnemyId(String type) {
        if (type.startsWith("Enemy")) {
            try {
                return Integer.parseInt(type.substring(5));
            } catch (NumberFormatException ignored) {}
        }
        return 1;
    }
}
