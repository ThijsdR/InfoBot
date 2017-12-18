package botCommands.clashofclans;

public class CoC_PlayerContainer {
    private int positionInClan;
    private String name;
    private String playerTag;
    private int expLevel;
    private int trophyCount;
    private String clanRole;
    private int unitsDonated;
    private int unitsReceived;

    public CoC_PlayerContainer(int positionInClan, String name, String playerTag, int expLevel, int trophyCount, String clanRole, int unitsDonated, int unitsReceived) {
        this.positionInClan = positionInClan;
        this.name = name;
        this.playerTag = playerTag;
        this.expLevel = expLevel;
        this.trophyCount = trophyCount;
        this.clanRole = clanRole;
        this.unitsDonated = unitsDonated;
        this.unitsReceived = unitsReceived;
    }

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
}
