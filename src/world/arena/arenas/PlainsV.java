package world.arena.arenas;

public class PlainsV extends StandardArena {

    public PlainsV() {
        super(2000, 2000, 5, "Plains");
    }

    @Override
    protected void setupEnemyTypes() {
        // Plains V: All enemies, highest frequency of harder enemies
        availableEnemyTypes.add("Enemy1"); // Basic pistol
        availableEnemyTypes.add("Enemy2"); // Basic rifle
        availableEnemyTypes.add("Enemy3"); // Basic shotgun
        availableEnemyTypes.add("Enemy4"); // Basic SMG
        availableEnemyTypes.add("Enemy5"); // Basic sniper
        availableEnemyTypes.add("Enemy6"); // Basic sword
        availableEnemyTypes.add("Enemy7"); // Basic hammer
        availableEnemyTypes.add("Enemy8"); // Basic dagger
        availableEnemyTypes.add("Enemy9"); // Basic scythe
    }
}
