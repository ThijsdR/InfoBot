package clashofclans;

import com.vdurmont.emoji.EmojiParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Deze klasse bevat methode(s) die betrekking hebben op een specifiek clanlid
 */
public class CoC_ClanMember {

    /**
     * Haal alle relevante data op van een specifieke speler in de opgegeven clan
     *
     * @param urlString         String om de request naar toe te sturen
     * @param memberName        Naam van de speler waarop gezocht moet worden
     * @return                  Alle data van het betreffende clanlid
     */
    public static String getClanMemberInfo(String urlString, String memberName) {

        /* Stuur aan de hand van de urlString een request naar de server */
        String returnJson = CoC_PROC.retrieveDataSupercellAPI(urlString);

        /* Return wanneer de server niks terugstuurt */
        if (returnJson.equals("SERVER ERROR")) {
            return "Ik kan de gevraagde data niet opvragen van de server...\nDe server is hoogstwaarschijnlijk offline";
        }

        /* Haal alle benodigde data uit de response */
        JSONObject json = new JSONObject(returnJson);
        JSONArray jsonArray = json.getJSONArray("items");

        /* Genereer het antwoord op basis van de response */
        StringBuilder botRespone = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.getJSONObject(i).getString("name").equals(memberName)) {
                botRespone.append("\n");
                botRespone.append("Name: ").append(jsonArray.getJSONObject(i).getString("name")).append("\n");
                botRespone.append("Tag: ").append(jsonArray.getJSONObject(i).getString("tag")).append("\n");
                botRespone.append("Level: ").append(jsonArray.getJSONObject(i).getInt("expLevel")).append("\n");
                botRespone.append(EmojiParser.parseToUnicode("Trophies: :trophy:")).append(jsonArray.getJSONObject(i).getInt("trophies")).append("\n");
                botRespone.append("Role: ").append(jsonArray.getJSONObject(i).getString("role")).append("\n\n");
                botRespone.append(EmojiParser.parseToUnicode("Donations: :arrow_forward:")).append(jsonArray.getJSONObject(i).getInt("donations")).append("\n");
                botRespone.append(EmojiParser.parseToUnicode("Received: :arrow_backward:")).append(jsonArray.getJSONObject(i).getInt("donationsReceived")).append("\n");
            }
        }

        /* Return op basis of het lid wel of niet bestaat */
        if (!String.valueOf(botRespone).isEmpty()) {
            return String.valueOf(botRespone);
        } else {
            return "Geen resultaten gevonden\nZit het opgegeven lid wel in de clan?\nZorg dat eventuele hoofdletters ook goed staan.";
        }
    }

    // TODO Finish
    public static String getClanMonitorInfo(String urlString, ArrayList<CoC_PlayerContainer> cocPlayers) {

        StringBuilder botResponse = new StringBuilder();

        /* Stuur aan de hand van de urlString een request naar de server */
        String returnJson = CoC_PROC.retrieveDataSupercellAPI(urlString);

        /* Haal alle benodigde data uit de response */
        JSONObject json = new JSONObject(returnJson);
        JSONArray jsonArray = json.getJSONArray("items");

        for (int i = 0; i < jsonArray.length(); i++) {
            String playerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/%23" + jsonArray.getJSONObject(i).getString("tag").substring(1));

            JSONObject playerJson = new JSONObject(playerData);
            JSONArray playerJsonArray = playerJson.getJSONArray("troops");


        }

        return String.valueOf(botResponse);
    }
}
