package world.arena.arenas;

public class PlainsII extends StandardArena {

    public PlainsII() {
        super(2000, 2000, 2, "Plains");
    }

    @Override
    protected void setupEnemyTypes() {
        // Plains II: More variety, introduce SMGs and basic melee
        availableEnemyTypes.add("Enemy1"); // Basic pistol
        availableEnemyTypes.add("Enemy2"); // Basic rifle
        availableEnemyTypes.add("Enemy3"); // Basic shotgun
        availableEnemyTypes.add("Enemy4"); // Basic SMG
        availableEnemyTypes.add("Enemy6"); // Basic sword
        availableEnemyTypes.add("Enemy8"); // Basic dagger
    }
}
