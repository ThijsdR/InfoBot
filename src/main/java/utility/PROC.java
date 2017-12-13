package utility;

import com.vdurmont.emoji.EmojiParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class PROC {
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

    public static String retrieveDataWeatherAPI(String urlString) {
        StringBuffer content = new StringBuffer();

        try {
            URL url = new URL(urlString);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Content-Type", "application/json");

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

    public static String weatherIconChecker(String icon) {
        String result = "";

        if (icon.equals("chanceflurries")) { result = (EmojiParser.parseToUnicode(" :snowflake::question:")); }
        if (icon.equals("chancerain")) { result = (EmojiParser.parseToUnicode(" :umbrella::question:")); }
        if (icon.equals("chancesleet")) { result = (EmojiParser.parseToUnicode(" :snowflake::question:")); }
        if (icon.equals("chancesnow")) { result = (EmojiParser.parseToUnicode(" :snowflake::question:")); }
        if (icon.equals("chancetstorms")) { result = (EmojiParser.parseToUnicode(" :zap::question:")); }
        if (icon.equals("clear")) { result = (EmojiParser.parseToUnicode(" :sunny:")); }
        if (icon.equals("cloudy")) { result = (EmojiParser.parseToUnicode(" :cloud:")); }
        if (icon.equals("flurries")) { result = (EmojiParser.parseToUnicode(" :snowflake:")); }
        if (icon.equals("fog")) { result = (EmojiParser.parseToUnicode(" :foggy:")); }
        if (icon.equals("hazy")) { result = (EmojiParser.parseToUnicode(" :foggy:")); }
        if (icon.equals("mostlycloudy")) { result = (EmojiParser.parseToUnicode(" :cloud:")); }
        if (icon.equals("mostlysunny")) { result = (EmojiParser.parseToUnicode(" :partly_sunny:")); }
        if (icon.equals("partlycloudy")) { result = (EmojiParser.parseToUnicode(" :cloud:")); }
        if (icon.equals("partlysunny")) { result = (EmojiParser.parseToUnicode(" :partly_sunny:")); }
        if (icon.equals("sleet")) { result = (EmojiParser.parseToUnicode(" :snowflake:")); }
        if (icon.equals("rain")) { result = (EmojiParser.parseToUnicode(" :umbrella:")); }
        if (icon.equals("snow")) { result = (EmojiParser.parseToUnicode(" :snowflake:")); }
        if (icon.equals("sunny")) { result = (EmojiParser.parseToUnicode(" :sunny:")); }
        if (icon.equals("tstorms")) { result = (EmojiParser.parseToUnicode(" :zap:")); }

        return result;
    }
}
