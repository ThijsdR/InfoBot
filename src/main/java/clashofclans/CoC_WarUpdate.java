package clashofclans;

import com.vdurmont.emoji.EmojiParser;
import help.H_Help;
import org.json.JSONArray;
import org.json.JSONObject;
import utility.TextFormatting;

import java.util.ArrayList;

public class CoC_WarUpdate
{
    public static String warAttacksUpdate(String warData, ArrayList<CoC_WarAttackContainer> currentClanAttacks, ArrayList<CoC_WarAttackContainer> currentOpponentAttacks, ArrayList<CoC_WarAttackContainer> updatedClanAttacks, ArrayList<CoC_WarAttackContainer> updatedOpponentAttacks, String cocApiKey)
    {
        StringBuilder attackString = new StringBuilder();
        JSONObject warJson = new JSONObject(warData);

        JSONObject clanJson = warJson.getJSONObject("clan");
        JSONArray clanJsonJSONArray = clanJson.getJSONArray("members");
        ArrayList<CoC_PlayerContainer> clanWarPlayers = new ArrayList<>();

        for (int i = 0; i < clanJsonJSONArray.length(); i++)
        {
            clanWarPlayers.add(new CoC_PlayerContainer(clanJsonJSONArray.getJSONObject(i).getInt("mapPosition"),
                    clanJsonJSONArray.getJSONObject(i).getString("tag"),
                    clanJsonJSONArray.getJSONObject(i).getString("name")));
        }

        JSONObject opponentJson = warJson.getJSONObject("opponent");
        JSONArray opponentJsonJSONArray = opponentJson.getJSONArray("members");
        ArrayList<CoC_PlayerContainer> opponentWarPlayers = new ArrayList<>();

        for (int i = 0; i < opponentJsonJSONArray.length(); i++)
        {
            opponentWarPlayers.add(new CoC_PlayerContainer(opponentJsonJSONArray.getJSONObject(i).getInt("mapPosition"),
                    opponentJsonJSONArray.getJSONObject(i).getString("tag"),
                    opponentJsonJSONArray.getJSONObject(i).getString("name")));
        }

        try
        {
            CoC_WarAttackContainer lastAttack;
            String clanPlayerName;
            String clanPlayerTag;
            String opponentPlayerName;
            String opponentPlayerTag;

            if (updatedClanAttacks.size() != currentClanAttacks.size() || updatedOpponentAttacks.size() != currentOpponentAttacks.size())
            {
                attackString.append(EmojiParser.parseToUnicode(":crossed_swords:"))
                        .append(TextFormatting.toBold("Oorlogsupdate!"))
                        .append("\n\n");
                attackString.append(TextFormatting.toItalic("Huidige stand: "))
                        .append(CoC_War.checkWarScore(warData))
                        .append("\n\n");
            }

            if (updatedClanAttacks.size() > currentClanAttacks.size())
            {
                for (int i = 1; i <= (updatedClanAttacks.size() - currentClanAttacks.size()); i++)
                {
                    lastAttack = updatedClanAttacks.get(updatedClanAttacks.size() - i);
                    String clanPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getAttackerTag().replace("#", "%23"), cocApiKey);
                    JSONObject clanPlayerJson = new JSONObject(clanPlayerData);
                    clanPlayerName = clanPlayerJson.getString("name");
                    clanPlayerTag = lastAttack.getAttackerTag();

                    String opponentPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getDefenderTag().replace("#", "%23"), cocApiKey);
                    JSONObject opponentPlayerJson = new JSONObject(opponentPlayerData);
                    opponentPlayerName = opponentPlayerJson.getString("name");
                    opponentPlayerTag = lastAttack.getDefenderTag();

                    switch (lastAttack.getStars())
                    {
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

                    for (CoC_PlayerContainer player : clanWarPlayers)
                    {
                        if (player.getPlayerTag().equals(clanPlayerTag))
                        {
                            attackString.append(TextFormatting.toBold(player.getPositionInClan() + ". ")).append(clanPlayerName).append(EmojiParser.parseToUnicode(" :arrow_forward: "));
                        }
                    }

                    for (CoC_PlayerContainer opponent : opponentWarPlayers)
                    {
                        if (opponent.getPlayerTag().equals(opponentPlayerTag))
                        {
                            attackString.append(TextFormatting.toBold(opponent.getPositionInClan() + ". ")).append(opponentPlayerName).append("\n\n");
                        }
                    }
                }
            }

            if (updatedOpponentAttacks.size() > currentOpponentAttacks.size())
            {
                for (int i = 1; i <= (updatedOpponentAttacks.size() - currentOpponentAttacks.size()); i++)
                {
                    lastAttack = updatedOpponentAttacks.get(updatedOpponentAttacks.size() - i);

                    String opponentPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getAttackerTag().replace("#", "%23"), cocApiKey);
                    JSONObject opponentPlayerJson = new JSONObject(opponentPlayerData);
                    opponentPlayerName = opponentPlayerJson.getString("name");
                    opponentPlayerTag = lastAttack.getAttackerTag();

                    String clanPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getDefenderTag().replace("#", "%23"), cocApiKey);
                    JSONObject clanPlayerJson = new JSONObject(clanPlayerData);
                    clanPlayerName = clanPlayerJson.getString("name");
                    clanPlayerTag = lastAttack.getDefenderTag();

                    switch (lastAttack.getStars())
                    {
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

                    for (CoC_PlayerContainer player : clanWarPlayers)
                    {
                        if (player.getPlayerTag().equals(clanPlayerTag))
                        {
                            attackString.append(TextFormatting.toBold(player.getPositionInClan() + ". "))
                                    .append(clanPlayerName)
                                    .append(EmojiParser.parseToUnicode(" :arrow_backward: "));
                        }
                    }

                    for (CoC_PlayerContainer opponent : opponentWarPlayers)
                    {
                        if (opponent.getPlayerTag().equals(opponentPlayerTag))
                        {
                            attackString.append(TextFormatting.toBold(opponent.getPositionInClan() + ". "))
                                    .append(opponentPlayerName)
                                    .append("\n\n");
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            System.out.println(H_Help.exceptionStacktraceToString(e));
        }

        return String.valueOf(attackString);
    }

    public static String war3StarUpdate(String warData, ArrayList<CoC_WarAttackContainer> currentClanAttacks, ArrayList<CoC_WarAttackContainer> updatedClanAttacks, String cocApiKey)
    {
        StringBuilder attackString = new StringBuilder();

        JSONObject warJson = new JSONObject(warData);

        JSONObject clanJson = warJson.getJSONObject("clan");
        JSONArray clanJsonJSONArray = clanJson.getJSONArray("members");
        ArrayList<CoC_PlayerContainer> clanWarPlayers = new ArrayList<>();

        for (int i = 0; i < clanJsonJSONArray.length(); i++)
        {
            clanWarPlayers.add(new CoC_PlayerContainer(clanJsonJSONArray.getJSONObject(i).getInt("mapPosition"),
                    clanJsonJSONArray.getJSONObject(i).getString("tag"),
                    clanJsonJSONArray.getJSONObject(i).getString("name")));
        }

        JSONObject opponentJson = warJson.getJSONObject("opponent");
        JSONArray opponentJsonJSONArray = opponentJson.getJSONArray("members");
        ArrayList<CoC_PlayerContainer> opponentWarPlayers = new ArrayList<>();

        for (int i = 0; i < opponentJsonJSONArray.length(); i++)
        {
            opponentWarPlayers.add(new CoC_PlayerContainer(opponentJsonJSONArray.getJSONObject(i).getInt("mapPosition"),
                    opponentJsonJSONArray.getJSONObject(i).getString("tag"),
                    opponentJsonJSONArray.getJSONObject(i).getString("name")));
        }

        try
        {
            if (updatedClanAttacks.size() > currentClanAttacks.size())
            {
                CoC_WarAttackContainer lastAttack;
                String clanPlayerName;
                String clanPlayerTag;
                String opponentPlayerName;
                String opponentPlayerTag;

                for (int i = 1; i <= (updatedClanAttacks.size() - currentClanAttacks.size()); i++)
                {
                    lastAttack = updatedClanAttacks.get(updatedClanAttacks.size() - i);

                    if (lastAttack.getDestructionPercentage() == 100)
                    {
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

                        for (CoC_PlayerContainer player : clanWarPlayers)
                        {
                            if (player.getPlayerTag().equals(clanPlayerTag))
                            {
                                attackString.append(TextFormatting.toBold(player.getPositionInClan() + ". "))
                                        .append(clanPlayerName)
                                        .append(EmojiParser.parseToUnicode(" :arrow_forward: "));
                            }
                        }

                        for (CoC_PlayerContainer opponent : opponentWarPlayers)
                        {
                            if (opponent.getPlayerTag().equals(opponentPlayerTag))
                            {
                                attackString.append(TextFormatting.toBold(opponent.getPositionInClan() + ". "))
                                        .append(opponentPlayerName)
                                        .append("\n\n");
                            }
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            System.out.println(H_Help.exceptionStacktraceToString(e));
        }

        return String.valueOf(attackString);
    }

    public static String war3StarUpdateOpponent(String warData, ArrayList<CoC_WarAttackContainer> currentOpponentAttacks, ArrayList<CoC_WarAttackContainer> updatedOpponentAttacks, String cocApiKey)
    {
        StringBuilder attackString = new StringBuilder();

        JSONObject warJson = new JSONObject(warData);

        JSONObject clanJson = warJson.getJSONObject("clan");
        JSONArray clanJsonJSONArray = clanJson.getJSONArray("members");
        ArrayList<CoC_PlayerContainer> clanWarPlayers = new ArrayList<>();

        for (int i = 0; i < clanJsonJSONArray.length(); i++)
        {
            clanWarPlayers.add(new CoC_PlayerContainer(clanJsonJSONArray.getJSONObject(i).getInt("mapPosition"),
                    clanJsonJSONArray.getJSONObject(i).getString("tag"),
                    clanJsonJSONArray.getJSONObject(i).getString("name")));
        }

        JSONObject opponentJson = warJson.getJSONObject("opponent");
        JSONArray opponentJsonJSONArray = opponentJson.getJSONArray("members");
        ArrayList<CoC_PlayerContainer> opponentWarPlayers = new ArrayList<>();

        for (int i = 0; i < opponentJsonJSONArray.length(); i++)
        {
            opponentWarPlayers.add(new CoC_PlayerContainer(opponentJsonJSONArray.getJSONObject(i).getInt("mapPosition"),
                    opponentJsonJSONArray.getJSONObject(i).getString("tag"),
                    opponentJsonJSONArray.getJSONObject(i).getString("name")));
        }

        try
        {
            if (updatedOpponentAttacks.size() > currentOpponentAttacks.size())
            {
                CoC_WarAttackContainer lastAttack;
                String clanPlayerName;
                String clanPlayerTag;
                String opponentPlayerName;
                String opponentPlayerTag;

                for (int i = 1; i <= (updatedOpponentAttacks.size() - currentOpponentAttacks.size()); i++)
                {
                    lastAttack = updatedOpponentAttacks.get(updatedOpponentAttacks.size() - i);

                    if (lastAttack.getDestructionPercentage() == 100)
                    {
                        attackString.append(TextFormatting.toBold("EEN CLANGENOOT IS ZOJUIST PLATGEGOOID!"))
                                .append("\n\n");

                        String clanPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getDefenderTag().replace("#", "%23"), cocApiKey);
                        JSONObject clanPlayerJson = new JSONObject(clanPlayerData);
                        clanPlayerName = clanPlayerJson.getString("name");
                        clanPlayerTag = lastAttack.getDefenderTag();

                        String opponentPlayerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + lastAttack.getAttackerTag().replace("#", "%23"), cocApiKey);
                        JSONObject opponentPlayerJson = new JSONObject(opponentPlayerData);
                        opponentPlayerName = opponentPlayerJson.getString("name");
                        opponentPlayerTag = lastAttack.getAttackerTag();

                        attackString.append(EmojiParser.parseToUnicode(":star::star::star: :100:%\n"));

                        for (CoC_PlayerContainer player : clanWarPlayers)
                        {
                            if (player.getPlayerTag().equals(clanPlayerTag))
                            {
                                attackString.append(TextFormatting.toBold(player.getPositionInClan() + ". "))
                                        .append(clanPlayerName)
                                        .append(EmojiParser.parseToUnicode(" :arrow_backward: "));
                            }
                        }

                        for (CoC_PlayerContainer opponent : opponentWarPlayers)
                        {
                            if (opponent.getPlayerTag().equals(opponentPlayerTag))
                            {
                                attackString.append(TextFormatting.toBold(opponent.getPositionInClan() + ". "))
                                        .append(opponentPlayerName)
                                        .append("\n\n");
                            }
                        }
                    }
                }
            }
        } catch (Exception e)
        {
            System.out.println(H_Help.exceptionStacktraceToString(e));
        }

        return String.valueOf(attackString);
    }
}
