package roda;

import java.util.Date;

public class R_BoeteContainer {
    private int boeteId;
    private String datum;
    private String spelerNaam;
    private boolean isBetaald;
    private double boeteBedrag;
    private String boeteOmschrijving;

    private int spelerId;
    private String boeteCode;


    public R_BoeteContainer(int boeteId, int spelerId, boolean isBetaald, String boeteCode, String datum) {
        this.boeteId = boeteId;
        this.spelerId = spelerId;
        this.isBetaald = isBetaald;
        this.boeteCode = boeteCode;
        this.datum = datum;
    }

    public R_BoeteContainer(int boeteId, String spelerNaam, boolean isBetaald, String boeteOmschrijving, double boeteBedrag, String datum) {
        this.boeteId = boeteId;
        this.spelerNaam = spelerNaam;
        this.isBetaald = isBetaald;
        this.boeteOmschrijving = boeteOmschrijving;
        this.boeteBedrag = boeteBedrag;
        this.datum = datum;
    }

    public int getBoeteId() {
        return boeteId;
    }

    public void setBoeteId(int boeteId) {
        this.boeteId = boeteId;
    }

    public int getSpelerId() {
        return spelerId;
    }

    public void setSpelerId(int spelerId) {
        this.spelerId = spelerId;
    }

    public boolean isBetaald() {
        return isBetaald;
    }

    public void setBetaald(boolean betaald) {
        isBetaald = betaald;
    }

    public String getBoeteCode() {
        return boeteCode;
    }

    public void setBoeteCode(String boeteCode) {
        this.boeteCode = boeteCode;
    }

    public String getDatum() {
        return datum;
    }

    public void setDatum(String datum) {
        this.datum = datum;
    }

    public String getSpelerNaam() {
        return spelerNaam;
    }

    public void setSpelerNaam(String spelerNaam) {
        this.spelerNaam = spelerNaam;
    }

    public String getBoeteOmschrijving() {
        return boeteOmschrijving;
    }

    public void setBoeteOmschrijving(String boeteOmschrijving) {
        this.boeteOmschrijving = boeteOmschrijving;
    }

    public double getBoeteBedrag() {
        return boeteBedrag;
    }

    public void setBoeteBedrag(double boeteBedrag) {
        this.boeteBedrag = boeteBedrag;
    }
}
