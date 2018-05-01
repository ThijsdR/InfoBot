package clashofclans;

import help.H_Help;
import org.apache.commons.io.FileUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;

/**
 * Deze klasse bevat hulpmethode(s) voor de verschillende Clash of Clans klassen
 */
public class CoC_PROC {

    /* Velden */
    private static int httpsCode;
    private static boolean isServerOnline = true;

    /**
     * Deze methode voert de verzoeken uit naar de server
     *
     * @param urlString     String om de request naar toe te sturen
     * @return              Response van de server
     */
    public static String retrieveDataSupercellAPI(String urlString, String apiKey) {
        StringBuffer content = new StringBuffer();

        try {
            URL url = new URL(urlString);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer " + apiKey);

            int responseCode = con.getResponseCode();
            if (responseCode == 503) {
                return "SERVER ERROR";
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;

            while ((inputLine = in.readLine()) != null) {
                content.append(inputLine);
            }

            in.close();
            con.disconnect();

        } catch (IOException e) {
            System.out.println(H_Help.exceptionStacktraceToString(e));
        }

        return String.valueOf(content);
    }

    /**
     * Deze methode schopt als het ware tegen de server aan om te kijken of deze nog online is.
     * De methode kijkt niet alleen of de server online of offline is,
     * maar ook of de server net een andere status heeft ingenomen.
     *
     * @return      Huidige serverstatus
     */
    public static CoC_ServerState getServerStatusCoC(String cocApiKey) {

        /* Probeer verbinding te maken met de server */
        try {
            URL url = new URL("https://api.clashofclans.com/v1/clans?name=%23J0C9CPY");
            HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
            httpsURLConnection.setRequestMethod("GET");
            httpsURLConnection.setRequestProperty("Authorization", "Bearer " + cocApiKey);

            /* Het antwoord van de server in de vorm van een code */
            httpsCode = httpsURLConnection.getResponseCode();

            httpsURLConnection.disconnect();
        } catch (IOException e) {
            System.out.println(H_Help.exceptionStacktraceToString(e));
        }

        /* Onderneem actie aan de hand van de gekregen code */
        if (isServerOnline && httpsCode == 503) {
            isServerOnline = false;
            return CoC_ServerState.COCWENTOFFLINE;
        }
        if (!isServerOnline && httpsCode == 503) {
            return CoC_ServerState.COCOFFLINE;
        }
        if (!isServerOnline && httpsCode == 200) {
            isServerOnline = true;
            return CoC_ServerState.COCWENTONLINE;
        }
        else {
            return CoC_ServerState.COCONLINE;
        }
    }

    public static ArrayList<Long> getSubscribedIds() {
        ArrayList<Long> ids = new ArrayList<>();

        try {
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/dbpass.txt")));
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ChatID FROM subscriberswar");

            while (rs.next()) {
                ids.add((long) rs.getInt("ChatID"));
            }

            rs.close();
            stmt.close();
            con.close();
        } catch (SQLException | ClassNotFoundException | IOException e) {
            System.out.println(H_Help.exceptionStacktraceToString(e));
        }

        return ids;
    }
}
