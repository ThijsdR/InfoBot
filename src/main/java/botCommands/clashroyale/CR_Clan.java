package botCommands.clashroyale;

import org.json.JSONArray;
import org.json.JSONObject;

public class CR_Clan {
    public static String getClanInfo(String urlString) {
        JSONObject json = new JSONObject(CR_PROC.retrieveDataRoyaleAPI(urlString));
        JSONObject jsonRegion = json.getJSONObject("region");

        StringBuilder botResponse = new StringBuilder();
        botResponse.append("Clan: ").append(json.getString("name"));
        botResponse.append("\nTag: #").append(json.getString("tag"));
        botResponse.append("\nScore: ").append(json.getString("score"));
        botResponse.append("\nMembers: ").append(json.getString("memberCount"));
        botResponse.append("\nType: ").append(json.getString("typeName"));
        botResponse.append("\nCountry: ").append(jsonRegion.getString("name"));

        return String.valueOf(botResponse);
    }

    public static String getClanMembers(String urlString) {
        JSONObject json = new JSONObject(CR_PROC.retrieveDataRoyaleAPI(urlString));
        JSONArray jsonArray = json.getJSONArray("members");

        StringBuilder botResponse = new StringBuilder();
        botResponse.append("Clan: ").append(json.getString("name"));
        botResponse.append("\nTag: #").append(json.getString("tag"));
        botResponse.append("\n------------------------n");

        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append(jsonArray.getJSONObject(i).getString("name")).append("(#").append(jsonArray.getJSONObject(i).getString("tag")).append(")");
            botResponse.append("\nLevel: ").append(jsonArray.getJSONObject(i).getString("expLevel"));
            botResponse.append("\nTrophies: ").append(jsonArray.getJSONObject(i).getString("trophies")).append("\n");
            botResponse.append("-~-~-~-~-~-~-~-~\n");
        }

        return String.valueOf(botResponse);
    }

    public static String getClanRoles(String urlString) {
        JSONObject json = new JSONObject(CR_PROC.retrieveDataRoyaleAPI(urlString));
        JSONArray jsonArray = json.getJSONArray("members");

        StringBuilder botResponse = new StringBuilder();
        botResponse.append("Clan: ").append(json.getString("name"));
        botResponse.append("\nTag: #").append(json.getString("tag"));
        botResponse.append("\n------------------------n");


        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append(jsonArray.getJSONObject(i).getString("name")).append("(#").append(jsonArray.getJSONObject(i).getString("tag")).append(")").append("\n");
            botResponse.append("Role: ").append(jsonArray.getJSONObject(i).getString("roleName")).append("\n");
            botResponse.append("-~-~-~-~-~-~-~-~\n");
        }

        return String.valueOf(botResponse);
    }

    public static String getClanDonations(String urlString) {
        JSONObject json = new JSONObject(CR_PROC.retrieveDataRoyaleAPI(urlString));
        JSONArray jsonArray = json.getJSONArray("members");

        StringBuilder botResponse = new StringBuilder();
        botResponse.append("Clan: ").append(json.getString("name"));
        botResponse.append("\nTag: #").append(json.getString("tag"));
        botResponse.append("\n------------------------n");

        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append(jsonArray.getJSONObject(i).getString("name")).append("(#").append(jsonArray.getJSONObject(i).getString("tag")).append(")");
            botResponse.append("Donations: ").append(jsonArray.getJSONObject(i).getString("donations")).append("\n");
            botResponse.append("-~-~-~-~-~-~-~-~\n");
        }

        return String.valueOf(botResponse);
    }

    public static String getClanchestContribution (String urlString) {
        JSONObject json = new JSONObject(CR_PROC.retrieveDataRoyaleAPI(urlString));
        JSONArray jsonArray = json.getJSONArray("members");

        StringBuilder botResponse = new StringBuilder();
        botResponse.append("Clan: ").append(json.getString("name"));
        botResponse.append("\nTag: #").append(json.getString("tag"));
        botResponse.append("\n------------------------n");

        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append(jsonArray.getJSONObject(i).getString("name")).append("(#").append(jsonArray.getJSONObject(i).getString("tag")).append(")");
            botResponse.append("Crowns: ").append(jsonArray.getJSONObject(i).getString("clanChestCrowns")).append("\n");
            botResponse.append("-~-~-~-~-~-~-~-~\n");

        }

        return String.valueOf(botResponse);
    }
}
