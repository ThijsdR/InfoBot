package clashofclans;

import com.vdurmont.emoji.EmojiParser;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 */
public class CoC_Clan {

    /**
     *
     * @param urlString
     * @return
     */
    public static String getClanInfo(String urlString) {
        String returnJson = CoC_PROC.retrieveDataSupercellAPI(urlString);
        JSONObject json = new JSONObject(returnJson);
        JSONArray jsonArray = json.getJSONArray("items");

        if (returnJson.equals("SERVER ERROR")) {
            return "Ik kan de gevraagde data niet opvragen van de server...\nDe server is hoogstwaarschijnlijk offline";
        }

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
     *
     * @param urlString
     * @return
     */
    public static String getClanDonaties(String urlString) {
        String returnJson = CoC_PROC.retrieveDataSupercellAPI(urlString);
        JSONObject json = new JSONObject(returnJson);
        JSONArray jsonArray = json.getJSONArray("items");

        if (returnJson.equals("SERVER ERROR")) {
            return "Ik kan de gevraagde data niet opvragen van de server...\nDe server is hoogstwaarschijnlijk offline";
        }

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
     *
     * @param urlString
     * @return
     */
    public static String getClanMembers(String urlString) {
        String returnJson = CoC_PROC.retrieveDataSupercellAPI(urlString);
        JSONObject json = new JSONObject(returnJson);
        JSONArray jsonArray = json.getJSONArray("items");

        if (returnJson.equals("SERVER ERROR")) {
            return "Ik kan de gevraagde data niet opvragen van de server...\nDe server is hoogstwaarschijnlijk offline";
        }

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
