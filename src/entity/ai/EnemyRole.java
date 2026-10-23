package entity.ai;

/**
 * How an enemy occupies space. Roles change movement and the attacks they bring, not just stats,
 * so a wave of mixed roles plays differently from a pack of identical chasers.
 */
public enum EnemyRole {

    /** Default: closes to the player and shoots or swings. */
    STRIKER,

    /** Holds a mid-range band and shoots; walking into them is optional, walking away is not. */
    KITER,

    /** Pushes into close range so a short-range weapon (shotgun) can actually connect. */
    CLOSER,

    /** Commits to a telegraphed ground slam instead of an instant melee tick. */
    SLAMMER
}
