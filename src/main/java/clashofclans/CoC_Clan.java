package clashofclans;

import clashofclans.player_resources.CoC_Hero;
import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Deze klasse bevat methode(s) welke betrekking hebben op de gehele clan
 */
public class CoC_Clan {

    /**
     * Deze methode wordt gebruikt om alle relevante informatie op te halen van de opgegeven clan.
     * Van de clan wordt de naam, het clanlevel, de tag, de score, het type, het ledenaantal en de locatie opgevraagd
     *
     * @param urlString String om de request naar toe te sturen
     * @return Een String met alle informatie omtrent de opgegeven clan
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
     * @param urlString String om de request naar toe te sturen
     * @return Een String met alle donatiegegevens van de opgegeven clan
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
     * @param urlString String om de request naar toe te sturen
     * @return Alle relevante data omtrent de leden van de opgegeven clan
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

    public static ArrayList<CoC_PlayerContainer> getCoCPlayerList() {
        ArrayList<CoC_PlayerContainer> playerList = new ArrayList<>();

        String returnJson = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/clans/%23J0C9CPY/members");

        JSONObject json = new JSONObject(returnJson);
        JSONArray jsonArray = json.getJSONArray("items");

        for (int i = 0; i < jsonArray.length(); i++) {
            String playerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/%23" + jsonArray.getJSONObject(i).getString("tag").substring(1));
            JSONObject playerJson = new JSONObject(playerData);
            JSONArray heroesJsonArray = playerJson.getJSONArray("heroes");

            CoC_PlayerContainer player = new CoC_PlayerContainer(playerJson.getString("name"),
                    playerJson.getString("tag"),
                    playerJson.getInt("townHallLevel"),
                    playerJson.getInt("trophies"));

            ArrayList<CoC_Hero> heroList = new ArrayList<>();

            for (int j = 0; j < heroesJsonArray.length(); j++) {
                heroList.add(new CoC_Hero(heroesJsonArray.getJSONObject(j).getString("name"),
                        heroesJsonArray.getJSONObject(j).getInt("level"),
                        heroesJsonArray.getJSONObject(j).getString("village")));
            }
            player.setHeroLevels(heroList);

            playerList.add(player);
        }

        return playerList;
    }

    public static String getClanChange(ArrayList<CoC_PlayerContainer> cocPlayersList, ArrayList<CoC_PlayerContainer> updatedList) {
        ArrayList<CoC_PlayerContainer> uniqueMembers = new ArrayList<>();

        boolean memberJoined = false;

        if (updatedList.size() > cocPlayersList.size()) {
            for (CoC_PlayerContainer player2 : updatedList) {
                boolean flag = false;
                for (CoC_PlayerContainer player1 : cocPlayersList) {
                    if (player2.getPlayerTag().equals(player1.getPlayerTag())) {
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    uniqueMembers.add(player2);
                }
            }
            memberJoined = true;
        }

        if (updatedList.size() < cocPlayersList.size()) {
            for (CoC_PlayerContainer player1 : cocPlayersList) {
                boolean flag = false;
                for (CoC_PlayerContainer player2 : updatedList) {
                    if (player1.getPlayerTag().equals(player2.getPlayerTag())) {
                        flag = true;
                        break;
                    }
                }

                if (!flag) {
                    uniqueMembers.add(player1);
                }
            }
            memberJoined = false;
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (!uniqueMembers.isEmpty()) {
            String blacklist = "";

            if (memberJoined) {
                stringBuilder = new StringBuilder("Een speler heeft zich bij de clan aangesloten!");
                try {
                    blacklist = FileUtils.readFileToString(new File("out/blacklist/cocblacklist.txt"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                stringBuilder = new StringBuilder("Een speler zit niet langer meer bij de clan!");
            }

            for (CoC_PlayerContainer player : uniqueMembers) {
                stringBuilder.append("\n\n");
                stringBuilder.append(player.getName());
                stringBuilder.append(" (").append(player.getPlayerTag()).append(")\n");
                stringBuilder.append(EmojiParser.parseToUnicode(":house: ")).append(player.getTownhallLevel()).append("  ");
                stringBuilder.append(EmojiParser.parseToUnicode(":trophy: ")).append(player.getTrophyCount()).append("  ");

                for (CoC_Hero hero : player.getHeroLevels()) {
                    String heroName = hero.getName();
                    switch (heroName) {
                        case "Barbarian King":
                            stringBuilder.append(EmojiParser.parseToUnicode(":guardsman: ")).append(hero.getLevel()).append("  ");
                            break;
                        case "Archer Queen":
                            stringBuilder.append(EmojiParser.parseToUnicode(":princess: ")).append(hero.getLevel()).append("  ");
                            break;
                        case "Grand Warden":
                            stringBuilder.append(EmojiParser.parseToUnicode(":angel: ")).append(hero.getLevel());
                            break;
                    }
                }

                if (blacklist.toLowerCase().contains(player.getPlayerTag().toLowerCase())) {
                    stringBuilder.append(EmojiParser.parseToUnicode("\n\n >> :warning: DEZE SPELER STAAT OP DE ZWARTE LIJST!!"));
                }
            }
        }
        return String.valueOf(stringBuilder);
    }
}
