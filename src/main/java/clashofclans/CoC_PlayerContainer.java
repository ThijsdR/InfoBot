package clashofclans;

/**
 * Klasse om gegevens van een individuele speler in op te slaan
 */
public class CoC_PlayerContainer {

    /* Velden */
    private int positionInClan;
    private String name;
    private String playerTag;
    private int expLevel;
    private int trophyCount;
    private String clanRole;
    private int unitsDonated;
    private int unitsReceived;
    private double ratio;

    /* Constructor */
    CoC_PlayerContainer(int positionInClan, String name, String playerTag, int expLevel, int trophyCount, String clanRole, int unitsDonated, int unitsReceived, double ratio) {
        this.positionInClan = positionInClan;
        this.name = name;
        this.playerTag = playerTag;
        this.expLevel = expLevel;
        this.trophyCount = trophyCount;
        this.clanRole = clanRole;
        this.unitsDonated = unitsDonated;
        this.unitsReceived = unitsReceived;
        this.ratio = ratio;
    }

    /* Getters */
    public int getPositionInClan() {
        return positionInClan;
    }

    public String getName() {
        return name;
    }

    public String getPlayerTag() {
        return playerTag;
    }

    public int getExpLevel() {
        return expLevel;
    }

    public int getTrophyCount() {
        return trophyCount;
    }

    public String getClanRole() {
        return clanRole;
    }

    public int getUnitsDonated() {
        return unitsDonated;
    }

    public int getUnitsReceived() {
        return unitsReceived;
    }

    public double getRatio() {
        return ratio;
    }
}