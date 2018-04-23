package roda;

public class R_BoeteSpecificatieContainer {
    private int code;
    private String omschrijving;
    private double bedrag;
    private boolean isKratje;

    public R_BoeteSpecificatieContainer(int code, String omschrijving, double bedrag, boolean isKratje) {
        this.code = code;
        this.omschrijving = omschrijving;
        this.bedrag = bedrag;
        this.isKratje = isKratje;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public void setOmschrijving(String omschrijving) {
        this.omschrijving = omschrijving;
    }

    public double getBedrag() {
        return bedrag;
    }

    public void setBedrag(double bedrag) {
        this.bedrag = bedrag;
    }

    public boolean isKratje() {
        return isKratje;
    }

    public void setKratje(boolean kratje) {
        isKratje = kratje;
    }
}
