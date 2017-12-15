package botCommands.clashofclans.clans;

import com.vdurmont.emoji.EmojiParser;
import org.json.JSONArray;
import org.json.JSONObject;
import utility.CoC_PROC;

public class CoC_Clan {
    public static String getClanInfo(String urlString) {
        JSONObject json = new JSONObject(CoC_PROC.retrieveDataSupercellAPI(urlString));
        JSONArray jsonArray = json.getJSONArray("items");

        StringBuilder botResponse = new StringBuilder("Answer from Inf0_B0t:\n");
        botResponse.append("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=\n");

        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append("\n");
            botResponse.append("Clan: ").append(jsonArray.getJSONObject(i).getString("name")).append("\n");
            botResponse.append("Level: ").append(jsonArray.getJSONObject(i).getInt("clanLevel")).append("\n");
            botResponse.append("Clan tag: ").append(jsonArray.getJSONObject(i).getString("tag")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Clanpoints: :trophy:")).append(jsonArray.getJSONObject(i).getInt("clanPoints")).append("\n");
            botResponse.append("Clan type: ").append(jsonArray.getJSONObject(i).getString("type")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Members: :family:")).append(jsonArray.getJSONObject(i).getInt("members")).append("\n");
            botResponse.append("Country: ").append(jsonArray.getJSONObject(i).getJSONObject("location").getString("name")).append("\n");
        }

        return botResponse.toString();
    }

    public static String getClanDonaties(String urlString) {
        JSONObject json = new JSONObject(CoC_PROC.retrieveDataSupercellAPI(urlString));
        JSONArray jsonArray = json.getJSONArray("items");

        StringBuilder botResponse = new StringBuilder("Answer from Inf0_B0t:\n");
        botResponse.append("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=\n");

        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append("\n");
            botResponse.append("-").append(i+1).append("-\n");
            botResponse.append("Name: ").append(jsonArray.getJSONObject(i).getString("name")).append("\n");
            botResponse.append("Tag: ").append(jsonArray.getJSONObject(i).getString("tag")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Donations: :arrow_forward:")).append(jsonArray.getJSONObject(i).getInt("donations")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Received: :arrow_backward:")).append(jsonArray.getJSONObject(i).getInt("donationsReceived")).append("\n");
        }

        return botResponse.toString();
    }

    public static String getClanMembers(String urlString) {
        JSONObject json = new JSONObject(CoC_PROC.retrieveDataSupercellAPI(urlString));
        JSONArray jsonArray = json.getJSONArray("items");

        StringBuilder botResponse = new StringBuilder("Answer from Inf0_B0t:\n");
        botResponse.append("=+=+=+=+=+=+=+=+=+=+=+=+=+=+=\n");

        for (int i = 0; i < jsonArray.length(); i++) {
            botResponse.append("\n");
            botResponse.append("-").append(i+1).append("-\n");
            botResponse.append("Name: ").append(jsonArray.getJSONObject(i).getString("name")).append("\n");
            botResponse.append("Tag: ").append(jsonArray.getJSONObject(i).getString("tag")).append("\n");
            botResponse.append("Level: ").append(jsonArray.getJSONObject(i).getInt("expLevel")).append("\n");
            botResponse.append(EmojiParser.parseToUnicode("Trophies: :trophy:")).append(jsonArray.getJSONObject(i).getInt("trophies")).append("\n");
            botResponse.append("Role: ").append(jsonArray.getJSONObject(i).getString("role")).append("\n");
        }

        return botResponse.toString();
    }
}
