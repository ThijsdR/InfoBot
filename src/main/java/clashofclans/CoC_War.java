package clashofclans;

import com.vdurmont.emoji.EmojiParser;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import utility.TextFormatting;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.text.DecimalFormat;
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
        JSONObject warJson = new JSONObject(warData);

        JSONObject clanJson = warJson.getJSONObject("clan");
        JSONArray clanJsonJSONArray = clanJson.getJSONArray("members");
        ArrayList<CoC_PlayerContainer> clanWarPlayers = new ArrayList<>();

        for (int i = 0; i < clanJsonJSONArray.length(); i++) {
            clanWarPlayers.add(new CoC_PlayerContainer(clanJsonJSONArray.getJSONObject(i).getInt("mapPosition"),
                    clanJsonJSONArray.getJSONObject(i).getString("tag"),
                    clanJsonJSONArray.getJSONObject(i).getString("name")));
        }

        JSONObject opponentJson = warJson.getJSONObject("opponent");
        JSONArray opponentJsonJSONArray = opponentJson.getJSONArray("members");
        ArrayList<CoC_PlayerContainer> opponentWarPlayers = new ArrayList<>();

        for (int i = 0; i < opponentJsonJSONArray.length(); i++) {
            opponentWarPlayers.add(new CoC_PlayerContainer(opponentJsonJSONArray.getJSONObject(i).getInt("mapPosition"),
                    opponentJsonJSONArray.getJSONObject(i).getString("tag"),
                    opponentJsonJSONArray.getJSONObject(i).getString("name")));
        }

        try {
            ArrayList<CoC_WarAttackContainer> clanWarAttacks = getCurrentClanAttacks(warData);
            ArrayList<CoC_WarAttackContainer> opponentWarAttacks = getCurrentOpponentAttacks(warData);

            CoC_WarAttackContainer lastAttack;
            String clanPlayerName;
            String clanPlayerTag;
            String opponentPlayerName;
            String opponentPlayerTag;

            if (clanWarAttacks.size() != currentClanAttacks.size() || opponentWarAttacks.size() != currentOpponentAttacks.size()) {
                attackString.append(EmojiParser.parseToUnicode(":crossed_swords:"))
                        .append(TextFormatting.toBold("Oorlogsupdate!"))
                        .append("\n\n");
                attackString.append(TextFormatting.toItalic("Huidige stand: "))
                        .append(checkWarScore(warData))
                        .append("\n\n");
            }

            if (clanWarAttacks.size() > currentClanAttacks.size()) {
                for (int i = 1; i <= (clanWarAttacks.size() - currentClanAttacks.size()); i++) {
                    lastAttack = clanWarAttacks.get(clanWarAttacks.size() - i);
                    String clanPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getAttackerTag().replace("#", "%23"), cocApiKey);
                    JSONObject clanPlayerJson = new JSONObject(clanPlayerData);
                    clanPlayerName = clanPlayerJson.getString("name");
                    clanPlayerTag = lastAttack.getAttackerTag();

                    String opponentPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getDefenderTag().replace("#", "%23"), cocApiKey);
                    JSONObject opponentPlayerJson = new JSONObject(opponentPlayerData);
                    opponentPlayerName = opponentPlayerJson.getString("name");
                    opponentPlayerTag = lastAttack.getDefenderTag();

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

                    attackString.append(lastAttack.getDestructionPercentage()).append("% \n");

                    for (CoC_PlayerContainer player : clanWarPlayers) {
                        if (player.getPlayerTag().equals(clanPlayerTag)) {
                            attackString.append(TextFormatting.toBold(player.getPositionInClan() + ". ")).append(clanPlayerName).append(EmojiParser.parseToUnicode(" :arrow_forward: "));
                        }
                    }

                    for (CoC_PlayerContainer opponent : opponentWarPlayers) {
                        if (opponent.getPlayerTag().equals(opponentPlayerTag)) {
                            attackString.append(TextFormatting.toBold(opponent.getPositionInClan() + ". ")).append(opponentPlayerName).append("\n\n");
                        }
                    }
                }
            }

            if (opponentWarAttacks.size() > currentOpponentAttacks.size()) {
                for (int i = 1; i <= (opponentWarAttacks.size() - currentOpponentAttacks.size()); i++) {
                    lastAttack = opponentWarAttacks.get(opponentWarAttacks.size() - i);

                    String opponentPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getAttackerTag().replace("#", "%23"), cocApiKey);
                    JSONObject opponentPlayerJson = new JSONObject(opponentPlayerData);
                    opponentPlayerName = opponentPlayerJson.getString("name");
                    opponentPlayerTag = lastAttack.getAttackerTag();

                    String clanPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getDefenderTag().replace("#", "%23"), cocApiKey);
                    JSONObject clanPlayerJson = new JSONObject(clanPlayerData);
                    clanPlayerName = clanPlayerJson.getString("name");
                    clanPlayerTag = lastAttack.getDefenderTag();

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

                    attackString.append(lastAttack.getDestructionPercentage()).append("% \n");

                    for (CoC_PlayerContainer player : clanWarPlayers) {
                        if (player.getPlayerTag().equals(clanPlayerTag)) {
                            attackString.append(TextFormatting.toBold(player.getPositionInClan() + ". "))
                                    .append(clanPlayerName)
                                    .append(EmojiParser.parseToUnicode(" :arrow_backward: "));
                        }
                    }

                    for (CoC_PlayerContainer opponent : opponentWarPlayers) {
                        if (opponent.getPlayerTag().equals(opponentPlayerTag)) {
                            attackString.append(TextFormatting.toBold(opponent.getPositionInClan() + ". "))
                                    .append(opponentPlayerName)
                                    .append("\n\n");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return String.valueOf(attackString);
    }

    public static String war3StarUpdate(String warData, ArrayList<CoC_WarAttackContainer> currentClanAttacks, String cocApiKey) {
        StringBuilder attackString = new StringBuilder();

        JSONObject warJson = new JSONObject(warData);

        JSONObject clanJson = warJson.getJSONObject("clan");
        JSONArray clanJsonJSONArray = clanJson.getJSONArray("members");
        ArrayList<CoC_PlayerContainer> clanWarPlayers = new ArrayList<>();

        for (int i = 0; i < clanJsonJSONArray.length(); i++) {
            clanWarPlayers.add(new CoC_PlayerContainer(clanJsonJSONArray.getJSONObject(i).getInt("mapPosition"),
                    clanJsonJSONArray.getJSONObject(i).getString("tag"),
                    clanJsonJSONArray.getJSONObject(i).getString("name")));
        }

        JSONObject opponentJson = warJson.getJSONObject("opponent");
        JSONArray opponentJsonJSONArray = opponentJson.getJSONArray("members");
        ArrayList<CoC_PlayerContainer> opponentWarPlayers = new ArrayList<>();

        for (int i = 0; i < opponentJsonJSONArray.length(); i++) {
            opponentWarPlayers.add(new CoC_PlayerContainer(opponentJsonJSONArray.getJSONObject(i).getInt("mapPosition"),
                    opponentJsonJSONArray.getJSONObject(i).getString("tag"),
                    opponentJsonJSONArray.getJSONObject(i).getString("name")));
        }

        try {
            ArrayList<CoC_WarAttackContainer> clanWarAttacks = getCurrentClanAttacks(warData);

            if (clanWarAttacks.size() > currentClanAttacks.size()) {
                CoC_WarAttackContainer lastAttack;
                String clanPlayerName;
                String clanPlayerTag;
                String opponentPlayerName;
                String opponentPlayerTag;

                for (int i = 1; i <= (clanWarAttacks.size() - currentClanAttacks.size()); i++) {
                    lastAttack = clanWarAttacks.get(clanWarAttacks.size() - i);

                    if (lastAttack.getDestructionPercentage() == 100) {
                        attackString.append(EmojiParser.parseToUnicode(":confetti_ball::tada: "))
                                .append(TextFormatting.toBold("NIEUWE 3-STER AANVAL!"))
                                .append("\n\n");

                        String clanPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getAttackerTag().replace("#", "%23"), cocApiKey);
                        JSONObject clanPlayerJson = new JSONObject(clanPlayerData);
                        clanPlayerName = clanPlayerJson.getString("name");
                        clanPlayerTag = lastAttack.getAttackerTag();

                        String opponentPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getDefenderTag().replace("#", "%23"), cocApiKey);
                        JSONObject opponentPlayerJson = new JSONObject(opponentPlayerData);
                        opponentPlayerName = opponentPlayerJson.getString("name");
                        opponentPlayerTag = lastAttack.getDefenderTag();

                        attackString.append(EmojiParser.parseToUnicode(":star::star::star: :100:%\n"));

                        for (CoC_PlayerContainer player : clanWarPlayers) {
                            if (player.getPlayerTag().equals(clanPlayerTag)) {
                                attackString.append(TextFormatting.toBold(player.getPositionInClan() + ". "))
                                        .append(clanPlayerName)
                                        .append(EmojiParser.parseToUnicode(" :arrow_forward: "));
                            }
                        }

                        for (CoC_PlayerContainer opponent : opponentWarPlayers) {
                            if (opponent.getPlayerTag().equals(opponentPlayerTag)) {
                                attackString.append(TextFormatting.toBold(opponent.getPositionInClan() + ". "))
                                        .append(opponentPlayerName)
                                        .append("\n\n");
                            }
                        }
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
        return TextFormatting.toBold("De oorlog tegen ") +
                TextFormatting.toItalic(opponentJson.getString("name")) +
                TextFormatting.toBold(" is begonnen!");
    }

    private static String checkWarScore(String warData) {
        JSONObject warJson = new JSONObject(warData);
        JSONObject clanJson = warJson.getJSONObject("clan");
        JSONObject opponentJson = warJson.getJSONObject("opponent");

        return clanJson.getInt("stars") + "-" + opponentJson.getInt("stars");
    }

    private static ArrayList<Integer> getWarStars(String warData) {
        JSONObject warJson = new JSONObject(warData);
        JSONObject clanJson = warJson.getJSONObject("clan");
        JSONObject opponentJson = warJson.getJSONObject("opponent");

        return new ArrayList<Integer>() {{
            add(clanJson.getInt("stars"));
            add(opponentJson.getInt("stars"));
        }};
    }

    public static String endWarRecap(String warData, String cocApiKey) {
        JSONObject warJson = new JSONObject(warData);
        JSONObject clanJson = warJson.getJSONObject("clan");
        JSONArray clanJsonJSONArray = clanJson.getJSONArray("members");
        ArrayList<CoC_PlayerContainer> clanWarPlayers = new ArrayList<>();

        for (int i = 0; i < clanJsonJSONArray.length(); i++) {
            clanWarPlayers.add(new CoC_PlayerContainer(clanJsonJSONArray.getJSONObject(i).getInt("mapPosition"),
                    clanJsonJSONArray.getJSONObject(i).getString("tag"),
                    clanJsonJSONArray.getJSONObject(i).getString("name")));
        }

        JSONObject opponentJson = warJson.getJSONObject("opponent");
        JSONArray opponentJsonJSONArray = opponentJson.getJSONArray("members");
        ArrayList<CoC_PlayerContainer> opponentWarPlayers = new ArrayList<>();

        for (int i = 0; i < opponentJsonJSONArray.length(); i++) {
            opponentWarPlayers.add(new CoC_PlayerContainer(opponentJsonJSONArray.getJSONObject(i).getInt("mapPosition"),
                    opponentJsonJSONArray.getJSONObject(i).getString("tag"),
                    opponentJsonJSONArray.getJSONObject(i).getString("name")));
        }

        int clanZeroStarAttacks = 0;
        int clanOneStarAttacks = 0;
        int clanTwoStarAttacks = 0;
        int clanThreeStarAttacks = 0;

        for (int i = 0; i < opponentJsonJSONArray.length(); i++) {
            JSONObject bestAttack = opponentJsonJSONArray.getJSONObject(i).getJSONObject("bestOpponentAttack");

            switch (bestAttack.getInt("stars")) {
                case 0: clanZeroStarAttacks++; break;
                case 1: clanOneStarAttacks++; break;
                case 2: clanTwoStarAttacks++; break;
                case 3: clanThreeStarAttacks++; break;
            }
        }

        int opponentZeroStarAttacks = 0;
        int opponentOneStarAttacks = 0;
        int opponentTwoStarAttacks = 0;
        int opponentThreeStarAttacks = 0;

        for (int i = 0; i < clanJsonJSONArray.length(); i++) {
            JSONObject bestAttack = clanJsonJSONArray.getJSONObject(i).getJSONObject("bestOpponentAttack");

            switch (bestAttack.getInt("stars")) {
                case 0: opponentZeroStarAttacks++; break;
                case 1: opponentOneStarAttacks++; break;
                case 2: opponentTwoStarAttacks++; break;
                case 3: opponentThreeStarAttacks++; break;
            }
        }

        ArrayList<CoC_WarAttackContainer> clanWarAttacks = getCurrentClanAttacks(warData);
        clanWarAttacks.sort(Comparator.comparing(CoC_WarAttackContainer::getDestructionPercentage));

        ArrayList<CoC_WarAttackContainer> opponentWarAttacks = getCurrentOpponentAttacks(warData);
        opponentWarAttacks.sort(Comparator.comparing(CoC_WarAttackContainer::getDestructionPercentage));

        int clanFailedAttacks = 0;

        for (CoC_WarAttackContainer attack : clanWarAttacks) {
            if (attack.getStars() == 0) {
                clanFailedAttacks++;
            }
        }

        int opponentFailedAttacks = 0;

        for (CoC_WarAttackContainer attack : opponentWarAttacks) {
            if (attack.getStars() == 0) {
                opponentFailedAttacks++;
            }
        }

        DecimalFormat df = new DecimalFormat("##.##");

        StringBuilder recap = new StringBuilder(TextFormatting.toBold("DE OORLOG IS AFGELOPEN! "));
        recap.append(EmojiParser.parseToUnicode(":checkered_flag:\n\n"));

        ArrayList<Integer> warStars = getWarStars(warData);
        if (warStars.get(0) > warStars.get(1)) {
            recap.append(TextFormatting.toItalic("We hebben deze oorlog gewonnen!")).append("\n\n");
        } else if (warStars.get(0) < warStars.get(1)) {
            recap.append(TextFormatting.toItalic("We hebben deze oorlog helaas verloren...")).append("\n\n");
        } else {
            if (clanJson.getDouble("destructionPercentage") > opponentJson.getDouble("destructionPercentage")) {
                recap.append(TextFormatting.toItalic("We hebben deze oorlog gewonnen!")).append("\n\n");
            } else if (clanJson.getDouble("destructionPercentage") < opponentJson.getDouble("destructionPercentage")) {
                recap.append(TextFormatting.toItalic("We hebben deze oorlog helaas verloren...")).append("\n\n");
            } else {
                recap.append(TextFormatting.toItalic("De oorlog is geeindigd in een gelijkspel...")).append("\n\n");
            }
        }

        recap.append(TextFormatting.toCode("<< Eindstand: " + checkWarScore(warData) + " >>")).append("\n\n\n");

        recap.append(TextFormatting.toBold(clanJson.getString("name")))
                .append(EmojiParser.parseToUnicode("\t\t:family:\t\t"))
                .append(TextFormatting.toBold(opponentJson.getString("name")));

        recap.append("```\n\n\n");
        recap.append("[   Statistieken   ]\n");
        recap.append("--------------------\n");
        recap.append("|");
        recap.append("   ").append(clanJson.getInt("stars"));
        recap.append(" |");
        recap.append(EmojiParser.parseToUnicode(" :star2: "));
        recap.append("| ");
        recap.append(opponentJson.getInt("stars")).append("   ");
        recap.append("|");
        recap.append("\n");
        recap.append("--------------------\n");
        recap.append("|");
        recap.append(" ").append(clanJson.getInt("attacks")).append("/").append(warJson.getInt("teamSize") * 2);
        recap.append("|");
        recap.append(EmojiParser.parseToUnicode(" :crossed_swords: "));
        recap.append("|");
        recap.append(opponentJson.getInt("attacks")).append("/").append(warJson.getInt("teamSize") * 2).append(" ");
        recap.append("|");
        recap.append("\n");
        recap.append("--------------------\n");
        recap.append("|");
        recap.append(df.format(clanJson.getDouble("destructionPercentage"))).append("%");
        recap.append("|");
        recap.append(EmojiParser.parseToUnicode(" :collision: "));
        recap.append("|");
        recap.append(df.format(opponentJson.getDouble("destructionPercentage"))).append("%");
        recap.append("|");
        recap.append("\n");
        recap.append("--------------------\n");
        recap.append("| ");
        recap.append(df.format((double)clanJson.getInt("stars") / (double)clanJson.getInt("attacks")));
        recap.append(" |");
        recap.append(EmojiParser.parseToUnicode(":star::crossed_swords:"));
        recap.append("| ");
        recap.append(df.format((double)opponentJson.getInt("stars") / (double)opponentJson.getInt("attacks")));
        recap.append(" |");
        recap.append("\n");

        recap.append("--------------------\n");
        recap.append("|");

        if (clanJson.getInt("attacks") - clanFailedAttacks >= 10) {
            recap.append("   ").append(clanJson.getInt("attacks") - clanFailedAttacks);
        } else {
            recap.append("    ").append(clanJson.getInt("attacks") - clanFailedAttacks);
        }

        recap.append(" |").append(EmojiParser.parseToUnicode(":white_check_mark::crossed_swords:")).append("| ");

        if (opponentJson.getInt("attacks") - opponentFailedAttacks >= 10) {
            recap.append(opponentJson.getInt("attacks") - opponentFailedAttacks).append("   ");
        } else {
            recap.append(opponentJson.getInt("attacks") - opponentFailedAttacks).append("    ");
        }
        recap.append("|");
        recap.append("\n");

        recap.append("--------------------\n");
        recap.append("|");

        if (clanFailedAttacks >= 10) {
            recap.append("   ").append(clanFailedAttacks);
        } else {
            recap.append("    ").append(clanFailedAttacks);
        }

        recap.append(" |").append(EmojiParser.parseToUnicode(":x::crossed_swords:")).append("| ");

        if (opponentFailedAttacks >= 10) {
            recap.append(opponentFailedAttacks).append("   ");
        } else {
            recap.append(opponentFailedAttacks).append("    ");
        }
        recap.append("|");
        recap.append("\n");
        recap.append("--------------------\n");

        recap.append("\n");
        recap.append("[ Beste  aanvallen ]\n");
        recap.append("--------------------\n");
        recap.append("| ");

        if (clanThreeStarAttacks >= 10) {
            recap.append(clanThreeStarAttacks);
        } else {
            recap.append(" ").append(clanThreeStarAttacks);
        }
        recap.append(" |");
        recap.append(EmojiParser.parseToUnicode(" :star::star::star: "));
        recap.append("| ");

        if (opponentThreeStarAttacks >= 10) {
            recap.append(opponentThreeStarAttacks);
        } else {
            recap.append(opponentThreeStarAttacks).append(" ");
        }
        recap.append(" |");
        recap.append("\n");
        recap.append("--------------------\n");
        recap.append("| ");

        if (clanTwoStarAttacks >= 10) {
            recap.append(clanTwoStarAttacks);
        } else {
            recap.append(" ").append(clanTwoStarAttacks);
        }
        recap.append(" |");
        recap.append(EmojiParser.parseToUnicode(" :star::star::heavy_multiplication_x: "));
        recap.append("| ");

        if (opponentTwoStarAttacks >= 10) {
            recap.append(opponentTwoStarAttacks);
        } else {
            recap.append(opponentTwoStarAttacks).append(" ");
        }
        recap.append(" |");
        recap.append("\n");
        recap.append("--------------------\n");
        recap.append("| ");

        if (clanOneStarAttacks >= 10) {
            recap.append(clanOneStarAttacks);
        } else {
            recap.append(" ").append(clanOneStarAttacks);
        }
        recap.append(" |");
        recap.append(EmojiParser.parseToUnicode(" :star::heavy_multiplication_x::heavy_multiplication_x: "));
        recap.append("| ");

        if (opponentOneStarAttacks >= 10) {
            recap.append(opponentOneStarAttacks);
        } else {
            recap.append(opponentOneStarAttacks).append(" ");
        }
        recap.append(" |");
        recap.append("\n");
        recap.append("--------------------\n");
        recap.append("| ");

        if (clanZeroStarAttacks >= 10) {
            recap.append(clanZeroStarAttacks);
        } else {
            recap.append(" ").append(clanZeroStarAttacks);
        }
        recap.append(" |");
        recap.append(EmojiParser.parseToUnicode(" :heavy_multiplication_x::heavy_multiplication_x::heavy_multiplication_x: "));
        recap.append("| ");

        if (opponentZeroStarAttacks >= 10) {
            recap.append(opponentZeroStarAttacks);
        } else {
            recap.append(opponentZeroStarAttacks).append(" ");
        }
        recap.append(" |");
        recap.append("\n");
        recap.append("--------------------\n");

        recap.append("```");

        recap.append("\n");

        String bassiePlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + clanWarAttacks.get(0).getAttackerTag().replace("#", "%23"), cocApiKey);
        JSONObject bassiePlayerJson = new JSONObject(bassiePlayerData);
        String bassiePlayerName = bassiePlayerJson.getString("name");
        String bassiePlayerTag = clanWarAttacks.get(0).getAttackerTag();

        String bassieOppoonentPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + clanWarAttacks.get(0).getDefenderTag().replace("#", "%23"), cocApiKey);
        JSONObject bassiepponentPlayerJson = new JSONObject(bassieOppoonentPlayerData);
        String bassieOpponentPlayerName = bassiepponentPlayerJson.getString("name");
        String bassieOpponentPlayerTag = clanWarAttacks.get(0).getDefenderTag();

        recap.append(TextFormatting.toBold("\nWINNAAR VAN DE BASSIE-AWARD ")).append(EmojiParser.parseToUnicode(":clown::trophy:\n\n"));

        switch (clanWarAttacks.get(0).getStars()) {
            case 0:
                recap.append(EmojiParser.parseToUnicode(":heavy_multiplication_x::heavy_multiplication_x::heavy_multiplication_x: "))
                        .append(clanWarAttacks.get(0).getDestructionPercentage())
                        .append("%");
                break;
            case 1:
                recap.append(EmojiParser.parseToUnicode(":star::heavy_multiplication_x::heavy_multiplication_x: "))
                        .append(clanWarAttacks.get(0).getDestructionPercentage())
                        .append("%");
                break;
            case 2:
                recap.append(EmojiParser.parseToUnicode(":star::star::heavy_multiplication_x: "))
                        .append(clanWarAttacks.get(0).getDestructionPercentage())
                        .append("%");
                break;
            case 3:
                recap.append(EmojiParser.parseToUnicode(":star::star::star: "))
                        .append(clanWarAttacks.get(0).getDestructionPercentage())
                        .append("%");
                break;
        }

        recap.append("\n");

        for (CoC_PlayerContainer player : clanWarPlayers) {
            if (player.getPlayerTag().equals(bassiePlayerTag)) {
                recap.append(TextFormatting.toBold(player.getPositionInClan() + ". "))
                        .append(bassiePlayerName)
                        .append(EmojiParser.parseToUnicode(" :arrow_forward: "));
            }
        }

        for (CoC_PlayerContainer opponent : opponentWarPlayers) {
            if (opponent.getPlayerTag().equals(bassieOpponentPlayerTag)) {
                recap.append(TextFormatting.toBold(opponent.getPositionInClan() + ". "))
                        .append(bassieOpponentPlayerName)
                        .append("\n\n");
            }
        }

        ArrayList<CoC_WarAttackContainer> perfectAttacks = new ArrayList<>();

        for (CoC_WarAttackContainer clanWarAttack : clanWarAttacks) {
            if (clanWarAttack.getDestructionPercentage() == 100) {
                perfectAttacks.add(clanWarAttack);
            }
        }

        if (!perfectAttacks.isEmpty()) {
            recap.append(TextFormatting.toBold("\nEERVOLLE VERMELDINGEN:"));
            recap.append(EmojiParser.parseToUnicode(" :speech_balloon:"));
            recap.append("\n\n");

            String clanPlayerName;
            String clanPlayerTag;
            String opponentPlayerName;
            String opponentPlayerTag;

            for (CoC_WarAttackContainer attack : perfectAttacks) {
                String clanPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + attack.getAttackerTag().replace("#", "%23"), cocApiKey);
                JSONObject clanPlayerJson = new JSONObject(clanPlayerData);
                clanPlayerName = clanPlayerJson.getString("name");
                clanPlayerTag = attack.getAttackerTag();

                String opponentPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + attack.getDefenderTag().replace("#", "%23"), cocApiKey);
                JSONObject opponentPlayerJson = new JSONObject(opponentPlayerData);
                opponentPlayerName = opponentPlayerJson.getString("name");
                opponentPlayerTag = attack.getDefenderTag();

                recap.append(EmojiParser.parseToUnicode(":star::star::star: :100:")).append("% \n");

                for (CoC_PlayerContainer player : clanWarPlayers) {
                    if (player.getPlayerTag().equals(clanPlayerTag)) {
                        recap.append(TextFormatting.toBold(player.getPositionInClan() + ". "))
                                .append(clanPlayerName)
                                .append(EmojiParser.parseToUnicode(" :arrow_forward: "));
                    }
                }

                for (CoC_PlayerContainer opponent : opponentWarPlayers) {
                    if (opponent.getPlayerTag().equals(opponentPlayerTag)) {
                        recap.append(TextFormatting.toBold(opponent.getPositionInClan() + ". "))
                                .append(opponentPlayerName)
                                .append("\n\n");
                    }
                }
            }
        }

        return String.valueOf(recap);
    }

    public static String newOpponentOverview(String warData) {
        JSONObject warJson = new JSONObject(warData);
        ArrayList<CoC_PlayerContainer> opponentPlayers = new ArrayList<>();
        JSONObject opponentJson = warJson.getJSONObject("opponent");
        JSONArray opponentMemberJsonArray = opponentJson.getJSONArray("members");

        StringBuilder opponentOverview = new StringBuilder(TextFormatting.toBold("De oorlog is verklaard aan: "));
        opponentOverview.append(TextFormatting.toItalic(opponentJson.getString("name"))).append("\n");

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
            opponentOverview.append(EmojiParser.parseToUnicode(" :house:")).append(player.getTownhallLevel()).append(" ");
            opponentOverview.append(player.getName());
        }

        return String.valueOf(opponentOverview);
    }

    public static String getCurrentOpponentOverview(String warData) {
        JSONObject warJson = new JSONObject(warData);
        String state = warJson.getString("state");
        StringBuilder botResponse = new StringBuilder();

        if (state.equals("inWar")) {
            ArrayList<CoC_PlayerContainer> opponentPlayers = new ArrayList<>();
            JSONObject opponentJson = warJson.getJSONObject("opponent");
            JSONArray opponentMemberJsonArray = opponentJson.getJSONArray("members");
            botResponse.append(TextFormatting.toBold("Tegenstander: "))
                    .append(TextFormatting.toItalic(opponentJson.getString("name")))
                    .append("\n\n");
            botResponse.append(TextFormatting.toItalic("Huidige stand: " + checkWarScore(warData)))
                    .append("\n\n");

            JSONObject clanJson = warJson.getJSONObject("clan");
            DecimalFormat df = new DecimalFormat(".##");
            botResponse.append(EmojiParser.parseToUnicode(":crossed_swords: ")).append(clanJson.getInt("attacks")).append("/").append(warJson.getInt("teamSize") * 2).append(" ");
            botResponse.append(EmojiParser.parseToUnicode(":collision: ")).append(df.format(clanJson.getDouble("destructionPercentage"))).append("%\n");

            for (int j = 0; j < opponentMemberJsonArray.length(); j++) {
                JSONObject opponentMemberJson = opponentMemberJsonArray.getJSONObject(j);
                JSONObject opponentBestAttack = null;

                if (opponentMemberJson.has("bestOpponentAttack")) {
                    opponentBestAttack = opponentMemberJson.getJSONObject("bestOpponentAttack");
                }

                opponentPlayers.add(new CoC_PlayerContainer(opponentMemberJson.getInt("mapPosition"),
                        opponentMemberJson.getString("name"),
                        opponentMemberJson.getInt("townhallLevel"),
                        new CoC_WarAttackContainer(opponentBestAttack == null ? 0 : opponentBestAttack.getInt("stars"),
                                opponentBestAttack == null ? 0 : opponentBestAttack.getInt("destructionPercentage"))));
            }

            opponentPlayers.sort(Comparator.comparing(CoC_PlayerContainer::getPositionInClan));

            for (CoC_PlayerContainer player : opponentPlayers) {
                botResponse.append("\n");
                if (player.getPositionInClan() < 10) {
                    botResponse.append("0").append(player.getPositionInClan()).append(")");
                } else {
                    botResponse.append(player.getPositionInClan()).append(")");
                }

                if (player.getTownhallLevel() < 10) {
                    botResponse.append(EmojiParser.parseToUnicode(" :house:"))
                            .append("0")
                            .append(player.getTownhallLevel())
                            .append(" ");
                } else {
                    botResponse.append(EmojiParser.parseToUnicode(" :house:"))
                            .append(player.getTownhallLevel())
                            .append(" ");
                }

                switch (player.getBestAttack().getStars()) {
                    case 0:
                        botResponse.append(EmojiParser.parseToUnicode(":heavy_multiplication_x::heavy_multiplication_x::heavy_multiplication_x: "))
                                .append(player.getBestAttack().getDestructionPercentage())
                                .append("%");
                        break;
                    case 1:
                        botResponse.append(EmojiParser.parseToUnicode(":star::heavy_multiplication_x::heavy_multiplication_x: "))
                                .append(player.getBestAttack().getDestructionPercentage())
                                .append("%");
                        break;
                    case 2:
                        botResponse.append(EmojiParser.parseToUnicode(":star::star::heavy_multiplication_x: "))
                                .append(player.getBestAttack().getDestructionPercentage())
                                .append("%");
                        break;
                    case 3:
                        botResponse.append(EmojiParser.parseToUnicode(":star::star::star: "))
                                .append(player.getBestAttack().getDestructionPercentage())
                                .append("%");
                        break;
                }
            }

            return String.valueOf(botResponse);
        }
        else {
            botResponse.append(TextFormatting.toItalic("Dit overzicht kan alleen bekeken worden wanneer de oorlog bezig is"));
            return String.valueOf(botResponse);
        }
    }

    public static String getBassieAwardTopDrie(ArrayList<CoC_WarAttackContainer> clanWarAttacks, String warState, String cocApiKey) {
        if (warState.equals("inWar") || warState.equals("warEnded")) {
            if (clanWarAttacks.size() >= 3) {
                clanWarAttacks.sort(Comparator.comparing(CoC_WarAttackContainer::getDestructionPercentage));

                StringBuilder bassieBuilder = new StringBuilder(EmojiParser.parseToUnicode(":trophy:"));
                bassieBuilder.append(TextFormatting.toBold("Bassie-award huidige top 3")).append("\n");
                for (int i = 0; i < 3; i++) {
                    JSONObject bassieJson = new JSONObject(CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + clanWarAttacks.get(i).getAttackerTag().replace("#", "%23"), cocApiKey));
                    String bassieAwardName = bassieJson.getString("name");

                    switch (i) {
                        case 0:
                            bassieBuilder.append(EmojiParser.parseToUnicode(":first_place_medal: " + bassieAwardName)).append(" - ").append(clanWarAttacks.get(i).getDestructionPercentage()).append("%\n");
                            break;
                        case 1:
                            bassieBuilder.append(EmojiParser.parseToUnicode(":second_place_medal: " + bassieAwardName)).append(" - ").append(clanWarAttacks.get(i).getDestructionPercentage()).append("%\n");
                            break;
                        case 2:
                            bassieBuilder.append(EmojiParser.parseToUnicode(":third_place_medal: " + bassieAwardName)).append(" - ").append(clanWarAttacks.get(i).getDestructionPercentage()).append("%\n");
                            break;
                    }
                }
                return String.valueOf(bassieBuilder);

            } else {
                return TextFormatting.toItalic("Om een top 3 te genereren moeten er tenminste 3 aanvallen zijn gedaan...");
            }
        } else {
            return TextFormatting.toItalic("De top 3 kan niet gemaakt worden als er geen gegevens zijn over de aanvallen van de oorlog...");
        }
    }

    public static String subscribeToWarEvents(long chatID) {
        try {
            ArrayList<Long> chatIDs = new ArrayList<>();
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/dbpass.txt")));
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ChatID FROM subscriberswar");
            while (rs.next()) {
                chatIDs.add((long) rs.getInt("ChatID"));
            }

            if (!chatIDs.contains(chatID)) {
                stmt.execute("INSERT INTO subscriberswar (ChatID) VALUES ('" + chatID + "')");
                rs.close();
                stmt.close();
                con.close();
                return TextFormatting.toItalic("Gelukt! Je bent nu geabonneerd op de oorlogsupdates");
            } else {
                rs.close();
                stmt.close();
                con.close();
                return TextFormatting.toItalic("Je bent al geabonneerd op de oorlogsupdates...");
            }

        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return TextFormatting.toItalic("Oeps, er is iets misgegaan...");
    }

    public static String unsubscribeToWarEvents(long chatID) {
        try {
            ArrayList<Long> chatIDs = new ArrayList<>();
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/dbpass.txt")));
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ChatID FROM subscriberswar");
            while (rs.next()) {
                chatIDs.add((long) rs.getInt("ChatID"));
            }

            if (chatIDs.contains(chatID)) {
                stmt.execute("DELETE FROM subscriberswar WHERE ChatID = '" + chatID + "'");
                rs.close();
                stmt.close();
                con.close();
                return TextFormatting.toItalic("Gelukt! Je bent niet langer geabonneerd op de oorlogsupdates");
            } else {
                rs.close();
                stmt.close();
                con.close();
                return TextFormatting.toItalic("Je bent niet geabonneerd op de oorlogsupdates, dus ik kan je ook niet verwijderen uit de lijst...");
            }

        } catch (SQLException | ClassNotFoundException | IOException e) {
            e.printStackTrace();
        }
        return TextFormatting.toItalic("Oeps, er is iets misgegaan...");
    }
}
