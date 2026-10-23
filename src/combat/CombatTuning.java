package combat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Numeric tuning for the readable-combat systems (dodge, i-frames, hitstop, knockback, combos,
 * telegraphed slams, projectile drop-off).
 *
 * <p>Values resolve in this order, first match wins:
 * <ol>
 *   <li>System property {@code abyss.combat.<key>}</li>
 *   <li>Environment variable {@code ABYSS_COMBAT_<KEY>} (dots become underscores, upper-cased)</li>
 *   <li>Properties file, {@code config/combat.properties} by default</li>
 *   <li>The compiled-in default</li>
 * </ol>
 *
 * <p>Every value is clamped to the range declared on its {@link Key}, so a bad config can retune
 * the feel of combat but can never produce a state the systems cannot handle (a zero-length dodge,
 * a multi-second hitstop freeze). Unparseable and out-of-range entries are logged and replaced.
 *
 * <p>Instances are immutable. Gameplay code reads the shared {@link #active()} snapshot; tests build
 * throwaway instances with {@link #fromProperties(Properties)}.
 */
public final class CombatTuning {

    /** Config file location, relative to the working directory unless overridden. */
    public static final String DEFAULT_CONFIG_PATH = "config/combat.properties";

    /** System property / env var that points at an alternative config file. */
    public static final String CONFIG_PATH_KEY = "config.path";

    private static final String SYSTEM_PROPERTY_PREFIX = "abyss.combat.";
    private static final String ENV_PREFIX = "ABYSS_COMBAT_";
    private static final Logger LOG = Logger.getLogger(CombatTuning.class.getName());

    /**
     * A tunable value: config key, compiled-in default, and the range outside which a configured
     * value is rejected as unusable.
     */
    public enum Key {
        DODGE_DISTANCE_PX("dodge.distancePx", 210, 40, 600),
        DODGE_DURATION_MS("dodge.durationMs", 180, 60, 600),
        DODGE_IFRAME_MS("dodge.iframeMs", 220, 0, 900),
        DODGE_COOLDOWN_MS("dodge.cooldownMs", 900, 100, 10_000),

        HURT_IFRAME_MS("hurt.iframeMs", 350, 0, 3_000),

        HITSTOP_MAX_MS("hitstop.maxMs", 120, 0, 400),
        HITSTOP_CRIT_MS("hitstop.critMs", 55, 0, 400),
        HITSTOP_HEAVY_MS("hitstop.heavyMs", 45, 0, 400),
        HITSTOP_HEAVY_DAMAGE("hitstop.heavyDamageThreshold", 120, 1, 100_000),

        MELEE_KNOCKBACK_BASE_PX("melee.knockbackBasePx", 8, 0, 200),
        MELEE_KNOCKBACK_PER_FORCE_PX("melee.knockbackPerForcePx", 2.5, 0, 50),
        MELEE_KNOCKBACK_MAX_PX("melee.knockbackMaxPx", 90, 1, 400),

        COMBO_WINDOW_MS("combo.windowMs", 900, 100, 5_000),

        BOSS_STUN_RESISTANCE("stun.bossResistance", 0.35, 0, 1),

        SLAM_WINDUP_MS("slam.windupMs", 700, 100, 5_000),
        SLAM_ACTIVE_MS("slam.activeMs", 140, 20, 2_000),
        SLAM_RECOVERY_MS("slam.recoveryMs", 450, 0, 5_000),
        SLAM_COOLDOWN_MS("slam.cooldownMs", 3_200, 250, 30_000),
        SLAM_RADIUS_PX("slam.radiusPx", 130, 20, 600),
        SLAM_DAMAGE_MULTIPLIER("slam.damageMultiplier", 1.8, 0.1, 10),

        SHOTGUN_FALLOFF_START_PX("shotgun.falloffStartPx", 170, 0, 4_000),
        SHOTGUN_FALLOFF_END_PX("shotgun.falloffEndPx", 520, 1, 8_000),
        SHOTGUN_FALLOFF_MIN_FRACTION("shotgun.falloffMinFraction", 0.28, 0, 1);

        private final String configKey;
        private final double defaultValue;
        private final double min;
        private final double max;

        Key(String configKey, double defaultValue, double min, double max) {
            this.configKey = configKey;
            this.defaultValue = defaultValue;
            this.min = min;
            this.max = max;
        }

        public String configKey() {
            return configKey;
        }

        public double defaultValue() {
            return defaultValue;
        }

        public double min() {
            return min;
        }

        public double max() {
            return max;
        }

        String systemPropertyName() {
            return SYSTEM_PROPERTY_PREFIX + configKey;
        }

        String environmentVariableName() {
            return ENV_PREFIX + configKey.replace('.', '_').toUpperCase(java.util.Locale.ROOT);
        }
    }

    private static volatile CombatTuning active;

    private final Map<Key, Double> values;

    private CombatTuning(Map<Key, Double> values) {
        this.values = values;
    }

    /**
     * Shared snapshot used by gameplay code. Loaded once from the environment and config file on
     * first access; {@link #reload()} replaces it.
     */
    public static CombatTuning active() {
        CombatTuning snapshot = active;
        if (snapshot == null) {
            synchronized (CombatTuning.class) {
                snapshot = active;
                if (snapshot == null) {
                    snapshot = load();
                    active = snapshot;
                }
            }
        }
        return snapshot;
    }

    /** Re-reads the environment and config file and republishes {@link #active()}. */
    public static CombatTuning reload() {
        CombatTuning reloaded = load();
        active = reloaded;
        return reloaded;
    }

    /** Compiled-in defaults, ignoring the environment and config file. */
    public static CombatTuning defaults() {
        return fromProperties(new Properties());
    }

    /**
     * Builds a snapshot from an explicit property set. System properties and environment variables
     * still take precedence, matching the resolution order documented on the class.
     */
    public static CombatTuning fromProperties(Properties overrides) {
        Properties source = overrides == null ? new Properties() : overrides;
        Map<Key, Double> resolved = new EnumMap<>(Key.class);
        for (Key key : Key.values()) {
            resolved.put(key, resolve(key, source));
        }
        return new CombatTuning(resolved);
    }

    private static CombatTuning load() {
        return fromProperties(readConfigFile(resolveConfigPath()));
    }

    private static String resolveConfigPath() {
        String fromSystem = System.getProperty(SYSTEM_PROPERTY_PREFIX + CONFIG_PATH_KEY);
        if (isPresent(fromSystem)) {
            return fromSystem.trim();
        }
        String fromEnv = System.getenv(ENV_PREFIX + "CONFIG_PATH");
        if (isPresent(fromEnv)) {
            return fromEnv.trim();
        }
        return DEFAULT_CONFIG_PATH;
    }

    /**
     * Reads the config file if it exists. A missing file is the normal case and stays silent; an
     * unreadable or malformed file is logged and treated as empty so the game still starts.
     */
    private static Properties readConfigFile(String configPath) {
        Properties loaded = new Properties();
        Path path;
        try {
            path = Path.of(configPath);
        } catch (InvalidPathException e) {
            LOG.log(Level.WARNING, "Combat tuning path is not a valid path, using defaults: " + configPath, e);
            return loaded;
        }
        if (!Files.isRegularFile(path)) {
            return loaded;
        }
        try (InputStream in = Files.newInputStream(path)) {
            loaded.load(in);
            LOG.log(Level.CONFIG, () -> "Loaded combat tuning overrides from " + path.toAbsolutePath());
        } catch (IOException | IllegalArgumentException e) {
            LOG.log(Level.WARNING, "Could not read combat tuning file, using defaults: " + path, e);
            return new Properties();
        }
        return loaded;
    }

    private static double resolve(Key key, Properties fileValues) {
        String raw = System.getProperty(key.systemPropertyName());
        String origin = "system property " + key.systemPropertyName();
        if (!isPresent(raw)) {
            raw = System.getenv(key.environmentVariableName());
            origin = "environment variable " + key.environmentVariableName();
        }
        if (!isPresent(raw)) {
            raw = fileValues.getProperty(key.configKey());
            origin = "config key " + key.configKey();
        }
        if (!isPresent(raw)) {
            return key.defaultValue();
        }

        double parsed;
        try {
            parsed = Double.parseDouble(raw.trim());
        } catch (NumberFormatException e) {
            LOG.warning("Ignoring non-numeric combat tuning value from " + origin + " ('" + raw
                    + "'), using default " + key.defaultValue());
            return key.defaultValue();
        }
        if (!Double.isFinite(parsed)) {
            LOG.warning("Ignoring non-finite combat tuning value from " + origin
                    + ", using default " + key.defaultValue());
            return key.defaultValue();
        }
        double clamped = Math.min(key.max(), Math.max(key.min(), parsed));
        if (clamped != parsed) {
            LOG.warning("Clamped combat tuning value from " + origin + " (" + parsed + ") to "
                    + clamped + "; allowed range is [" + key.min() + ", " + key.max() + "]");
        }
        return clamped;
    }

    private static boolean isPresent(String value) {
        return value != null && !value.isBlank();
    }

    /** Raw resolved value; prefer the named accessors below. */
    public double value(Key key) {
        Double resolved = values.get(key);
        if (resolved == null) {
            throw new IllegalStateException("Combat tuning is missing a value for " + key);
        }
        return resolved;
    }

    private int intValue(Key key) {
        return (int) Math.round(value(key));
    }

    private long longValue(Key key) {
        return Math.round(value(key));
    }

    public int getDodgeDistancePx() {
        return intValue(Key.DODGE_DISTANCE_PX);
    }

    public long getDodgeDurationMs() {
        return longValue(Key.DODGE_DURATION_MS);
    }

    public long getDodgeInvulnerabilityMs() {
        return longValue(Key.DODGE_IFRAME_MS);
    }

    public long getDodgeCooldownMs() {
        return longValue(Key.DODGE_COOLDOWN_MS);
    }

    public long getHurtInvulnerabilityMs() {
        return longValue(Key.HURT_IFRAME_MS);
    }

    public long getHitstopMaxMs() {
        return longValue(Key.HITSTOP_MAX_MS);
    }

    public long getHitstopCritMs() {
        return longValue(Key.HITSTOP_CRIT_MS);
    }

    public long getHitstopHeavyMs() {
        return longValue(Key.HITSTOP_HEAVY_MS);
    }

    public double getHitstopHeavyDamageThreshold() {
        return value(Key.HITSTOP_HEAVY_DAMAGE);
    }

    public double getMeleeKnockbackBasePx() {
        return value(Key.MELEE_KNOCKBACK_BASE_PX);
    }

    public double getMeleeKnockbackPerForcePx() {
        return value(Key.MELEE_KNOCKBACK_PER_FORCE_PX);
    }

    public int getMeleeKnockbackMaxPx() {
        return intValue(Key.MELEE_KNOCKBACK_MAX_PX);
    }

    public long getComboWindowMs() {
        return longValue(Key.COMBO_WINDOW_MS);
    }

    /** Multiplier applied to stun durations aimed at bosses, so heavy weapons cannot stun-lock them. */
    public double getBossStunResistanceMultiplier() {
        return value(Key.BOSS_STUN_RESISTANCE);
    }

    public long getSlamWindupMs() {
        return longValue(Key.SLAM_WINDUP_MS);
    }

    public long getSlamActiveMs() {
        return longValue(Key.SLAM_ACTIVE_MS);
    }

    public long getSlamRecoveryMs() {
        return longValue(Key.SLAM_RECOVERY_MS);
    }

    public long getSlamCooldownMs() {
        return longValue(Key.SLAM_COOLDOWN_MS);
    }

    public int getSlamRadiusPx() {
        return intValue(Key.SLAM_RADIUS_PX);
    }

    public double getSlamDamageMultiplier() {
        return value(Key.SLAM_DAMAGE_MULTIPLIER);
    }

    public int getShotgunFalloffStartPx() {
        return intValue(Key.SHOTGUN_FALLOFF_START_PX);
    }

    public int getShotgunFalloffEndPx() {
        return intValue(Key.SHOTGUN_FALLOFF_END_PX);
    }

    public double getShotgunFalloffMinFraction() {
        return value(Key.SHOTGUN_FALLOFF_MIN_FRACTION);
    }
}
