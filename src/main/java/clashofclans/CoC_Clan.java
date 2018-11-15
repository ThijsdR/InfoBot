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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Deze klasse bevat methode(s) welke betrekking hebben op de gehele clan
 */
public class CoC_Clan
{

    public static int getClanSize(String cocApiKey)
    {
        String returnJson = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/clans/%23J0C9CPY", cocApiKey);
        JSONObject json = new JSONObject(returnJson);
        return json.getInt("members");
    }

    public static ArrayList<CoC_PlayerContainer> getCoCPlayerList(String cocApiKey)
    {
        ArrayList<CoC_PlayerContainer> playerList = new ArrayList<>();

        String returnJson = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/clans/%23J0C9CPY/members", cocApiKey);

        JSONObject json = new JSONObject(returnJson);
        JSONArray jsonArray = json.getJSONArray("items");

        for (int i = 0; i < jsonArray.length(); i++)
        {
            String playerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/%23" + jsonArray.getJSONObject(i).getString("tag").substring(1), cocApiKey);
            JSONObject playerJson = new JSONObject(playerData);
            JSONArray heroesJsonArray = playerJson.getJSONArray("heroes");

            CoC_PlayerContainer player = new CoC_PlayerContainer(playerJson.getString("name"),
                    playerJson.getString("tag"),
                    playerJson.getInt("townHallLevel"),
                    playerJson.getInt("trophies"));

            ArrayList<CoC_Hero> heroList = new ArrayList<>();

            for (int j = 0; j < heroesJsonArray.length(); j++)
            {
                heroList.add(new CoC_Hero(heroesJsonArray.getJSONObject(j).getString("name"),
                        heroesJsonArray.getJSONObject(j).getInt("level")));
            }
            player.setHeroLevels(heroList);
            playerList.add(player);
        }

        return playerList;
    }

    public static String getClanChange(ArrayList<CoC_PlayerContainer> cocPlayersList, ArrayList<CoC_PlayerContainer> updatedList)
    {
        ArrayList<CoC_PlayerContainer> uniqueMembers = new ArrayList<>();

        boolean memberJoined = false;

        if (updatedList.size() > cocPlayersList.size())
        {
            for (CoC_PlayerContainer player2 : updatedList)
            {
                boolean flag = false;
                for (CoC_PlayerContainer player1 : cocPlayersList)
                {
                    if (player2.getPlayerTag().equals(player1.getPlayerTag()))
                    {
                        flag = true;
                        break;
                    }
                }

                if (!flag)
                {
                    uniqueMembers.add(player2);
                }
            }
            memberJoined = true;
        }

        if (updatedList.size() < cocPlayersList.size())
        {
            for (CoC_PlayerContainer player1 : cocPlayersList)
            {
                boolean flag = false;
                for (CoC_PlayerContainer player2 : updatedList)
                {
                    if (player1.getPlayerTag().equals(player2.getPlayerTag()))
                    {
                        flag = true;
                        break;
                    }
                }

                if (!flag)
                {
                    uniqueMembers.add(player1);
                }
            }
            memberJoined = false;
        }

        StringBuilder stringBuilder = new StringBuilder();
        if (!uniqueMembers.isEmpty())
        {
            if (memberJoined)
            {
                stringBuilder = new StringBuilder(TextFormatting.toBold("Een speler heeft zich bij de clan aangesloten!"));
            } else
            {
                stringBuilder = new StringBuilder(TextFormatting.toBold("Een speler zit niet langer meer bij de clan!"));
            }

            for (CoC_PlayerContainer player : uniqueMembers)
            {
                stringBuilder.append("\n\n");
                stringBuilder.append(player.getName());
                stringBuilder.append(" (")
                        .append(player.getPlayerTag())
                        .append(")\n");
                stringBuilder.append(EmojiParser.parseToUnicode(":house: "))
                        .append(player.getTownhallLevel())
                        .append(" ");
                stringBuilder.append(EmojiParser.parseToUnicode(":trophy: "))
                        .append(player.getTrophyCount())
                        .append(" ");

                for (CoC_Hero hero : player.getHeroLevels())
                {
                    String heroName = hero.getName();
                    switch (heroName)
                    {
                        case "Barbarian King":
                            stringBuilder.append(EmojiParser.parseToUnicode(":prince: "))
                                    .append(hero.getLevel())
                                    .append(" ");
                            break;
                        case "Archer Queen":
                            stringBuilder.append(EmojiParser.parseToUnicode(":princess: "))
                                    .append(hero.getLevel())
                                    .append(" ");
                            break;
                        case "Grand Warden":
                            stringBuilder.append(EmojiParser.parseToUnicode(":angel: "))
                                    .append(hero.getLevel());
                            break;
                    }
                }

                if (memberJoined)
                {
                    Map<String, String> blacklistMap = new HashMap<>();

                    try
                    {
                        Class.forName("com.mysql.jdbc.Driver");
                        Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/infobotdb", "root", FileUtils.readFileToString(new File("C:/Users/Administrator/Documents/InfoBotfiles/dbpass.txt")));
                        Statement stmt = con.createStatement();
                        ResultSet rs = stmt.executeQuery("SELECT Tag, Reason FROM blacklist");
                        while (rs.next())
                        {
                            blacklistMap.put(rs.getString("Tag"), rs.getString("Reason"));
                        }
                        rs.close();
                        stmt.close();
                        con.close();
                    } catch (SQLException | ClassNotFoundException | IOException e)
                    {
                        System.out.println(H_Help.exceptionStacktraceToString(e));
                    }

                    for (Map.Entry<String, String> entry : blacklistMap.entrySet())
                    {
                        if (entry.getKey().toLowerCase().equals(player.getPlayerTag().toLowerCase()))
                        {
                            stringBuilder.append("\n\n >> ")
                                    .append(EmojiParser.parseToUnicode(" :warning: "))
                                    .append(TextFormatting.toBold("Deze speler staat op de zwarte lijst!"))
                                    .append("\n")
                                    .append(TextFormatting.toItalic(entry.getValue()));
                        }
                    }

                    String playerName = player.getName().replaceAll("\\s", "-").toLowerCase();
                    stringBuilder.append("\n\n")
                            .append("https://www.clashofstats.com/players/")
                            .append(playerName)
                            .append("-")
                            .append(player.getPlayerTag().substring(1))
                            .append("/profile/clans-history#tabs");
                }
            }
        }
        return String.valueOf(stringBuilder);
    }
}
