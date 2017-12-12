package botCommands.clans;

import botCommands.utility.PROC;
import org.json.JSONArray;
import org.json.JSONObject;

public class Clan {
    public static String getClanDonaties(String urlString) {
        JSONObject json = new JSONObject(PROC.retrieveData(urlString));
        JSONArray jsonArray = json.getJSONArray("items");

        StringBuilder botResponse = new StringBuilder("Answer from Inf0_B0t:\n");

        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append("\n");
            botResponse.append("Naam: ").append(jsonArray.getJSONObject(i).getString("name")).append("\n");
            botResponse.append("Positie: ").append(jsonArray.getJSONObject(i).getString("role")).append("\n");
            botResponse.append("Donaties: ").append(jsonArray.getJSONObject(i).getInt("donations")).append("\n");
            botResponse.append("Ontvangen: ").append(jsonArray.getJSONObject(i).getInt("donationsReceived")).append("\n");
        }

        return botResponse.toString();
    }

    public static String getClanInfo(String urlString) {
        JSONObject json = new JSONObject(PROC.retrieveData(urlString));
        JSONArray jsonArray = json.getJSONArray("items");

        StringBuilder botResponse = new StringBuilder("Answer from Inf0_B0t:\n");

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
