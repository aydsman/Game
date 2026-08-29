package entity;

public class PlayerStats {
    private int kills = 0;
    private int deaths = 0;
    private double damageDealt = 0;
    private double damageTaken = 0;
    private int shotsFired = 0;
    private int shotsHit = 0;

    public void reset() {
        kills = 0;
        deaths = 0;
        damageDealt = 0;
        damageTaken = 0;
        shotsFired = 0;
        shotsHit = 0;
    }

    public int getKills() { return kills; }
    public void addKill() { kills++; }
    public int getDeaths() { return deaths; }
    public void addDeath() { deaths++; }
    public double getDamageDealt() { return damageDealt; }
    public void addDamageDealt(double amount) { damageDealt += amount; }
    public double getDamageTaken() { return damageTaken; }
    public void addDamageTaken(double amount) { damageTaken += amount; }
    public void addShotFired() { shotsFired++; }
    public void addShotHit() { shotsHit++; }
    public double getAccuracy() {
        if (shotsFired == 0) return 0;
        return (shotsHit * 100.0) / shotsFired;
    }
}
