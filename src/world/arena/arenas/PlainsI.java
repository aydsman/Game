package world.arena.arenas;

public class PlainsI extends StandardArena {

    public PlainsI() {
        super(2000, 2000, 1, "Plains");
    }

    @Override
    protected void setupEnemyTypes() {
        // Plains I: Basic enemies only, mostly ranged with some melee introduction
        availableEnemyTypes.add("Enemy1"); // Basic pistol
        availableEnemyTypes.add("Enemy2"); // Basic rifle
        availableEnemyTypes.add("Enemy3"); // Basic shotgun
        availableEnemyTypes.add("Enemy6"); // Basic sword (first melee)
    }
}
