package clashroyale;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Deze klasse bevat methode(s) welke betrekking hebben op de gehele clan
 */
public class CR_Clan {

    /**
     * Deze methode wordt gebruikt om alle relevante informatie op te halen van de opgegeven clan.
     * Van de clan wordt de naam, de tag, de score, het ledenaantal, het type en de locatie opgevraagd
     *
     * @param urlString     String om de request naar toe te sturen
     * @return              Een String met alle informatie omtrent de opgegeven clan
     */
    public static String getClanInfo(String urlString) {

        /* Stuur aan de hand van de urlString een request naar de server */
        String returnJson = CR_PROC.retrieveDataRoyaleAPI(urlString);

        /* Return wanneer de server niks terugstuurt */
        if (returnJson.equals("SERVER ERROR")) {
            return "Ik kan de gevraagde data niet opvragen van de server...\nDe server is hoogstwaarschijnlijk offline";
        }

        /* Haal alle benodigde data uit de response */
        JSONObject json = new JSONObject(returnJson);
        JSONObject jsonLocation = json.getJSONObject("location");

        /* Genereer het antwoord op basis van de response */
        StringBuilder botResponse = new StringBuilder();
        botResponse.append("Clan: ").append(json.getString("name"));
        botResponse.append("\nTag: #").append(json.getString("tag"));
        botResponse.append("\nScore: ").append(json.getInt("score"));
        botResponse.append("\nMembers: ").append(json.getInt("memberCount"));
        botResponse.append("\nType: ").append(json.getString("type"));
        botResponse.append("\nLocation: ").append(jsonLocation.getString("name"));

        return String.valueOf(botResponse);
    }

    /**
     * Deze methode haalt alle data met betrekking tot de leden van de opgegeven clan
     *
     * @param urlString     String om de request naar toe te sturen
     * @return              Alle relevante data omtrent de leden van de opgegeven clan
     */
    public static String getClanMembers(String urlString) {

        /* Stuur aan de hand van de urlString een request naar de server */
        String returnJson = CR_PROC.retrieveDataRoyaleAPI(urlString);

        /* Return wanneer de server niks terugstuurt */
        if (returnJson.equals("SERVER ERROR")) {
            return "Ik kan de gevraagde data niet opvragen van de server...\nDe server is hoogstwaarschijnlijk offline";
        }

        /* Haal alle benodigde data uit de response */
        JSONObject json = new JSONObject(returnJson);
        JSONArray jsonArray = json.getJSONArray("members");

        /* Genereer het antwoord op basis van de response */
        StringBuilder botResponse = new StringBuilder();
        botResponse.append("Clan: ").append(json.getString("name"));
        botResponse.append("\nTag: #").append(json.getString("tag"));
        botResponse.append("\n------------------------\n");

        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append(jsonArray.getJSONObject(i).getString("name")).append(" (#").append(jsonArray.getJSONObject(i).getString("tag")).append(")");
            botResponse.append("\nLevel: ").append(jsonArray.getJSONObject(i).getInt("expLevel"));
            botResponse.append("\nTrophies: ").append(jsonArray.getJSONObject(i).getInt("trophies")).append("\n");
            botResponse.append("-~-~-~-~-~-~-~-~\n");
        }

        return String.valueOf(botResponse);
    }

    /**
     * Deze methode haalt alle data met betrekking tot de rollen van de leden van de opgegeven clan
     *
     * @param urlString     String om de request naar toe te sturen
     * @return              Alle relevante data omtrent de rollen van clanleden
     */
    public static String getClanRoles(String urlString) {

        /* Stuur aan de hand van de urlString een request naar de server */
        String returnJson = CR_PROC.retrieveDataRoyaleAPI(urlString);

        /* Return wanneer de server niks terugstuurt */
        if (returnJson.equals("SERVER ERROR")) {
            return "Ik kan de gevraagde data niet opvragen van de server...\nDe server is hoogstwaarschijnlijk offline";
        }

        /* Haal alle benodigde data uit de response */
        JSONObject json = new JSONObject(returnJson);
        JSONArray jsonArray = json.getJSONArray("members");

        /* Genereer het antwoord op basis van de response */
        StringBuilder botResponse = new StringBuilder();
        botResponse.append("Clan: ").append(json.getString("name"));
        botResponse.append("\nTag: #").append(json.getString("tag"));
        botResponse.append("\n------------------------\n");

        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append(jsonArray.getJSONObject(i).getString("name")).append(" (#").append(jsonArray.getJSONObject(i).getString("tag")).append(")").append("\n");
            botResponse.append("Role: ").append(jsonArray.getJSONObject(i).getString("role")).append("\n");
            botResponse.append("-~-~-~-~-~-~-~-~\n");
        }

        return String.valueOf(botResponse);
    }

    /**
     * Deze methode haalt alle data met betrekking tot de donaties van de leden van de opgegeven clan
     *
     * @param urlString     String om de request naar toe te sturen
     * @return              Alle relevante data omtrent de donaties van clanleden
     */
    public static String getClanDonations(String urlString) {

        /* Stuur aan de hand van de urlString een request naar de server */
        String returnJson = CR_PROC.retrieveDataRoyaleAPI(urlString);

        /* Return wanneer de server niks terugstuurt */
        if (returnJson.equals("SERVER ERROR")) {
            return "Ik kan de gevraagde data niet opvragen van de server...\nDe server is hoogstwaarschijnlijk offline";
        }

        /* Haal alle benodigde data uit de response */
        JSONObject json = new JSONObject(returnJson);
        JSONArray jsonArray = json.getJSONArray("members");

        /* Genereer het antwoord op basis van de response */
        StringBuilder botResponse = new StringBuilder();
        botResponse.append("Clan: ").append(json.getString("name"));
        botResponse.append("\nTag: #").append(json.getString("tag"));
        botResponse.append("\n------------------------\n");

        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append(jsonArray.getJSONObject(i).getString("name")).append(" (#").append(jsonArray.getJSONObject(i).getString("tag")).append(")");
            botResponse.append("Donations: ").append(jsonArray.getJSONObject(i).getInt("donations")).append("\n");
            botResponse.append("-~-~-~-~-~-~-~-~\n");
        }

        return String.valueOf(botResponse);
    }

    /**
     * Deze methode haalt alle data met betrekking tot de clanchestdeelname van de leden van de opgegeven clan
     *
     * @param urlString     String om de request naar toe te sturen
     * @return              Alle relevante data omtrent de clanchestdeelname van clanleden
     */
    public static String getClanchestContribution (String urlString) {

        /* Stuur aan de hand van de urlString een request naar de server */
        String returnJson = CR_PROC.retrieveDataRoyaleAPI(urlString);

        /* Return wanneer de server niks terugstuurt */
        if (returnJson.equals("SERVER ERROR")) {
            return "Ik kan de gevraagde data niet opvragen van de server...\nDe server is hoogstwaarschijnlijk offline";
        }

        /* Haal alle benodigde data uit de response */
        JSONObject json = new JSONObject(returnJson);
        JSONArray jsonArray = json.getJSONArray("members");

        /* Genereer het antwoord op basis van de response */
        StringBuilder botResponse = new StringBuilder();
        botResponse.append("Clan: ").append(json.getString("name"));
        botResponse.append("\nTag: #").append(json.getString("tag"));
        botResponse.append("\n------------------------\n");

        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append(jsonArray.getJSONObject(i).getString("name")).append(" (#").append(jsonArray.getJSONObject(i).getString("tag")).append(")");
            botResponse.append("\nCrowns: ").append(jsonArray.getJSONObject(i).getInt("clanChestCrowns")).append("\n");
            botResponse.append("-~-~-~-~-~-~-~-~\n");
        }

        return String.valueOf(botResponse);
    }
}
