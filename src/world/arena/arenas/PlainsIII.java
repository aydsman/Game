package world.arena.arenas;

public class PlainsIII extends StandardArena {

    public PlainsIII() {
        super(2000, 2000, 3, "Plains");
    }

    @Override
    protected void setupEnemyTypes() {
        // Plains III: All basic enemies plus hammer introduction
        availableEnemyTypes.add("Enemy1"); // Basic pistol
        availableEnemyTypes.add("Enemy2"); // Basic rifle
        availableEnemyTypes.add("Enemy3"); // Basic shotgun
        availableEnemyTypes.add("Enemy4"); // Basic SMG
        availableEnemyTypes.add("Enemy5"); // Basic sniper
        availableEnemyTypes.add("Enemy6"); // Basic sword
        availableEnemyTypes.add("Enemy7"); // Basic hammer
        availableEnemyTypes.add("Enemy8"); // Basic dagger
    }
}
