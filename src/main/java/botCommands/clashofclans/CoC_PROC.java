package botCommands.clashofclans;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class CoC_PROC {
    public static String retrieveDataSupercellAPI(String urlString) {
        StringBuffer content = new StringBuffer();

        try {
            URL url = new URL(urlString);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6IjhmMzFmNjMxLWE2Y2YtNDI0NC04YjRiLTQ2YjQyM2Y1ZTVhMiIsImlhdCI6MTUxMzEwMjE1OCwic3ViIjoiZGV2ZWxvcGVyLzY3ZDQxYzE1LWIzN2EtMGMzNy0yMGViLTQ3Y2JjOTQzNWE3ZSIsInNjb3BlcyI6WyJjbGFzaCJdLCJsaW1pdHMiOlt7InRpZXIiOiJkZXZlbG9wZXIvc2lsdmVyIiwidHlwZSI6InRocm90dGxpbmcifSx7ImNpZHJzIjpbIjk1Ljk3LjExNS4xMjYiLCI3Ny4xNjMuMTguMzQiXSwidHlwZSI6ImNsaWVudCJ9XX0.YIF0QjSBnu422UCrInGQ_XHbl-2tjfVIoRYeWaYYlhiE-ImCV1gijpRSB_j5EsoGbV9Q1sRUNFFVHMP4cbIksw");

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