package entity;

public class PlayerStats {

    private int kills = 0;
    private int deaths = 0;
    private double damageDealt = 0;
    private double damageTaken = 0;
    private int shotsFired = 0;
    private int shotsHit = 0;

    public void addKill() {
        kills++;
    }

    public void addDeath() {
        deaths++;
    }

    public void addDamageDealt(double damage) {
        damageDealt += damage;
    }

    public void addDamageTaken(double damage) {
        damageTaken += damage;
    }

    public void addShotFired() {
        shotsFired++;
    }

    public void addShotHit() {
        shotsHit++;
    }

    public int getKills() {
        return kills;
    }

    public int getDeaths() {
        return deaths;
    }

    public double getDamageDealt() {
        return damageDealt;
    }

    public double getDamageTaken() {
        return damageTaken;
    }

    public int getShotsFired() {
        return shotsFired;
    }

    public int getShotsHit() {
        return shotsHit;
    }

    public double getAccuracy() {
        if (shotsFired == 0) return 0.0;
        return (double) shotsHit / shotsFired * 100.0;
    }

    public void reset() {
        kills = 0;
        deaths = 0;
        damageDealt = 0;
        damageTaken = 0;
        shotsFired = 0;
        shotsHit = 0;
    }
}
