package combat.powers;

public class Lightning extends Power {
    public Lightning() {
        this(1);
    }

    public Lightning(int tier) {
        super(tier);
        name = "Lightning";
        iconPath = "assets/items/powers/lightning/lightning_icon.png";
        description = "Wield the storm to strike foes with electrifying speed.";
        
        addTemplatedMove("Chain Bolt", 1, MoveTemplates.projectileBomb(
                1600, 22, 360, 46,
                "assets/effects/lightning/chain_bolt_cast.png",
                "assets/effects/lightning/chain_bolt_hit.png"));
        addTemplatedMove("Stun Field", 2, MoveTemplates.slowField(
                7000, 8, 0, 150, 1800,
                "assets/effects/lightning/stun_field_cast.png",
                "assets/effects/lightning/stun_field_hit.png"));
        addTemplatedMove("Shock Burst", 3, MoveTemplates.heavyFinisher(
                9000, 40, 280, 70,
                "assets/effects/lightning/shock_burst_cast.png",
                "assets/effects/lightning/shock_burst_hit.png"));
        
        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}
