package botCommands.clashofclans;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

public class CoC_PROC {
    private static int httpsCode;
    private static boolean isServerOnline = true;

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

    public static CoC_ServerState checkServerStatusCoC() {
        try {
            URL url = new URL("https://api.clashofclans.com/v1/clans?name=%23J0C9CPY");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6IjhmMzFmNjMxLWE2Y2YtNDI0NC04YjRiLTQ2YjQyM2Y1ZTVhMiIsImlhdCI6MTUxMzEwMjE1OCwic3ViIjoiZGV2ZWxvcGVyLzY3ZDQxYzE1LWIzN2EtMGMzNy0yMGViLTQ3Y2JjOTQzNWE3ZSIsInNjb3BlcyI6WyJjbGFzaCJdLCJsaW1pdHMiOlt7InRpZXIiOiJkZXZlbG9wZXIvc2lsdmVyIiwidHlwZSI6InRocm90dGxpbmcifSx7ImNpZHJzIjpbIjk1Ljk3LjExNS4xMjYiLCI3Ny4xNjMuMTguMzQiXSwidHlwZSI6ImNsaWVudCJ9XX0.YIF0QjSBnu422UCrInGQ_XHbl-2tjfVIoRYeWaYYlhiE-ImCV1gijpRSB_j5EsoGbV9Q1sRUNFFVHMP4cbIksw");

            httpsCode = con.getResponseCode();

            con.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

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
}
