package clashofclans;

import com.vdurmont.emoji.EmojiParser;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Deze klasse bevat methode(s) welke betrekking hebben op de gehele clan
 */
public class CoC_Clan {

    /**
     * Deze methode wordt gebruikt om alle relevante informatie op te halen van de opgegeven clan.
     * Van de clan wordt de naam, het clanlevel, de tag, de score, het type, het ledenaantal en de locatie opgevraagd
     *
     * @param urlString     String om de request naar toe te sturen
     * @return              Een String met alle informatie omtrent de opgegeven clan
     */
    public static String getClanInfo(String urlString) {

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
        StringBuilder botResponse = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append("Clan: ").append(jsonArray.getJSONObject(i).getString("name")).append("\n");
            botResponse.append("Level: ").append(jsonArray.getJSONObject(i).getInt("clanLevel")).append("\n");
            botResponse.append("Clan tag: ").append(jsonArray.getJSONObject(i).getString("tag")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Clanpoints: :trophy:")).append(jsonArray.getJSONObject(i).getInt("clanPoints")).append("\n");
            botResponse.append("Clan type: ").append(jsonArray.getJSONObject(i).getString("type")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Members: :family:")).append(jsonArray.getJSONObject(i).getInt("members")).append("\n");
            botResponse.append("Country: ").append(jsonArray.getJSONObject(i).getJSONObject("location").getString("name")).append("\n");
            botResponse.append("-~-~-~-~-~-~-~-~\n");
        }

        return String.valueOf(botResponse);
    }

    /**
     * Deze methode haalt alle donatiegegevens op van de opgegeven clan.
     * De hoeveelheid gedoneerde en ontvangen troepen worden getoond.
     *
     * @param urlString     String om de request naar toe te sturen
     * @return              Een String met alle donatiegegevens van de opgegeven clan
     */
    public static String getClanDonaties(String urlString) {

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
        StringBuilder botResponse = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append("-").append(jsonArray.getJSONObject(i).getInt("clanRank")).append("-\n");
            botResponse.append("Name: ").append(jsonArray.getJSONObject(i).getString("name")).append("\n");
            botResponse.append("Tag: ").append(jsonArray.getJSONObject(i).getString("tag")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Donations: :arrow_forward:")).append(jsonArray.getJSONObject(i).getInt("donations")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Received: :arrow_backward:")).append(jsonArray.getJSONObject(i).getInt("donationsReceived")).append("\n");
            botResponse.append("-~-~-~-~-~-~-~-~\n");
        }

        return String.valueOf(botResponse);
    }

    /**
     * Deze methode haalt alle relevante data op van clanleden van de opgegeven clan.
     * De positie, de naam, de tag, het level, de trophies en de rol worden per lid opgehaald.
     *
     * @param urlString     String om de request naar toe te sturen
     * @return              Alle relevante data omtrent de leden van de opgegeven clan
     */
    public static String getClanMembers(String urlString) {

        /* Stuur aan de hand van de urlString een request naar de server */
        String returnJson = CoC_PROC.retrieveDataSupercellAPI(urlString);

        /* Return wanneer de server niks terugstuurt */
        if (returnJson.equals("SERVER ERROR")) {
            return "Ik kan de gevraagde data niet opvragen van de server...\nDe server is hoogstwaarschijnlijk offline";
        }

        /* Haal alle benodigde data uit de response */
        JSONObject json = new JSONObject(returnJson);
        JSONArray jsonArray = json.getJSONArray("items");

        StringBuilder botResponse = new StringBuilder();
        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append("-").append(jsonArray.getJSONObject(i).getInt("clanRank")).append("-\n");
            botResponse.append("Name: ").append(jsonArray.getJSONObject(i).getString("name")).append("\n");
            botResponse.append("Tag: ").append(jsonArray.getJSONObject(i).getString("tag")).append("\n");
            botResponse.append("Level: ").append(jsonArray.getJSONObject(i).getInt("expLevel")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Trophies: :trophy:")).append(jsonArray.getJSONObject(i).getInt("trophies")).append("\n");
            botResponse.append("Role: ").append(jsonArray.getJSONObject(i).getString("role")).append("\n");
            botResponse.append("-~-~-~-~-~-~-~-~\n");
        }

        return String.valueOf(botResponse);
    }
}
