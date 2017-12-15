package botCommands.clashofclans.clans;

import utility.CoC_PROC;
import org.json.JSONArray;
import org.json.JSONObject;

public class CoC_Clan {
    public static String getClanDonaties(String urlString) {
        JSONObject json = new JSONObject(CoC_PROC.retrieveDataSupercellAPI(urlString));
        JSONArray jsonArray = json.getJSONArray("items");

        StringBuilder botResponse = new StringBuilder("Answer from Inf0_B0t:\n");

        // Todo: Naam en tag van de clan toevoegen!

        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append("\n");
            botResponse.append("Name: ").append(jsonArray.getJSONObject(i).getString("name")).append("\n");
            botResponse.append("Tag: ").append(jsonArray.getJSONObject(i).getString("tag")).append("\n");
            botResponse.append("Role: ").append(jsonArray.getJSONObject(i).getString("role")).append("\n");
            botResponse.append("Donations: ").append(jsonArray.getJSONObject(i).getInt("donations")).append("\n");
            botResponse.append("Received: ").append(jsonArray.getJSONObject(i).getInt("donationsReceived")).append("\n");
        }

        return botResponse.toString();
    }

    public static String getClanInfo(String urlString) {
        JSONObject json = new JSONObject(CoC_PROC.retrieveDataSupercellAPI(urlString));
        JSONArray jsonArray = json.getJSONArray("items");

        StringBuilder botResponse = new StringBuilder("Answer from Inf0_B0t:\n");

        // Todo: Gegevens clan toevoegen (score en country + naam speler en tag)!

        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append("\n");
            botResponse.append("Clan: ").append(jsonArray.getJSONObject(i).getString("name")).append("\n");
            botResponse.append("Clan tag: ").append(jsonArray.getJSONObject(i).getString("tag")).append("\n");
            botResponse.append("Clan type: ").append(jsonArray.getJSONObject(i).getString("type")).append("\n");
            botResponse.append("Members: ").append(jsonArray.getJSONObject(i).getInt("members")).append("\n");
        }

        return botResponse.toString();
    }
}
