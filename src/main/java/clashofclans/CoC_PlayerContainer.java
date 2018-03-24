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

    private ArrayList<CoC_Hero> heroLevels;

    private int townhallLevel;

    private ArrayList<CoC_WarAttackContainer> warAttacks;

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

    CoC_PlayerContainer(String name, String playerTag, int townhallLevel, int trophyCount) {
        this.name = name;
        this.playerTag = playerTag;
        this.townhallLevel = townhallLevel;
        this.trophyCount = trophyCount;
    }

    CoC_PlayerContainer(int positionInClan, String name, int townhallLevel) {
        this.positionInClan = positionInClan;
        this.name = name;
        this.townhallLevel = townhallLevel;
    }

    /* Getters */
    int getPositionInClan() {
        return positionInClan;
    }
    public String getName() {
        return name;
    }
    String getPlayerTag() {
        return playerTag;
    }
    int getTrophyCount() {
        return trophyCount;
    }
    List<CoC_Hero> getHeroLevels() {
        return heroLevels;
    }
    int getTownhallLevel() {
        return townhallLevel;
    }

    void setHeroLevels(ArrayList<CoC_Hero> heroLevels) {
        this.heroLevels = heroLevels;
    }
}
