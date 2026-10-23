package combat.powers.fruits;

import combat.powers.Move;
import combat.powers.Power;

public class NikaFruit extends Power {
    public NikaFruit() { this(5); }
    public NikaFruit(int tier) {
        super(tier);
        // Nika Fruit: mythic freedom combat with absurdly explosive finishers.
        name = "Nika Fruit";
        iconPath = "assets/items/powers/fruits/nika_icon.png";
        description = "Unleash mythic elasticity and overwhelming liberation-force attacks.";

        // Move 1: Fast elastic punch barrage used as your core combo pressure tool.
        addMove(new Move("Dawn Gatling", 1));

        // Move 2: Cartoon-style run used for quick mobility, disengage, and repositioning.
        addMove(new Move("Toon Dash", 2));

        // Move 3: Slams the ground to rubberize an area, slowing enemies inside the radius.
        addMove(new Move("Rubber Realm", 3));

        // Move 4: Grabs and launches a lightning bolt as a high-impact ranged strike.
        addMove(new Move("Thunder Spear", 4));

        // Move 5: Massive finishing punch inspired by the Wano final blow against Kaido.
        addMove(new Move("Bajrang Gun", 5));
    }
}
