package clashofclans;

import clashofclans.player_resources.CoC_Buildings;
import clashofclans.player_resources.CoC_Hero;
import clashofclans.player_resources.CoC_Spells;
import clashofclans.player_resources.CoC_Troops;

import java.util.ArrayList;
import java.util.List;

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

    private ArrayList<CoC_Buildings> buildingLevels;
    private ArrayList<CoC_Troops> troopLevels;
    private ArrayList<CoC_Spells> spellLevels;
    private ArrayList<CoC_Hero> heroLevels;

    private int townhallLevel;

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

    CoC_PlayerContainer(ArrayList<CoC_Buildings> buildingLevels, ArrayList<CoC_Troops> troopLevels, ArrayList<CoC_Spells> spellLevels, ArrayList<CoC_Hero> heroLevels) {
        this.buildingLevels = buildingLevels;
        this.troopLevels = troopLevels;
        this.spellLevels = spellLevels;
        this.heroLevels = heroLevels;
    }

    CoC_PlayerContainer(String name, String playerTag, int townhallLevel, int trophyCount) {
        this.name = name;
        this.playerTag = playerTag;
        this.townhallLevel = townhallLevel;
        this.trophyCount = trophyCount;
    }

    CoC_PlayerContainer(String name, String playerTag) {
        this.name = name;
        this.playerTag = playerTag;
    }

    /* Getters */
    public int getPositionInClan() {
        return positionInClan;
    }
    public String getName() {
        return name;
    }
    String getPlayerTag() {
        return playerTag;
    }
    public int getExpLevel() {
        return expLevel;
    }
    int getTrophyCount() {
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

    public List<CoC_Buildings> getBuildingLevels() {
        return buildingLevels;
    }
    public List<CoC_Troops> getTroopLevels() {
        return troopLevels;
    }
    public List<CoC_Spells> getSpellLevels() {
        return spellLevels;
    }
    List<CoC_Hero> getHeroLevels() {
        return heroLevels;
    }

    int getTownhallLevel() {
        return townhallLevel;
    }

    public void setBuildingLevels(ArrayList<CoC_Buildings> buildingLevels) {
        this.buildingLevels = buildingLevels;
    }
    public void setTroopLevels(ArrayList<CoC_Troops> troopLevels) {
        this.troopLevels = troopLevels;
    }
    public void setSpellLevels(ArrayList<CoC_Spells> spellLevels) {
        this.spellLevels = spellLevels;
    }
    void setHeroLevels(ArrayList<CoC_Hero> heroLevels) {
        this.heroLevels = heroLevels;
    }
}
