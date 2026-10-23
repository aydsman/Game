package combat.defense;

/**
 * What happened to one incoming damage event. Returned by
 * {@link entity.Player#applyIncomingDamage(double)} so callers can tell a real hit from a negated
 * one instead of assuming their damage landed.
 */
public enum DamageOutcome {

    /** Health was reduced. */
    APPLIED,

    /** Negated by dodge invulnerability frames. */
    NEGATED_DODGE,

    /** Negated by the invulnerability granted after the previous hit. */
    NEGATED_IFRAMES,

    /** Nothing to apply: the amount was zero or negative, or the target was already dead. */
    IGNORED;

    public boolean isApplied() {
        return this == APPLIED;
    }

    /** True when a hit connected but was cancelled by an active defensive window. */
    public boolean isNegated() {
        return this == NEGATED_DODGE || this == NEGATED_IFRAMES;
    }
}
