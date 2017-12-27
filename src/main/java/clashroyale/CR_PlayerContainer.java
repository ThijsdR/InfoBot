package clashroyale;

/**
 * Klasse welke alle data bevat over een individuele speler
 */
public class CR_PlayerContainer {
    private String name;
    private String tag;
    private int rank;
    private String role;
    private int expLevel;
    private int trophies;
    private int clanchestCrowns;
    private int donations;
    private int donationsReceived;
    private double ratio;

    /* Constructor */
    public CR_PlayerContainer(String name, String tag, int rank, String role, int expLevel, int trophies, int clanchestCrowns, int donations, int donationsReceived, double ratio) {
        this.name = name;
        this.tag = tag;
        this.rank = rank;
        this.role = role;
        this.expLevel = expLevel;
        this.trophies = trophies;
        this.clanchestCrowns = clanchestCrowns;
        this.donations = donations;
        this.donationsReceived = donationsReceived;
        this.ratio = ratio;
    }


    /* Getters */
    public String getName() {
        return name;
    }

    public String getTag() {
        return tag;
    }

    public int getRank() {
        return rank;
    }

    public String getRole() {
        return role;
    }

    public int getExpLevel() {
        return expLevel;
    }

    public int getTrophies() {
        return trophies;
    }

    public int getClanchestCrowns() {
        return clanchestCrowns;
    }

    public int getDonations() {
        return donations;
    }

    public int getDonationsReceived() {
        return donationsReceived;
    }

    public double getRatio() {
        return ratio;
    }
}
