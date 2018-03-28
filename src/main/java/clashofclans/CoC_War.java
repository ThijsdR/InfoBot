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
        StringBuilder attackString = new StringBuilder();
        try {
            ArrayList<CoC_WarAttackContainer> clanWarAttacks = getCurrentClanAttacks(warData);
            ArrayList<CoC_WarAttackContainer> opponentWarAttacks = getCurrentOpponentAttacks(warData);

            CoC_WarAttackContainer lastAttack;
            String clanPlayerName;
            String opponentPlayerName;

            if (clanWarAttacks.size() != currentClanAttacks.size() || opponentWarAttacks.size() != currentOpponentAttacks.size()) {
                attackString.append("*Oorlogsupdate!*\n\n");
                attackString.append("_Huidige stand: ").append(checkWarScore(warData)).append("_\n\n");
            }

            if (clanWarAttacks.size() > currentClanAttacks.size()) {
                for (int i = 1; i <= (clanWarAttacks.size() - currentClanAttacks.size()); i++) {
                    lastAttack = clanWarAttacks.get(clanWarAttacks.size() - i);
                    String clanPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getAttackerTag().replace("#", "%23"), cocApiKey);
                    JSONObject clanPlayerJson = new JSONObject(clanPlayerData);
                    clanPlayerName = clanPlayerJson.getString("name");

                    String opponentPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getDefenderTag().replace("#", "%23"), cocApiKey);
                    JSONObject opponentPlayerJson = new JSONObject(opponentPlayerData);
                    opponentPlayerName = opponentPlayerJson.getString("name");

                    attackString.append("`");
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
                    attackString.append(clanPlayerName).append(EmojiParser.parseToUnicode(" :arrow_right: ")).append(opponentPlayerName).append("`\n");
                }
            }

            if (opponentWarAttacks.size() > currentOpponentAttacks.size()) {
                for (int i = 1; i <= (opponentWarAttacks.size() - currentOpponentAttacks.size()); i++) {
                    lastAttack = opponentWarAttacks.get(opponentWarAttacks.size() - i);

                    String clanPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getAttackerTag().replace("#", "%23"), cocApiKey);
                    JSONObject clanPlayerJson = new JSONObject(clanPlayerData);
                    clanPlayerName = clanPlayerJson.getString("name");

                    String opponentPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getDefenderTag().replace("#", "%23"), cocApiKey);
                    JSONObject opponentPlayerJson = new JSONObject(opponentPlayerData);
                    opponentPlayerName = opponentPlayerJson.getString("name");

                    attackString.append("`");
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
                    attackString.append(opponentPlayerName).append(EmojiParser.parseToUnicode(" :arrow_left: ")).append(clanPlayerName).append("`\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(attackString);
    }

    public static String warAttacksUpdate3(String warData, ArrayList<CoC_WarAttackContainer> currentClanAttacks, String cocApiKey) {
        StringBuilder attackString = new StringBuilder();

        try {
            ArrayList<CoC_WarAttackContainer> clanWarAttacks = getCurrentClanAttacks(warData);

            if (clanWarAttacks.size() > currentClanAttacks.size()) {
                CoC_WarAttackContainer lastAttack;
                String clanPlayerName;
                String opponentPlayerName;

                for (int i = 1; i <= (clanWarAttacks.size() - currentClanAttacks.size()); i++) {
                    lastAttack = clanWarAttacks.get(clanWarAttacks.size() - i);

                    if (lastAttack.getDestructionPercentage() == 100) {
                        attackString.append(EmojiParser.parseToUnicode(":confetti_ball::tada: *NIEUWE 3-STER AANVAL!*\n\n"));

                        String clanPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getAttackerTag().replace("#", "%23"), cocApiKey);
                        JSONObject clanPlayerJson = new JSONObject(clanPlayerData);
                        clanPlayerName = clanPlayerJson.getString("name");

                        String opponentPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getDefenderTag().replace("#", "%23"), cocApiKey);
                        JSONObject opponentPlayerJson = new JSONObject(opponentPlayerData);
                        opponentPlayerName = opponentPlayerJson.getString("name");

                        attackString.append(EmojiParser.parseToUnicode("`:star::star::star: :100:")).append("% - ");
                        attackString.append(clanPlayerName).append(EmojiParser.parseToUnicode(" :arrow_right: ")).append(opponentPlayerName).append("`\n\n");
                    }
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
        return "*De oorlog tegen* `" + opponentJson.getString("name") + "` *is begonnen!*";
    }

    private static String checkWarScore(String warData) {
        JSONObject warJson = new JSONObject(warData);
        JSONObject clanJson = warJson.getJSONObject("clan");
        JSONObject opponentJson = warJson.getJSONObject("opponent");

        return clanJson.getInt("stars") + "-" + opponentJson.getInt("stars");
    }

    public static String endWarRecap(String warData, String cocApiKey) {
        JSONObject warJson = new JSONObject(warData);
        JSONObject clanJson = warJson.getJSONObject("clan");
        JSONObject opponentJson = warJson.getJSONObject("opponent");

        ArrayList<CoC_WarAttackContainer> clanWarAttacks = getCurrentClanAttacks(warData);
        clanWarAttacks.sort(Comparator.comparing(CoC_WarAttackContainer::getDestructionPercentage));
        String bassieData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + clanWarAttacks.get(clanWarAttacks.size() - 1).getAttackerTag().replace("#", "%23"), cocApiKey);
        JSONObject bassieJson = new JSONObject(bassieData);
        String bassieAwardName = bassieJson.getString("name");
        String bassieAward = bassieAwardName + " met " + clanWarAttacks.get(clanWarAttacks.size() - 1).getDestructionPercentage() + "%";

        StringBuilder recap = new StringBuilder(EmojiParser.parseToUnicode("*De oorlog is afgelopen!* :checkered_flag:\n\n"));
        recap.append("_Eindstand: ").append(checkWarScore(warData)).append("_\n`");
        recap.append(EmojiParser.parseToUnicode(":family: ")).append(clanJson.getString("name")).append("\n");
        recap.append(EmojiParser.parseToUnicode(":collision: ")).append(clanJson.getInt("attacks")).append("/").append(warJson.getInt("teamSize") * 2).append("\n");
        recap.append(EmojiParser.parseToUnicode(":bar_chart: ")).append(clanJson.getDouble("destructionPercentage")).append("%\n\n");
        recap.append(EmojiParser.parseToUnicode(":family: ")).append(opponentJson.getString("name")).append("\n");
        recap.append(EmojiParser.parseToUnicode(":collision: ")).append(opponentJson.getInt("attacks")).append("/").append(warJson.getInt("teamSize") * 2).append("\n");
        recap.append(EmojiParser.parseToUnicode(":bar_chart: ")).append(opponentJson.getDouble("destructionPercentage")).append("%`\n\n");
        recap.append(EmojiParser.parseToUnicode(":ghost::trophy: *De Bassie-award gaat deze ronde naar:*\n_" + bassieAward + "_\n\n"));

        ArrayList<CoC_WarAttackContainer> attacks = new ArrayList<>();
        for (CoC_WarAttackContainer clanWarAttack : clanWarAttacks) {
            if (clanWarAttack.getDestructionPercentage() == 100) {
                attacks.add(clanWarAttack);
            }
        }

        if (!attacks.isEmpty()) {
            recap.append("*Helden van deze oorlog:*\n");

            for (CoC_WarAttackContainer attack : attacks) {
                recap.append(EmojiParser.parseToUnicode(":star::star::star: `"));
                recap.append(attack.getDestructionPercentage()).append("% - ");
                String attackerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + attack.getAttackerTag().replace("#", "%23"), cocApiKey);
                JSONObject attackerJson = new JSONObject(attackerData);
                recap.append(attackerJson.getString("name"));
                recap.append(EmojiParser.parseToUnicode(":arrow_right:"));
                String defenderData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + attack.getDefenderTag().replace("#", "%23"), cocApiKey);
                JSONObject defenderJson = new JSONObject(defenderData);
                recap.append(defenderJson.getString("name"));
                recap.append("`\n");
            }
        }

        return String.valueOf(recap);
    }

    public static String newOpponentOverview(String warData) {
        JSONObject warJson = new JSONObject(warData);
        ArrayList<CoC_PlayerContainer> opponentPlayers = new ArrayList<>();
        JSONObject opponentJson = warJson.getJSONObject("opponent");
        JSONArray opponentMemberJsonArray = opponentJson.getJSONArray("members");

        StringBuilder opponentOverview = new StringBuilder("*De oorlog is verklaard aan:* _");
        opponentOverview.append(opponentJson.getString("name")).append("_\n*------------------*");

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
                opponentOverview.append("0").append(player.getPositionInClan()).append(")");
            } else {
                opponentOverview.append(player.getPositionInClan()).append(")");
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
            botResponse.append("*Tegenstander:* _").append(opponentJson.getString("name")).append("_\n*------------------*");

            for (int j = 0; j < opponentMemberJsonArray.length(); j++) {
                JSONObject opponentMemberJson = opponentMemberJsonArray.getJSONObject(j);
                JSONObject opponentBestAttack = opponentMemberJson.getJSONObject("bestOpponentAttack");

                opponentPlayers.add(new CoC_PlayerContainer(opponentMemberJson.getInt("mapPosition"),
                        opponentMemberJson.getString("name"),
                        opponentMemberJson.getInt("townhallLevel"),
                        new CoC_WarAttackContainer(opponentBestAttack.getInt("stars"), opponentBestAttack.getInt("destructionPercentage"))));
            }

            opponentPlayers.sort(Comparator.comparing(CoC_PlayerContainer::getPositionInClan));

            for (CoC_PlayerContainer player : opponentPlayers) {
                botResponse.append("\n");
                if (player.getPositionInClan() < 10) {
                    botResponse.append("0").append(player.getPositionInClan()).append(")");
                } else {
                    botResponse.append(player.getPositionInClan()).append(")");
                }
                botResponse.append(EmojiParser.parseToUnicode(" - :house:")).append(player.getTownhallLevel()).append(" -> ");

                switch (player.getBestAttack().getStars()) {
                    case 0:
                        botResponse.append(EmojiParser.parseToUnicode(":heavy_multiplication_x::heavy_multiplication_x::heavy_multiplication_x: ")).
                                append(player.getBestAttack().getDestructionPercentage()).append("%");
                        break;
                    case 1:
                        botResponse.append(EmojiParser.parseToUnicode(":star:heavy_multiplication_x::heavy_multiplication_x: ")).
                                append(player.getBestAttack().getDestructionPercentage()).append("%");
                        break;
                    case 2:
                        botResponse.append(EmojiParser.parseToUnicode(":star::star::heavy_multiplication_x: ")).
                                append(player.getBestAttack().getDestructionPercentage()).append("%");
                        break;
                    case 3:
                        botResponse.append(EmojiParser.parseToUnicode(":star::star::star: ")).
                                append(player.getBestAttack().getDestructionPercentage()).append("%");
                        break;
                }
            }

            return String.valueOf(botResponse);
        }
        else {
            botResponse.append("_Er is momenteel geen tegenstander in de clanoorlog_");
            return String.valueOf(botResponse);
        }
    }

    public static String getBassieAwardTopDrie(ArrayList<CoC_WarAttackContainer> clanWarAttacks, String warState, String cocApiKey) {
        if (warState.equals("inWar")) {
            if (clanWarAttacks.size() >= 3) {
                clanWarAttacks.sort(Comparator.comparing(CoC_WarAttackContainer::getDestructionPercentage));

                StringBuilder bassieBuilder = new StringBuilder(EmojiParser.parseToUnicode(":trophy: *Bassie-award huidige top 3*\n\n"));
                for (int i = 0; i < 3; i++) {
                    JSONObject bassieJson = new JSONObject(CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + clanWarAttacks.get(i).getAttackerTag().replace("#", "%23"), cocApiKey));
                    String bassieAwardName = bassieJson.getString("name");

                    bassieBuilder.append("`");
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
                    bassieBuilder.append("`");
                }
                return String.valueOf(bassieBuilder);

            } else {
                return "_Om een top 3 te genereren moeten er tenminste 3 aanvallen zijn gedaan..._";
            }
        } else {
            return "_De top 3 kan niet gemaakt worden als de oorlog niet bezig is..._";
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
                return "_Gelukt! Je bent nu geabonneerd op de oorlogsupdates_";
            } else {
                return "_Je bent al geabonneerd op de oorlogsupdates..._";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "_Oeps, er is iets misgegaan..._";
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
                return "_Gelukt! Je bent niet langer geabonneerd op de oorlogsupdates_";
            } else {
                return "_Je bent niet geabonneerd op de oorlogsupdates, dus ik kan je ook niet verwijderen uit de lijst..._";
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "_Oeps, er is iets misgegaan..._";
    }
}
