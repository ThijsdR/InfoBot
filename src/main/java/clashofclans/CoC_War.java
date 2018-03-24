package clashofclans;

import com.vdurmont.emoji.EmojiParser;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Comparator;

public class CoC_War {
    public static String getCurrentWarState(String warData) {
        JSONObject warJson = new JSONObject(warData);
        return warJson.getString("state");
    }

    public static ArrayList<CoC_WarAttackContainer> getCurrentClanAttacks(String warData) {
        ArrayList<CoC_WarAttackContainer> allAttacks = new ArrayList<>();

        JSONObject warJson = new JSONObject(warData);
        JSONObject clanJson = warJson.getJSONObject("clan");
        JSONArray clanMemberJsonArray = clanJson.getJSONArray("members");

        for (int i = 0; i < clanMemberJsonArray.length(); i++) {
            JSONObject clanMemberJson = clanMemberJsonArray.getJSONObject(i);

            if (clanMemberJson.has("attacks")) {
                JSONArray clanMemberAttacks = clanMemberJson.getJSONArray("attacks");

                for (int ii = 0; ii < clanMemberAttacks.length(); ii++) {
                    JSONObject attackJson = clanMemberAttacks.getJSONObject(ii);
                    allAttacks.add(new CoC_WarAttackContainer(attackJson.getString("attackerTag"),
                            attackJson.getString("defenderTag"),
                            attackJson.getInt("stars"),
                            attackJson.getInt("destructionPercentage"),
                            attackJson.getInt("order")));
                }
            }
        }

        allAttacks.sort(Comparator.comparing(CoC_WarAttackContainer::getOrder));
        return allAttacks;
    }

    public static ArrayList<CoC_WarAttackContainer> getCurrentOpponentAttacks(String warData) {
        ArrayList<CoC_WarAttackContainer> allAttacks = new ArrayList<>();

        JSONObject warJson = new JSONObject(warData);
        JSONObject opponentJson = warJson.getJSONObject("opponent");
        JSONArray opponentMemberJsonArray = opponentJson.getJSONArray("members");

        for (int j = 0; j < opponentMemberJsonArray.length(); j++) {
            JSONObject opponentMemberJson = opponentMemberJsonArray.getJSONObject(j);

            if (opponentMemberJson.has("attacks")) {
                JSONArray opponentMemberAttacks = opponentMemberJson.getJSONArray("attacks");

                for (int jj = 0; jj < opponentMemberAttacks.length(); jj++) {
                    JSONObject attackJson = opponentMemberAttacks.getJSONObject(jj);
                    allAttacks.add(new CoC_WarAttackContainer(attackJson.getString("attackerTag"),
                            attackJson.getString("defenderTag"),
                            attackJson.getInt("stars"),
                            attackJson.getInt("destructionPercentage"),
                            attackJson.getInt("order")));
                }
            }
        }

        allAttacks.sort(Comparator.comparing(CoC_WarAttackContainer::getOrder));
        return allAttacks;
    }

    public static String warAttacksUpdate(String warData, ArrayList<CoC_WarAttackContainer> currentClanAttacks, ArrayList<CoC_WarAttackContainer> currentOpponentAttacks, String cocApiKey) {
        StringBuilder attackString = null;
        try {
            ArrayList<CoC_WarAttackContainer> clanWarAttacks = getCurrentClanAttacks(warData);
            ArrayList<CoC_WarAttackContainer> opponentWarAttacks = getCurrentOpponentAttacks(warData);

            CoC_WarAttackContainer lastAttack;
            attackString = new StringBuilder();

            String clanPlayerName;
            String opponentPlayerName;

            if (clanWarAttacks.size() != currentClanAttacks.size() || opponentWarAttacks.size() != currentOpponentAttacks.size()) {
                attackString.append("Oorlogsupdate!\n\n");
                attackString.append("Huidige stand: ").append(checkWarScore(warData)).append("\n\n");
            }

            if (clanWarAttacks.size() > currentClanAttacks.size()) {
                for (int i = 1; i <= (currentClanAttacks.size() - clanWarAttacks.size()); i++) {
                    lastAttack = clanWarAttacks.get(clanWarAttacks.size() - i);
                    String clanPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getAttackerTag().replace("#", "%23"), cocApiKey);
                    JSONObject clanPlayerJson = new JSONObject(clanPlayerData);
                    clanPlayerName = clanPlayerJson.getString("name");

                    String opponentPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getDefenderTag().replace("#", "%23"), cocApiKey);
                    JSONObject opponentPlayerJson = new JSONObject(opponentPlayerData);
                    opponentPlayerName = opponentPlayerJson.getString("name");

                    switch (lastAttack.getStars()) {
                        case 0:
                            attackString.append(EmojiParser.parseToUnicode(":heavy_multiplication_x::heavy_multiplication_x::heavy_multiplication_x: "));
                            break;
                        case 1:
                            attackString.append(EmojiParser.parseToUnicode(":star::heavy_multiplication_x::heavy_multiplication_x: "));
                            break;
                        case 2:
                            attackString.append(EmojiParser.parseToUnicode(":star::star::heavy_multiplication_x: "));
                            break;
                        case 3:
                            attackString.append(EmojiParser.parseToUnicode(":star::star::star: "));
                            break;
                    }

                    attackString.append(lastAttack.getDestructionPercentage()).append("% - ");
                    attackString.append(clanPlayerName).append(EmojiParser.parseToUnicode(" :arrow_right: ")).append(opponentPlayerName).append("\n");
                }
            }


            if (opponentWarAttacks.size() > currentOpponentAttacks.size()) {
                for (int i = 1; i <= (currentOpponentAttacks.size() - opponentWarAttacks.size()); i++) {
                    lastAttack = opponentWarAttacks.get(opponentWarAttacks.size() - i);

                    String clanPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getAttackerTag().replace("#", "%23"), cocApiKey);
                    JSONObject clanPlayerJson = new JSONObject(clanPlayerData);
                    clanPlayerName = clanPlayerJson.getString("name");

                    String opponentPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getDefenderTag().replace("#", "%23"), cocApiKey);
                    JSONObject opponentPlayerJson = new JSONObject(opponentPlayerData);
                    opponentPlayerName = opponentPlayerJson.getString("name");

                    switch (lastAttack.getStars()) {
                        case 0:
                            attackString.append(EmojiParser.parseToUnicode(":heavy_multiplication_x::heavy_multiplication_x::heavy_multiplication_x: "));
                            break;
                        case 1:
                            attackString.append(EmojiParser.parseToUnicode(":star::heavy_multiplication_x::heavy_multiplication_x: "));
                            break;
                        case 2:
                            attackString.append(EmojiParser.parseToUnicode(":star::star::heavy_multiplication_x: "));
                            break;
                        case 3:
                            attackString.append(EmojiParser.parseToUnicode(":star::star::star: "));
                            break;
                    }

                    attackString.append(lastAttack.getDestructionPercentage()).append("% - ");
                    attackString.append(opponentPlayerName).append(EmojiParser.parseToUnicode(" :arrow_left: ")).append(clanPlayerName).append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(attackString);
    }

    public static String warAttacksUpdate3(String warData, ArrayList<CoC_WarAttackContainer> currentClanAttacks,String cocApiKey) {
        StringBuilder attackString = null;

        try {
            ArrayList<CoC_WarAttackContainer> clanWarAttacks = getCurrentClanAttacks(warData);

            CoC_WarAttackContainer lastAttack;
            attackString = new StringBuilder();

            String clanPlayerName;
            String opponentPlayerName;

            if (clanWarAttacks.size() != currentClanAttacks.size()) {
                attackString.append("Oorlogsupdate!\n\n");
                attackString.append("Huidige stand: ").append(checkWarScore(warData)).append("\n\n");
            }

            for (int i = 1; i <= (currentClanAttacks.size() - clanWarAttacks.size()); i++) {
                lastAttack = clanWarAttacks.get(clanWarAttacks.size() - i);

                if (lastAttack.getDestructionPercentage() == 100) {
                    String clanPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getAttackerTag().replace("#", "%23"), cocApiKey);
                    JSONObject clanPlayerJson = new JSONObject(clanPlayerData);
                    clanPlayerName = clanPlayerJson.getString("name");

                    String opponentPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getDefenderTag().replace("#", "%23"), cocApiKey);
                    JSONObject opponentPlayerJson = new JSONObject(opponentPlayerData);
                    opponentPlayerName = opponentPlayerJson.getString("name");

                    attackString.append(EmojiParser.parseToUnicode(":star::star::star: :100:")).append("% - ");
                    attackString.append(clanPlayerName).append(EmojiParser.parseToUnicode(" :arrow_right: ")).append(opponentPlayerName).append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(attackString);
    }

    public static String warStartMessage(String warData) {
        JSONObject warJson = new JSONObject(warData);
        JSONObject opponentJson = warJson.getJSONObject("opponent");
        return "De oorlog tegen " + opponentJson.getString("name") + " is begonnen!";
    }

    private static String checkWarScore(String warData) {
        JSONObject warJson = new JSONObject(warData);
        JSONObject clanJson = warJson.getJSONObject("clan");
        JSONObject opponentJson = warJson.getJSONObject("opponent");

        return clanJson.getInt("stars") + "-" + opponentJson.getInt("stars");
    }

    public static String endWarRecap(String warData, ArrayList<CoC_WarAttackContainer> clanWarAttacks, String cocApiKey) {
        JSONObject warJson = new JSONObject(warData);
        JSONObject clanJson = warJson.getJSONObject("clan");
        JSONObject opponentJson = warJson.getJSONObject("opponent");

        clanWarAttacks.sort(Comparator.comparing(CoC_WarAttackContainer::getDestructionPercentage));
        String bassieData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + clanWarAttacks.get(clanWarAttacks.size() - 1).getAttackerTag().replace("#", "%23"), cocApiKey);
        JSONObject bassieJson = new JSONObject(bassieData);
        String bassieAwardName = bassieJson.getString("name");
        String bassieAward = bassieAwardName + " met " + clanWarAttacks.get(clanWarAttacks.size() - 1).getDestructionPercentage() + "%";

        StringBuilder recap = new StringBuilder(EmojiParser.parseToUnicode("De oorlog is afgelopen! :checkered_flag:\n\n"));
        recap.append("Eindstand: ").append(checkWarScore(warData)).append("\n");
        recap.append(EmojiParser.parseToUnicode(":family: ")).append(clanJson.getString("name")).append("\n");
        recap.append(EmojiParser.parseToUnicode(":collision: ")).append(clanJson.getInt("attacks")).append("/").append(warJson.getInt("teamSize") * 2).append("\n");
        recap.append(EmojiParser.parseToUnicode(":bar_chart: ")).append(clanJson.getDouble("destructionPercentage")).append("%\n\n");
        recap.append(EmojiParser.parseToUnicode(":family: ")).append(opponentJson.getString("name")).append("\n");
        recap.append(EmojiParser.parseToUnicode(":collision: ")).append(opponentJson.getInt("attacks")).append("/").append(warJson.getInt("teamSize") * 2).append("\n");
        recap.append(EmojiParser.parseToUnicode(":bar_chart: ")).append(opponentJson.getDouble("destructionPercentage")).append("%\n\n");
        recap.append(EmojiParser.parseToUnicode(":ghost::trophy: De Bassie-award gaat deze ronde naar: " + bassieAward + "\n\n"));

        ArrayList<CoC_WarAttackContainer> attacks = new ArrayList<>();
        for (CoC_WarAttackContainer clanWarAttack : clanWarAttacks) {
            if (clanWarAttack.getDestructionPercentage() == 100) {
                attacks.add(clanWarAttack);
            }
        }

        if (!attacks.isEmpty()) {
            recap.append("Helden van deze oorlog:\n");

            for (CoC_WarAttackContainer attack : attacks) {
                recap.append(EmojiParser.parseToUnicode(":star::star::star: "));
                recap.append(attack.getDestructionPercentage()).append("% - ");
                recap.append(CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + attack.getAttackerTag().replace("#", "%23"), cocApiKey));
                recap.append(EmojiParser.parseToUnicode(":arrow_right:"));
                recap.append(CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + attack.getDefenderTag().replace("#", "%23"), cocApiKey));
                recap.append("\n");
            }
        }

        return String.valueOf(recap);
    }

    public static String newOpponentOverview(String warData) {
        JSONObject warJson = new JSONObject(warData);
        ArrayList<CoC_PlayerContainer> opponentPlayers = new ArrayList<>();
        JSONObject opponentJson = warJson.getJSONObject("opponent");
        JSONArray opponentMemberJsonArray = opponentJson.getJSONArray("members");

        StringBuilder opponentOverview = new StringBuilder("De oorlog is verklaard aan: ");
        opponentOverview.append(opponentJson.getString("name")).append("\n------------------");

        for (int j = 0; j < opponentMemberJsonArray.length(); j++) {
            JSONObject opponentMemberJson = opponentMemberJsonArray.getJSONObject(j);
            opponentPlayers.add(new CoC_PlayerContainer(opponentMemberJson.getInt("mapPosition"),
                    opponentMemberJson.getString("name"),
                    opponentMemberJson.getInt("townhallLevel")));
        }

        opponentPlayers.sort(Comparator.comparing(CoC_PlayerContainer::getPositionInClan));

        for (CoC_PlayerContainer player : opponentPlayers) {
            opponentOverview.append("\n");
            if (player.getPositionInClan() < 10) {
                opponentOverview.append("0").append(player.getPositionInClan()).append(") ");
            } else {
                opponentOverview.append(player.getPositionInClan()).append(") ");
            }
            opponentOverview.append(EmojiParser.parseToUnicode(" - :house:")).append(player.getTownhallLevel()).append(" - ");
            opponentOverview.append(player.getName());
        }


        return String.valueOf(opponentOverview);
    }

    public static String getCurrentOpponentOverview(String warData) {
        JSONObject warJson = new JSONObject(warData);
        String state = warJson.getString("state");
        StringBuilder botResponse = new StringBuilder();

        if (state.equals("preparation") || state.equals("inWar")) {
            ArrayList<CoC_PlayerContainer> opponentPlayers = new ArrayList<>();
            JSONObject opponentJson = warJson.getJSONObject("opponent");
            JSONArray opponentMemberJsonArray = opponentJson.getJSONArray("members");
            botResponse.append("Tegenstander: ").append(opponentJson.getString("name")).append("\n------------------");

            for (int j = 0; j < opponentMemberJsonArray.length(); j++) {
                JSONObject opponentMemberJson = opponentMemberJsonArray.getJSONObject(j);
                opponentPlayers.add(new CoC_PlayerContainer(opponentMemberJson.getInt("mapPosition"),
                        opponentMemberJson.getString("name"),
                        opponentMemberJson.getInt("townhallLevel")));
            }

            opponentPlayers.sort(Comparator.comparing(CoC_PlayerContainer::getPositionInClan));

            for (CoC_PlayerContainer player : opponentPlayers) {
                botResponse.append("\n");
                if (player.getPositionInClan() < 10) {
                    botResponse.append("0").append(player.getPositionInClan()).append(") ");
                } else {
                    botResponse.append(player.getPositionInClan()).append(") ");
                }
                botResponse.append(EmojiParser.parseToUnicode(" - :house:")).append(player.getTownhallLevel()).append(" - ");
                botResponse.append(player.getName());
            }

            return String.valueOf(botResponse);
        }
        else {
            botResponse.append("Er is momenteel geen tegenstander in de clanoorlog");
            return String.valueOf(botResponse);
        }
    }

    public static String getBassieAwardTopDrie(ArrayList<CoC_WarAttackContainer> clanWarAttacks, String warState, String cocApiKey) {
        if (warState.equals("inWar")) {
            if (clanWarAttacks.size() >= 3) {
                clanWarAttacks.sort(Comparator.comparing(CoC_WarAttackContainer::getDestructionPercentage));

                StringBuilder bassieBuilder = new StringBuilder(EmojiParser.parseToUnicode(":trophy: Bassie-award huidige top 3\n\n"));
                for (int i = 0; i < 3; i++) {
                    JSONObject bassieJson = new JSONObject(CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + clanWarAttacks.get(i).getAttackerTag().replace("#", "%23"), cocApiKey));
                    String bassieAwardName = bassieJson.getString("name");

                    switch (i) {
                        case 0:
                            bassieBuilder.append(EmojiParser.parseToUnicode(":one: " + bassieAwardName)).append(" - ").append(clanWarAttacks.get(i).getDestructionPercentage()).append("%\n");
                            break;
                        case 1:
                            bassieBuilder.append(EmojiParser.parseToUnicode(":two: " + bassieAwardName)).append(" - ").append(clanWarAttacks.get(i).getDestructionPercentage()).append("%\n");
                            break;
                        case 2:
                            bassieBuilder.append(EmojiParser.parseToUnicode(":three: " + bassieAwardName)).append(" - ").append(clanWarAttacks.get(i).getDestructionPercentage()).append("%\n");
                            break;
                    }
                }
                return String.valueOf(bassieBuilder);

            } else {
                return "Om een top 3 te genereren moeten er tenminste 3 aanvallen zijn gedaan...";
            }
        } else {
            return "De top 3 kan niet gemaakt worden als de oorlog niet bezig is...";
        }
    }

    public static String subscribeToWarEvents(long chatID, Connection con) {
        Statement stmt;
        try {
            ArrayList<Long> chatIDs = new ArrayList<>();
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ChatID FROM subscriberswar");
            while (rs.next()) {
                chatIDs.add((long) rs.getInt("ChatID"));
            }

            if (!chatIDs.contains(chatID)) {
                stmt.execute("INSERT INTO subscriberswar (ChatID) VALUES ('" + chatID + "')");
                return "Gelukt! Je bent nu geabonneerd op de oorlogsupdates";
            } else {
                return "Je bent al geabonneerd op de oorlogsupdates...";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Oeps, er is iets misgegaan...";
    }

    public static String unsubscribeToWarEvents(long chatID, Connection con) {
        Statement stmt;
        try {
            ArrayList<Long> chatIDs = new ArrayList<>();
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ChatID FROM subscriberswar");
            while (rs.next()) {
                chatIDs.add((long) rs.getInt("ChatID"));
            }

            if (chatIDs.contains(chatID)) {
                stmt.execute("DELETE FROM subscriberswar WHERE ChatID = '" + chatID + "'");
                return "Gelukt! Je bent niet langer geabonneerd op de oorlogsupdates";
            } else {
                return "Je bent niet geabonneerd op de oorlogsupdates, dus ik kan je ook niet verwijderen uit de lijst...";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "Oeps, er is iets misgegaan...";
    }
}
