package botCommands.clashroyale;

import org.json.JSONArray;
import org.json.JSONObject;
import utility.CR_PROC;

public class CR_Clan {
    public static String getClanInfo(String urlString) {
        JSONObject json = new JSONObject(CR_PROC.retrieveDataRoyaleAPI(urlString));
        JSONObject jsonRegion = json.getJSONObject("region");
        JSONArray jsonArray = json.getJSONArray("members");

        StringBuilder botResponse = new StringBuilder("Answer from Inf0_B0t:\n\n");

        botResponse.append("Clan: ").append(json.getString("name"));
        botResponse.append("\nTag: #").append(json.getString("tag"));
        botResponse.append("\nScore: ").append(json.getString("score"));
        botResponse.append("\nMembers: ").append(json.getString("memberCount"));
        botResponse.append("\nType: ").append(json.getString("typeName"));
        botResponse.append("\nCountry: ").append(jsonRegion.getString("name"));
        botResponse.append("\n\n================================\n");

        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append("\n");
            botResponse.append(jsonArray.getJSONObject(i).getString("name")).append("(#").append(jsonArray.getJSONObject(i).getString("tag")).append(")");
        }

        return botResponse.toString();
    }

    public static String getClanRoles(String urlString) {
        JSONObject json = new JSONObject(CR_PROC.retrieveDataRoyaleAPI(urlString));
        JSONArray jsonArray = json.getJSONArray("members");

        StringBuilder botResponse = new StringBuilder("Answer from Inf0_B0t:\n\n");

        botResponse.append("Clan: ").append(json.getString("name"));
        botResponse.append("\nTag: #").append(json.getString("tag"));
        botResponse.append("\n\n================================\n");

        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append("\n");
            botResponse.append(jsonArray.getJSONObject(i).getString("name")).append("(#").append(jsonArray.getJSONObject(i).getString("tag")).append(")");
            botResponse.append("Role: ").append(jsonArray.getJSONObject(i).getString("roleName")).append("\n");
        }

        return botResponse.toString();
    }

    public static String getClanDonations(String urlString) {

        // ToDo

        return "";
    }

    public static String getClanchestContribution (String urlString) {

        // ToDo

        return "";
    }
}