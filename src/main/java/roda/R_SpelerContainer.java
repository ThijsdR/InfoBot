package roda;

public class R_SpelerContainer {
    private int id;
    private String naam;
    private double openstaandBedrag;
    private double betaaldBedrag;

    public R_SpelerContainer(int id, String naam, double openstaandBedrag, double betaaldBedrag) {
        this.id = id;
        this.naam = naam;
        this.openstaandBedrag = openstaandBedrag;
        this.betaaldBedrag = betaaldBedrag;
    }

    public R_SpelerContainer(int id, String naam) {
        this.id = id;
        this.naam = naam;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public double getOpenstaandBedrag() {
        return openstaandBedrag;
    }

    public void setOpenstaandBedrag(double openstaandBedrag) {
        this.openstaandBedrag = openstaandBedrag;
    }

    public double getBetaaldBedrag() {
        return betaaldBedrag;
    }

    public void setBetaaldBedrag(double betaaldBedrag) {
        this.betaaldBedrag = betaaldBedrag;
    }
}
