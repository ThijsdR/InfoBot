package clashroyale;

import org.json.JSONArray;
import org.json.JSONObject;

public class CR_Clan {
    public static String getClanInfo(String urlString) {
        String returnJson = CR_PROC.retrieveDataRoyaleAPI(urlString);

        if (returnJson.equals("SERVER ERROR")) {
            return "Ik kan de gevraagde data niet opvragen van de server...\nDe server is hoogstwaarschijnlijk offline";
        }

        JSONObject json = new JSONObject(returnJson);
        JSONObject jsonLocation = json.getJSONObject("location");

        StringBuilder botResponse = new StringBuilder();
        botResponse.append("Clan: ").append(json.getString("name"));
        botResponse.append("\nTag: #").append(json.getString("tag"));
        botResponse.append("\nScore: ").append(json.getInt("score"));
        botResponse.append("\nMembers: ").append(json.getInt("memberCount"));
        botResponse.append("\nType: ").append(json.getString("type"));
        botResponse.append("\nLocation: ").append(jsonLocation.getString("name"));

        return String.valueOf(botResponse);
    }

    public static String getClanMembers(String urlString) {
        String returnJson = CR_PROC.retrieveDataRoyaleAPI(urlString);

        if (returnJson.equals("SERVER ERROR")) {
            return "Ik kan de gevraagde data niet opvragen van de server...\nDe server is hoogstwaarschijnlijk offline";
        }

        JSONObject json = new JSONObject(returnJson);
        JSONArray jsonArray = json.getJSONArray("members");

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

    public static String getClanRoles(String urlString) {
        String returnJson = CR_PROC.retrieveDataRoyaleAPI(urlString);

        if (returnJson.equals("SERVER ERROR")) {
            return "Ik kan de gevraagde data niet opvragen van de server...\nDe server is hoogstwaarschijnlijk offline";
        }

        JSONObject json = new JSONObject(returnJson);
        JSONArray jsonArray = json.getJSONArray("members");

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

    public static String getClanDonations(String urlString) {
        String returnJson = CR_PROC.retrieveDataRoyaleAPI(urlString);

        if (returnJson.equals("SERVER ERROR")) {
            return "Ik kan de gevraagde data niet opvragen van de server...\nDe server is hoogstwaarschijnlijk offline";
        }

        JSONObject json = new JSONObject(returnJson);
        JSONArray jsonArray = json.getJSONArray("members");

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

    public static String getClanchestContribution (String urlString) {
        String returnJson = CR_PROC.retrieveDataRoyaleAPI(urlString);

        if (returnJson.equals("SERVER ERROR")) {
            return "Ik kan de gevraagde data niet opvragen van de server...\nDe server is hoogstwaarschijnlijk offline";
        }

        JSONObject json = new JSONObject(returnJson);
        JSONArray jsonArray = json.getJSONArray("members");

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
