package clashroyale;

import utility.IConstants;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Deze klasse bevat hulpmethode(s) voor de verschillende Clash of Clans klassen
 */
class CR_PROC {

    /**
     * Deze methode voert de verzoeken uit naar de server
     *
     * @param urlString     String om de request naar toe te sturen
     * @return              Response van de server
     */
    static String retrieveDataRoyaleAPI(String urlString) {
        StringBuffer content = new StringBuffer();

        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("User-Agent", IConstants.CRUSERAGENT);
            con.setRequestProperty("auth", IConstants.CRAPIKEY);

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
            e.printStackTrace();
        }

        return String.valueOf(content);
    }
}