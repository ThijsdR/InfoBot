package clashofclans;

import com.vdurmont.emoji.EmojiParser;
import help.H_Help;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import utility.TextFormatting;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class CoC_War
{
    public static String getCurrentWarState(String warData)
    {
        JSONObject warJson = new JSONObject(warData);
        return warJson.getString("state");
    }

    public static int getCurrentNumberClanAttacks(String warData)
    {
        return new JSONObject(warData).getJSONObject("clan").getInt("attacks");
    }

    public static int getCurrentNumberOpponentAttacks(String warData)
    {
        return new JSONObject(warData).getJSONObject("opponent").getInt("attacks");
    }

    public static ArrayList<CoC_WarAttackContainer> getCurrentClanAttacks(String warData)
    {
        ArrayList<CoC_WarAttackContainer> allAttacks = new ArrayList<>();

        JSONObject warJson = new JSONObject(warData);
        JSONObject clanJson = warJson.getJSONObject("clan");
        JSONArray clanMemberJsonArray = clanJson.getJSONArray("members");

        for (int i = 0; i < clanMemberJsonArray.length(); i++)
        {
            JSONObject clanMemberJson = clanMemberJsonArray.getJSONObject(i);

            if (clanMemberJson.has("attacks"))
            {
                JSONArray clanMemberAttacks = clanMemberJson.getJSONArray("attacks");

                for (int ii = 0; ii < clanMemberAttacks.length(); ii++)
                {
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

    public static ArrayList<CoC_WarAttackContainer> getCurrentOpponentAttacks(String warData)
    {
        ArrayList<CoC_WarAttackContainer> allAttacks = new ArrayList<>();

        JSONObject warJson = new JSONObject(warData);
        JSONObject opponentJson = warJson.getJSONObject("opponent");
        JSONArray opponentMemberJsonArray = opponentJson.getJSONArray("members");

        for (int j = 0; j < opponentMemberJsonArray.length(); j++)
        {
            JSONObject opponentMemberJson = opponentMemberJsonArray.getJSONObject(j);

            if (opponentMemberJson.has("attacks"))
            {
                JSONArray opponentMemberAttacks = opponentMemberJson.getJSONArray("attacks");

                for (int jj = 0; jj < opponentMemberAttacks.length(); jj++)
                {
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

    public static String warStartMessage(String warData)
    {
        JSONObject warJson = new JSONObject(warData);
        JSONObject opponentJson = warJson.getJSONObject("opponent");
        return TextFormatting.toBold("De oorlog tegen ") +
                TextFormatting.toItalic(opponentJson.getString("name")) +
                TextFormatting.toBold(" is begonnen!");
    }

    public static String warEndMessage(String warData)
    {
        JSONObject warJson = new JSONObject(warData);
        JSONObject opponentJson = warJson.getJSONObject("opponent");
        return TextFormatting.toBold("De oorlog tegen ") +
                TextFormatting.toItalic(opponentJson.getString("name")) +
                TextFormatting.toBold(" is afgelopen!");
    }

    static String checkWarScore(String warData)
    {
        JSONObject warJson = new JSONObject(warData);
        JSONObject clanJson = warJson.getJSONObject("clan");
        JSONObject opponentJson = warJson.getJSONObject("opponent");

        return clanJson.getInt("stars") + "-" + opponentJson.getInt("stars");
    }

    private static ArrayList<Integer> getWarStars(String warData)
    {
        JSONObject warJson = new JSONObject(warData);
        JSONObject clanJson = warJson.getJSONObject("clan");
        JSONObject opponentJson = warJson.getJSONObject("opponent");

        return new ArrayList<Integer>()
        {{
            add(clanJson.getInt("stars"));
            add(opponentJson.getInt("stars"));
        }};
    }

    private static ArrayList<CoC_PlayerContainer> getClanWarPlayers(JSONArray clanJsonJSONArray)
    {
        ArrayList<CoC_PlayerContainer> clanWarPlayers = new ArrayList<>();

        for (int i = 0; i < clanJsonJSONArray.length(); i++)
        {
            clanWarPlayers.add(new CoC_PlayerContainer(clanJsonJSONArray.getJSONObject(i).getInt("mapPosition"),
                    clanJsonJSONArray.getJSONObject(i).getString("tag"),
                    clanJsonJSONArray.getJSONObject(i).getString("name")));
        }

        return clanWarPlayers;
    }

    private static ArrayList<CoC_PlayerContainer> getClanWarOpponent(JSONArray opponentJsonJSONArray)
    {
        ArrayList<CoC_PlayerContainer> opponentWarPlayers = new ArrayList<>();

        for (int i = 0; i < opponentJsonJSONArray.length(); i++)
        {
            opponentWarPlayers.add(new CoC_PlayerContainer(opponentJsonJSONArray.getJSONObject(i).getInt("mapPosition"),
                    opponentJsonJSONArray.getJSONObject(i).getString("tag"),
                    opponentJsonJSONArray.getJSONObject(i).getString("name")));
        }

        return opponentWarPlayers;
    }

    private static ArrayList<Integer> getWarStarAttacks(JSONArray clanJsonJSONArray, JSONArray opponentJsonJSONArray)
    {
        int clanZeroStarAttacks = 0;
        int clanOneStarAttacks = 0;
        int clanTwoStarAttacks = 0;
        int clanThreeStarAttacks = 0;

        for (int i = 0; i < opponentJsonJSONArray.length(); i++)
        {
            int bestAttackStars = opponentJsonJSONArray.getJSONObject(i).getJSONObject("bestOpponentAttack").getInt("stars");

            switch (bestAttackStars)
            {
                case 0:
                    clanZeroStarAttacks++;
                    break;
                case 1:
                    clanOneStarAttacks++;
                    break;
                case 2:
                    clanTwoStarAttacks++;
                    break;
                case 3:
                    clanThreeStarAttacks++;
                    break;
            }
        }

        int opponentZeroStarAttacks = 0;
        int opponentOneStarAttacks = 0;
        int opponentTwoStarAttacks = 0;
        int opponentThreeStarAttacks = 0;

        for (int i = 0; i < clanJsonJSONArray.length(); i++)
        {
            int bestAttackStars = clanJsonJSONArray.getJSONObject(i).getJSONObject("bestOpponentAttack").getInt("stars");

            switch (bestAttackStars)
            {
                case 0:
                    opponentZeroStarAttacks++;
                    break;
                case 1:
                    opponentOneStarAttacks++;
                    break;
                case 2:
                    opponentTwoStarAttacks++;
                    break;
                case 3:
                    opponentThreeStarAttacks++;
                    break;
            }
        }

        return new ArrayList<>(Arrays.asList(clanThreeStarAttacks, clanTwoStarAttacks, clanOneStarAttacks, clanZeroStarAttacks,
                opponentThreeStarAttacks, opponentTwoStarAttacks, opponentOneStarAttacks, opponentZeroStarAttacks));
    }

    private static String getBassieWinner(ArrayList<CoC_WarAttackContainer> clanWarAttacks, ArrayList<CoC_PlayerContainer> clanWarPlayers, ArrayList<CoC_PlayerContainer> opponentWarPlayers, String cocApiKey)
    {
        StringBuilder bassieBuilder = new StringBuilder();

        String bassiePlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + clanWarAttacks.get(0).getAttackerTag().replace("#", "%23"), cocApiKey);
        JSONObject bassiePlayerJson = new JSONObject(bassiePlayerData);
        String bassiePlayerName = bassiePlayerJson.getString("name");
        String bassiePlayerTag = clanWarAttacks.get(0).getAttackerTag();

        String bassieOppoonentPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + clanWarAttacks.get(0).getDefenderTag().replace("#", "%23"), cocApiKey);
        JSONObject bassiepponentPlayerJson = new JSONObject(bassieOppoonentPlayerData);
        String bassieOpponentPlayerName = bassiepponentPlayerJson.getString("name");
        String bassieOpponentPlayerTag = clanWarAttacks.get(0).getDefenderTag();

        bassieBuilder.append(TextFormatting.toBold("WINNAAR VAN DE BASSIE-AWARD ")).append(EmojiParser.parseToUnicode(":clown::trophy:")).append(TextFormatting.toBold("\n-~-~-~-~-~-~-~-~-~-~-~\n"));

        switch (clanWarAttacks.get(0).getStars())
        {
            case 0:
                bassieBuilder.append(EmojiParser.parseToUnicode(":heavy_multiplication_x::heavy_multiplication_x::heavy_multiplication_x: "))
                        .append(clanWarAttacks.get(0).getDestructionPercentage())
                        .append("%");
                break;
            case 1:
                bassieBuilder.append(EmojiParser.parseToUnicode(":star::heavy_multiplication_x::heavy_multiplication_x: "))
                        .append(clanWarAttacks.get(0).getDestructionPercentage())
                        .append("%");
                break;
            case 2:
                bassieBuilder.append(EmojiParser.parseToUnicode(":star::star::heavy_multiplication_x: "))
                        .append(clanWarAttacks.get(0).getDestructionPercentage())
                        .append("%");
                break;
            case 3:
                bassieBuilder.append(EmojiParser.parseToUnicode(":star::star::star: "))
                        .append(clanWarAttacks.get(0).getDestructionPercentage())
                        .append("%");
                break;
        }

        bassieBuilder.append("\n");

        for (CoC_PlayerContainer player : clanWarPlayers)
        {
            if (player.getPlayerTag().equals(bassiePlayerTag))
            {
                bassieBuilder.append(TextFormatting.toBold(player.getPositionInClan() + ". ")).append(bassiePlayerName).append(EmojiParser.parseToUnicode(" :arrow_forward: "));
            }
        }

        for (CoC_PlayerContainer opponent : opponentWarPlayers)
        {
            if (opponent.getPlayerTag().equals(bassieOpponentPlayerTag))
            {
                bassieBuilder.append(TextFormatting.toBold(opponent.getPositionInClan() + ". ")).append(bassieOpponentPlayerName).append("\n");
            }
        }

        return String.valueOf(bassieBuilder);
    }

    public static String endWarRecap(String warData, String cocApiKey)
    {
        JSONObject warJson = new JSONObject(warData);

        JSONObject clanJson = warJson.getJSONObject("clan");
        JSONArray clanJsonJSONArray = clanJson.getJSONArray("members");

        JSONObject opponentJson = warJson.getJSONObject("opponent");
        JSONArray opponentJsonJSONArray = opponentJson.getJSONArray("members");

        ArrayList<CoC_PlayerContainer> clanWarPlayers = getClanWarPlayers(clanJsonJSONArray);
        ArrayList<CoC_PlayerContainer> opponentWarPlayers = getClanWarOpponent(opponentJsonJSONArray);

        ArrayList<CoC_WarAttackContainer> clanWarAttacks = getCurrentClanAttacks(warData);
        clanWarAttacks.sort(Comparator.comparing(CoC_WarAttackContainer::getDestructionPercentage));

        ArrayList<CoC_WarAttackContainer> opponentWarAttacks = getCurrentOpponentAttacks(warData);
        opponentWarAttacks.sort(Comparator.comparing(CoC_WarAttackContainer::getDestructionPercentage));

        int clanFailedAttacks = 0;

        for (CoC_WarAttackContainer attack : clanWarAttacks)
        {
            if (attack.getStars() == 0)
            {
                clanFailedAttacks++;
            }
        }

        int opponentFailedAttacks = 0;

        for (CoC_WarAttackContainer attack : opponentWarAttacks)
        {
            if (attack.getStars() == 0)
            {
                opponentFailedAttacks++;
            }
        }

        StringBuilder recap = new StringBuilder(TextFormatting.toBold("DE OORLOG IS AFGELOPEN! "));
        recap.append(EmojiParser.parseToUnicode(":checkered_flag:")).append(TextFormatting.toBold("\n-~-~-~-~-~-~-~-~-~-~-~\n"));

        ArrayList<Integer> warStars = getWarStars(warData);
        if (warStars.get(0) > warStars.get(1))
        {
            recap.append(TextFormatting.toItalic("We hebben deze oorlog gewonnen!")).append("\n\n");
        } else if (warStars.get(0) < warStars.get(1))
        {
            recap.append(TextFormatting.toItalic("We hebben deze oorlog helaas verloren...")).append("\n\n");
        } else
        {
            if (clanJson.getDouble("destructionPercentage") > opponentJson.getDouble("destructionPercentage"))
            {
                recap.append(TextFormatting.toItalic("We hebben deze oorlog gewonnen!")).append("\n\n");
            } else if (clanJson.getDouble("destructionPercentage") < opponentJson.getDouble("destructionPercentage"))
            {
                recap.append(TextFormatting.toItalic("We hebben deze oorlog helaas verloren...")).append("\n\n");
            } else
            {
                recap.append(TextFormatting.toItalic("De oorlog is geeindigd in een gelijkspel...")).append("\n\n");
            }
        }

        recap.append(TextFormatting.toCode("Eindstand: " + checkWarScore(warData))).append("\n\n");

        recap.append(TextFormatting.toBold(clanJson.getString("name"))).append(EmojiParser.parseToUnicode("\t\t:family:\t\t")).append(TextFormatting.toBold(opponentJson.getString("name"))).append(TextFormatting.toBold("\n-~-~-~-~-~-~-~-~-~-~-~"));

        recap.append("<pre>\n\n");
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
        recap.append(String.format("%.2f", clanJson.getDouble("destructionPercentage"))).append("%");
        recap.append("|");
        recap.append(EmojiParser.parseToUnicode(" :collision: "));
        recap.append("|");
        recap.append(String.format("%.2f", opponentJson.getDouble("destructionPercentage"))).append("%");
        recap.append("|");
        recap.append("\n");
        recap.append("--------------------\n");
        recap.append("| ");
        recap.append(String.format("%.2f", (double) clanJson.getInt("stars") / (double) clanJson.getInt("attacks")));
        recap.append(" |");
        recap.append(EmojiParser.parseToUnicode(":star::crossed_swords:"));
        recap.append("| ");
        recap.append(String.format("%.2f", (double) opponentJson.getInt("stars") / (double) opponentJson.getInt("attacks")));
        recap.append(" |");
        recap.append("\n");

        recap.append("--------------------\n");
        recap.append("|");

        if (clanJson.getInt("attacks") - clanFailedAttacks >= 10)
        {
            recap.append("   ").append(clanJson.getInt("attacks") - clanFailedAttacks);
        } else
        {
            recap.append("    ").append(clanJson.getInt("attacks") - clanFailedAttacks);
        }

        recap.append(" |").append(EmojiParser.parseToUnicode(":white_check_mark::crossed_swords:")).append("| ");

        if (opponentJson.getInt("attacks") - opponentFailedAttacks >= 10)
        {
            recap.append(opponentJson.getInt("attacks") - opponentFailedAttacks).append("   ");
        } else
        {
            recap.append(opponentJson.getInt("attacks") - opponentFailedAttacks).append("    ");
        }
        recap.append("|");
        recap.append("\n");

        recap.append("--------------------\n");
        recap.append("|");

        if (clanFailedAttacks >= 10)
        {
            recap.append("   ").append(clanFailedAttacks);
        } else
        {
            recap.append("    ").append(clanFailedAttacks);
        }

        recap.append(" |").append(EmojiParser.parseToUnicode(":x::crossed_swords:")).append("| ");

        if (opponentFailedAttacks >= 10)
        {
            recap.append(opponentFailedAttacks).append("   ");
        } else
        {
            recap.append(opponentFailedAttacks).append("    ");
        }
        recap.append("|");
        recap.append("\n");
        recap.append("--------------------\n");

        recap.append("\n");
        recap.append("[ Beste  aanvallen ]\n");
        recap.append("--------------------\n");
        recap.append("| ");

        ArrayList<Integer> warAttacks = getWarStarAttacks(clanJsonJSONArray, opponentJsonJSONArray);

        if (warAttacks.get(0) >= 10)
        {
            recap.append(warAttacks.get(0));
        } else
        {
            recap.append(" ").append(warAttacks.get(0));
        }
        recap.append(" |");
        recap.append(EmojiParser.parseToUnicode(" :star::star::star: "));
        recap.append("| ");

        if (warAttacks.get(4) >= 10)
        {
            recap.append(warAttacks.get(4));
        } else
        {
            recap.append(warAttacks.get(4)).append(" ");
        }
        recap.append(" |");
        recap.append("\n");
        recap.append("--------------------\n");
        recap.append("| ");

        if (warAttacks.get(1) >= 10)
        {
            recap.append(warAttacks.get(1));
        } else
        {
            recap.append(" ").append(warAttacks.get(1));
        }
        recap.append(" |");
        recap.append(EmojiParser.parseToUnicode(" :star::star::heavy_multiplication_x: "));
        recap.append("| ");

        if (warAttacks.get(5) >= 10)
        {
            recap.append(warAttacks.get(5));
        } else
        {
            recap.append(warAttacks.get(5)).append(" ");
        }
        recap.append(" |");
        recap.append("\n");
        recap.append("--------------------\n");
        recap.append("| ");

        if (warAttacks.get(2) >= 10)
        {
            recap.append(warAttacks.get(2));
        } else
        {
            recap.append(" ").append(warAttacks.get(2));
        }
        recap.append(" |");
        recap.append(EmojiParser.parseToUnicode(" :star::heavy_multiplication_x::heavy_multiplication_x: "));
        recap.append("| ");

        if (warAttacks.get(6) >= 10)
        {
            recap.append(warAttacks.get(6));
        } else
        {
            recap.append(warAttacks.get(6)).append(" ");
        }
        recap.append(" |");
        recap.append("\n");
        recap.append("--------------------\n");
        recap.append("| ");

        if (warAttacks.get(3) >= 10)
        {
            recap.append(warAttacks.get(3));
        } else
        {
            recap.append(" ").append(warAttacks.get(3));
        }
        recap.append(" |");
        recap.append(EmojiParser.parseToUnicode(" :heavy_multiplication_x::heavy_multiplication_x::heavy_multiplication_x: "));
        recap.append("| ");

        if (warAttacks.get(7) >= 10)
        {
            recap.append(warAttacks.get(7));
        } else
        {
            recap.append(warAttacks.get(7)).append(" ");
        }
        recap.append(" |");
        recap.append("\n");
        recap.append("--------------------\n");

        recap.append("</pre>");

        recap.append("\n");

        ArrayList<CoC_WarAttackContainer> perfectAttacks = new ArrayList<>();

        for (CoC_WarAttackContainer clanWarAttack : clanWarAttacks)
        {
            if (clanWarAttack.getDestructionPercentage() == 100)
            {
                perfectAttacks.add(clanWarAttack);
            }
        }

        recap.append(getBassieWinner(clanWarAttacks, clanWarPlayers, opponentWarPlayers, cocApiKey));

        if (!perfectAttacks.isEmpty())
        {
            recap.append(TextFormatting.toBold("\nEERVOLLE VERMELDINGEN")).append(EmojiParser.parseToUnicode(" :speech_balloon:")).append(TextFormatting.toBold("\n-~-~-~-~-~-~-~-~-~-~-~\n"));

            String clanPlayerName;
            String clanPlayerTag;
            String opponentPlayerName;
            String opponentPlayerTag;

            for (CoC_WarAttackContainer attack : perfectAttacks)
            {
                String clanPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + attack.getAttackerTag().replace("#", "%23"), cocApiKey);
                JSONObject clanPlayerJson = new JSONObject(clanPlayerData);
                clanPlayerName = clanPlayerJson.getString("name");
                clanPlayerTag = attack.getAttackerTag();

                String opponentPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + attack.getDefenderTag().replace("#", "%23"), cocApiKey);
                JSONObject opponentPlayerJson = new JSONObject(opponentPlayerData);
                opponentPlayerName = opponentPlayerJson.getString("name");
                opponentPlayerTag = attack.getDefenderTag();

                recap.append(EmojiParser.parseToUnicode(":star::star::star: :100:")).append("% \n");

                for (CoC_PlayerContainer player : clanWarPlayers)
                {
                    if (player.getPlayerTag().equals(clanPlayerTag))
                    {
                        recap.append(TextFormatting.toBold(player.getPositionInClan() + ". ")).append(clanPlayerName).append(EmojiParser.parseToUnicode(" :arrow_forward: "));
                    }
                }

                for (CoC_PlayerContainer opponent : opponentWarPlayers)
                {
                    if (opponent.getPlayerTag().equals(opponentPlayerTag))
                    {
                        recap.append(TextFormatting.toBold(opponent.getPositionInClan() + ". ")).append(opponentPlayerName).append("\n\n");
                    }
                }
            }
        }

        return String.valueOf(recap);
    }

    public static String newOpponentOverview(String warData)
    {
        JSONObject warJson = new JSONObject(warData);
        ArrayList<CoC_PlayerContainer> opponentPlayers = new ArrayList<>();
        JSONObject opponentJson = warJson.getJSONObject("opponent");
        JSONArray opponentMemberJsonArray = opponentJson.getJSONArray("members");

        StringBuilder opponentOverview = new StringBuilder(TextFormatting.toBold("De oorlog is verklaard aan: "));
        opponentOverview.append(TextFormatting.toItalic(opponentJson.getString("name"))).append("\n");

        for (int j = 0; j < opponentMemberJsonArray.length(); j++)
        {
            JSONObject opponentMemberJson = opponentMemberJsonArray.getJSONObject(j);
            opponentPlayers.add(new CoC_PlayerContainer(opponentMemberJson.getInt("mapPosition"),
                    opponentMemberJson.getString("name"),
                    opponentMemberJson.getInt("townhallLevel")));
        }

        opponentPlayers.sort(Comparator.comparing(CoC_PlayerContainer::getPositionInClan));

        for (CoC_PlayerContainer player : opponentPlayers)
        {
            opponentOverview.append("\n");
            if (player.getPositionInClan() < 10)
            {
                opponentOverview.append("0").append(player.getPositionInClan()).append(")");
            } else
            {
                opponentOverview.append(player.getPositionInClan()).append(")");
            }
            opponentOverview.append(EmojiParser.parseToUnicode(" :house:")).append(player.getTownhallLevel()).append(" ").append(player.getName());
        }

        return String.valueOf(opponentOverview);
    }

    public static String timeToEnd(String warData) {
        JSONObject warJson = new JSONObject(warData);
        String state = warJson.getString("state");
        StringBuilder botResponse = new StringBuilder();
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSSX");

        if (state.equals("inWar"))
        {
            String endTime = warJson.getString("endTime");
            LocalDateTime dtime = LocalDateTime.parse(endTime, format);
            long hoursToEnd = LocalDateTime.now(Clock.systemUTC()).until(dtime, ChronoUnit.HOURS);
            long minutesToEnd = LocalDateTime.now(Clock.systemUTC()).until(dtime, ChronoUnit.MINUTES) % 60;
            long secondsToEnd = (LocalDateTime.now(Clock.systemUTC()).until(dtime, ChronoUnit.SECONDS) % 3600) % 60;
            String timeUntil = hoursToEnd + "u " + minutesToEnd + "m " + secondsToEnd + "s";

            botResponse.append(TextFormatting.toBold("Oorlog afgelopen over:\n")).append(EmojiParser.parseToUnicode(":clock1: " + timeUntil)).append("\n\n");
            return String.valueOf(botResponse);
        }
        else
        {
            botResponse.append(TextFormatting.toItalic("De eindtijd kan alleen opgevraagd worden als de oorlog bezig is"));
            return String.valueOf(botResponse);
        }
    }

    public static String getCurrentWarStats(String warData)
    {
        JSONObject warJson = new JSONObject(warData);
        String state = warJson.getString("state");
        StringBuilder botResponse = new StringBuilder();

        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss.SSSX");

        switch (state)
        {
            case "inWar":
            {
                ArrayList<CoC_PlayerContainer> opponentPlayers = new ArrayList<>();
                ArrayList<CoC_PlayerContainer> clanPlayers = new ArrayList<>();

                JSONObject opponentJson = warJson.getJSONObject("opponent");
                JSONArray opponentMemberJsonArray = opponentJson.getJSONArray("members");

                JSONObject clanJson = warJson.getJSONObject("clan");
                JSONArray clanMemberJsonArray = clanJson.getJSONArray("members");

                botResponse.append(TextFormatting.toBold("Tegenstander: "))
                        .append(TextFormatting.toItalic(opponentJson.getString("name")))
                        .append("\n\n");
                botResponse.append(TextFormatting.toBold("Huidige stand: ")).append(TextFormatting.toItalic(checkWarScore(warData)))
                        .append("\n\n");

                String endTime = warJson.getString("endTime");
                LocalDateTime dtime = LocalDateTime.parse(endTime, format);
                long hoursToEnd = LocalDateTime.now(Clock.systemUTC()).until(dtime, ChronoUnit.HOURS);
                long minutesToEnd = LocalDateTime.now(Clock.systemUTC()).until(dtime, ChronoUnit.MINUTES) % 60;
                long secondsToEnd = (LocalDateTime.now(Clock.systemUTC()).until(dtime, ChronoUnit.SECONDS) % 3600) % 60;
                String timeUntil = hoursToEnd + "u " + minutesToEnd + "m " + secondsToEnd + "s";

                botResponse.append(TextFormatting.toBold("Oorlog afgelopen over:\n")).append(EmojiParser.parseToUnicode(":clock1: " + timeUntil)).append("\n\n");

                opponentPlayers = getBestAttackPerPlayer(opponentPlayers, opponentMemberJsonArray);
                clanPlayers = getBestAttackPerPlayer(clanPlayers, clanMemberJsonArray);


                botResponse.append(EmojiParser.parseToUnicode(":family: ")).append(TextFormatting.toBold(clanJson.getString("name"))).append("\n").append(TextFormatting.toBold("~~~~~~~~~~~~~~~~~~~\n\n"));
                botResponse.append(EmojiParser.parseToUnicode(":star2: ")).append(clanJson.getInt("stars")).append(" ");
                botResponse.append(EmojiParser.parseToUnicode(":crossed_swords: ")).append(clanJson.getInt("attacks")).append("/").append(warJson.getInt("teamSize") * 2).append(" ");
                botResponse.append(EmojiParser.parseToUnicode(":collision: ")).append(String.format("%.2f", clanJson.getDouble("destructionPercentage"))).append("%\n");
                botResponse.append(getBestAttacks(opponentPlayers, false, false));
                botResponse.append("\n\n");

                botResponse.append(EmojiParser.parseToUnicode(":family: ")).append(TextFormatting.toBold(opponentJson.getString("name"))).append("\n").append(TextFormatting.toBold("~~~~~~~~~~~~~~~~~~~\n\n"));
                botResponse.append(EmojiParser.parseToUnicode(":star2: ")).append(opponentJson.getInt("stars")).append(" ");
                botResponse.append(EmojiParser.parseToUnicode(":crossed_swords: ")).append(opponentJson.getInt("attacks")).append("/").append(warJson.getInt("teamSize") * 2).append(" ");
                botResponse.append(EmojiParser.parseToUnicode(":collision: ")).append(String.format("%.2f", opponentJson.getDouble("destructionPercentage"))).append("%\n");
                botResponse.append(getBestAttacks(clanPlayers, true, true));

                return String.valueOf(botResponse);
            }
            case "preparation":
            {
                botResponse.append(TextFormatting.toItalic("We zitten momenteel in de voorbereiding voor de oorlog! \n\n"));
                botResponse.append(TextFormatting.toBold("De oorlog begint over:\n"));

                String startTime = warJson.getString("startTime");
                LocalDateTime dtime = LocalDateTime.parse(startTime, format);
                long hoursToEnd = LocalDateTime.now(Clock.systemUTC()).until(dtime, ChronoUnit.HOURS);
                long minutesToEnd = LocalDateTime.now(Clock.systemUTC()).until(dtime, ChronoUnit.MINUTES) % 60;
                long secondsToEnd = (LocalDateTime.now(Clock.systemUTC()).until(dtime, ChronoUnit.SECONDS) % 3600) % 60;
                String timeUntil = hoursToEnd + "u " + minutesToEnd + "m " + secondsToEnd + "s";

                botResponse.append(EmojiParser.parseToUnicode(":clock1: " + timeUntil));
                return String.valueOf(botResponse);
            }
            default:
                botResponse.append(TextFormatting.toItalic("Dit overzicht kan alleen bekeken worden wanneer de oorlog bezig is"));
                return String.valueOf(botResponse);
        }
    }

    private static ArrayList<CoC_PlayerContainer> getBestAttackPerPlayer(ArrayList<CoC_PlayerContainer> players, JSONArray jsonArray)
    {
        for (int j = 0; j < jsonArray.length(); j++)
        {
            JSONObject memberJson = jsonArray.getJSONObject(j);
            JSONObject bestAttack = null;

            if (memberJson.has("bestOpponentAttack"))
            {
                bestAttack = memberJson.getJSONObject("bestOpponentAttack");
            }

            players.add(new CoC_PlayerContainer(memberJson.getInt("mapPosition"),
                    memberJson.getString("name"),
                    memberJson.getInt("townhallLevel"),
                    new CoC_WarAttackContainer(bestAttack == null ? 0 : bestAttack.getInt("stars"),
                            bestAttack == null ? 0 : bestAttack.getInt("destructionPercentage"))));
        }

        return players;
    }

    private static String getBestAttacks(ArrayList<CoC_PlayerContainer> players, boolean isBrabant, boolean threeStarOnly)
    {
        StringBuilder stringBuilder = new StringBuilder();
        players.sort(Comparator.comparing(CoC_PlayerContainer::getPositionInClan));

        for (CoC_PlayerContainer player : players)
        {
            if (!isBrabant)
            {
                stringBuilder.append("\n");
                if (player.getPositionInClan() < 10)
                {
                    stringBuilder.append("0").append(player.getPositionInClan()).append(")");
                } else
                {
                    stringBuilder.append(player.getPositionInClan()).append(")");
                }

                if (player.getTownhallLevel() < 10)
                {
                    stringBuilder.append(EmojiParser.parseToUnicode(" :house:"))
                            .append("0")
                            .append(player.getTownhallLevel())
                            .append(" ");
                } else
                {
                    stringBuilder.append(EmojiParser.parseToUnicode(" :house:"))
                            .append(player.getTownhallLevel())
                            .append(" ");
                }
            } else
            {
                stringBuilder.append(" ");
            }

            if (!threeStarOnly)
            {
                switch (player.getBestAttack().getStars())
                {
                    case 0:
                        stringBuilder.append(EmojiParser.parseToUnicode(":heavy_multiplication_x::heavy_multiplication_x::heavy_multiplication_x: "))
                                .append(player.getBestAttack().getDestructionPercentage())
                                .append("%");
                        break;
                    case 1:
                        stringBuilder.append(EmojiParser.parseToUnicode(":star::heavy_multiplication_x::heavy_multiplication_x: "))
                                .append(player.getBestAttack().getDestructionPercentage())
                                .append("%");
                        break;
                    case 2:
                        stringBuilder.append(EmojiParser.parseToUnicode(":star::star::heavy_multiplication_x: "))
                                .append(player.getBestAttack().getDestructionPercentage())
                                .append("%");
                        break;
                    case 3:
                        stringBuilder.append(EmojiParser.parseToUnicode(":star::star::star: "))
                                .append(player.getBestAttack().getDestructionPercentage())
                                .append("%");
                        break;
                }
            } else
            {
                if (player.getBestAttack().getStars() == 3)
                {
                    stringBuilder.append("\n");
                    if (player.getPositionInClan() < 10)
                    {
                        stringBuilder.append("0").append(player.getPositionInClan()).append(")");
                    } else
                    {
                        stringBuilder.append(player.getPositionInClan()).append(")");
                    }

                    stringBuilder.append(EmojiParser.parseToUnicode(" :star::star::star: "))
                            .append(player.getBestAttack().getDestructionPercentage())
                            .append("%")
                            .append(" - ")
                            .append(player.getName());
                }
            }
        }
        return String.valueOf(stringBuilder);
    }

    public static String getBassieAwardTopDrie(ArrayList<CoC_WarAttackContainer> clanWarAttacks, String warState, String cocApiKey)
    {
        if (warState.equals("inWar") || warState.equals("warEnded"))
        {
            if (clanWarAttacks.size() >= 3)
            {
                clanWarAttacks.sort(Comparator.comparing(CoC_WarAttackContainer::getStars));
                clanWarAttacks.sort(Comparator.comparing(CoC_WarAttackContainer::getDestructionPercentage));

                StringBuilder bassieBuilder = new StringBuilder(EmojiParser.parseToUnicode(":trophy:"));
                bassieBuilder.append(TextFormatting.toBold(" Bassie-award huidige top 3\n~~~~~~~~~~~~~~~~~~~")).append("\n");
                for (int i = 0; i < 3; i++)
                {
                    JSONObject bassieJson = new JSONObject(CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + clanWarAttacks.get(i).getAttackerTag().replace("#", "%23"), cocApiKey));
                    String bassieAwardName = bassieJson.getString("name");

                    switch (i)
                    {
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

            } else
            {
                return TextFormatting.toItalic("Om een top 3 te genereren moeten er tenminste 3 aanvallen zijn gedaan...");
            }
        } else
        {
            return TextFormatting.toItalic("De top 3 kan niet gemaakt worden als er geen gegevens zijn over de aanvallen van de oorlog...");
        }
    }

    public static String subscribeToWarEvents(long chatID)
    {
        try
        {
            ArrayList<Long> chatIDs = new ArrayList<>();
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("C:/Users/Administrator/Documents/InfoBotfiles/dbpass.txt")));
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ChatID FROM subscriberswar");
            while (rs.next())
            {
                chatIDs.add((long) rs.getInt("ChatID"));
            }

            if (!chatIDs.contains(chatID))
            {
                stmt.execute("INSERT INTO subscriberswar (ChatID) VALUES ('" + chatID + "')");
                rs.close();
                stmt.close();
                con.close();
                return TextFormatting.toItalic("Gelukt! Je bent nu geabonneerd op de oorlogsupdates");
            } else
            {
                rs.close();
                stmt.close();
                con.close();
                return TextFormatting.toItalic("Je bent al geabonneerd op de oorlogsupdates...");
            }

        } catch (SQLException | ClassNotFoundException | IOException e)
        {
            System.out.println(H_Help.exceptionStacktraceToString(e));
        }

        return TextFormatting.toItalic("Oeps, er is iets misgegaan...");
    }

    public static String unsubscribeToWarEvents(long chatID)
    {
        try
        {
            ArrayList<Long> chatIDs = new ArrayList<>();
            Class.forName("com.mysql.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("C:/Users/Administrator/Documents/InfoBotfiles/dbpass.txt")));
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT ChatID FROM subscriberswar");
            while (rs.next())
            {
                chatIDs.add((long) rs.getInt("ChatID"));
            }

            if (chatIDs.contains(chatID))
            {
                stmt.execute("DELETE FROM subscriberswar WHERE ChatID = '" + chatID + "'");
                rs.close();
                stmt.close();
                con.close();
                return TextFormatting.toItalic("Gelukt! Je bent niet langer geabonneerd op de oorlogsupdates");
            } else
            {
                rs.close();
                stmt.close();
                con.close();
                return TextFormatting.toItalic("Je bent niet geabonneerd op de oorlogsupdates, dus ik kan je ook niet verwijderen uit de lijst...");
            }

        } catch (SQLException | ClassNotFoundException | IOException e)
        {
            System.out.println(H_Help.exceptionStacktraceToString(e));

        }
        return TextFormatting.toItalic("Oeps, er is iets misgegaan...");
    }
}
