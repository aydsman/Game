package combat.powers;

public class KingOfCurses extends Power {
    public KingOfCurses() {
        this(5);
    }

    public KingOfCurses(int tier) {
        super(tier);
        name = "King of Curses";
        iconPath = "assets/items/powers/king of curses/king_of_curses_icon.png";
        description = "The ultimate cursed technique of Sukuna, the King of Curses.";

        // Define moves using addMove()
        addMove(new Move("Cleave", 1));
        addMove(new Move("Fuga", 2));
        addMove(new Move("World Cutting Slash", 3));
        addMove(new Move("Malevolent Shrine", 4));

        applyTierMultipliers();
    }

    private void applyTierMultipliers() {
        // Add tier-specific effects here later
    }
}
