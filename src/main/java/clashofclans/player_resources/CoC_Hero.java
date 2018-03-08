package clashofclans.player_resources;

public class CoC_Hero {
    private String name;
    private int level;
    private String village;

    public CoC_Hero(String name, int level, String village) {
        this.name = name;
        this.level = level;
        this.village = village;
    }

    public String getName() {
        return name;
    }

    public int getLevel() {
        return level;
    }

    public String getVillage() {
        return village;
    }
}
