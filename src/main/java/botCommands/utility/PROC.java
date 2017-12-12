package botCommands.utility;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class PROC {
    public static String retrieveData(String urlString) {
        StringBuffer content = new StringBuffer();

        try {
            URL url = new URL(urlString);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("Authorization", "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiIsImtpZCI6IjI4YTMxOGY3LTAwMDAtYTFlYi03ZmExLTJjNzQzM2M2Y2NhNSJ9.eyJpc3MiOiJzdXBlcmNlbGwiLCJhdWQiOiJzdXBlcmNlbGw6Z2FtZWFwaSIsImp0aSI6IjFjZmVlZjA1LThhZjUtNGI5Mi04NjFhLWIzYzUxNDVmYzI3OCIsImlhdCI6MTUxMzA3NDQ3OSwic3ViIjoiZGV2ZWxvcGVyLzY3ZDQxYzE1LWIzN2EtMGMzNy0yMGViLTQ3Y2JjOTQzNWE3ZSIsInNjb3BlcyI6WyJjbGFzaCJdLCJsaW1pdHMiOlt7InRpZXIiOiJkZXZlbG9wZXIvc2lsdmVyIiwidHlwZSI6InRocm90dGxpbmcifSx7ImNpZHJzIjpbIjk1Ljk3LjExNS4xMjYiXSwidHlwZSI6ImNsaWVudCJ9XX0.UTZ8CAvHwPoYvqbp3tOHl0n006Ph1LesPNmpBHmKLQZI80uU8aHefqk_BtY7xTjDwClZlAfnzradprqwFQgLzA");

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
