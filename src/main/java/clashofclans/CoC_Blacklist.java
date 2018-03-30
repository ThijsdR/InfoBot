package clashofclans;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Klasse met methoden welke betrekking hebben op de zwarte lijst
 */
public class CoC_Blacklist {

    /**
     * Deze methode voegt een speler toe aan de zwarte lijst
     *
     * @param playerTag Tag van de toe te voegen speler
     * @param reason    Reden waarom de speler moet worden toegevoegd
     * @param con       Connectie naar de sql server
     * @return          Informatie over de toegevoegde speler ter bevestiging
     */
    public static String addToCOCBlacklist(String playerTag, String[] reason, Connection con, String cocApiKey) {

        /* Haal de naam op van de speler gekoppeld aan de spelerstag */
        String playerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + playerTag.replace("#", "%23"), cocApiKey);
        JSONObject playerJson = new JSONObject(playerData);
        String playerName = playerJson.getString("name");

        /* Wanneer er geen speler is gevonden bij de opgegeven spelerstag */
        if (playerName == null) {
            return "_Er is geen speler gevonden met deze spelerstag..._";
        }

        /* Maak een string van de opgegeven reden */
        StringBuilder reasonBuilder = new StringBuilder();
        for (String s : reason) {
            reasonBuilder.append(s).append(" ");
        }

        /* Voeg de opgegeven speler, tag en reden toe aan de database */
        Statement stmt;
        try {
            stmt = con.createStatement();
            stmt.execute("INSERT INTO blacklist (Tag, Name, Reason) VALUES ('" + playerTag + "','" + playerName + "','" + String.valueOf(reasonBuilder) + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "_" + playerName + " (" + playerTag + ") is toegevoegd aan de zwarte lijst vanwege: " + String.valueOf(reasonBuilder) + "_";
    }

    /**
     * Deze methode verwijdert de opgegeven speler van de zwarte lijst
     *
     * @param playerTag Tag van de te verwijderen voegen speler
     * @param con       Connectie naar de sql server
     * @return          Informatie over de verwijderde speler ter bevestiging
     */
    public static String removeFromCOCBlacklist(String playerTag, Connection con) {

        /* Controleer of de speler op de zwarte lijst staat */
        Statement stmt;
        String player = null;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Name FROM blacklist WHERE Tag = '" + playerTag + "'");
            while (rs.next()) {
                player = rs.getString("Name");
            }

            /* Als een speler im de database is gevonden met de opgegeven tag, verwijder deze */
            if (!(player == null)) {
                stmt.execute("DELETE FROM blacklist WHERE Tag = '" + playerTag + "'");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!(player == null)) {
            return "_" + player + " (" + playerTag + ") is verwijderd van de zwarte lijst!_";
        } else {
            return "_Deze speler stond niet op de zwarte lijst_";
        }
    }

    /**
     * Deze methode controleert of de opgegeven speler op de zwarte lijst staat
     *
     * @param playerTag De te controleren speler
     * @param con       Connectie naar de sql server
     * @return          Bevestiging of de speler wel of niet op de zwarte lijst staat
     */
    public static String checkBlacklistPlayer(String playerTag, Connection con) {
        Statement stmt;
        String player = null;
        String reason = null;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Name, Reason FROM blacklist WHERE Tag = '" + playerTag + "'");
            while (rs.next()) {
                player = rs.getString("Name");
                reason = rs.getString("Reason");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!(player == null)) {
            return "_" + player + " (" + playerTag + ") staat op de zwarte lijst vanwege:\n" + reason + "_";
        } else {
            return "_De speler met de opgegeven spelerstag (" + playerTag + ") staat niet op de zwarte lijst_";
        }
    }

    /**
     * Deze methode haalt alle spelersname, tags en redenen op uit de database die op de zwarte lijst staan
     * en geeft dit in een File bestand
     *
     * @param con       Connectie naar de sql server
     * @return          Bestand met alle spelers op de zwarte lijst
     */
    public static File getBlacklist(Connection con) {
        File blacklistFile = new File("Blacklist.txt");
        Statement stmt;
        StringBuilder blacklistBuilder = new StringBuilder();
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM blacklist");
            while (rs.next()) {
                blacklistBuilder.append(rs.getString("Name")).append(" (")
                        .append(rs.getString("Tag")).append(") - ")
                        .append(rs.getString("Reason")).append("\n");
            }
            FileUtils.writeStringToFile(blacklistFile, String.valueOf(blacklistBuilder));
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        return blacklistFile;
    }
}
