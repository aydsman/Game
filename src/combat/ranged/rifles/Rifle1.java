package combat.ranged.rifles;

public class Rifle1 extends Rifle {

    public Rifle1() {
        this(1); // Default to Tier I
    }

    public Rifle1(int tier) {
        super(tier);
        // Rifle1 specific stats (basic assault rifle)
        damage = 30;
        magazineSize = 25;
        name = "AK-47";
        iconPath = "assets/items/ranged/rifles/rifle1/ak.png";
        description = "This assault rifle has a slower rate of fire, but deals more damage per shot.";
        // Re-apply tier multipliers after setting base damage
        applyTierMultipliers();
    }
}
