package combat.powers.fruits;

import combat.powers.Power;

public class StringFruit extends Power {
    public StringFruit() { this(2); }
    public StringFruit(int tier) {
        super(tier);
        // String Fruit: precision control through binds and cuts.
        name = "String Fruit";
        iconPath = "assets/items/powers/fruits/string_icon.png";
        description = "Cut, bind, and puppet foes with razor-precise threads.";

        // Move 1: Fast forward string thrust. Low cooldown poke that damages anything in a thin line in front of the player.
        addMove("Overheat", 1, 220, 700, "assets/effects/temp/string_overheat.png");

        // Move 2: Latches up to 6 thin strings onto unique enemies within a radius and stuns them for 5 seconds.
        addMove("Parasite", 2, 800, 12000, "assets/effects/temp/string_parasite.png");

        // Move 3: Shoots 16 strings at the nearest enemy in a large radius. Strings fire 0.1s apart.
        addMove("God Thread", 3, 2000, 12000, "assets/effects/temp/string_god_thread.png");
    }
}
