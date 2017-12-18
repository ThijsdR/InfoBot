package botCommands.clashofclans.clans;

import com.vdurmont.emoji.EmojiParser;
import org.json.JSONArray;
import org.json.JSONObject;
import botCommands.clashofclans.CoC_PROC;

public class CoC_ClanTagMembers {
    public static String getClanMemberInfo(String urlString, String memberName) {
        JSONObject json = new JSONObject(CoC_PROC.retrieveDataSupercellAPI(urlString));
        JSONArray jsonArray = json.getJSONArray("items");

        StringBuilder botResponePart = new StringBuilder();

        for (int i = 0; i < jsonArray.length(); i++) {
            if (jsonArray.getJSONObject(i).getString("name").equals(memberName)) {

                botResponePart.append("\n");
                botResponePart.append("Name: ").append(jsonArray.getJSONObject(i).getString("name")).append("\n");
                botResponePart.append("Tag: ").append(jsonArray.getJSONObject(i).getString("tag")).append("\n");
                botResponePart.append("Level: ").append(jsonArray.getJSONObject(i).getInt("expLevel")).append("\n");
                botResponePart.append(EmojiParser.parseToUnicode("Trophies: :trophy:")).append(jsonArray.getJSONObject(i).getInt("trophies")).append("\n");
                botResponePart.append("Role: ").append(jsonArray.getJSONObject(i).getString("role")).append("\n\n");
                botResponePart.append(EmojiParser.parseToUnicode("Donations: :arrow_forward:")).append(jsonArray.getJSONObject(i).getInt("donations")).append("\n");
                botResponePart.append(EmojiParser.parseToUnicode("Received: :arrow_backward:")).append(jsonArray.getJSONObject(i).getInt("donationsReceived")).append("\n");
            }
        }

        if (!botResponePart.toString().isEmpty()) {
            return "Answer from Inf0_B0t:\n" + "=+=+=+=+=+=+=+=+=+=+=+=+=+=+=\n" + botResponePart.toString();
        } else {
            return "Geen resultaten gevonden\nZit het opgegeven lid wel in de clan?\nZorg dat eventuele hoofdletters ook goed staan.";
        }
    }
}
