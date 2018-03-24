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
        String playerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + playerTag.replace("#", "%23"), cocApiKey);
        JSONObject playerJson = new JSONObject(playerData);
        String playerName = playerJson.getString("name");
        StringBuilder reasonBuilder = new StringBuilder();

        for (String s : reason) {
            reasonBuilder.append(s).append(" ");
        }

        Statement stmt;
        try {
            stmt = con.createStatement();
            stmt.execute("INSERT INTO blacklist (Tag, Name, Reason) VALUES ('" + playerTag + "','" + playerName + "','" + String.valueOf(reasonBuilder) + "')");
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return playerName + " (" + playerTag + ") is toegevoegd aan de zwarte lijst vanwege: " + String.valueOf(reasonBuilder);
    }

    /**
     * Deze methode verwijdert de opgegeven speler van de zwarte lijst
     *
     * @param playerTag Tag van de te verwijderen voegen speler
     * @param con       Connectie naar de sql server
     * @return          Informatie over de verwijderde speler ter bevestiging
     */
    public static String removeFromCOCBlacklist(String playerTag, Connection con) {
        Statement stmt;
        String player = null;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT Name FROM blacklist WHERE Tag = '" + playerTag + "'");
            while (rs.next()) {
                player = rs.getString("Name");
            }

            if (!(player == null)) {
                stmt.execute("DELETE FROM blacklist WHERE Tag = '" + playerTag + "'");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!(player == null)) {
            return player + " (" + playerTag + ") is verwijderd van de zwarte lijst!";
        } else {
            return "Deze speler stond niet op de zwarte lijst";
        }
    }

    /**
     *
     * @param playerTag
     * @param con
     * @return
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
            return player + " (" + playerTag + ") staat op de zwarte lijst vanwege:\n" + reason;
        } else {
            return "De speler met de opgegeven spelerstag (" + playerTag + ") staat niet op de zwarte lijst";
        }
    }

    /**
     *
     * @param con
     * @return
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
