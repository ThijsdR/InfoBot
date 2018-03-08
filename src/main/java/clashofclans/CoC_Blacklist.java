package clashofclans;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SystemUtils;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

public class CoC_Blacklist {
    public static String addToCOCBlacklist(String playerTag) throws IOException {
        String playerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + playerTag.replace("#", "%23"));
        JSONObject playerJson = new JSONObject(playerData);

        StringBuilder blacklist = new StringBuilder();
        blacklist.append(FileUtils.readFileToString(new File("out/blacklist/cocblacklist.txt")));
        blacklist.append(playerTag);
        if (SystemUtils.IS_OS_WINDOWS) {
            FileUtils.writeStringToFile(new File("out/blacklist/cocblacklist.txt"), String.valueOf(blacklist));
        } else if (SystemUtils.IS_OS_LINUX) {
            FileUtils.writeStringToFile(new File("/home/thijs/Infobotfiles/cocblacklist.txt"), String.valueOf(blacklist));
        }
        return "Speler: " + playerJson.getString("name") + " met tag: " + playerTag + " is toegevoegd aan de zwarte lijst!";
    }

    public static String removeFromCOCBlacklist(String playerTag) throws IOException {
        String playerData = CoC_PROC.retrieveDataSupercellAPI("https://api.clashofclans.com/v1/players/" + playerTag.replace("#", "%23"));
        JSONObject playerJson = new JSONObject(playerData);

        String blacklist = "";
        if (SystemUtils.IS_OS_WINDOWS) {
            blacklist = FileUtils.readFileToString(new File("out/blacklist/cocblacklist.txt"));
        } else if (SystemUtils.IS_OS_LINUX) {
            blacklist = FileUtils.readFileToString(new File("/home/thijs/Infobotfiles/cocblacklist.txt"));
        }
        blacklist = blacklist.replace(playerTag, "");
        if (SystemUtils.IS_OS_WINDOWS) {
            FileUtils.writeStringToFile(new File("out/blacklist/cocblacklist.txt"), blacklist);
        } else if (SystemUtils.IS_OS_LINUX) {
            FileUtils.writeStringToFile(new File("/home/thijs/Infobotfiles/cocblacklist.txt"), blacklist);
        }
        return "Speler: " + playerJson.getString("name") + " met tag: " + playerTag + " is verwijderd van de zwarte lijst!";
    }
}
